package org.gzw.backend.entity.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

/**
 * 学生导入 VO 类
 * 用于 Excel 导入
 */
@ColumnWidth(15)
public class StudentImportVO {
    
    @ExcelProperty("用户名")
    private String userName;
    
    @ExcelProperty("姓名")
    private String name;
    
    @ExcelProperty("密码")
    private String password;
    
    @ExcelProperty("性别")
    private String sex;
    
    @ExcelProperty("学号")
    private Integer studentCode;
    
    @ExcelProperty("邮箱")
    private String email;
    
    @ExcelProperty("电话")
    private Long phone;
    
    @ExcelProperty("年级")
    private Integer gra;
    
    @ExcelProperty("班级")
    private Integer cla;
    
    @ExcelProperty("学生状态")
    private String studentStatus;
    
    @ExcelProperty("学生类型")
    private String studentType;

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

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
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

    public String getStudentStatus() {
        return studentStatus;
    }

    public void setStudentStatus(String studentStatus) {
        this.studentStatus = studentStatus;
    }

    public String getStudentType() {
        return studentType;
    }

    public void setStudentType(String studentType) {
        this.studentType = studentType;
    }
}
