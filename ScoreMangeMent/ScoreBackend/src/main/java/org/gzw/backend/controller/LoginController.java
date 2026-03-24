package org.gzw.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.gzw.backend.common.Result;
import org.gzw.backend.entity.dto.LoginRequest;
import org.gzw.backend.entity.dto.LoginResponse;
import org.gzw.backend.service.LoginService;
import org.gzw.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = loginService.login(loginRequest);
            return Result.success(response);
        } catch (RuntimeException e) {
            return Result.error(401, e.getMessage());
        } catch (Exception e) {
            return Result.error(500, "登录失败：" + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                Long userId = JwtUtil.getUserIdFromToken(token);
                loginService.logout(userId);
            } catch (Exception e) {
            }
        }
        return Result.success(null);
    }

    @GetMapping("/userInfo")
    public Result<LoginResponse> getUserInfo(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        try {
            Long userId = JwtUtil.getUserIdFromToken(token);
            Integer role = JwtUtil.getRoleFromToken(token);
            
            LoginResponse response = new LoginResponse();
            response.setUserId(userId);
            response.setRole(role);
            response.setRedirectPath(getRedirectPath(role));
            
            return Result.success(response);
        } catch (Exception e) {
            return Result.error(401, "获取用户信息失败");
        }
    }

    private String getRedirectPath(Integer role) {
        if (role == null) {
            return "/login";
        }
        
        switch (role) {
            case 14981001:
                return "/student";
            case 14981002:
                return "/teacher";
            case 14981003:
                return "/admin";
            default:
                return "/login";
        }
    }
}
