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

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 阿里云 OCR 服务工具类
 * 使用阿里云整页试卷识别 API
 */
@Component
public class AliyunOcrService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ai.aliyun-ocr.access-key-id}")
    private String accessKeyId;

    @Value("${ai.aliyun-ocr.access-key-secret}")
    private String accessKeySecret;

    @Value("${ai.aliyun-ocr.edu-paper-url}")
    private String eduPaperUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 识别整页试卷（阿里云 OCR）
     *
     * @param imageUrl 图片 URL
     * @return OCR 识别结果
     */
    public String recognizeEduPaper(String imageUrl) {
        try {
            // 调用阿里云整页试卷识别 API
            String result = callAliyunEduPaperOcr(imageUrl, "scan");
            return formatEduPaperResult(result);
        } catch (Exception e) {
            throw new RuntimeException("阿里云 OCR 识别失败：" + e.getMessage(), e);
        }
    }

    /**
     * 调用阿里云整页试卷识别 API
     *
     * @param imageUrl 图片 URL
     * @param imageType 图片类型：scan-扫描图，photo-实拍图
     * @return OCR 识别结果（JSON 字符串）
     */
    private String callAliyunEduPaperOcr(String imageUrl, String imageType) throws Exception {
        // 生成时间戳
        String timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(new Date());
        
        // 生成随机数
        String nonce = UUID.randomUUID().toString();

        // 构建请求参数
        Map<String, String> params = new TreeMap<>();
        params.put("AccessKeyId", accessKeyId);
        params.put("Action", "RecognizeEduPaperOcr");
        params.put("Format", "JSON");
        params.put("ImageType", imageType);
        params.put("SignatureMethod", "HMAC-SHA1");
        params.put("SignatureNonce", nonce);
        params.put("SignatureVersion", "1.0");
        params.put("Timestamp", timestamp);
        params.put("Version", "2021-07-07");
        params.put("Url", imageUrl);

        // 生成签名
        String signature = generateSignature(params, accessKeySecret);
        params.put("Signature", signature);

        // 构建请求 URL
        StringBuilder urlBuilder = new StringBuilder(eduPaperUrl);
        urlBuilder.append("?");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            urlBuilder.append(entry.getKey()).append("=")
                    .append(URLEncoder.encode(entry.getValue(), "UTF-8"))
                    .append("&");
        }
        String fullUrl = urlBuilder.substring(0, urlBuilder.length() - 1);

        // 构建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 发送请求
        ResponseEntity<String> response = restTemplate.getForEntity(fullUrl, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new RuntimeException("阿里云 OCR API 调用失败，响应码：" + response.getStatusCode());
        }
    }

    /**
     * 生成阿里云 API 签名
     */
    private String generateSignature(Map<String, String> params, String accessKeySecret) throws Exception {
        // 构建待签名字符串
        StringBuilder sortedQuery = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sortedQuery.append(entry.getKey()).append("=")
                    .append(URLEncoder.encode(entry.getValue(), "UTF-8"))
                    .append("&");
        }
        String sortedQueryStr = sortedQuery.substring(0, sortedQuery.length() - 1);

        // 构建签名字符串
        String stringToSign = "GET&%2F&" + URLEncoder.encode(sortedQueryStr, "UTF-8");

        // 计算 HMAC-SHA1 签名
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(new SecretKeySpec((accessKeySecret + "&").getBytes(StandardCharsets.UTF_8), "HmacSHA1"));
        byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));

        // Base64 编码
        return Base64.getEncoder().encodeToString(signData);
    }

    /**
     * 格式化阿里云整页试卷识别结果
     *
     * @param jsonResponse JSON 格式的识别结果
     * @return 格式化后的文本
     */
    private String formatEduPaperResult(String jsonResponse) throws Exception {
        JsonNode rootNode = objectMapper.readTree(jsonResponse);
        StringBuilder result = new StringBuilder();

        result.append("【整页试卷识别结果】\n\n");

        // 检查是否有错误
        if (rootNode.has("Code")) {
            String code = rootNode.path("Code").asText();
            String message = rootNode.path("Message").asText();
            result.append("【识别警告】\n");
            result.append("错误码：").append(code).append("\n");
            result.append("错误信息：").append(message).append("\n\n");
        }

        // 获取 Data 字段
        if (rootNode.has("Data")) {
            JsonNode dataNode = rootNode.path("Data");

            // 识别的文字内容
            if (dataNode.has("content")) {
                result.append("【识别内容】\n");
                result.append(dataNode.path("content").asText()).append("\n\n");
            }

            // 文字块信息
            if (dataNode.has("prism_wordsInfo")) {
                JsonNode wordsInfo = dataNode.path("prism_wordsInfo");
                if (wordsInfo.isArray() && wordsInfo.size() > 0) {
                    result.append("【文字识别详情】\n");
                    result.append("识别到的文字块数量：").append(wordsInfo.size()).append("\n\n");

                    // 提取所有文字内容
                    StringBuilder allText = new StringBuilder();
                    for (JsonNode wordInfo : wordsInfo) {
                        String word = wordInfo.path("word").asText();
                        int prob = wordInfo.has("prob") ? wordInfo.path("prob").asInt() : 0;
                        allText.append(word).append(" (置信度：").append(prob).append("%)\n");
                    }
                    result.append(allText).append("\n");
                }
            }

            // 图片信息
            if (dataNode.has("width") && dataNode.has("height")) {
                result.append("【图片信息】\n");
                result.append("宽度：").append(dataNode.path("width").asInt()).append("px\n");
                result.append("高度：").append(dataNode.path("height").asInt()).append("px\n");
            }

            // 图案信息（如果有）
            if (dataNode.has("figure")) {
                JsonNode figures = dataNode.path("figure");
                if (figures.isArray() && figures.size() > 0) {
                    result.append("\n【图案信息】\n");
                    result.append("检测到图案数量：").append(figures.size()).append("\n");
                }
            }
        }

        // 返回原始 JSON 供 AI 进一步分析
        result.append("\n【原始识别数据】\n");
        result.append(jsonResponse);

        return result.toString().trim();
    }
}
