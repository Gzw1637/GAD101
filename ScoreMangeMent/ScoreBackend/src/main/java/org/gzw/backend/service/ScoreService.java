package org.gzw.backend.service;

import org.gzw.backend.entity.Score;
import org.gzw.backend.entity.vo.ScoreExportVO;
import org.gzw.backend.entity.vo.ScoreImportVO;
import org.gzw.backend.entity.vo.ScoreVO;
import java.util.List;

public interface ScoreService {
    
    int addScore(Score score);
    
    int updateScore(Score score);
    
    int deleteScore(Long scoreId);
    
    ScoreVO getScoreById(Long scoreId);
    
    List<ScoreVO> getAllScores();
    
    List<ScoreVO> getScoresByExamId(Long examId);
    
    List<ScoreVO> getScoresByStudentId(Long studentId);
    
    List<ScoreVO> getScoresByStudentIdAndFilters(Long studentId, Long examId, Integer subjectType, String examName);
    
    List<ScoreVO> getScoresByFilters(Integer subjectType, String examName);
    
    Long getStudentIdByUserId(Long userId);
    
    int importScores(List<ScoreImportVO> scores);
    
    List<ScoreExportVO> exportScores(List<ScoreVO> scores);
}
