package org.gzw.backend.service;

import org.gzw.backend.entity.User;
import org.gzw.backend.entity.vo.UserVO;
import java.util.List;

public interface UserService {
    
    int addUser(User user);
    
    int updateUser(User user);
    
    int deleteUser(Long userId);
    
    UserVO getUserById(Long userId);
    
    List<UserVO> getAllUsers();
    
    List<UserVO> searchUsersByName(String name);
    
    List<UserVO> searchUsersByUserName(String userName);
    
    List<UserVO> searchUsersByConditions(Long role, Integer userStatus, String name);
    
    int unlockUser(Long userId);
}
