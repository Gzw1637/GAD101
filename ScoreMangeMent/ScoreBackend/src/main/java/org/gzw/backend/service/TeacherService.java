package org.gzw.backend.service;

import org.gzw.backend.entity.Teacher;
import org.gzw.backend.entity.vo.TeacherImportVO;
import org.gzw.backend.entity.vo.TeacherVO;
import java.util.List;

public interface TeacherService {
    
    int addTeacher(Teacher teacher);
    
    int updateTeacher(Teacher teacher);
    
    int deleteTeacher(Long teacherId);
    
    TeacherVO getTeacherById(Long teacherId);
    
    List<TeacherVO> getAllTeachers();
    
    List<TeacherVO> getSubjectLeaders();
    
    List<TeacherVO> getTeachersBySubject(Integer subjectCode);
    
    List<TeacherVO> searchTeachersByConditions(Integer teachSubject,
                                               Integer gra,
                                               Integer cla,
                                               Integer teacherType,
                                               String name);
    
    int importTeachers(List<TeacherImportVO> teachers);
    
    TeacherVO getTeacherByUserId(Long userId);
}
