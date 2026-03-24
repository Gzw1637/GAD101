package org.gzw.backend;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptTest {
    public static void main(String[] args) {
        // 创建BCryptPasswordEncoder实例
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        
        // 测试密码
        String password = "123456";
        
        // 加密密码
        String encryptedPassword = passwordEncoder.encode(password);
        System.out.println("原始密码: " + password);
        System.out.println("加密后的密码: " + encryptedPassword);
        
        // 验证密码
        boolean matches = passwordEncoder.matches(password, encryptedPassword);
        System.out.println("密码验证结果: " + matches);
        
        // 验证一个错误的密码
        boolean wrongMatches = passwordEncoder.matches("wrong", encryptedPassword);
        System.out.println("错误密码验证结果: " + wrongMatches);
        
        // 验证数据库中的密码
        String dbPassword = "$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi";
        boolean dbMatches = passwordEncoder.matches(password, dbPassword);
        System.out.println("数据库密码验证结果: " + dbMatches);
    }
}