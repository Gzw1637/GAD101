package org.gzw.backend.service.Impl;

import org.gzw.backend.entity.User;
import org.gzw.backend.entity.vo.UserVO;
import org.gzw.backend.mapper.UserMapper;
import org.gzw.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public int addUser(User user) {
        // 对密码进行 BCrypt 加密
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userMapper.insert(user);
    }

    @Override
    public int updateUser(User user) {
        // 如果密码被修改（不是占位符），则进行 BCrypt 加密
        if (user.getPassword() != null && !user.getPassword().isEmpty() 
            && !user.getPassword().equals("********")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            // 密码未修改则设为 null，避免覆盖原密码
            user.setPassword(null);
        }
        return userMapper.update(user);
    }

    @Override
    public int deleteUser(Long userId) {
        return userMapper.deleteById(userId);
    }

    @Override
    public UserVO getUserById(Long userId) {
        return userMapper.selectById(userId);
    }

    @Override
    public List<UserVO> getAllUsers() {
        return userMapper.selectAll();
    }

    @Override
    public List<UserVO> searchUsersByName(String name) {
        return userMapper.selectByName(name);
    }

    @Override
    public List<UserVO> searchUsersByUserName(String userName) {
        return userMapper.selectByUserNameLike(userName);
    }

    @Override
    public List<UserVO> searchUsersByConditions(Long role, Integer userStatus, String name) {
        return userMapper.searchByConditions(role, userStatus, name);
    }

    @Override
    public int unlockUser(Long userId) {
        return userMapper.unlockUser(userId);
    }
}
