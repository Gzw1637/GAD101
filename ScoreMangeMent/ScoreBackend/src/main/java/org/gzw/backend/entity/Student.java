package org.gzw.backend.entity;

/**
 * 学生信息实体类
 * 对应数据库表：zw_student
 */
public class Student {
    /** 学生 ID */
    private Long studentId;
    /** 关联用户 ID */
    private Long userId;
    /** 用户名 */
    private String userName;
    /** 姓名 */
    private String name;
    /** 密码 */
    private String password;
    /** 性别 */
    private Integer sex;
    /** 学号 */
    private Integer studentCode;
    /** 邮箱 */
    private String email;
    /** 学生状态 */
    private Integer studentStatus;
    /** 学生类型 */
    private Integer studentType;
    /** 电话 */
    private Long phone;
    /** 年级 */
    private Integer gra;
    /** 班级 */
    private Integer cla;

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(Integer studentCode) {
        this.studentCode = studentCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getStudentStatus() {
        return studentStatus;
    }

    public void setStudentStatus(Integer studentStatus) {
        this.studentStatus = studentStatus;
    }

    public Integer getStudentType() {
        return studentType;
    }

    public void setStudentType(Integer studentType) {
        this.studentType = studentType;
    }

    public Long getPhone() {
        return phone;
    }

    public void setPhone(Long phone) {
        this.phone = phone;
    }

    public Integer getGra() {
        return gra;
    }

    public void setGra(Integer gra) {
        this.gra = gra;
    }

    public Integer getCla() {
        return cla;
    }

    public void setCla(Integer cla) {
        this.cla = cla;
    }
}
