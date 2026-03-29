package org.gzw.backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.gzw.backend.entity.StudentExamPaper;

import java.util.List;

/**
 * 学生试卷 Mapper 接口
 */
@Mapper
public interface StudentExamPaperMapper {

    /**
     * 插入试卷记录
     */
    int insert(StudentExamPaper paper);

    /**
     * 根据 ID 查询试卷记录
     */
    StudentExamPaper selectById(@Param("paperId") Long paperId);

    /**
     * 根据学生 ID 和考试 ID 查询
     */
    StudentExamPaper selectByStudentAndExam(@Param("studentId") Long studentId, @Param("examId") Long examId);

    /**
     * 根据考试 ID 查询所有试卷
     */
    List<StudentExamPaper> selectByExamId(@Param("examId") Long examId);

    /**
     * 根据学生 ID 查询所有试卷
     */
    List<StudentExamPaper> selectByStudentId(@Param("studentId") Long studentId);

    /**
     * 更新 OCR 识别结果
     */
    int updateOcrResult(@Param("paperId") Long paperId, @Param("ocrResult") String ocrResult);

    /**
     * 更新 AI 分析结果
     */
    int updateAiAnalysis(@Param("paperId") Long paperId, @Param("aiAnalysis") String aiAnalysis);

    /**
     * 删除试卷记录
     */
    int deleteById(@Param("paperId") Long paperId);
}
