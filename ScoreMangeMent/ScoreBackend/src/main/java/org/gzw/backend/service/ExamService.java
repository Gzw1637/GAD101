package org.gzw.backend.service;

import org.gzw.backend.entity.Exam;
import org.gzw.backend.entity.vo.ExamImportVO;
import org.gzw.backend.entity.vo.ExamVO;
import java.util.List;

public interface ExamService {
    
    int addExam(Exam exam);
    
    int updateExam(Exam exam);
    
    int deleteExam(Long examId);
    
    ExamVO getExamById(Long examId);
    
    List<ExamVO> getAllExams();
    
    List<ExamVO> getExamsByGrade(Integer gra);
    
    int importExams(List<ExamImportVO> exams);
}
