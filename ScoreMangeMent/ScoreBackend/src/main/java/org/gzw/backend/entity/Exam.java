package org.gzw.backend.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 考试实体类
 */
public class Exam implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 考试 ID
     */
    private Long examId;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 考试代码
     */
    private Long examCode;

    /**
     * 考试名称
     */
    private String examName;

    /**
     * 科目类型
     */
    private Integer subjectType;

    /**
     * 考试级别
     */
    private Integer examLv;

    /**
     * 考试时间
     */
    private Date examTime;

    /**
     * 年级
     */
    private Integer gra;

    public Exam() {
    }

    public Exam(Long examId, Long userId, Long examCode, String examName, Integer subjectType, Integer examLv, Date examTime, Integer gra) {
        this.examId = examId;
        this.userId = userId;
        this.examCode = examCode;
        this.examName = examName;
        this.subjectType = subjectType;
        this.examLv = examLv;
        this.examTime = examTime;
        this.gra = gra;
    }

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

    public Integer getExamLv() {
        return examLv;
    }

    public void setExamLv(Integer examLv) {
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

    @Override
    public String toString() {
        return "Exam{" +
                "examId=" + examId +
                ", userId=" + userId +
                ", examCode=" + examCode +
                ", examName='" + examName + '\'' +
                ", subjectType=" + subjectType +
                ", examLv=" + examLv +
                ", examTime=" + examTime +
                ", gra=" + gra +
                '}';
    }
}
