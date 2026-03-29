package org.gzw.backend.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.gzw.backend.common.Result;
import org.gzw.backend.entity.Exam;
import org.gzw.backend.entity.vo.*;
import org.gzw.backend.service.ExamService;
import org.gzw.backend.service.ScoreService;
import org.gzw.backend.service.StudentService;
import org.gzw.backend.service.UserService;
import org.gzw.backend.service.TeacherService;
import org.gzw.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/exam")
@CrossOrigin
public class ExamController {

    @Autowired
    private ExamService examService;

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private UserService userService;

    @Autowired
    private TeacherService teacherService;

    @PostMapping("/add")
    public Result<Integer> addExam(@RequestBody Exam exam) {
        int result = examService.addExam(exam);
        if (result > 0) {
            return Result.success(result);
        }
        return Result.error(500, "添加考试失败");
    }

    @PutMapping("/update")
    public Result<Integer> updateExam(@RequestBody Exam exam) {
        int result = examService.updateExam(exam);
        if (result > 0) {
            return Result.success(result);
        }
        return Result.error(500, "更新考试失败");
    }

    @DeleteMapping("/delete/{examId}")
    public Result<Integer> deleteExam(@PathVariable Long examId) {
        int result = examService.deleteExam(examId);
        if (result > 0) {
            return Result.success(result);
        }
        return Result.error(500, "删除考试失败");
    }

    @GetMapping("/getById/{examId}")
    public Result<ExamVO> getExamById(@PathVariable Long examId) {
        ExamVO exam = examService.getExamById(examId);
        if (exam != null) {
            return Result.success(exam);
        }
        return Result.error(404, "考试不存在");
    }

    @GetMapping("/list")
    public Result<List<ExamVO>> listExams(HttpServletRequest request) {
        try {
            // 获取当前用户信息
            String token = getToken(request);
            Long currentUserId = JwtUtil.getUserIdFromToken(token);
            
            // 获取用户信息，判断角色
            UserVO user = userService.getUserById(currentUserId);
            if (user == null) {
                return Result.error(404, "未找到用户信息");
            }
            
            // 如果是管理员，返回所有考试
            if (user.getRole() == 14981003) {
                List<ExamVO> exams = examService.getAllExams();
                return Result.success(exams);
            }
            
            // 如果是教师，按照原来的逻辑过滤
            TeacherVO teacher = teacherService.getTeacherByUserId(currentUserId);
            if (teacher == null) {
                return Result.error(404, "未找到教师信息");
            }
            
            // 获取教师所在年级和负责科目
            Integer userGrade = teacher.getGra();
            List<Integer> userSubjects = new ArrayList<>();
            userSubjects.add(teacher.getTeachSubject());
            
            // 查询考试列表，添加过滤条件
            List<ExamVO> exams = examService.selectByUserGradeAndSubjects(userGrade, userSubjects);
            return Result.success(exams);
        } catch (Exception e) {
            return Result.error(500, "查询考试列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取请求中的 token
     */
    private String getToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return token;
    }

    @GetMapping("/my-list")
    public Result<List<ExamVO>> getMyExams(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            Long userId = JwtUtil.getUserIdFromToken(token);
            
            if (userId == null) {
                return Result.error(401, "未登录或登录已过期");
            }
            
            Long studentId = scoreService.getStudentIdByUserId(userId);
            
            if (studentId == null) {
                return Result.error(404, "未找到学生信息");
            }
            
            // 通过 studentId 获取学生信息，获取年级
            StudentVO student = studentService.getStudentById(studentId);
            if (student == null || student.getGra() == null) {
                return Result.error(404, "未找到学生年级信息");
            }
            
            // 根据年级查询考试列表
            List<ExamVO> exams = examService.getExamsByGrade(student.getGra());
            
            return Result.success(exams);
        } catch (Exception e) {
            return Result.error(500, "获取考试列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据考试名称和科目查询考试
     */
    @GetMapping("/search")
    public Result<List<ExamVO>> searchExams(
            @RequestParam("examName") String examName,
            @RequestParam("subjectType") Integer subjectType) {
        try {
            List<ExamVO> exams = examService.searchByExamNameAndSubject(examName, subjectType);
            return Result.success(exams);
        } catch (Exception e) {
            return Result.error(500, "查询考试失败：" + e.getMessage());
        }
    }

    /**
     * 下载考试导入模板
     */
    @GetMapping("/download-template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("考试导入模板.xlsx", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);
        
        // 使用 ExamImportVO 类自动生成横向表头，不写入示例数据
        EasyExcel.write(response.getOutputStream(), ExamImportVO.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .sheet("考试导入模板")
                .doWrite(new ArrayList<>());
    }

    /**
     * 批量导入考试
     */
    @PostMapping("/import")
    public Result<Integer> importExams(@RequestParam("file") MultipartFile file) {
        try {
            List<ExamImportVO> exams = EasyExcel.read(file.getInputStream())
                    .head(ExamImportVO.class)
                    .sheet()
                    .doReadSync();
            
            if (exams == null || exams.isEmpty()) {
                return Result.error(400, "Excel 文件为空");
            }
            
            int successCount = examService.importExams(exams);
            return Result.success(successCount);
        } catch (Exception e) {
            return Result.error(500, "导入失败：" + e.getMessage());
        }
    }

    /**
     * 导出考试数据
     */
    @PostMapping("/export")
    public void exportExams(@RequestBody(required = false) List<ExamVO> exams,
                            HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("考试数据.xlsx", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);
        
        // 如果没有传入数据，则导出所有考试
        if (exams == null || exams.isEmpty()) {
            exams = examService.getAllExams();
        }
        
        // 转换为导出 VO
        List<ExamExportVO> exportList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (int i = 0; i < exams.size(); i++) {
            ExamVO exam = exams.get(i);
            ExamExportVO exportVO = new ExamExportVO();
            exportVO.setIndex(i + 1); // 序号
            exportVO.setExamName(exam.getExamName());
            exportVO.setSubject(exam.getSubjectTypeDesc());
            exportVO.setExamLv(exam.getExamLvDesc());
            exportVO.setExamTime(exam.getExamTime());
            exportVO.setGrade(exam.getGra() != null ? exam.getGra() + "级" : "");
            exportVO.setTeacherName(exam.getName()); // 负责人姓名
            exportList.add(exportVO);
        }
        
        // 导出 Excel，带样式优化
        EasyExcel.write(response.getOutputStream(), ExamExportVO.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .sheet("考试数据")
                .doWrite(exportList);
    }
}
