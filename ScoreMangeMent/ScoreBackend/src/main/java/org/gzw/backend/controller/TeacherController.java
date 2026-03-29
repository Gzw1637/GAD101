package org.gzw.backend.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import jakarta.servlet.http.HttpServletRequest;
import org.gzw.backend.common.Result;
import org.gzw.backend.entity.Teacher;
import org.gzw.backend.entity.vo.ScoreExportVO;
import org.gzw.backend.entity.vo.ScoreVO;
import org.gzw.backend.entity.vo.TeacherExportVO;
import org.gzw.backend.entity.vo.TeacherImportVO;
import org.gzw.backend.entity.vo.TeacherVO;
import org.gzw.backend.service.ScoreService;
import org.gzw.backend.service.TeacherService;
import org.gzw.backend.util.JwtUtil;
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
    
    @Autowired
    private ScoreService scoreService;

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

    @GetMapping("/user/{userId}")
    public Result<TeacherVO> getTeacherByUserId(@PathVariable Long userId) {
        TeacherVO teacher = teacherService.getTeacherByUserId(userId);
        if (teacher != null) {
            return Result.success(teacher);
        }
        return Result.error(404, "教师不存在");
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
    public void exportTeachers(
            @RequestParam(required = false) Integer teachSubject,
            @RequestParam(required = false) Integer gra,
            @RequestParam(required = false) Integer cla,
            @RequestParam(required = false) Integer teacherType,
            @RequestParam(required = false) String name,
            HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("教师数据.xlsx", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);
        
        // 根据查询条件获取教师列表
        List<TeacherVO> teachers = teacherService.searchTeachersByConditions(teachSubject, gra, cla, teacherType, name);
        
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
        
        // 导出 Excel，带样式优化
        EasyExcel.write(response.getOutputStream(), TeacherExportVO.class)
                .registerWriteHandler(styleStrategy)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .sheet("教师数据")
                .doWrite(exportList);
    }
    
    /**
     * 导出教师所教班级的成绩数据
     */
    @PostMapping("/export-scores")
    public void exportTeacherScores(
            HttpServletRequest request,
            @RequestParam(required = false) String examName,
            @RequestParam(required = false) String sortBy,
            HttpServletResponse response) throws IOException {
        try {
            // 获取 token 并解析 userId
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            Long userId = JwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                response.setContentType("application/json");
                response.setCharacterEncoding("utf-8");
                response.getWriter().write("{\"code\":401,\"message\":\"未登录或登录已过期\"}");
                return;
            }
            
            // 获取教师信息
            TeacherVO teacher = teacherService.getTeacherByUserId(userId);
            if (teacher == null) {
                response.setContentType("application/json");
                response.setCharacterEncoding("utf-8");
                response.getWriter().write("{\"code\":404,\"message\":\"未找到教师信息\"}");
                return;
            }
            
            // 根据教师的年级和学科过滤成绩
            List<ScoreVO> scores = scoreService.getScoresByFilters(teacher.getTeachSubject(), examName);
            List<ScoreVO> filteredScores = new ArrayList<>();
            for (ScoreVO score : scores) {
                // 检查年级是否匹配
                if (score.getGra() != null && score.getGra().equals(teacher.getGra())) {
                    filteredScores.add(score);
                }
            }
            
            // 根据排序方式进行排序
            if (sortBy != null && !sortBy.isEmpty()) {
                filteredScores.sort((a, b) -> {
                    if ("gradeRankAsc".equals(sortBy)) {
                        Integer rankA = a.getGradeRank() != null ? a.getGradeRank() : 999999;
                        Integer rankB = b.getGradeRank() != null ? b.getGradeRank() : 999999;
                        return rankA.compareTo(rankB);
                    } else if ("gradeRankDesc".equals(sortBy)) {
                        Integer rankA = a.getGradeRank() != null ? a.getGradeRank() : 999999;
                        Integer rankB = b.getGradeRank() != null ? b.getGradeRank() : 999999;
                        return rankB.compareTo(rankA);
                    } else if ("classRankAsc".equals(sortBy)) {
                        Integer rankA = a.getClassRank() != null ? a.getClassRank() : 999999;
                        Integer rankB = b.getClassRank() != null ? b.getClassRank() : 999999;
                        return rankA.compareTo(rankB);
                    } else if ("classRankDesc".equals(sortBy)) {
                        Integer rankA = a.getClassRank() != null ? a.getClassRank() : 999999;
                        Integer rankB = b.getClassRank() != null ? b.getClassRank() : 999999;
                        return rankB.compareTo(rankA);
                    }
                    return 0;
                });
            }
            
            // 转换为导出 VO
            List<ScoreExportVO> exportList = scoreService.exportScores(filteredScores);
            
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("教师成绩数据.xlsx", "UTF-8").replaceAll("\\+", "%20");
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
            
            EasyExcel.write(response.getOutputStream(), ScoreExportVO.class)
                    .registerWriteHandler(styleStrategy)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .sheet("成绩数据")
                    .doWrite(exportList);
        } catch (Exception e) {
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().write("{\"code\":500,\"message\":\"导出失败：" + e.getMessage() + "\"}");
        }
    }
}
