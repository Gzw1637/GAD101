package org.gzw.backend.controller;

import org.gzw.backend.common.Result;
import org.gzw.backend.entity.User;
import org.gzw.backend.entity.vo.UserVO;
import org.gzw.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/add")
    public Result<Integer> addUser(@RequestBody User user) {
        int result = userService.addUser(user);
        if (result > 0) {
            return Result.success(result);
        }
        return Result.error(500, "添加用户失败");
    }

    @PutMapping("/update")
    public Result<Integer> updateUser(@RequestBody User user) {
        int result = userService.updateUser(user);
        if (result > 0) {
            return Result.success(result);
        }
        return Result.error(500, "更新用户失败");
    }

    @DeleteMapping("/{userId}")
    public Result<Integer> deleteUser(@PathVariable Long userId) {
        int result = userService.deleteUser(userId);
        if (result > 0) {
            return Result.success(result);
        }
        return Result.error(500, "删除用户失败");
    }

    @GetMapping("/search")
    public Result<List<UserVO>> searchUsers(@RequestParam String name) {
        List<UserVO> users = userService.searchUsersByName(name);
        return Result.success(users);
    }

    @GetMapping("/searchByUsername")
    public Result<List<UserVO>> searchUsersByUsername(@RequestParam String username) {
        List<UserVO> users = userService.searchUsersByUserName(username);
        return Result.success(users);
    }

    @PostMapping("/search/advanced")
    public Result<List<UserVO>> searchUsersByConditions(
            @RequestParam(required = false) Long role,
            @RequestParam(required = false) Integer userStatus,
            @RequestParam(required = false) String name) {
        List<UserVO> users = userService.searchUsersByConditions(role, userStatus, name);
        return Result.success(users);
    }

    @GetMapping("/list")
    public Result<List<UserVO>> getAllUsers() {
        List<UserVO> users = userService.getAllUsers();
        return Result.success(users);
    }

    @GetMapping("/{userId}")
    public Result<UserVO> getUserById(@PathVariable Long userId) {
        UserVO user = userService.getUserById(userId);
        if (user != null) {
            return Result.success(user);
        }
        return Result.error(404, "用户不存在");
    }

    @PutMapping("/unlock/{userId}")
    public Result<Integer> unlockUser(@PathVariable Long userId) {
        int result = userService.unlockUser(userId);
        if (result > 0) {
            return Result.success(result);
        }
        return Result.error(500, "解封用户失败");
    }

    @PostMapping("/change-password")
    public Result<Integer> changePassword(HttpServletRequest request, @RequestBody Map<String, String> passwordMap) {
        try {
            // 从请求属性中获取当前用户的 ID
            Long userId = (Long) request.getAttribute("userId");
            
            // 根据用户 ID 获取用户信息，以获取用户名
            UserVO userVO = userService.getUserById(userId);
            if (userVO == null) {
                return Result.error(404, "用户不存在");
            }
            
            // 从请求体中获取旧密码和新密码
            String oldPassword = passwordMap.get("oldPassword");
            String newPassword = passwordMap.get("newPassword");
            
            // 验证参数
            if (oldPassword == null || oldPassword.isEmpty() || newPassword == null || newPassword.isEmpty()) {
                return Result.error(400, "旧密码和新密码不能为空");
            }
            
            // 调用修改密码的方法
            int result = userService.changePassword(userVO.getUserName(), oldPassword, newPassword);
            if (result > 0) {
                return Result.success(result);
            }
            return Result.error(500, "密码修改失败");
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            return Result.error(500, "系统异常，请稍后重试");
        }
    }
}
