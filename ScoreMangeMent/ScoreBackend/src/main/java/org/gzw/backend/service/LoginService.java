package org.gzw.backend.service;

import org.gzw.backend.entity.dto.LoginRequest;
import org.gzw.backend.entity.dto.LoginResponse;

public interface LoginService {
    
    LoginResponse login(LoginRequest loginRequest);
    
    void logout(Long userId);
}
