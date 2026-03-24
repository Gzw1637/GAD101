package org.gzw.backend.service.Impl;

import org.gzw.backend.entity.User;
import org.gzw.backend.entity.dto.LoginRequest;
import org.gzw.backend.entity.dto.LoginResponse;
import org.gzw.backend.entity.vo.UserVO;
import org.gzw.backend.mapper.UserMapper;
import org.gzw.backend.service.LoginService;
import org.gzw.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class LoginServiceImpl implements LoginService {

    private static final int MAX_LOGIN_FAIL_COUNT = 5;
    private static final long LOCK_DURATION_MINUTES = 30;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        User user = userMapper.selectByUserName(loginRequest.getUserName());
        
        if (user == null) {
            throw new RuntimeException("用户名不存在");
        }

        if (user.getLockTime() != null) {
            if (isLocked(user.getLockTime())) {
                Date unlockTime = getUnlockTime(user.getLockTime());
                throw new RuntimeException("账号已被锁定，请于 " + 
                    java.time.LocalDateTime.ofInstant(unlockTime.toInstant(), 
                        java.time.ZoneId.systemDefault()) + 
                    " 后重试");
            } else {
                resetLoginFailCount(user.getUserId());
                user = userMapper.selectByUserName(loginRequest.getUserName());
            }
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            int failCount = user.getLoginFailCount() == null ? 0 : user.getLoginFailCount();
            failCount++;
            
            if (failCount >= MAX_LOGIN_FAIL_COUNT) {
                Date lockTime = new Date();
                userMapper.updateLoginFailCount(user.getUserId(), failCount, lockTime);
                throw new RuntimeException("密码错误次数过多，账号已被锁定 " + 
                    LOCK_DURATION_MINUTES + " 分钟，请稍后再试");
            } else {
                userMapper.updateLoginFailCount(user.getUserId(), failCount, null);
                int remaining = MAX_LOGIN_FAIL_COUNT - failCount;
                throw new RuntimeException("密码错误，您还有 " + remaining + " 次尝试机会");
            }
        }

        resetLoginFailCount(user.getUserId());

        if (user.getUserStatus() != null && user.getUserStatus() != 10001001) {
            throw new RuntimeException("账号已被禁用，请联系管理员");
        }

        UserVO userVO = userMapper.selectById(user.getUserId());
        
        String token = JwtUtil.generateToken(user.getUserId(), user.getRole());
        
        LoginResponse response = new LoginResponse();
        response.setUserId(userVO.getUserId());
        response.setUserName(userVO.getUserName());
        response.setName(userVO.getName());
        response.setRole(userVO.getRole());
        response.setRoleDesc(userVO.getRoleDesc());
        response.setToken(token);
        response.setRedirectPath(getRedirectPath(userVO.getRole()));
        
        return response;
    }

    @Override
    public void logout(Long userId) {
    }

    private boolean isLocked(Date lockTime) {
        if (lockTime == null) {
            return false;
        }
        long currentTime = System.currentTimeMillis();
        long lockTimeMillis = lockTime.getTime();
        long lockDurationMillis = LOCK_DURATION_MINUTES * 60 * 1000;
        return (currentTime - lockTimeMillis) < lockDurationMillis;
    }

    private Date getUnlockTime(Date lockTime) {
        if (lockTime == null) {
            return null;
        }
        return new Date(lockTime.getTime() + LOCK_DURATION_MINUTES * 60 * 1000);
    }

    private void resetLoginFailCount(Long userId) {
        userMapper.updateLoginFailCount(userId, 0, null);
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
