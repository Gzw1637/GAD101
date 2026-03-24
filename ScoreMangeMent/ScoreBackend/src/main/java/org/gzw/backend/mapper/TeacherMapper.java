package org.gzw.backend.mapper;

import org.gzw.backend.entity.Teacher;
import org.gzw.backend.entity.vo.TeacherVO;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TeacherMapper {
    
    int insert(Teacher teacher);
    
    int update(Teacher teacher);
    
    int deleteById(Long teacherId);
    
    TeacherVO selectById(Long teacherId);
    
    List<TeacherVO> selectAll();
    
    List<TeacherVO> searchByConditions(@Param("teachSubject") Integer teachSubject,
                                       @Param("gra") Integer gra,
                                       @Param("cla") Integer cla,
                                       @Param("teacherType") Integer teacherType,
                                       @Param("name") String name);
    
    int insertUser(Teacher teacher);
    
    int updateUser(Teacher teacher);
    
    int deleteUserByUserId(Long userId);
    
    List<TeacherVO> selectSubjectLeaders();
    
    List<TeacherVO> selectBySubject(Integer subjectCode);
    
    List<TeacherVO> selectByName(String name);
    
    TeacherVO selectByUserId(Long userId);
}
