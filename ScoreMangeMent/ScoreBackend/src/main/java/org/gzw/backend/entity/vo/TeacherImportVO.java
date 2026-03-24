package org.gzw.backend.entity.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

/**
 * 教师导入 VO 类
 * 用于 Excel 导入
 */
@ColumnWidth(15)
public class TeacherImportVO {
    
    @ExcelProperty("用户名")
    private String userName;
    
    @ExcelProperty("姓名")
    private String name;
    
    @ExcelProperty("密码")
    private String password;
    
    @ExcelProperty("性别")
    private String sex;
    
    @ExcelProperty("教师编号")
    private Integer teacherCode;
    
    @ExcelProperty("教学科目")
    private String teachSubject;
    
    @ExcelProperty("电话")
    private Long phone;
    
    @ExcelProperty("年级")
    private Integer gra;
    
    @ExcelProperty("班级")
    private Integer cla;
    
    @ExcelProperty("教师状态")
    private String teacherStatus;
    
    @ExcelProperty("教师类型")
    private String teacherType;

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

    public Integer getTeacherCode() {
        return teacherCode;
    }

    public void setTeacherCode(Integer teacherCode) {
        this.teacherCode = teacherCode;
    }

    public String getTeachSubject() {
        return teachSubject;
    }

    public void setTeachSubject(String teachSubject) {
        this.teachSubject = teachSubject;
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

    public String getTeacherStatus() {
        return teacherStatus;
    }

    public void setTeacherStatus(String teacherStatus) {
        this.teacherStatus = teacherStatus;
    }

    public String getTeacherType() {
        return teacherType;
    }

    public void setTeacherType(String teacherType) {
        this.teacherType = teacherType;
    }
}
