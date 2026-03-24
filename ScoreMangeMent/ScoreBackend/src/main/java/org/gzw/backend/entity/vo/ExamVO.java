package org.gzw.backend.entity.vo;

import java.util.Date;

public class ExamVO {
    private Long examId;
    private Long userId;
    private Long examCode;
    private String examName;
    private Integer subjectType;
    private String subjectTypeDesc;
    private Integer examLv;
    private String examLvDesc;
    private Date examTime;
    private Integer gra;
    private String userName;
    private String name;

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getExamCode() {
        return examCode;
    }

    public void setExamCode(Long examCode) {
        this.examCode = examCode;
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

    public Integer getExamLv() {
        return examLv;
    }

    public void setExamLv(Integer examLv) {
        this.examLv = examLv;
    }

    public String getExamLvDesc() {
        return examLvDesc;
    }

    public void setExamLvDesc(String examLvDesc) {
        this.examLvDesc = examLvDesc;
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

    @Override
    public String toString() {
        return "ExamVO{" +
                "examId=" + examId +
                ", userId=" + userId +
                ", examCode=" + examCode +
                ", examName='" + examName + '\'' +
                ", subjectType=" + subjectType +
                ", examLv=" + examLv +
                ", examTime=" + examTime +
                ", gra=" + gra +
                ", userName='" + userName + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}