package org.gzw.backend.entity;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 成绩实体类
 * 对应数据库表：zw_score
 */
public class Score implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 成绩 ID
     */
    private Long scoreId;

    /**
     * 考试 ID
     */
    private Long examId;

    /**
     * 学生 ID
     */
    private Long studentId;

    /**
     * 分数
     */
    private BigDecimal score;

    public Score() {
    }

    public Score(Long scoreId, Long examId, Long studentId, BigDecimal score) {
        this.scoreId = scoreId;
        this.examId = examId;
        this.studentId = studentId;
        this.score = score;
    }

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

    @Override
    public String toString() {
        return "Score{" +
                "scoreId=" + scoreId +
                ", examId=" + examId +
                ", studentId=" + studentId +
                ", score=" + score +
                '}';
    }
}
