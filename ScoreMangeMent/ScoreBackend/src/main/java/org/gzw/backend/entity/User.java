package org.gzw.backend.entity;

import java.util.Date;

/**
 * 系统用户实体类
 * 对应数据库表：g_user
 */
public class User {
    /** 用户 ID */
    private Long userId;
    /** 用户名 */
    private String userName;
    /** BCrypt 加密密码 */
    private String password;
    /** 姓名 */
    private String name;
    /** 用户状态 */
    private Integer userStatus;
    /** 用户身份 */
    private Integer role;
    /** 登录失败次数 */
    private Integer loginFailCount;
    /** 账号锁定时间 */
    private Date lockTime;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(Integer userStatus) {
        this.userStatus = userStatus;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public Integer getLoginFailCount() {
        return loginFailCount;
    }

    public void setLoginFailCount(Integer loginFailCount) {
        this.loginFailCount = loginFailCount;
    }

    public Date getLockTime() {
        return lockTime;
    }

    public void setLockTime(Date lockTime) {
        this.lockTime = lockTime;
    }
}
