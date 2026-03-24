package org.gzw.backend.service.Impl;

import org.gzw.backend.entity.Teacher;
import org.gzw.backend.entity.vo.TeacherImportVO;
import org.gzw.backend.entity.vo.TeacherVO;
import org.gzw.backend.mapper.TeacherMapper;
import org.gzw.backend.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TeacherServiceImpl implements TeacherService {

    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public int addTeacher(Teacher teacher) {
        teacher.setUserId(System.currentTimeMillis());
        
        if (teacher.getPassword() != null && !teacher.getPassword().isEmpty()) {
            teacher.setPassword(passwordEncoder.encode(teacher.getPassword()));
        }
        
        int userResult = teacherMapper.insertUser(teacher);
        if (userResult <= 0) {
            throw new RuntimeException("添加用户失败");
        }
        
        int teacherResult = teacherMapper.insert(teacher);
        if (teacherResult <= 0) {
            throw new RuntimeException("添加教师失败");
        }
        
        return teacherResult;
    }

    @Override
    @Transactional
    public int updateTeacher(Teacher teacher) {
        if (teacher.getPassword() != null && !teacher.getPassword().isEmpty() 
            && !teacher.getPassword().equals("********")) {
            teacher.setPassword(passwordEncoder.encode(teacher.getPassword()));
        } else {
            teacher.setPassword(null);
        }
        
        int userResult = teacherMapper.updateUser(teacher);
        
        int teacherResult = teacherMapper.update(teacher);
        if (teacherResult <= 0) {
            throw new RuntimeException("更新教师信息失败");
        }
        
        return teacherResult;
    }

    @Override
    @Transactional
    public int deleteTeacher(Long teacherId) {
        TeacherVO teacher = teacherMapper.selectById(teacherId);
        if (teacher == null) {
            throw new RuntimeException("教师不存在");
        }
        
        int teacherResult = teacherMapper.deleteById(teacherId);
        if (teacherResult <= 0) {
            throw new RuntimeException("删除教师失败");
        }
        
        int userResult = teacherMapper.deleteUserByUserId(teacher.getUserId());
        if (userResult <= 0) {
            throw new RuntimeException("删除用户失败");
        }
        
        return teacherResult;
    }

    @Override
    public TeacherVO getTeacherById(Long teacherId) {
        return teacherMapper.selectById(teacherId);
    }

    @Override
    public List<TeacherVO> getAllTeachers() {
        return teacherMapper.selectAll();
    }

    @Override
    public List<TeacherVO> getSubjectLeaders() {
        return teacherMapper.selectSubjectLeaders();
    }

    @Override
    public List<TeacherVO> getTeachersBySubject(Integer subjectCode) {
        return teacherMapper.selectBySubject(subjectCode);
    }

    @Override
    public List<TeacherVO> searchTeachersByConditions(Integer teachSubject,
                                                      Integer gra,
                                                      Integer cla,
                                                      Integer teacherType,
                                                      String name) {
        return teacherMapper.searchByConditions(teachSubject, gra, cla, teacherType, name);
    }
    
    @Override
    public TeacherVO getTeacherByUserId(Long userId) {
        return teacherMapper.selectByUserId(userId);
    }

    @Override
    @Transactional
    public int importTeachers(List<TeacherImportVO> teachers) {
        int successCount = 0;
        for (TeacherImportVO teacherVO : teachers) {
            try {
                Teacher teacher = new Teacher();
                
                // 转换性别：男->10031001, 女->10031002
                Integer sexValue = "男".equals(teacherVO.getSex()) ? 10031001 : 
                                 ("女".equals(teacherVO.getSex()) ? 10031002 : null);
                
                // 转换教师状态：在职->10021001, 离职->10021002, 休假->10021003
                Integer statusValue = "在职".equals(teacherVO.getTeacherStatus()) ? 10021001 :
                                    ("离职".equals(teacherVO.getTeacherStatus()) ? 10021002 :
                                     ("休假".equals(teacherVO.getTeacherStatus()) ? 10021003 : 10021001));
                
                // 转换教师类型：普通教师->11021001, 学科组长->11021002, 班主任->11021003
                Integer typeValue = "普通教师".equals(teacherVO.getTeacherType()) ? 11021001 :
                                  ("学科组长".equals(teacherVO.getTeacherType()) ? 11021002 :
                                   ("班主任".equals(teacherVO.getTeacherType()) ? 11021003 : 11021001));
                
                // 教学科目转换（需要根据实际科目代码调整）
                Integer subjectCode = convertSubjectToCode(teacherVO.getTeachSubject());
                
                teacher.setUserId(System.currentTimeMillis() + System.nanoTime());
                teacher.setUserName(teacherVO.getUserName());
                teacher.setName(teacherVO.getName());
                teacher.setPassword(passwordEncoder.encode(teacherVO.getPassword()));
                teacher.setSex(sexValue);
                teacher.setTeacherCode(teacherVO.getTeacherCode());
                teacher.setTeachSubject(subjectCode);
                teacher.setTeacherStatus(statusValue);
                teacher.setTeacherType(typeValue);
                teacher.setPhone(teacherVO.getPhone());
                teacher.setGra(teacherVO.getGra());
                teacher.setCla(teacherVO.getCla());
                
                // 插入用户表和教师表
                int userResult = teacherMapper.insertUser(teacher);
                if (userResult > 0) {
                    int teacherResult = teacherMapper.insert(teacher);
                    if (teacherResult > 0) {
                        successCount++;
                    }
                }
            } catch (Exception e) {
                System.err.println("导入教师失败：" + teacherVO.getName() + ", 错误：" + e.getMessage());
            }
        }
        return successCount;
    }
    
    /**
     * 将教学科目名称转换为代码
     */
    private Integer convertSubjectToCode(String subjectName) {
        if (subjectName == null) return null;
        
        switch (subjectName) {
            case "语文": return 10111001;
            case "数学": return 10111002;
            case "英语": return 10111003;
            case "物理": return 10111004;
            case "化学": return 10111005;
            case "生物": return 10111006;
            case "地理": return 10111007;
            case "政治": return 10111008;
            case "历史": return 10111009;
            default: return 10111001; // 默认语文
        }
    }
}
