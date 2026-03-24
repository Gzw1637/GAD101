package org.gzw.backend.controller;

import org.gzw.backend.common.Result;
import org.gzw.backend.entity.User;
import org.gzw.backend.entity.vo.UserVO;
import org.gzw.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
}
