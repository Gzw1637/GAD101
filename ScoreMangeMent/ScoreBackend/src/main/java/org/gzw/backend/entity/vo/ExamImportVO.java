package org.gzw.backend.entity.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import java.util.Date;

/**
 * 考试导入 VO 类
 * 用于 Excel 导入
 */
@ColumnWidth(20)
public class ExamImportVO {
    
    @ExcelProperty("考试代码")
    private Long examCode;
    
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
    private Integer gra;
    
    @ExcelProperty("负责人姓名")
    private String teacherName;

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public Long getExamCode() {
        return examCode;
    }

    public void setExamCode(Long examCode) {
        this.examCode = examCode;
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

    public Integer getGra() {
        return gra;
    }

    public void setGra(Integer gra) {
        this.gra = gra;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }
}
