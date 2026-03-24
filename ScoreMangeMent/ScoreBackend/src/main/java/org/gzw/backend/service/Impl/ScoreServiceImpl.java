package org.gzw.backend.service.Impl;

import org.gzw.backend.entity.Exam;
import org.gzw.backend.entity.Score;
import org.gzw.backend.entity.Student;
import org.gzw.backend.entity.vo.ExamVO;
import org.gzw.backend.entity.vo.ScoreExportVO;
import org.gzw.backend.entity.vo.ScoreImportVO;
import org.gzw.backend.entity.vo.ScoreVO;
import org.gzw.backend.entity.vo.StudentVO;
import org.gzw.backend.mapper.ExamMapper;
import org.gzw.backend.mapper.ScoreMapper;
import org.gzw.backend.mapper.StudentMapper;
import org.gzw.backend.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScoreServiceImpl implements ScoreService {

    @Autowired
    private ScoreMapper scoreMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private ExamMapper examMapper;

    @Override
    public List<ScoreExportVO> exportScores(List<ScoreVO> scores) {
        List<ScoreExportVO> exportList = new ArrayList<>();
        for (int i = 0; i < scores.size(); i++) {
            ScoreVO score = scores.get(i);
            ScoreExportVO exportVO = new ScoreExportVO();
            exportVO.setIndex(i + 1);
            exportVO.setExamName(score.getExamName());
            exportVO.setSubject(score.getSubjectTypeDesc());
            exportVO.setStudentCode(score.getStudentCode());
            exportVO.setStudentName(score.getName());
            exportVO.setGrade(score.getGra() != null ? score.getGra() + "级" : "");
            exportVO.setClassName(score.getCla() != null ? score.getCla() + "班" : "");
            exportVO.setScore(score.getScore() != null ? score.getScore().doubleValue() : null);
            exportVO.setClassRank(score.getClassRank());
            exportVO.setGradeRank(score.getGradeRank());
            exportList.add(exportVO);
        }
        return exportList;
    }

    @Override
    @Transactional
    public int addScore(Score score) {
        int result = scoreMapper.insert(score);
        if (result <= 0) {
            throw new RuntimeException("添加成绩失败");
        }
        return result;
    }

    @Override
    @Transactional
    public int updateScore(Score score) {
        int result = scoreMapper.update(score);
        if (result <= 0) {
            throw new RuntimeException("更新成绩失败");
        }
        return result;
    }

    @Override
    @Transactional
    public int deleteScore(Long scoreId) {
        ScoreVO score = scoreMapper.selectById(scoreId);
        if (score == null) {
            throw new RuntimeException("成绩记录不存在");
        }
        
        int result = scoreMapper.deleteById(scoreId);
        if (result <= 0) {
            throw new RuntimeException("删除成绩失败");
        }
        return result;
    }

    @Override
    public ScoreVO getScoreById(Long scoreId) {
        return scoreMapper.selectById(scoreId);
    }

    @Override
    public List<ScoreVO> getAllScores() {
        return scoreMapper.selectAll();
    }

    @Override
    public List<ScoreVO> getScoresByExamId(Long examId) {
        return scoreMapper.selectByExamId(examId);
    }

    @Override
    public List<ScoreVO> getScoresByStudentId(Long studentId) {
        return scoreMapper.selectByStudentId(studentId);
    }

    @Override
    public List<ScoreVO> getScoresByStudentIdAndFilters(Long studentId, Long examId, Integer subjectType, String examName) {
        return scoreMapper.selectByStudentIdAndFilters(studentId, examId, subjectType, examName);
    }

    @Override
    public List<ScoreVO> getScoresByFilters(Integer subjectType, String examName) {
        return scoreMapper.selectByFilters(subjectType, examName);
    }

    @Override
    public Long getStudentIdByUserId(Long userId) {
        return studentMapper.selectStudentIdByUserId(userId);
    }

    @Override
    @Transactional
    public int importScores(List<ScoreImportVO> scoreImportVOs) {
        int successCount = 0;
        for (ScoreImportVO scoreVO : scoreImportVOs) {
            try {
                // 1. 根据考试名称和科目查找考试
                Exam exam = findExamByNameAndSubject(scoreVO.getExamName(), scoreVO.getSubject());
                if (exam == null) {
                    System.err.println("导入成绩失败：未找到考试 " + scoreVO.getExamName() + " - " + scoreVO.getSubject());
                    continue;
                }

                // 2. 根据学号和姓名查找学生
                Student student = findStudentByCodeAndName(scoreVO.getStudentCode(), scoreVO.getStudentName());
                if (student == null) {
                    System.err.println("导入成绩失败：未找到学生 " + scoreVO.getStudentName() + " (学号：" + scoreVO.getStudentCode() + ")");
                    continue;
                }

                // 3. 检查该学生该考试的成绩是否已存在
                List<ScoreVO> existingScores = scoreMapper.selectByStudentIdAndFilters(
                    student.getStudentId(), exam.getExamId(), exam.getSubjectType(), null);
                
                if (existingScores != null && !existingScores.isEmpty()) {
                    // 更新已有成绩
                    Score existingScore = new Score();
                    existingScore.setScoreId(existingScores.get(0).getScoreId());
                    existingScore.setScore(new BigDecimal(scoreVO.getScore().toString()));
                    scoreMapper.update(existingScore);
                } else {
                    // 4. 创建成绩记录
                    Score score = new Score();
                    score.setExamId(exam.getExamId());
                    score.setStudentId(student.getStudentId());
                    score.setScore(new BigDecimal(scoreVO.getScore().toString()));
                    
                    scoreMapper.insert(score);
                }
                
                successCount++;
            } catch (Exception e) {
                System.err.println("导入成绩失败：" + scoreVO.getStudentName() + ", 错误：" + e.getMessage());
            }
        }
        return successCount;
    }

    /**
     * 根据考试名称和科目查找考试
     */
    private Exam findExamByNameAndSubject(String examName, String subject) {
        if (examName == null || subject == null) {
            return null;
        }
        
        // 获取所有考试，查找匹配的
        List<ExamVO> allExams = examMapper.selectAll();
        Integer subjectCode = convertSubjectToCode(subject);
        
        for (ExamVO examVO : allExams) {
            if (examVO.getExamName().equals(examName) && 
                examVO.getSubjectType() != null && 
                examVO.getSubjectType().equals(subjectCode)) {
                // 转换为 Exam 实体
                Exam exam = new Exam();
                exam.setExamId(examVO.getExamId());
                exam.setUserId(examVO.getUserId());
                exam.setExamCode(examVO.getExamCode());
                exam.setExamName(examVO.getExamName());
                exam.setSubjectType(examVO.getSubjectType());
                exam.setExamLv(examVO.getExamLv());
                exam.setExamTime(examVO.getExamTime());
                exam.setGra(examVO.getGra());
                return exam;
            }
        }
        return null;
    }

    /**
     * 根据学号和姓名查找学生
     */
    private Student findStudentByCodeAndName(Integer studentCode, String studentName) {
        if (studentCode == null || studentName == null) {
            return null;
        }
        
        List<StudentVO> allStudents = studentMapper.selectAll();
        for (StudentVO studentVO : allStudents) {
            if (studentVO.getStudentCode() != null && 
                studentVO.getStudentCode().equals(studentCode) &&
                studentVO.getName() != null &&
                studentVO.getName().equals(studentName)) {
                // 转换为 Student 实体
                Student student = new Student();
                student.setStudentId(studentVO.getStudentId());
                student.setUserId(studentVO.getUserId());
                student.setStudentCode(studentVO.getStudentCode());
                student.setName(studentVO.getName());
                student.setGra(studentVO.getGra());
                student.setCla(studentVO.getCla());
                return student;
            }
        }
        return null;
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
            default: return null;
        }
    }
}
