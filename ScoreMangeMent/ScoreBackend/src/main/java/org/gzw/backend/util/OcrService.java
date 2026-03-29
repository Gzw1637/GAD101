package org.gzw.backend.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * OCR 识别服务工具类
 * 使用阿里云整页试卷识别 API（主），百度 OCR（备用）
 */
@Component
public class OcrService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AliyunOcrService aliyunOcrService;

    @Value("${ai.baidu-ocr.api-key}")
    private String apiKey;

    @Value("${ai.baidu-ocr.secret-key}")
    private String secretKey;

    @Value("${ai.baidu-ocr.general-url}")
    private String generalOcrUrl;

    @Value("${aliyun.upload.exam-papers-path}")
    private String uploadPath;

    @Value("${aliyun.upload.exam-papers-base-url}")
    private String baseUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 上传试卷图片并保存到本地
     */
    public String uploadPaperImage(MultipartFile file, Long studentId, Long examId) throws IOException {
        // 创建上传目录
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 生成文件名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".") 
                ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
                : ".jpg";
        String filename = "paper_" + studentId + "_" + examId + "_" + System.currentTimeMillis() + extension;

        // 保存文件
        String filePath = uploadPath + "/" + filename;
        file.transferTo(new File(filePath));

        // 返回访问 URL
        return baseUrl + "/" + filename;
    }

    /**
     * 识别试卷图片（使用阿里云整页试卷识别，百度 OCR 备用）
     *
     * @param imageUrl 图片 URL
     * @return OCR 识别结果
     */
    public String recognizePaper(String imageUrl) {
        try {
            // 优先使用阿里云整页试卷识别
            return aliyunOcrService.recognizeEduPaper(imageUrl);
        } catch (Exception e) {
            // 阿里云 OCR 失败，降级使用百度通用 OCR
            try {
                String localPath = imageUrl.replace(baseUrl, uploadPath);
                byte[] imageBytes = Files.readAllBytes(Paths.get(localPath));
                String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                return callBaiduGeneralOcrApi(base64Image);
            } catch (Exception ex) {
                throw new RuntimeException("OCR 识别失败：" + ex.getMessage(), ex);
            }
        }
    }

    /**
     * 获取百度 OCR Access Token
     */
    private String getAccessToken() {
        try {
            String tokenUrl = "https://aip.baidubce.com/oauth/2.0/token?" +
                    "grant_type=client_credentials&" +
                    "client_id=" + apiKey + "&" +
                    "client_secret=" + secretKey;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                return rootNode.path("access_token").asText();
            }

            throw new RuntimeException("获取 Access Token 失败");
        } catch (Exception e) {
            throw new RuntimeException("获取百度 OCR Access Token 失败：" + e.getMessage(), e);
        }
    }

    /**
     * 调用百度 OCR API（通用文字识别 - 备用）
     *
     * @param base64Image Base64 编码的图片
     * @return OCR 识别结果
     */
    private String callBaiduGeneralOcrApi(String base64Image) throws Exception {
        // 获取 Access Token
        String accessToken = getAccessToken();

        // OCR API URL
        String apiUrl = generalOcrUrl + "/general_basic?access_token=" + accessToken;

        // 构建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 构建请求体
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("image", base64Image);
        body.add("language_type", "CHN_ENG"); // 中英文混合
        body.add("detect_direction", "false");
        body.add("detect_language", "false");
        body.add("probability", "false");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        // 发送请求
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            // 解析响应，提取识别的文字
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            JsonNode wordsResultNode = rootNode.path("words_result");
            
            StringBuilder result = new StringBuilder();
            if (wordsResultNode.isArray()) {
                for (JsonNode item : wordsResultNode) {
                    String words = item.path("words").asText();
                    result.append(words).append("\n");
                }
            }
            
            return result.toString().trim();
        } else {
            throw new RuntimeException("百度 OCR API 调用失败，响应码：" + response.getStatusCode());
        }
    }

    /**
     * 删除试卷图片
     */
    public void deletePaperImage(String imageUrl) {
        try {
            String localPath = imageUrl.replace(baseUrl, uploadPath);
            File file = new File(localPath);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            throw new RuntimeException("删除图片失败：" + e.getMessage(), e);
        }
    }
}
