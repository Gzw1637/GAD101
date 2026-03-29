package org.gzw.backend.mapper;

import org.apache.ibatis.annotations.Param;
import org.gzw.backend.entity.Student;
import org.gzw.backend.entity.vo.StudentVO;
import java.util.List;

public interface StudentMapper {
    
    int insert(Student student);
    
    int update(Student student);
    
    int deleteById(Long studentId);
    
    StudentVO selectById(Long studentId);
    
    List<StudentVO> selectAll();
    
    List<StudentVO> searchByConditions(@Param("gra") Integer gra,
                                       @Param("cla") Integer cla,
                                       @Param("studentType") Integer studentType,
                                       @Param("name") String name);
    
    int insertUser(Student student);
    
    int updateUser(Student student);
    
    int deleteUserByUserId(Long userId);
    
    Long selectStudentIdByUserId(Long userId);
    
    /**
     * 根据学号查询学生
     */
    StudentVO searchByStudentCode(String studentCode);
    
    /**
     * 根据用户 ID 查询学生
     */
    StudentVO selectByUserId(Long userId);
}
