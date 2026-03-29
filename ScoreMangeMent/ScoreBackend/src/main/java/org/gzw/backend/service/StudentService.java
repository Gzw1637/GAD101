package org.gzw.backend.service;

import org.gzw.backend.entity.Student;
import org.gzw.backend.entity.vo.StudentImportVO;
import org.gzw.backend.entity.vo.StudentExportVO;
import org.gzw.backend.entity.vo.StudentVO;
import java.util.List;

public interface StudentService {
    
    int addStudent(Student student);
    
    int updateStudent(Student student);
    
    int deleteStudent(Long studentId);
    
    StudentVO getStudentById(Long studentId);
    
    List<StudentVO> getAllStudents();
    
    List<StudentVO> searchStudentsByConditions(Integer gra, Integer cla, Integer studentType, String name);

    List<StudentExportVO> exportStudents(Integer gra, Integer cla, Integer studentType, String name);
    
    int importStudents(List<StudentImportVO> students);
    
    StudentVO searchByStudentCode(String studentCode);
    
    StudentVO getStudentByUserId(Long userId);
}
