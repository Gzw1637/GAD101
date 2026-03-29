package org.gzw.backend.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.gzw.backend.common.Result;
import org.gzw.backend.entity.Score;
import org.gzw.backend.entity.vo.ScoreExportVO;
import org.gzw.backend.entity.vo.ScoreImportVO;
import org.gzw.backend.entity.vo.ScoreVO;
import org.gzw.backend.entity.vo.TeacherVO;
import org.gzw.backend.service.ScoreService;
import org.gzw.backend.service.TeacherService;
import org.gzw.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/score")
@CrossOrigin
public class ScoreController {

    private static final Logger logger = LoggerFactory.getLogger(ScoreController.class);

    @Autowired
    private ScoreService scoreService;
    
    @Autowired
    private TeacherService teacherService;

    @PostMapping("/add")
    public Result<Integer> addScore(@RequestBody Score score) {
        int result = scoreService.addScore(score);
        if (result > 0) {
            return Result.success(result);
        }
        return Result.error(500, "添加成绩失败");
    }

    @PutMapping("/update")
    public Result<Integer> updateScore(@RequestBody Score score) {
        int result = scoreService.updateScore(score);
        if (result > 0) {
            return Result.success(result);
        }
        return Result.error(500, "更新成绩失败");
    }

    @DeleteMapping("/delete/{scoreId}")
    public Result<Integer> deleteScore(@PathVariable Long scoreId) {
        int result = scoreService.deleteScore(scoreId);
        if (result > 0) {
            return Result.success(result);
        }
        return Result.error(500, "删除成绩失败");
    }

    @GetMapping("/getById/{scoreId}")
    public Result<ScoreVO> getScoreById(@PathVariable Long scoreId) {
        ScoreVO score = scoreService.getScoreById(scoreId);
        if (score != null) {
            return Result.success(score);
        }
        return Result.error(404, "成绩记录不存在");
    }

    @GetMapping("/list")
    public Result<List<ScoreVO>> getAllScores(
            HttpServletRequest request,
            @RequestParam(required = false) Integer subjectType,
            @RequestParam(required = false) String examName) {
        try {
            // 获取token并解析userId
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            Long userId = JwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                logger.warn("未登录或登录已过期，userId为null");
                return Result.error(401, "未登录或登录已过期");
            }
            
            // 获取用户角色
            Integer role = JwtUtil.getRoleFromToken(token);
            logger.info("获取成绩列表，userId: {}, role: {}", userId, role);
            
            // 获取所有成绩
            List<ScoreVO> scores;
            if (subjectType != null || (examName != null && !examName.isEmpty())) {
                logger.info("使用过滤条件查询成绩：subjectType={}, examName={}", subjectType, examName);
                scores = scoreService.getScoresByFilters(subjectType, examName);
            } else {
                logger.info("查询所有成绩");
                scores = scoreService.getAllScores();
            }
            
            logger.info("查询到的成绩总数：{}", scores.size());
            
            // 如果是管理员，直接返回所有成绩
            if (role != null && role == 14981003) { // 假设1是管理员角色
                logger.info("管理员用户，返回所有成绩");
                return Result.success(scores);
            }
            
            // 非管理员用户，需要获取教师信息并过滤成绩
            // 获取教师信息
            TeacherVO teacher = teacherService.getTeacherByUserId(userId);
            if (teacher == null) {
                logger.warn("未找到教师信息，userId: {}", userId);
                return Result.error(404, "未找到教师信息");
            }
            
            logger.info("教师信息：id={}, name={}, gra={}, teachSubject={}", 
                       teacher.getTeacherId(), teacher.getName(), teacher.getGra(), teacher.getTeachSubject());
            
            // 根据教师的年级和学科过滤成绩
            List<ScoreVO> filteredScores = new ArrayList<>();
            for (ScoreVO score : scores) {
                // 检查年级和学科是否匹配
                if (score.getGra() != null && score.getGra().equals(teacher.getGra()) &&
                    score.getSubjectType() != null && score.getSubjectType().equals(teacher.getTeachSubject())) {
                    filteredScores.add(score);
                }
            }
            
            logger.info("过滤后的成绩数量：{}", filteredScores.size());
            
            return Result.success(filteredScores);
        } catch (Exception e) {
            logger.error("获取成绩失败", e);
            return Result.error(500, "获取成绩失败：" + e.getMessage());
        }
    }

    @GetMapping("/list-by-exam/{examId}")
    public Result<List<ScoreVO>> getScoresByExamId(
            HttpServletRequest request,
            @PathVariable Long examId) {
        try {
            // 获取token并解析userId
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            Long userId = JwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                logger.warn("未登录或登录已过期，userId为null");
                return Result.error(401, "未登录或登录已过期");
            }
            
            // 获取用户角色
            Integer role = JwtUtil.getRoleFromToken(token);
            logger.info("根据考试ID获取成绩列表，userId: {}, role: {}, examId: {}", userId, role, examId);
            
            // 获取所有成绩
            List<ScoreVO> scores = scoreService.getScoresByExamId(examId);
            
            logger.info("查询到的成绩总数：{}", scores.size());
            
            // 如果是管理员，直接返回所有成绩
            if (role != null && role == 1) { // 假设1是管理员角色
                logger.info("管理员用户，返回所有成绩");
                return Result.success(scores);
            }
            
            // 非管理员用户，需要获取教师信息并过滤成绩
            // 获取教师信息
            TeacherVO teacher = teacherService.getTeacherByUserId(userId);
            if (teacher == null) {
                logger.warn("未找到教师信息，userId: {}", userId);
                return Result.error(404, "未找到教师信息");
            }
            
            logger.info("教师信息：id={}, name={}, gra={}, teachSubject={}", 
                       teacher.getTeacherId(), teacher.getName(), teacher.getGra(), teacher.getTeachSubject());
            
            // 根据教师的年级和学科过滤成绩
            List<ScoreVO> filteredScores = new ArrayList<>();
            for (ScoreVO score : scores) {
                // 检查年级和学科是否匹配
                if (score.getGra() != null && score.getGra().equals(teacher.getGra()) &&
                    score.getSubjectType() != null && score.getSubjectType().equals(teacher.getTeachSubject())) {
                    filteredScores.add(score);
                }
            }
            
            logger.info("过滤后的成绩数量：{}", filteredScores.size());
            
            return Result.success(filteredScores);
        } catch (Exception e) {
            logger.error("获取成绩失败", e);
            return Result.error(500, "获取成绩失败：" + e.getMessage());
        }
    }

    @GetMapping("/my")
    public Result<List<ScoreVO>> getMyScores(
            HttpServletRequest request,
            @RequestParam(required = false) Long examId,
            @RequestParam(required = false) Integer subjectType,
            @RequestParam(required = false) String examName) {
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
            
            List<ScoreVO> scores = scoreService.getScoresByStudentIdAndFilters(studentId, examId, subjectType, examName);
            return Result.success(scores);
        } catch (Exception e) {
            return Result.error(500, "获取成绩失败：" + e.getMessage());
        }
    }

    @GetMapping("/download-template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("成绩导入模板.xlsx", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);
        
        EasyExcel.write(response.getOutputStream(), ScoreImportVO.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .sheet("成绩导入模板")
                .doWrite(new ArrayList<>());
    }

    @PostMapping("/import")
    public Result<Integer> importScores(@RequestParam("file") MultipartFile file) {
        try {
            List<ScoreImportVO> scores = EasyExcel.read(file.getInputStream())
                    .head(ScoreImportVO.class)
                    .sheet()
                    .doReadSync();
            
            if (scores == null || scores.isEmpty()) {
                return Result.error(400, "Excel 文件为空");
            }
            
            int successCount = scoreService.importScores(scores);
            return Result.success(successCount);
        } catch (Exception e) {
            return Result.error(500, "导入失败：" + e.getMessage());
        }
    }

    /**
     * 导出成绩数据
     */
    @PostMapping("/export")
    public void exportScores(
            HttpServletRequest request,
            @RequestParam(required = false) Integer subjectType,
            @RequestParam(required = false) String examName,
            HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("成绩数据.xlsx", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);
        
        // 根据查询条件获取成绩列表
        List<ScoreVO> scores = scoreService.getScoresByFilters(subjectType, examName);
        
        List<ScoreExportVO> exportList = scoreService.exportScores(scores);
        
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
    }
}
