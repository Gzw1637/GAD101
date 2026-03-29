package org.gzw.backend.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 学生试卷实体类
 * 对应数据库表：student_exam_paper
 */
public class StudentExamPaper implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 试卷记录 ID
     */
    private Long paperId;

    /**
     * 学生 ID
     */
    private Long studentId;

    /**
     * 考试 ID
     */
    private Long examId;

    /**
     * 试卷图片存储路径
     */
    private String paperImageUrl;

    /**
     * OCR 识别结果
     */
    private String ocrResult;

    /**
     * AI 分析结果
     */
    private String aiAnalysis;

    /**
     * 上传时间
     */
    private LocalDateTime uploadTime;

    public StudentExamPaper() {
    }

    public StudentExamPaper(Long paperId, Long studentId, Long examId, String paperImageUrl, 
                           String ocrResult, String aiAnalysis, LocalDateTime uploadTime) {
        this.paperId = paperId;
        this.studentId = studentId;
        this.examId = examId;
        this.paperImageUrl = paperImageUrl;
        this.ocrResult = ocrResult;
        this.aiAnalysis = aiAnalysis;
        this.uploadTime = uploadTime;
    }

    public Long getPaperId() {
        return paperId;
    }

    public void setPaperId(Long paperId) {
        this.paperId = paperId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public String getPaperImageUrl() {
        return paperImageUrl;
    }

    public void setPaperImageUrl(String paperImageUrl) {
        this.paperImageUrl = paperImageUrl;
    }

    public String getOcrResult() {
        return ocrResult;
    }

    public void setOcrResult(String ocrResult) {
        this.ocrResult = ocrResult;
    }

    public String getAiAnalysis() {
        return aiAnalysis;
    }

    public void setAiAnalysis(String aiAnalysis) {
        this.aiAnalysis = aiAnalysis;
    }

    public LocalDateTime getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(LocalDateTime uploadTime) {
        this.uploadTime = uploadTime;
    }

    @Override
    public String toString() {
        return "StudentExamPaper{" +
                "paperId=" + paperId +
                ", studentId=" + studentId +
                ", examId=" + examId +
                ", uploadTime=" + uploadTime +
                '}';
    }
}
