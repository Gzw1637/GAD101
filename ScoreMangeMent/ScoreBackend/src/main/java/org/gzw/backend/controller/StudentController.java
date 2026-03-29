package org.gzw.backend.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import org.gzw.backend.common.Result;
import org.gzw.backend.entity.Student;
import org.gzw.backend.entity.vo.StudentExportVO;
import org.gzw.backend.entity.vo.StudentImportVO;
import org.gzw.backend.entity.vo.StudentVO;
import org.gzw.backend.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;

@RestController
@RequestMapping("/api/student")
@CrossOrigin
public class StudentController {

    @Autowired
    private StudentService studentService;

    @PostMapping("/add")
    public Result<Integer> addStudent(@RequestBody Student student) {
        int result = studentService.addStudent(student);
        if (result > 0) {
            return Result.success(result);
        }
        return Result.error(500, "添加学生失败");
    }

    @PutMapping("/update")
    public Result<Integer> updateStudent(@RequestBody Student student) {
        int result = studentService.updateStudent(student);
        if (result > 0) {
            return Result.success(result);
        }
        return Result.error(500, "更新学生失败");
    }

    @DeleteMapping("/delete/{studentId}")
    public Result<Integer> deleteStudent(@PathVariable Long studentId) {
        int result = studentService.deleteStudent(studentId);
        if (result > 0) {
            return Result.success(result);
        }
        return Result.error(500, "删除学生失败");
    }

    @GetMapping("/getById/{studentId}")
    public Result<StudentVO> getStudentById(@PathVariable Long studentId) {
        StudentVO student = studentService.getStudentById(studentId);
        if (student != null) {
            return Result.success(student);
        }
        return Result.error(404, "学生不存在");
    }

    @GetMapping("/list")
    public Result<List<StudentVO>> getAllStudents() {
        List<StudentVO> students = studentService.getAllStudents();
        return Result.success(students);
    }

    @PostMapping("/search/advanced")
    public Result<List<StudentVO>> searchStudentsByConditions(
            @RequestParam(required = false) Integer gra,
            @RequestParam(required = false) Integer cla,
            @RequestParam(required = false) Integer studentType,
            @RequestParam(required = false) String name) {
        List<StudentVO> students = studentService.searchStudentsByConditions(gra, cla, studentType, name);
        return Result.success(students);
    }
    
    /**
     * 根据学号查询学生
     */
    @GetMapping("/search-by-student-code")
    public Result<StudentVO> searchStudentByStudentCode(@RequestParam("studentCode") String studentCode) {
        try {
            StudentVO student = studentService.searchByStudentCode(studentCode);
            if (student != null) {
                return Result.success(student);
            } else {
                return Result.error(404, "未找到该学生");
            }
        } catch (Exception e) {
            return Result.error(500, "查询学生失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据用户 ID 查询学生
     */
    @GetMapping("/user/{userId}")
    public Result<StudentVO> getStudentByUserId(@PathVariable Long userId) {
        try {
            StudentVO student = studentService.getStudentByUserId(userId);
            if (student != null) {
                return Result.success(student);
            } else {
                return Result.error(404, "未找到该学生");
            }
        } catch (Exception e) {
            return Result.error(500, "查询学生失败：" + e.getMessage());
        }
    }

    @PostMapping("/export")
    public void exportStudents(
            @RequestParam(required = false) Integer gra,
            @RequestParam(required = false) Integer cla,
            @RequestParam(required = false) Integer studentType,
            @RequestParam(required = false) String name,
            HttpServletResponse response) throws IOException {
        List<StudentExportVO> exportList = studentService.exportStudents(gra, cla, studentType, name);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("学生数据导出.xlsx", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);

        // 表头颜色不要太深 + 内容样式适中
        WriteCellStyle headStyle = new WriteCellStyle();
        headStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        headStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        headStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        WriteFont headFont = new WriteFont();
        headFont.setBold(true);
        headStyle.setWriteFont(headFont);

        WriteCellStyle contentStyle = new WriteCellStyle();
        contentStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        contentStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        HorizontalCellStyleStrategy styleStrategy = new HorizontalCellStyleStrategy(headStyle, contentStyle);

        EasyExcel.write(response.getOutputStream(), StudentExportVO.class)
                .registerWriteHandler(styleStrategy)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .sheet("学生数据")
                .doWrite(exportList);
    }

    /**
     * 下载学生导入模板
     */
    @GetMapping("/download-template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("学生导入模板.xlsx", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);
        
        // 使用 StudentImportVO 类自动生成横向表头，不写入示例数据
        EasyExcel.write(response.getOutputStream(), StudentImportVO.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .sheet("学生导入模板")
                .doWrite(new ArrayList<>());
    }

    /**
     * 批量导入学生
     */
    @PostMapping("/import")
    public Result<Integer> importStudents(@RequestParam("file") MultipartFile file) {
        try {
            List<StudentImportVO> students = EasyExcel.read(file.getInputStream())
                    .head(StudentImportVO.class)
                    .sheet()
                    .doReadSync();
            
            if (students == null || students.isEmpty()) {
                return Result.error(400, "Excel 文件为空");
            }
            
            int successCount = studentService.importStudents(students);
            return Result.success(successCount);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "导入失败：" + e.getMessage());
        }
    }
}
