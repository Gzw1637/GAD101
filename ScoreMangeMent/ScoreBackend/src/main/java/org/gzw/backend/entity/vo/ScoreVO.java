package org.gzw.backend.entity.vo;

import java.math.BigDecimal;

/**
 * 成绩 VO 类
 * 用于前端展示，包含关联表的信息
 */
public class ScoreVO {
    private Long scoreId;
    private Long examId;
    private Long studentId;
    private BigDecimal score;
    
    // 来自 g_exam 表
    private String examName;
    private Integer subjectType;
    private String subjectTypeDesc;
    private java.time.LocalDateTime examTime;
    private String remark;
    
    // 来自 zw_student 表
    private Integer studentCode;
    private Integer gra;
    private Integer cla;
    
    // 排名
    private Integer classRank;
    private Integer gradeRank;
    
    // 来自 g_user 表
    private String name;

    public Long getScoreId() {
        return scoreId;
    }

    public void setScoreId(Long scoreId) {
        this.scoreId = scoreId;
    }

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public Integer getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(Integer subjectType) {
        this.subjectType = subjectType;
    }

    public String getSubjectTypeDesc() {
        return subjectTypeDesc;
    }

    public void setSubjectTypeDesc(String subjectTypeDesc) {
        this.subjectTypeDesc = subjectTypeDesc;
    }

    public Integer getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(Integer studentCode) {
        this.studentCode = studentCode;
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

    public Integer getClassRank() {
        return classRank;
    }

    public void setClassRank(Integer classRank) {
        this.classRank = classRank;
    }

    public Integer getGradeRank() {
        return gradeRank;
    }

    public void setGradeRank(Integer gradeRank) {
        this.gradeRank = gradeRank;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public java.time.LocalDateTime getExamTime() {
        return examTime;
    }

    public void setExamTime(java.time.LocalDateTime examTime) {
        this.examTime = examTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "ScoreVO{" +
                "scoreId=" + scoreId +
                ", examId=" + examId +
                ", studentId=" + studentId +
                ", score=" + score +
                ", examName='" + examName + '\'' +
                ", subjectType=" + subjectType +
                ", subjectTypeDesc='" + subjectTypeDesc + '\'' +
                ", examTime=" + examTime +
                ", remark='" + remark + '\'' +
                ", studentCode=" + studentCode +
                ", gra=" + gra +
                ", cla=" + cla +
                ", classRank=" + classRank +
                ", gradeRank=" + gradeRank +
                ", name='" + name + '\'' +
                '}';
    }
}
