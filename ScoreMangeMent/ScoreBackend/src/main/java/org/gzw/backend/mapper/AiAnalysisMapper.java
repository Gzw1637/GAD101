package org.gzw.backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.gzw.backend.entity.AiAnalysisRecord;

import java.util.List;

/**
 * AI 分析记录 Mapper 接口
 */
@Mapper
public interface AiAnalysisMapper {

    /**
     * 插入分析记录
     */
    int insert(AiAnalysisRecord record);

    /**
     * 根据 ID 查询分析记录
     */
    AiAnalysisRecord selectById(@Param("analysisId") Long analysisId);

    /**
     * 根据考试 ID 查询分析记录
     */
    List<AiAnalysisRecord> selectByExamId(@Param("examId") Long examId);

    /**
     * 根据教师 ID 查询分析历史
     */
    List<AiAnalysisRecord> selectByTeacherId(@Param("teacherId") Long teacherId);

    /**
     * 根据考试 ID 和类型查询分析记录
     */
    AiAnalysisRecord selectByExamIdAndType(@Param("examId") Long examId, @Param("analysisType") Integer analysisType);

    /**
     * 删除分析记录
     */
    int deleteById(@Param("analysisId") Long analysisId);
}
