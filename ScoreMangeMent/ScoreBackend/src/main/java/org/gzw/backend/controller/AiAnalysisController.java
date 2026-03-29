package org.gzw.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.gzw.backend.common.Result;
import org.gzw.backend.entity.StudentExamPaper;
import org.gzw.backend.entity.vo.AiAnalysisVO;
import org.gzw.backend.entity.vo.UserVO;
import org.gzw.backend.service.AiAnalysisService;
import org.gzw.backend.service.UserService;
import org.gzw.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI 成绩分析控制器
 */
@RestController
@RequestMapping("/api/ai-analysis")
@CrossOrigin
public class AiAnalysisController {

    @Autowired
    private AiAnalysisService aiAnalysisService;

    @Autowired
    private UserService userService;

    /**
     * 分析班级成绩
     *
     * @param examId 考试 ID
     * @param request HTTP 请求（用于获取当前用户）
     * @return AI 分析结果
     */
    @PostMapping("/class")
    public Result<AiAnalysisVO> analyzeClass(@RequestParam("examId") Long examId,
                                             HttpServletRequest request) {
        try {
            // 获取当前登录用户
            Long teacherId = getCurrentUserId(request);
            if (teacherId == null) {
                return Result.error(401, "请先登录");
            }

            // 调用 AI 分析
            AiAnalysisVO result = aiAnalysisService.analyzeClassPerformance(examId, teacherId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(500, "分析失败：" + e.getMessage());
        }
    }

    /**
     * 上传并分析学生试卷
     *
     * @param file 试卷图片文件
     * @param studentId 学生 ID
     * @param examId 考试 ID
     * @param request HTTP 请求
     * @return 试卷记录 ID
     */
    @PostMapping("/paper/upload")
    public Result<Map<String, Object>> uploadPaper(@RequestParam("file") MultipartFile file,
                                                    @RequestParam("studentId") Long studentId,
                                                    @RequestParam("examId") Long examId,
                                                    HttpServletRequest request) {
        try {
            // 验证文件
            if (file.isEmpty()) {
                return Result.error(400, "请选择要上传的文件");
            }

            // 验证文件大小（最大 10MB）
            if (file.getSize() > 10 * 1024 * 1024) {
                return Result.error(400, "文件大小不能超过 10MB");
            }

            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return Result.error(400, "请上传图片文件");
            }

            // 获取当前用户
            Long teacherId = getCurrentUserId(request);
            if (teacherId == null) {
                return Result.error(401, "请先登录");
            }

            // 上传并分析
            Long paperId = aiAnalysisService.uploadAndAnalyzePaper(file, studentId, examId, teacherId);

            Map<String, Object> response = new HashMap<>();
            response.put("paperId", paperId);
            response.put("message", "上传成功，正在分析中...");

            return Result.success(response);
        } catch (Exception e) {
            return Result.error(500, "上传失败：" + e.getMessage());
        }
    }

    /**
     * 分析学生试卷
     *
     * @param paperId 试卷记录 ID
     * @return AI 分析结果
     */
    @PostMapping("/paper/analyze")
    public Result<AiAnalysisVO> analyzePaper(@RequestParam("paperId") Long paperId) {
        try {
            AiAnalysisVO result = aiAnalysisService.analyzeStudentPaper(paperId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(500, "分析失败：" + e.getMessage());
        }
    }

    /**
     * 获取分析报告
     *
     * @param examId 考试 ID
     * @return AI 分析报告
     */
    @GetMapping("/report/{examId}")
    public Result<AiAnalysisVO> getReport(@PathVariable Long examId) {
        try {
            AiAnalysisVO result = aiAnalysisService.getAnalysisReport(examId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(500, "获取报告失败：" + e.getMessage());
        }
    }

    /**
     * 获取分析历史
     *
     * @param request HTTP 请求
     * @return 分析历史列表
     */
    @GetMapping("/history")
    public Result<List<StudentExamPaper>> getHistory(HttpServletRequest request) {
        try {
            Long teacherId = getCurrentUserId(request);
            if (teacherId == null) {
                return Result.error(401, "请先登录");
            }

            List<StudentExamPaper> papers = aiAnalysisService.getAnalysisHistory(teacherId);
            return Result.success(papers);
        } catch (Exception e) {
            return Result.error(500, "获取历史失败：" + e.getMessage());
        }
    }

    /**
     * 获取试卷详情
     *
     * @param paperId 试卷记录 ID
     * @return 试卷详情
     */
    @GetMapping("/paper/{paperId}")
    public Result<StudentExamPaper> getPaperDetail(@PathVariable Long paperId) {
        try {
            StudentExamPaper paper = aiAnalysisService.getPaperById(paperId);
            return Result.success(paper);
        } catch (Exception e) {
            return Result.error(500, "获取详情失败：" + e.getMessage());
        }
    }

    /**
     * 分析学生个人成绩
     *
     * @param scoreId 成绩记录 ID
     * @param request HTTP 请求
     * @return AI 分析结果
     */
    @PostMapping("/student-score/{scoreId}")
    public Result<AiAnalysisVO> analyzeStudentScore(@PathVariable("scoreId") Long scoreId,
                                                    HttpServletRequest request) {
        try {
            // 获取当前登录用户
            Long userId = getCurrentUserId(request);
            if (userId == null) {
                return Result.error(401, "请先登录");
            }

            // 调用 AI 分析
            AiAnalysisVO result = aiAnalysisService.analyzeStudentScore(scoreId, userId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(500, "分析失败：" + e.getMessage());
        }
    }

    /**
     * 获取当前登录用户 ID
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            return JwtUtil.getUserIdFromToken(token);
        } catch (Exception e) {
            return null;
        }
    }
}
