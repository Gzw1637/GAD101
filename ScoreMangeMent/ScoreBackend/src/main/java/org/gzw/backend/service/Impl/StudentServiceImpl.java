package org.gzw.backend.service.Impl;

import org.gzw.backend.entity.Student;
import org.gzw.backend.entity.vo.StudentExportVO;
import org.gzw.backend.entity.vo.StudentImportVO;
import org.gzw.backend.entity.vo.StudentVO;
import org.gzw.backend.mapper.StudentMapper;
import org.gzw.backend.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public int addStudent(Student student) {
        // 设置默认用户角色和状态
        student.setUserId(System.currentTimeMillis()); // 使用时间戳生成唯一 ID
        
        // 对密码进行 BCrypt 加密
        if (student.getPassword() != null && !student.getPassword().isEmpty()) {
            student.setPassword(passwordEncoder.encode(student.getPassword()));
        }
        
        // 先插入用户表
        int userResult = studentMapper.insertUser(student);
        if (userResult <= 0) {
            throw new RuntimeException("添加用户失败");
        }
        
        // 再插入学生表
        int studentResult = studentMapper.insert(student);
        if (studentResult <= 0) {
            throw new RuntimeException("添加学生失败");
        }
        
        return studentResult;
    }

    @Override
    @Transactional
    public int updateStudent(Student student) {
        // 如果密码被修改（不是占位符），则进行 BCrypt 加密
        if (student.getPassword() != null && !student.getPassword().isEmpty() 
            && !student.getPassword().equals("********")) {
            student.setPassword(passwordEncoder.encode(student.getPassword()));
        } else {
            // 密码未修改则设为 null，避免覆盖原密码
            student.setPassword(null);
        }
        
        // 更新用户表
        int userResult = studentMapper.updateUser(student);
        // 更新用户可能返回 0（没有字段被更新），所以只要不小于 0 都视为成功
        
        // 更新学生表
        int studentResult = studentMapper.update(student);
        if (studentResult <= 0) {
            throw new RuntimeException("更新学生信息失败");
        }
        
        return studentResult;
    }

    @Override
    @Transactional
    public int deleteStudent(Long studentId) {
        // 先查询学生信息获取 userId
        StudentVO student = studentMapper.selectById(studentId);
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }
        
        // 先删除学生表记录
        int studentResult = studentMapper.deleteById(studentId);
        if (studentResult <= 0) {
            throw new RuntimeException("删除学生失败");
        }
        
        // 再删除用户表记录
        int userResult = studentMapper.deleteUserByUserId(student.getUserId());
        if (userResult <= 0) {
            throw new RuntimeException("删除用户失败");
        }
        
        return studentResult;
    }

    @Override
    public StudentVO getStudentById(Long studentId) {
        return studentMapper.selectById(studentId);
    }

    @Override
    public List<StudentVO> getAllStudents() {
        return studentMapper.selectAll();
    }

    @Override
    public List<StudentVO> searchStudentsByConditions(Integer gra, Integer cla, Integer studentType, String name) {
        return studentMapper.searchByConditions(gra, cla, studentType, name);
    }

    @Override
    public List<StudentExportVO> exportStudents(Integer gra, Integer cla, Integer studentType, String name) {
        List<StudentVO> students = studentMapper.searchByConditions(gra, cla, studentType, name);
        if (students == null || students.isEmpty()) {
            return new ArrayList<>();
        }

        List<StudentExportVO> exportList = new ArrayList<>(students.size());
        for (StudentVO s : students) {
            StudentExportVO vo = new StudentExportVO();
            vo.setUserName(s.getUserName());
            vo.setName(s.getName());
            vo.setSex(s.getSexDesc() != null ? s.getSexDesc() : (s.getSex() != null && s.getSex() == 10031001 ? "男" : "女"));
            vo.setStudentCode(s.getStudentCode());
            vo.setEmail(s.getEmail());
            vo.setPhone(s.getPhone());
            vo.setGra(s.getGra());
            vo.setCla(s.getCla());
            vo.setStudentStatus(s.getStudentStatusDesc());
            vo.setStudentType(s.getStudentTypeDesc());
            exportList.add(vo);
        }
        return exportList;
    }
    
    @Override
    @Transactional
    public int importStudents(List<StudentImportVO> students) {
        int successCount = 0;
        for (StudentImportVO studentVO : students) {
            try {
                // 创建 Student 实体
                Student student = new Student();
                
                // 转换性别：男->10031001, 女->10031002
                Integer sexValue = "男".equals(studentVO.getSex()) ? 10031001 : 
                                 ("女".equals(studentVO.getSex()) ? 10031002 : null);
                
                // 转换学生状态：就读->10011001, 毕业->10011002, 休学->10011003, 留级->10011004
                Integer statusValue = "就读".equals(studentVO.getStudentStatus()) ? 10011001 :
                                    ("毕业".equals(studentVO.getStudentStatus()) ? 10011002 :
                                     ("休学".equals(studentVO.getStudentStatus()) ? 10011003 :
                                      ("留级".equals(studentVO.getStudentStatus()) ? 10011004 : 10011001))); // 默认就读
                
                // 转换学生类型：普通学生->11011001, 艺体生->11011002, 尖子生->11011003
                Integer typeValue = "普通学生".equals(studentVO.getStudentType()) ? 11011001 :
                                  ("艺体生".equals(studentVO.getStudentType()) ? 11011002 :
                                   ("尖子生".equals(studentVO.getStudentType()) ? 11011003 : 11011001)); // 默认普通学生
                
                student.setUserId(System.currentTimeMillis() + System.nanoTime()); // 使用时间戳 + 纳秒生成唯一 ID
                student.setUserName(studentVO.getUserName());
                student.setName(studentVO.getName());
                student.setPassword(passwordEncoder.encode(studentVO.getPassword()));
                student.setSex(sexValue);
                student.setStudentCode(studentVO.getStudentCode());
                student.setEmail(studentVO.getEmail());
                student.setPhone(studentVO.getPhone());
                student.setGra(studentVO.getGra());
                student.setCla(studentVO.getCla());
                student.setStudentStatus(statusValue);
                student.setStudentType(typeValue);
                
                // 先插入用户表
                int userResult = studentMapper.insertUser(student);
                if (userResult <= 0) {
                    throw new RuntimeException("添加用户失败");
                }
                
                // 再插入学生表
                int studentResult = studentMapper.insert(student);
                if (studentResult <= 0) {
                    throw new RuntimeException("添加学生失败");
                }
                
                successCount++;
            } catch (Exception e) {
                // 记录错误但继续处理下一个
                System.err.println("导入学生失败: " + studentVO.getName() + ", 错误: " + e.getMessage());
            }
        }
        return successCount;
    }
    
    @Override
    public StudentVO searchByStudentCode(String studentCode) {
        return studentMapper.searchByStudentCode(studentCode);
    }
    
    @Override
    public StudentVO getStudentByUserId(Long userId) {
        return studentMapper.selectByUserId(userId);
    }
}
