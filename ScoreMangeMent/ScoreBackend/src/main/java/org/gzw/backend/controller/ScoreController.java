package org.gzw.backend.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
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

@RestController
@RequestMapping("/api/score")
@CrossOrigin
public class ScoreController {

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
                return Result.error(401, "未登录或登录已过期");
            }
            
            // 获取教师信息
            TeacherVO teacher = teacherService.getTeacherByUserId(userId);
            if (teacher == null) {
                return Result.error(404, "未找到教师信息");
            }
            
            // 获取所有成绩
            List<ScoreVO> scores;
            if (subjectType != null || (examName != null && !examName.isEmpty())) {
                scores = scoreService.getScoresByFilters(subjectType, examName);
            } else {
                scores = scoreService.getAllScores();
            }
            
            // 根据教师的年级和学科过滤成绩
            List<ScoreVO> filteredScores = new ArrayList<>();
            for (ScoreVO score : scores) {
                // 检查年级和学科是否匹配
                if (score.getGra() != null && score.getGra().equals(teacher.getGra()) &&
                    score.getSubjectType() != null && score.getSubjectType().equals(teacher.getTeachSubject())) {
                    filteredScores.add(score);
                }
            }
            
            return Result.success(filteredScores);
        } catch (Exception e) {
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
                return Result.error(401, "未登录或登录已过期");
            }
            
            // 获取教师信息
            TeacherVO teacher = teacherService.getTeacherByUserId(userId);
            if (teacher == null) {
                return Result.error(404, "未找到教师信息");
            }
            
            // 获取所有成绩
            List<ScoreVO> scores = scoreService.getScoresByExamId(examId);
            
            // 根据教师的年级和学科过滤成绩
            List<ScoreVO> filteredScores = new ArrayList<>();
            for (ScoreVO score : scores) {
                // 检查年级和学科是否匹配
                if (score.getGra() != null && score.getGra().equals(teacher.getGra()) &&
                    score.getSubjectType() != null && score.getSubjectType().equals(teacher.getTeachSubject())) {
                    filteredScores.add(score);
                }
            }
            
            return Result.success(filteredScores);
        } catch (Exception e) {
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

    @PostMapping("/export")
    public void exportScores(@RequestBody(required = false) List<ScoreVO> scores,
                            HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("成绩数据.xlsx", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);
        
        if (scores == null || scores.isEmpty()) {
            scores = scoreService.getAllScores();
        }
        
        List<ScoreExportVO> exportList = scoreService.exportScores(scores);
        
        EasyExcel.write(response.getOutputStream(), ScoreExportVO.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .sheet("成绩数据")
                .doWrite(exportList);
    }
}
