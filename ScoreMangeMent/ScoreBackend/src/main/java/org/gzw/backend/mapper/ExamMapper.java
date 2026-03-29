package org.gzw.backend.mapper;

import org.apache.ibatis.annotations.Param;
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
    
    /**
     * 根据考试名称和科目查询考试
     */
    List<ExamVO> searchByExamNameAndSubject(
        @Param("examName") String examName,
        @Param("subjectType") Integer subjectType
    );
    
    /**
     * 根据用户年级和科目查询考试
     */
    List<ExamVO> selectByUserGradeAndSubjects(
        @Param("grade") Integer grade,
        @Param("subjects") List<Integer> subjects
    );
}
