package org.gzw.backend.service.Impl;

import org.gzw.backend.entity.Exam;
import org.gzw.backend.entity.vo.ExamImportVO;
import org.gzw.backend.entity.vo.ExamVO;
import org.gzw.backend.mapper.ExamMapper;
import org.gzw.backend.mapper.TeacherMapper;
import org.gzw.backend.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExamServiceImpl implements ExamService {

    @Autowired
    private ExamMapper examMapper;
    
    @Autowired
    private TeacherMapper teacherMapper;

    @Override
    @Transactional
    public int addExam(Exam exam) {
        int result = examMapper.insert(exam);
        if (result <= 0) {
            throw new RuntimeException("添加考试失败");
        }
        return result;
    }

    @Override
    @Transactional
    public int updateExam(Exam exam) {
        int result = examMapper.update(exam);
        if (result <= 0) {
            throw new RuntimeException("更新考试失败");
        }
        return result;
    }

    @Override
    @Transactional
    public int deleteExam(Long examId) {
        ExamVO exam = examMapper.selectById(examId);
        if (exam == null) {
            throw new RuntimeException("考试不存在");
        }
        
        int result = examMapper.deleteById(examId);
        if (result <= 0) {
            throw new RuntimeException("删除考试失败");
        }
        return result;
    }

    @Override
    public ExamVO getExamById(Long examId) {
        return examMapper.selectById(examId);
    }

    @Override
    public List<ExamVO> selectAll() {
        return examMapper.selectAll();
    }

    @Override
    public List<ExamVO> selectByUserGradeAndSubjects(Integer grade, List<Integer> subjects) {
        return examMapper.selectByUserGradeAndSubjects(grade, subjects);
    }

    @Override
    public List<ExamVO> getExamsByGrade(Integer gra) {
        return examMapper.selectByGrade(gra);
    }

    @Override
    @Transactional
    public int importExams(List<ExamImportVO> exams) {
        int successCount = 0;
        for (ExamImportVO examVO : exams) {
            try {
                Exam exam = new Exam();
                
                // 设置考试代码（使用手动输入）
                exam.setExamCode(examVO.getExamCode());
                
                // 设置考试名称
                exam.setExamName(examVO.getExamName());
                
                // 转换科目名称为代码
                Integer subjectCode = convertSubjectToCode(examVO.getSubject());
                exam.setSubjectType(subjectCode);
                
                // 转换考试级别
                Integer examLv = convertExamLv(examVO.getExamLv());
                exam.setExamLv(examLv);
                
                // 设置考试时间
                exam.setExamTime(examVO.getExamTime());
                
                // 设置年级
                exam.setGra(examVO.getGra());
                
                // 根据负责人姓名查找用户 ID
                Long userId = findUserIdByName(examVO.getTeacherName());
                if (userId != null) {
                    exam.setUserId(userId);
                }
                
                // 插入考试表
                int result = examMapper.insert(exam);
                if (result > 0) {
                    successCount++;
                }
            } catch (Exception e) {
                System.err.println("导入考试失败：" + examVO.getExamName() + ", 错误：" + e.getMessage());
            }
        }
        return successCount;
    }
    
    /**
     * 将科目名称转换为代码
     */
    private Integer convertSubjectToCode(String subjectName) {
        if (subjectName == null) return null;
        
        switch (subjectName) {
            case "语文": return 10111001;
            case "数学": return 10111002;
            case "英语": return 10111003;
            case "物理": return 10111004;
            case "历史": return 10111005;
            case "化学": return 10111006;
            case "生物": return 10111007;
            case "地理": return 10111008;
            case "政治": return 10111009;
            default: return 10111001; // 默认语文
        }
    }
    
    /**
     * 将考试级别名称转换为代码
     */
    private Integer convertExamLv(String examLvName) {
        if (examLvName == null) return null;
        
        switch (examLvName) {
            case "D": return 10121001;
            case "C": return 10121002;
            case "B": return 10121003;
            case "A": return 10121004;
            case "S": return 10121005;
            default: return 10121001; // 默认 D
        }
    }
    
    /**
     * 根据姓名查找用户 ID
     */
    private Long findUserIdByName(String teacherName) {
        if (teacherName == null || teacherName.trim().isEmpty()) {
            return null;
        }
        
        // 从教师表中查找匹配的教师
        List<org.gzw.backend.entity.vo.TeacherVO> teachers = teacherMapper.selectByName(teacherName);
        if (teachers != null && !teachers.isEmpty()) {
            // 返回第一个匹配的教师的用户 ID
            return teachers.get(0).getUserId();
        }
        return null;
    }
    
    @Override
    public List<ExamVO> searchByExamNameAndSubject(String examName, Integer subjectType) {
        return examMapper.searchByExamNameAndSubject(examName, subjectType);
    }
    
    @Override
    public List<ExamVO> getAllExams() {
        return examMapper.selectAll();
    }
}
