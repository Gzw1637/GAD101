package org.gzw.backend.entity.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;

import java.util.Date;

/**
 * 考试导出 VO 类
 * 用于 Excel 导出，带样式优化
 */
@ColumnWidth(18)
@ContentRowHeight(20)
@HeadRowHeight(25)
public class ExamExportVO {
    
    @ExcelProperty("序号")
    private Integer index;
    
    @ExcelProperty("考试名称")
    private String examName;
    
    @ExcelProperty("科目")
    private String subject;
    
    @ExcelProperty("考试级别")
    private String examLv;
    
    @ExcelProperty("考试时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private Date examTime;
    
    @ExcelProperty("年级")
    private String grade;
    
    @ExcelProperty("负责人")
    private String teacherName;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getExamLv() {
        return examLv;
    }

    public void setExamLv(String examLv) {
        this.examLv = examLv;
    }

    public Date getExamTime() {
        return examTime;
    }

    public void setExamTime(Date examTime) {
        this.examTime = examTime;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }
}
