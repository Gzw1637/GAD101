package org.gzw.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 安全配置类：管理BCrypt单例实例
 */
@Configuration
public class SecurityConfig {

    /**
     * 全局唯一的BCrypt密码编码器实例
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        // 固定版本与强度，确保匹配一致性
        return new BCryptPasswordEncoder(10);
    }
}