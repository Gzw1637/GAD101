package org.gzw.backend.entity.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;

/**
 * 教师导出 VO 类
 * 用于 Excel 导出，带样式优化
 */
@ColumnWidth(18)
@ContentRowHeight(20)
@HeadRowHeight(25)
public class TeacherExportVO {
    
    @ExcelProperty("序号")
    private Integer index;
    
    @ExcelProperty("用户名")
    private String userName;
    
    @ExcelProperty("姓名")
    private String name;
    
    @ExcelProperty("性别")
    private String sex;
    
    @ExcelProperty("教师编号")
    private Integer teacherCode;
    
    @ExcelProperty("教学科目")
    private String teachSubject;
    
    @ExcelProperty("教师类型")
    private String teacherType;
    
    @ExcelProperty("年级")
    private String grade;
    
    @ExcelProperty("班级")
    private String className;
    
    @ExcelProperty("电话")
    private String phone;
    
    @ExcelProperty("教师状态")
    private String teacherStatus;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
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

    public String getTeacherType() {
        return teacherType;
    }

    public void setTeacherType(String teacherType) {
        this.teacherType = teacherType;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTeacherStatus() {
        return teacherStatus;
    }

    public void setTeacherStatus(String teacherStatus) {
        this.teacherStatus = teacherStatus;
    }
}
