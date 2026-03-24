package org.gzw.backend.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import jakarta.servlet.http.HttpServletResponse;
import org.gzw.backend.common.Result;
import org.gzw.backend.entity.Teacher;
import org.gzw.backend.entity.vo.TeacherExportVO;
import org.gzw.backend.entity.vo.TeacherImportVO;
import org.gzw.backend.entity.vo.TeacherVO;
import org.gzw.backend.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/teacher")
@CrossOrigin
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @PostMapping("/add")
    public Result<Integer> addTeacher(@RequestBody Teacher teacher) {
        int result = teacherService.addTeacher(teacher);
        if (result > 0) {
            return Result.success(result);
        }
        return Result.error(500, "添加教师失败");
    }

    @PutMapping("/update")
    public Result<Integer> updateTeacher(@RequestBody Teacher teacher) {
        int result = teacherService.updateTeacher(teacher);
        if (result > 0) {
            return Result.success(result);
        }
        return Result.error(500, "更新教师失败");
    }

    @DeleteMapping("/{teacherId}")
    public Result<Integer> deleteTeacher(@PathVariable Long teacherId) {
        int result = teacherService.deleteTeacher(teacherId);
        if (result > 0) {
            return Result.success(result);
        }
        return Result.error(500, "删除教师失败");
    }

    @GetMapping("/{teacherId}")
    public Result<TeacherVO> getTeacherById(@PathVariable Long teacherId) {
        TeacherVO teacher = teacherService.getTeacherById(teacherId);
        if (teacher != null) {
            return Result.success(teacher);
        }
        return Result.error(404, "教师不存在");
    }

    @GetMapping("/list")
    public Result<List<TeacherVO>> getAllTeachers() {
        List<TeacherVO> teachers = teacherService.getAllTeachers();
        return Result.success(teachers);
    }

    @PostMapping("/search/advanced")
    public Result<List<TeacherVO>> searchTeachersByConditions(
            @RequestParam(required = false) Integer teachSubject,
            @RequestParam(required = false) Integer gra,
            @RequestParam(required = false) Integer cla,
            @RequestParam(required = false) Integer teacherType,
            @RequestParam(required = false) String name) {
        List<TeacherVO> teachers = teacherService.searchTeachersByConditions(teachSubject, gra, cla, teacherType, name);
        return Result.success(teachers);
    }

    @GetMapping("/subject-leaders/list")
    public Result<List<TeacherVO>> getSubjectLeaders() {
        List<TeacherVO> teachers = teacherService.getSubjectLeaders();
        return Result.success(teachers);
    }

    @GetMapping("/list-by-subject/{subjectCode}")
    public Result<List<TeacherVO>> getTeachersBySubject(@PathVariable Integer subjectCode) {
        List<TeacherVO> teachers = teacherService.getTeachersBySubject(subjectCode);
        return Result.success(teachers);
    }

    /**
     * 下载教师导入模板
     */
    @GetMapping("/download-template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("教师导入模板.xlsx", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);
        
        // 使用 TeacherImportVO 类自动生成横向表头，不写入示例数据
        EasyExcel.write(response.getOutputStream(), TeacherImportVO.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .sheet("教师导入模板")
                .doWrite(new ArrayList<>());
    }

    /**
     * 批量导入教师
     */
    @PostMapping("/import")
    public Result<Integer> importTeachers(@RequestParam("file") MultipartFile file) {
        try {
            List<TeacherImportVO> teachers = EasyExcel.read(file.getInputStream())
                    .head(TeacherImportVO.class)
                    .sheet()
                    .doReadSync();
            
            if (teachers == null || teachers.isEmpty()) {
                return Result.error(400, "Excel 文件为空");
            }
            
            int successCount = teacherService.importTeachers(teachers);
            return Result.success(successCount);
        } catch (Exception e) {
            return Result.error(500, "导入失败：" + e.getMessage());
        }
    }

    /**
     * 导出教师数据
     */
    @PostMapping("/export")
    public void exportTeachers(@RequestBody(required = false) List<TeacherVO> teachers,
                               HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("教师数据.xlsx", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);
        
        // 如果没有传入数据，则导出所有教师
        if (teachers == null || teachers.isEmpty()) {
            teachers = teacherService.getAllTeachers();
        }
        
        // 转换为导出 VO
        List<TeacherExportVO> exportList = new ArrayList<>();
        for (int i = 0; i < teachers.size(); i++) {
            TeacherVO teacher = teachers.get(i);
            TeacherExportVO exportVO = new TeacherExportVO();
            exportVO.setIndex(i + 1); // 序号
            exportVO.setUserName(teacher.getUserName());
            exportVO.setName(teacher.getName());
            exportVO.setSex(teacher.getSexDesc());
            exportVO.setTeacherCode(teacher.getTeacherCode());
            exportVO.setTeachSubject(teacher.getTeachSubjectDesc());
            exportVO.setTeacherType(teacher.getTeacherTypeDesc());
            exportVO.setGrade(teacher.getGra() != null ? teacher.getGra() + "级" : "");
            exportVO.setClassName(teacher.getCla() != null ? teacher.getCla() + "班" : "");
            exportVO.setPhone(teacher.getPhone() != null ? teacher.getPhone().toString() : "");
            exportVO.setTeacherStatus(teacher.getTeacherStatusDesc());
            exportList.add(exportVO);
        }
        
        // 导出 Excel，带样式优化
        EasyExcel.write(response.getOutputStream(), TeacherExportVO.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .sheet("教师数据")
                .doWrite(exportList);
    }
}
