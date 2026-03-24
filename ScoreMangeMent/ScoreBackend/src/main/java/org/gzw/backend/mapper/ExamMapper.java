package org.gzw.backend.mapper;

import org.gzw.backend.entity.Exam;
import org.gzw.backend.entity.vo.ExamVO;
import java.util.List;

public interface ExamMapper {
    
    int insert(Exam exam);
    
    int update(Exam exam);
    
    int deleteById(Long examId);
    
    ExamVO selectById(Long examId);
    
    List<ExamVO> selectAll();
    
    List<ExamVO> selectByGrade(Integer gra);
}
