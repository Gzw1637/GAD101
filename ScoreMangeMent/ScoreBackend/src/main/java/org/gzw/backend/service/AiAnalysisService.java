package org.gzw.backend.service;

import org.gzw.backend.entity.StudentExamPaper;
import org.gzw.backend.entity.vo.AiAnalysisVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * AI 分析服务接口
 */
public interface AiAnalysisService {

    /**
     * 分析班级成绩
     *
     * @param examId 考试 ID
     * @param teacherId 教师 ID
     * @return AI 分析结果
     */
    AiAnalysisVO analyzeClassPerformance(Long examId, Long teacherId);

    /**
     * 上传并分析学生试卷
     *
     * @param file 试卷图片文件
     * @param studentId 学生 ID
     * @param examId 考试 ID
     * @param teacherId 教师 ID
     * @return 试卷记录 ID
     */
    Long uploadAndAnalyzePaper(MultipartFile file, Long studentId, Long examId, Long teacherId);

    /**
     * 分析学生试卷（根据已有记录）
     *
     * @param paperId 试卷记录 ID
     * @return AI 分析结果
     */
    AiAnalysisVO analyzeStudentPaper(Long paperId);

    /**
     * 获取分析报告
     *
     * @param examId 考试 ID
     * @return AI 分析报告
     */
    AiAnalysisVO getAnalysisReport(Long examId);

    /**
     * 获取分析历史
     *
     * @param teacherId 教师 ID
     * @return 分析历史列表
     */
    List<StudentExamPaper> getAnalysisHistory(Long teacherId);

    /**
     * 获取学生试卷记录
     *
     * @param paperId 试卷记录 ID
     * @return 试卷记录
     */
    StudentExamPaper getPaperById(Long paperId);

    /**
     * 分析学生个人成绩
     *
     * @param scoreId 成绩记录 ID
     * @param studentId 学生 ID
     * @return AI 分析结果
     */
    AiAnalysisVO analyzeStudentScore(Long scoreId, Long studentId);
}
