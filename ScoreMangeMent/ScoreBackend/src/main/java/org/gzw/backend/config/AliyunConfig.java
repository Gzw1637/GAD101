package org.gzw.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * AI 服务配置类
 */
@Configuration
public class AliyunConfig {

    @Value("${ai.deepseek.api-key}")
    private String deepSeekApiKey;

    @Value("${ai.deepseek.base-url}")
    private String deepSeekBaseUrl;

    @Value("${ai.baidu-ocr.api-key}")
    private String baiduOcrApiKey;

    @Value("${ai.baidu-ocr.secret-key}")
    private String baiduOcrSecretKey;

    @Value("${aliyun.upload.exam-papers-path}")
    private String uploadPath;

    @Value("${aliyun.upload.exam-papers-base-url}")
    private String baseUrl;

    /**
     * 配置 RestTemplate 用于 HTTP 调用
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * 获取 DeepSeek API Key
     */
    public String getDeepSeekApiKey() {
        return deepSeekApiKey;
    }

    /**
     * 获取 DeepSeek 基础 URL
     */
    public String getDeepSeekBaseUrl() {
        return deepSeekBaseUrl;
    }

    /**
     * 获取百度 OCR API Key
     */
    public String getBaiduOcrApiKey() {
        return baiduOcrApiKey;
    }

    /**
     * 获取百度 OCR Secret Key
     */
    public String getBaiduOcrSecretKey() {
        return baiduOcrSecretKey;
    }

    /**
     * 获取上传路径
     */
    public String getUploadPath() {
        return uploadPath;
    }

    /**
     * 获取基础 URL
     */
    public String getBaseUrl() {
        return baseUrl;
    }
}
