package org.gzw.backend.entity.vo;

import java.util.List;
import java.util.Map;

/**
 * AI 分析结果 VO 类
 * 用于前端展示 AI 生成的分析报告
 */
public class AiAnalysisVO {

    /**
     * 考试 ID
     */
    private Long examId;

    /**
     * 考试名称
     */
    private String examName;

    /**
     * 科目类型
     */
    private Integer subjectType;

    /**
     * 科目名称
     */
    private String subjectName;

    /**
     * 年级
     */
    private Integer grade;

    /**
     * 班级
     */
    private Integer className;

    /**
     * 参考人数
     */
    private Integer totalStudents;

    /**
     * 平均分
     */
    private Double averageScore;

    /**
     * 最高分
     */
    private Double maxScore;

    /**
     * 最低分
     */
    private Double minScore;

    /**
     * 及格率（%）
     */
    private Double passRate;

    /**
     * 优秀率（%）
     */
    private Double excellentRate;

    /**
     * 分数段分布
     * key: 分数段名称，value: 人数
     */
    private Map<String, Integer> scoreDistribution;

    /**
     * 班级排名（分数段人数）
     */
    private List<StudentRankVO> classRanking;

    /**
     * 进步学生名单
     */
    private List<StudentProgressVO> improvedStudents;

    /**
     * 退步学生名单
     */
    private List<StudentProgressVO> declinedStudents;

    /**
     * AI 生成的班级整体评语
     */
    private String classComment;

    /**
     * AI 指出的主要问题
     */
    private String mainProblems;

    /**
     * AI 给出的教学建议
     */
    private List<String> teachingSuggestions;

    /**
     * 需要关注的学生名单（后 5 名）
     */
    private List<StudentBasicInfoVO> needAttentionStudents;

    /**
     * 表现优异的学生名单（前 5 名）
     */
    private List<StudentBasicInfoVO> excellentStudents;

    /**
     * 历次考试对比
     */
    private ExamComparisonVO examComparison;

    /**
     * 得分明细（试卷分析用）
     */
    private String scoreBreakdown;

    /**
     * 优点列表（试卷分析用）
     */
    private List<String> advantages;

    /**
     * 薄弱点列表（试卷分析用）
     */
    private List<String> weaknesses;

    /**
     * 错误类型列表（试卷分析用）
     */
    private List<String> errorTypes;

    /**
     * 表现等级（试卷分析用）
     */
    private String performanceLevel;

    /**
     * 详细分析报告（试卷分析用）
     */
    private String detailedAnalysis;

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
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

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public Integer getClassName() {
        return className;
    }

    public void setClassName(Integer className) {
        this.className = className;
    }

    public Integer getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(Integer totalStudents) {
        this.totalStudents = totalStudents;
    }

    public Double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(Double averageScore) {
        this.averageScore = averageScore;
    }

    public Double getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(Double maxScore) {
        this.maxScore = maxScore;
    }

    public Double getMinScore() {
        return minScore;
    }

    public void setMinScore(Double minScore) {
        this.minScore = minScore;
    }

    public Double getPassRate() {
        return passRate;
    }

    public void setPassRate(Double passRate) {
        this.passRate = passRate;
    }

    public Double getExcellentRate() {
        return excellentRate;
    }

    public void setExcellentRate(Double excellentRate) {
        this.excellentRate = excellentRate;
    }

    public Map<String, Integer> getScoreDistribution() {
        return scoreDistribution;
    }

    public void setScoreDistribution(Map<String, Integer> scoreDistribution) {
        this.scoreDistribution = scoreDistribution;
    }

    public List<StudentRankVO> getClassRanking() {
        return classRanking;
    }

    public void setClassRanking(List<StudentRankVO> classRanking) {
        this.classRanking = classRanking;
    }

    public List<StudentProgressVO> getImprovedStudents() {
        return improvedStudents;
    }

    public void setImprovedStudents(List<StudentProgressVO> improvedStudents) {
        this.improvedStudents = improvedStudents;
    }

    public List<StudentProgressVO> getDeclinedStudents() {
        return declinedStudents;
    }

    public void setDeclinedStudents(List<StudentProgressVO> declinedStudents) {
        this.declinedStudents = declinedStudents;
    }

    public String getClassComment() {
        return classComment;
    }

    public void setClassComment(String classComment) {
        this.classComment = classComment;
    }

    public String getMainProblems() {
        return mainProblems;
    }

    public void setMainProblems(String mainProblems) {
        this.mainProblems = mainProblems;
    }

    public List<String> getTeachingSuggestions() {
        return teachingSuggestions;
    }

    public void setTeachingSuggestions(List<String> teachingSuggestions) {
        this.teachingSuggestions = teachingSuggestions;
    }

    public List<StudentBasicInfoVO> getNeedAttentionStudents() {
        return needAttentionStudents;
    }

    public void setNeedAttentionStudents(List<StudentBasicInfoVO> needAttentionStudents) {
        this.needAttentionStudents = needAttentionStudents;
    }

    public List<StudentBasicInfoVO> getExcellentStudents() {
        return excellentStudents;
    }

    public void setExcellentStudents(List<StudentBasicInfoVO> excellentStudents) {
        this.excellentStudents = excellentStudents;
    }

    public ExamComparisonVO getExamComparison() {
        return examComparison;
    }

    public void setExamComparison(ExamComparisonVO examComparison) {
        this.examComparison = examComparison;
    }

    public String getScoreBreakdown() {
        return scoreBreakdown;
    }

    public void setScoreBreakdown(String scoreBreakdown) {
        this.scoreBreakdown = scoreBreakdown;
    }

    public List<String> getAdvantages() {
        return advantages;
    }

    public void setAdvantages(List<String> advantages) {
        this.advantages = advantages;
    }

    public List<String> getWeaknesses() {
        return weaknesses;
    }

    public void setWeaknesses(List<String> weaknesses) {
        this.weaknesses = weaknesses;
    }

    public List<String> getErrorTypes() {
        return errorTypes;
    }

    public void setErrorTypes(List<String> errorTypes) {
        this.errorTypes = errorTypes;
    }

    public String getPerformanceLevel() {
        return performanceLevel;
    }

    public void setPerformanceLevel(String performanceLevel) {
        this.performanceLevel = performanceLevel;
    }

    public String getDetailedAnalysis() {
        return detailedAnalysis;
    }

    public void setDetailedAnalysis(String detailedAnalysis) {
        this.detailedAnalysis = detailedAnalysis;
    }

    @Override
    public String toString() {
        return "AiAnalysisVO{" +
                "examId=" + examId +
                ", examName='" + examName + '\'' +
                ", averageScore=" + averageScore +
                ", totalStudents=" + totalStudents +
                ", passRate=" + passRate +
                ", excellentRate=" + excellentRate +
                '}';
    }

    /**
     * 学生排名 VO
     */
    public static class StudentRankVO {
        private Integer rank;
        private Long studentId;
        private String studentName;
        private Integer studentCode;
        private Double score;

        public Integer getRank() {
            return rank;
        }

        public void setRank(Integer rank) {
            this.rank = rank;
        }

        public Long getStudentId() {
            return studentId;
        }

        public void setStudentId(Long studentId) {
            this.studentId = studentId;
        }

        public String getStudentName() {
            return studentName;
        }

        public void setStudentName(String studentName) {
            this.studentName = studentName;
        }

        public Integer getStudentCode() {
            return studentCode;
        }

        public void setStudentCode(Integer studentCode) {
            this.studentCode = studentCode;
        }

        public Double getScore() {
            return score;
        }

        public void setScore(Double score) {
            this.score = score;
        }
    }

    /**
     * 学生进步情况 VO
     */
    public static class StudentProgressVO {
        private Long studentId;
        private String studentName;
        private Integer studentCode;
        private Double currentScore;
        private Double previousScore;
        private Double scoreChange;
        private String progressType;

        public Long getStudentId() {
            return studentId;
        }

        public void setStudentId(Long studentId) {
            this.studentId = studentId;
        }

        public String getStudentName() {
            return studentName;
        }

        public void setStudentName(String studentName) {
            this.studentName = studentName;
        }

        public Integer getStudentCode() {
            return studentCode;
        }

        public void setStudentCode(Integer studentCode) {
            this.studentCode = studentCode;
        }

        public Double getCurrentScore() {
            return currentScore;
        }

        public void setCurrentScore(Double currentScore) {
            this.currentScore = currentScore;
        }

        public Double getPreviousScore() {
            return previousScore;
        }

        public void setPreviousScore(Double previousScore) {
            this.previousScore = previousScore;
        }

        public Double getScoreChange() {
            return scoreChange;
        }

        public void setScoreChange(Double scoreChange) {
            this.scoreChange = scoreChange;
        }

        public String getProgressType() {
            return progressType;
        }

        public void setProgressType(String progressType) {
            this.progressType = progressType;
        }
    }

    /**
     * 学生基本信息 VO
     */
    public static class StudentBasicInfoVO {
        private Long studentId;
        private String studentName;
        private Integer studentCode;
        private Double score;

        public Long getStudentId() {
            return studentId;
        }

        public void setStudentId(Long studentId) {
            this.studentId = studentId;
        }

        public String getStudentName() {
            return studentName;
        }

        public void setStudentName(String studentName) {
            this.studentName = studentName;
        }

        public Integer getStudentCode() {
            return studentCode;
        }

        public void setStudentCode(Integer studentCode) {
            this.studentCode = studentCode;
        }

        public Double getScore() {
            return score;
        }

        public void setScore(Double score) {
            this.score = score;
        }
    }

    /**
     * 考试对比 VO
     */
    public static class ExamComparisonVO {
        private Long previousExamId;
        private String previousExamName;
        private Double previousAverageScore;
        private Double currentAverageScore;
        private Double averageChange;
        private String trend;

        public Long getPreviousExamId() {
            return previousExamId;
        }

        public void setPreviousExamId(Long previousExamId) {
            this.previousExamId = previousExamId;
        }

        public String getPreviousExamName() {
            return previousExamName;
        }

        public void setPreviousExamName(String previousExamName) {
            this.previousExamName = previousExamName;
        }

        public Double getPreviousAverageScore() {
            return previousAverageScore;
        }

        public void setPreviousAverageScore(Double previousAverageScore) {
            this.previousAverageScore = previousAverageScore;
        }

        public Double getCurrentAverageScore() {
            return currentAverageScore;
        }

        public void setCurrentAverageScore(Double currentAverageScore) {
            this.currentAverageScore = currentAverageScore;
        }

        public Double getAverageChange() {
            return averageChange;
        }

        public void setAverageChange(Double averageChange) {
            this.averageChange = averageChange;
        }

        public String getTrend() {
            return trend;
        }

        public void setTrend(String trend) {
            this.trend = trend;
        }
    }
}
