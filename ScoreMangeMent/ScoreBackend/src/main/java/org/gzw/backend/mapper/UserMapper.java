package org.gzw.backend.mapper;

import org.apache.ibatis.annotations.Param;
import org.gzw.backend.entity.User;
import org.gzw.backend.entity.vo.UserVO;
import java.util.Date;
import java.util.List;

public interface UserMapper {
    
    int insert(User user);
    
    int update(User user);
    
    int deleteById(Long userId);
    
    UserVO selectById(Long userId);
    
    List<UserVO> selectAll();
    
    User selectByUserName(String userName);
    
    List<UserVO> selectByUserNameLike(String userName);
    
    List<UserVO> selectByName(String name);
    
    List<UserVO> searchByConditions(@Param("role") Long role, @Param("userStatus") Integer userStatus, @Param("name") String name);
    
    int updateLoginFailCount(@Param("userId") Long userId, @Param("failCount") Integer failCount, @Param("lockTime") Date lockTime);
    
    int unlockUser(Long userId);
}
