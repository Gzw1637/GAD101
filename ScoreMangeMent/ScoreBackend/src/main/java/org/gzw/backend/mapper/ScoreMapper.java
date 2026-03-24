package org.gzw.backend.mapper;

import org.gzw.backend.entity.Score;
import org.gzw.backend.entity.vo.ScoreVO;
import java.util.List;

public interface ScoreMapper {
    
    int insert(Score score);
    
    int update(Score score);
    
    int deleteById(Long scoreId);
    
    ScoreVO selectById(Long scoreId);
    
    List<ScoreVO> selectAll();
    
    List<ScoreVO> selectByExamId(Long examId);
    
    List<ScoreVO> selectByStudentId(Long studentId);
    
    List<ScoreVO> selectByStudentIdAndFilters(Long studentId, Long examId, Integer subjectType, String examName);
    
    List<ScoreVO> selectByFilters(Integer subjectType, String examName);
}
