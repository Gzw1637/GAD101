package org.gzw.backend.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI 分析记录实体类
 * 对应数据库表：ai_analysis_record
 */
public class AiAnalysisRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分析记录 ID
     */
    private Long analysisId;

    /**
     * 考试 ID
     */
    private Long examId;

    /**
     * 教师 ID
     */
    private Long teacherId;

    /**
     * 分析类型：1=成绩趋势，2=试卷识图，3=综合报告
     */
    private Integer analysisType;

    /**
     * AI 生成的分析内容（JSON 格式）
     */
    private String analysisContent;

    /**
     * 学生 ID（试卷识图分析时用）
     */
    private Long studentId;

    /**
     * 试卷图片 URL（识图分析时用）
     */
    private String imageUrl;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    public AiAnalysisRecord() {
    }

    public AiAnalysisRecord(Long analysisId, Long examId, Long teacherId, Integer analysisType, 
                           String analysisContent, Long studentId, String imageUrl, LocalDateTime createTime) {
        this.analysisId = analysisId;
        this.examId = examId;
        this.teacherId = teacherId;
        this.analysisType = analysisType;
        this.analysisContent = analysisContent;
        this.studentId = studentId;
        this.imageUrl = imageUrl;
        this.createTime = createTime;
    }

    public Long getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(Long analysisId) {
        this.analysisId = analysisId;
    }

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public Integer getAnalysisType() {
        return analysisType;
    }

    public void setAnalysisType(Integer analysisType) {
        this.analysisType = analysisType;
    }

    public String getAnalysisContent() {
        return analysisContent;
    }

    public void setAnalysisContent(String analysisContent) {
        this.analysisContent = analysisContent;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "AiAnalysisRecord{" +
                "analysisId=" + analysisId +
                ", examId=" + examId +
                ", teacherId=" + teacherId +
                ", analysisType=" + analysisType +
                ", studentId=" + studentId +
                ", createTime=" + createTime +
                '}';
    }
}
