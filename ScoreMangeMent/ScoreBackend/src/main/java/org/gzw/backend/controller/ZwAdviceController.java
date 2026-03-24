package org.gzw.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.gzw.backend.common.Result;
import org.gzw.backend.entity.ZwAdvice;
import org.gzw.backend.entity.ZwAdviceImage;
import org.gzw.backend.entity.vo.ZwAdviceVO;
import org.gzw.backend.service.ZwAdviceImageService;
import org.gzw.backend.service.ZwAdviceService;
import org.gzw.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/advice")
@CrossOrigin
public class ZwAdviceController {

    @Autowired
    private ZwAdviceService zwAdviceService;

    @Autowired
    private ZwAdviceImageService zwAdviceImageService;

    @Value("${advice.upload.path:/tmp/advice-images}")
    private String uploadPath;

    @Value("${advice.upload.base-url:/api/advice/images}")
    private String baseUrl;

    @PostMapping("/add")
    public Result<Long> addAdvice(@RequestBody ZwAdvice zwAdvice) {
        Long result = zwAdviceService.addAdvice(zwAdvice);
        if (result != null && result > 0) {
            return Result.success(result);
        }
        return Result.error(500, "添加意见失败");
    }

    @PutMapping("/update")
    public Result<Integer> updateAdvice(@RequestBody ZwAdvice zwAdvice) {
        int result = zwAdviceService.updateAdvice(zwAdvice);
        if (result > 0) {
            return Result.success(result);
        }
        return Result.error(500, "更新意见失败");
    }

    @DeleteMapping("/delete/{adviceId}")
    public Result<Integer> deleteAdvice(@PathVariable Long adviceId) {
        int result = zwAdviceService.deleteAdvice(adviceId);
        if (result > 0) {
            return Result.success(result);
        }
        return Result.error(500, "删除意见失败");
    }

    @GetMapping("/getById/{adviceId}")
    public Result<ZwAdviceVO> getAdviceById(@PathVariable Long adviceId) {
        ZwAdviceVO advice = zwAdviceService.getAdviceById(adviceId);
        if (advice != null) {
            return Result.success(advice);
        }
        return Result.error(404, "意见不存在");
    }

    @GetMapping("/list")
    public Result<List<ZwAdviceVO>> getAllAdvices() {
        List<ZwAdviceVO> advices = zwAdviceService.getAllAdvices();
        return Result.success(advices);
    }

    @GetMapping("/recipient/{recipientId}")
    public Result<List<ZwAdviceVO>> getAdvicesByRecipientId(@PathVariable Long recipientId) {
        List<ZwAdviceVO> advices = zwAdviceService.getAdvicesByRecipientId(recipientId);
        return Result.success(advices);
    }

    @GetMapping("/sender/{senderId}")
    public Result<List<ZwAdviceVO>> getAdvicesBySenderId(@PathVariable Long senderId) {
        List<ZwAdviceVO> advices = zwAdviceService.getAdvicesBySenderId(senderId);
        return Result.success(advices);
    }

    @PostMapping("/search/advanced")
    public Result<List<ZwAdviceVO>> searchAdvicesByConditions(
            @RequestParam(required = false) Long recipientId,
            @RequestParam(required = false) Long senderId,
            @RequestParam(required = false) Integer adviceType,
            @RequestParam(required = false) Integer adviceStatus) {
        List<ZwAdviceVO> advices = zwAdviceService.searchAdvicesByConditions(recipientId, senderId, adviceType, adviceStatus);
        return Result.success(advices);
    }

    @PostMapping("/upload/{adviceId}")
    public Result<Integer> uploadAdviceImages(
            @PathVariable Long adviceId,
            @RequestParam("files") MultipartFile[] files) {
        try {
            int count = zwAdviceService.uploadAdviceImages(adviceId, files);
            if (count > 0) {
                return Result.success(count);
            }
            return Result.error(400, "没有上传任何图片");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "上传失败：" + e.getMessage());
        }
    }

    @GetMapping("/images/{fileName}")
    public ResponseEntity<Resource> downloadAdviceImage(@PathVariable String fileName) {
        Path filePath = Paths.get(uploadPath).resolve(fileName).normalize();

        File file = filePath.toFile();
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(file);

        try {
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "inline; filename=\"" + URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20") + "\"")
                    .body(resource);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/image/{imageId}")
    public Result<Integer> deleteAdviceImage(@PathVariable Long imageId) {
        int result = zwAdviceService.deleteAdviceImage(imageId);
        if (result > 0) {
            return Result.success(result);
        }
        return Result.error(500, "删除图片失败");
    }

    @GetMapping("/images/list/{adviceId}")
    public Result<List<ZwAdviceImage>> getAdviceImagesByAdviceId(@PathVariable Long adviceId) {
        List<ZwAdviceImage> images = zwAdviceImageService.getAdviceImagesByAdviceId(adviceId);
        return Result.success(images);
    }

    @GetMapping("/my/sent")
    public Result<List<ZwAdviceVO>> getMySentAdvices(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                return Result.error(401, "未授权");
            }
            token = token.substring(7);
            Long currentUserId = JwtUtil.getUserIdFromToken(token);
            List<ZwAdviceVO> advices = zwAdviceService.getMySentAdvices(currentUserId);
            return Result.success(advices);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "获取发件箱失败：" + e.getMessage());
        }
    }

    @GetMapping("/my/received")
    public Result<List<ZwAdviceVO>> getMyReceivedAdvices(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                return Result.error(401, "未授权");
            }
            token = token.substring(7);
            Long currentUserId = JwtUtil.getUserIdFromToken(token);
            List<ZwAdviceVO> advices = zwAdviceService.getMyReceivedAdvices(currentUserId);
            return Result.success(advices);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "获取收件箱失败：" + e.getMessage());
        }
    }

    @PostMapping("/reply")
    public Result<Integer> replyAdvice(@RequestBody ZwAdvice zwAdvice, HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                return Result.error(401, "未授权");
            }
            token = token.substring(7);
            Long currentUserId = JwtUtil.getUserIdFromToken(token);
            zwAdvice.setSenderId(currentUserId);
            int result = zwAdviceService.replyAdvice(zwAdvice);
            if (result > 0) {
                return Result.success(result);
            }
            return Result.error(500, "回复意见失败");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "回复意见失败：" + e.getMessage());
        }
    }
}
