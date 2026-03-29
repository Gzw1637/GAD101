package org.gzw.backend.service.Impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.gzw.backend.entity.*;
import org.gzw.backend.entity.vo.AiAnalysisVO;
import org.gzw.backend.entity.vo.ExamVO;
import org.gzw.backend.entity.vo.ScoreVO;
import org.gzw.backend.entity.vo.StudentVO;
import org.gzw.backend.mapper.AiAnalysisMapper;
import org.gzw.backend.mapper.ExamMapper;
import org.gzw.backend.mapper.StudentMapper;
import org.gzw.backend.mapper.StudentExamPaperMapper;
import org.gzw.backend.service.AiAnalysisService;
import org.gzw.backend.service.ScoreService;
import org.gzw.backend.util.OcrService;
import org.gzw.backend.util.QwenAiClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * AI 分析服务实现类
 */
@Service
public class AiAnalysisServiceImpl implements AiAnalysisService {

    @Autowired
    private AiAnalysisMapper aiAnalysisMapper;

    @Autowired
    private StudentExamPaperMapper studentExamPaperMapper;

    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private QwenAiClient qwenAiClient;

    @Autowired
    private OcrService ocrService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public AiAnalysisVO analyzeClassPerformance(Long examId, Long teacherId) {
        // 1. 检查是否已有分析记录
        AiAnalysisRecord existingRecord = aiAnalysisMapper.selectByExamIdAndType(examId, 1);
        if (existingRecord != null) {
            // 先解析 AI 分析结果
            AiAnalysisVO cachedResult = parseAnalysisResult(existingRecord.getAnalysisContent());
            
            // 重新获取成绩数据来填充统计信息
            List<ScoreVO> scores = scoreService.getScoresByExamId(examId);
            if (scores != null && !scores.isEmpty()) {
                // 获取考试信息来判断科目
                ExamVO examVO = examMapper.selectById(examId);
                Integer subjectType = examVO != null ? examVO.getSubjectType() : null;
                
                // 计算统计数据
                Map<String, Object> stats = calculateStatistics(scores, subjectType);
                
                // 填充基础数据
                cachedResult.setExamId(examId);
                cachedResult.setTotalStudents(scores.size());
                cachedResult.setAverageScore((Double) stats.get("averageScore"));
                cachedResult.setMaxScore((Double) stats.get("maxScore"));
                cachedResult.setMinScore((Double) stats.get("minScore"));
                cachedResult.setPassRate((Double) stats.get("passRate"));
                cachedResult.setExcellentRate((Double) stats.get("excellentRate"));
                cachedResult.setScoreDistribution((Map<String, Integer>) stats.get("scoreDistribution"));
                
                // 填充考试信息（使用已获取的 examVO）
                if (examVO != null) {
                    cachedResult.setExamName(examVO.getExamName());
                    cachedResult.setSubjectType(examVO.getSubjectType());
                    cachedResult.setSubjectName(getSubjectName(examVO.getSubjectType()));
                    cachedResult.setGrade(examVO.getGra());
                }
                
                // 构建班级排名
                cachedResult.setClassRanking(buildClassRanking(scores));
                
                // 获取上次考试对比
                Double previousAverage = getPreviousExamAverage(examId, cachedResult.getGrade());
                if (previousAverage != null) {
                    cachedResult.setExamComparison(buildExamComparison(examId, 
                            cachedResult.getExamName(), 
                            (Double) stats.get("averageScore"), 
                            previousAverage));
                }
            }
            
            return cachedResult;
        }

        // 2. 获取考试成绩数据
        List<ScoreVO> scores = scoreService.getScoresByExamId(examId);
        if (scores == null || scores.isEmpty()) {
            throw new RuntimeException("该考试暂无成绩数据");
        }

        // 3. 获取考试信息
        ExamVO examVO = examMapper.selectById(examId);
        if (examVO == null) {
            throw new RuntimeException("考试不存在");
        }
        
        // 将 ExamVO 转换为 Exam 实体（用于后续操作）
        Exam exam = new Exam();
        BeanUtils.copyProperties(examVO, exam);

        // 4. 计算统计数据
        Map<String, Object> stats = calculateStatistics(scores, exam.getSubjectType());

        // 5. 构建分数段分布字符串
        String scoreDistribution = buildScoreDistribution((Map<String, Integer>) stats.get("scoreDistribution"));

        // 6. 获取上次考试平均分（如果有）
        Double previousAverage = getPreviousExamAverage(examId, exam.getGra());

        // 7. 获取科目名称
        String subjectName = getSubjectName(exam.getSubjectType());

        // 8. 调用 AI 生成分析报告
        String aiResult = qwenAiClient.analyzeClassPerformance(
                exam.getExamName(),
                subjectName,
                exam.getGra(),
                null,
                (Double) stats.get("averageScore"),
                (Double) stats.get("maxScore"),
                (Double) stats.get("minScore"),
                (Double) stats.get("passRate"),
                (Double) stats.get("excellentRate"),
                scoreDistribution,
                previousAverage
        );

        // 9. 保存分析记录
        AiAnalysisRecord record = new AiAnalysisRecord();
        record.setExamId(examId);
        record.setTeacherId(teacherId);
        record.setAnalysisType(1);
        record.setAnalysisContent(aiResult);
        record.setCreateTime(LocalDateTime.now());
        aiAnalysisMapper.insert(record);

        // 10. 解析并返回结果
        AiAnalysisVO result = parseAnalysisResult(aiResult);
        
        // 补充基础统计数据
        result.setExamId(examId);
        result.setExamName(exam.getExamName());
        result.setSubjectType(exam.getSubjectType());
        result.setSubjectName(subjectName);
        result.setGrade(exam.getGra());
        result.setTotalStudents(scores.size());
        result.setAverageScore((Double) stats.get("averageScore"));
        result.setMaxScore((Double) stats.get("maxScore"));
        result.setMinScore((Double) stats.get("minScore"));
        result.setPassRate((Double) stats.get("passRate"));
        result.setExcellentRate((Double) stats.get("excellentRate"));
        result.setScoreDistribution((Map<String, Integer>) stats.get("scoreDistribution"));

        // 添加班级排名
        result.setClassRanking(buildClassRanking(scores));

        // 添加考试对比
        if (previousAverage != null) {
            result.setExamComparison(buildExamComparison(examId, exam.getExamName(), 
                    (Double) stats.get("averageScore"), previousAverage));
        }

        return result;
    }

    @Override
    @Transactional
    public Long uploadAndAnalyzePaper(MultipartFile file, Long studentId, Long examId, Long teacherId) {
        try {
            // 1. 上传试卷图片
            String imageUrl = ocrService.uploadPaperImage(file, studentId, examId);

            // 2. 创建试卷记录
            StudentExamPaper paper = new StudentExamPaper();
            paper.setStudentId(studentId);
            paper.setExamId(examId);
            paper.setPaperImageUrl(imageUrl);
            paper.setUploadTime(LocalDateTime.now());
            studentExamPaperMapper.insert(paper);

            // 3. OCR 识别试卷
            String ocrResult = ocrService.recognizePaper(imageUrl);
            studentExamPaperMapper.updateOcrResult(paper.getPaperId(), ocrResult);

            // 4. 获取学生和考试信息
            StudentVO studentVO = studentMapper.selectById(studentId);
            if (studentVO == null) {
                throw new RuntimeException("学生不存在");
            }
            Student student = new Student();
            BeanUtils.copyProperties(studentVO, student);
            
            ExamVO examVO = examMapper.selectById(examId);
            if (examVO == null) {
                throw new RuntimeException("考试不存在");
            }
            Exam exam = new Exam();
            BeanUtils.copyProperties(examVO, exam);

            // 5. 调用 AI 分析试卷
            String aiAnalysis = qwenAiClient.analyzeStudentPaper(
                    student.getName(),
                    getSubjectName(exam.getSubjectType()),
                    exam.getSubjectType(),
                    ocrResult
            );

            // 6. 保存 AI 分析结果
            studentExamPaperMapper.updateAiAnalysis(paper.getPaperId(), aiAnalysis);

            // 7. 保存分析记录
            AiAnalysisRecord record = new AiAnalysisRecord();
            record.setExamId(examId);
            record.setTeacherId(teacherId);
            record.setAnalysisType(2);
            record.setStudentId(studentId);
            record.setImageUrl(imageUrl);
            record.setAnalysisContent(aiAnalysis);
            record.setCreateTime(LocalDateTime.now());
            aiAnalysisMapper.insert(record);

            return paper.getPaperId();
        } catch (IOException e) {
            throw new RuntimeException("上传试卷失败：" + e.getMessage(), e);
        }
    }

    @Override
    public AiAnalysisVO analyzeStudentPaper(Long paperId) {
        // 1. 获取试卷记录
        StudentExamPaper paper = studentExamPaperMapper.selectById(paperId);
        if (paper == null) {
            throw new RuntimeException("试卷记录不存在");
        }

        // 2. 如果已有 AI 分析结果，直接返回
        if (paper.getAiAnalysis() != null && !paper.getAiAnalysis().isEmpty()) {
            return parsePaperAnalysisResult(paper.getAiAnalysis());
        }

        // 3. 重新分析
        StudentVO studentVO = studentMapper.selectById(paper.getStudentId());
        if (studentVO == null) {
            throw new RuntimeException("学生不存在");
        }
        Student student = new Student();
        BeanUtils.copyProperties(studentVO, student);
        
        ExamVO examVO = examMapper.selectById(paper.getExamId());
        if (examVO == null) {
            throw new RuntimeException("考试不存在");
        }
        Exam exam = new Exam();
        BeanUtils.copyProperties(examVO, exam);

        String aiAnalysis = qwenAiClient.analyzeStudentPaper(
                student.getName(),
                getSubjectName(exam.getSubjectType()),
                exam.getSubjectType(),
                paper.getOcrResult()
        );

        studentExamPaperMapper.updateAiAnalysis(paperId, aiAnalysis);

        return parsePaperAnalysisResult(aiAnalysis);
    }

    @Override
    public AiAnalysisVO getAnalysisReport(Long examId) {
        return analyzeClassPerformance(examId, 0L);
    }

    @Override
    public List<StudentExamPaper> getAnalysisHistory(Long teacherId) {
        List<AiAnalysisRecord> records = aiAnalysisMapper.selectByTeacherId(teacherId);
        List<StudentExamPaper> papers = new ArrayList<>();
        
        for (AiAnalysisRecord record : records) {
            if (record.getStudentId() != null && record.getAnalysisType() == 2) {
                StudentExamPaper paper = studentExamPaperMapper.selectByStudentAndExam(
                        record.getStudentId(), record.getExamId());
                if (paper != null) {
                    papers.add(paper);
                }
            }
        }
        
        return papers;
    }

    @Override
    public StudentExamPaper getPaperById(Long paperId) {
        return studentExamPaperMapper.selectById(paperId);
    }

    /**
     * 计算成绩统计数据
     */
    private Map<String, Object> calculateStatistics(List<ScoreVO> scores, Integer subjectType) {
        Map<String, Object> result = new HashMap<>();

        // 提取所有分数
        List<Double> scoreList = scores.stream()
                .map(s -> s.getScore().doubleValue())
                .collect(Collectors.toList());

        // 平均分
        double average = scoreList.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        result.put("averageScore", average);

        // 最高分
        result.put("maxScore", Collections.max(scoreList));

        // 最低分
        result.put("minScore", Collections.min(scoreList));

        // 判断满分值
        int fullScore = isFullScore150(subjectType) ? 150 : 100;
        double passLine = fullScore * 0.6;    // 60% 及格线
        double excellentLine = fullScore * 0.8; // 80% 优秀线

        // 及格率
        long passCount = scoreList.stream().filter(s -> s >= passLine).count();
        result.put("passRate", (passCount * 100.0) / scoreList.size());

        // 优秀率
        long excellentCount = scoreList.stream().filter(s -> s >= excellentLine).count();
        result.put("excellentRate", (excellentCount * 100.0) / scoreList.size());

        // 分数段分布（根据满分值动态调整）
        Map<String, Integer> distribution = buildScoreDistributionBySubject(scoreList, subjectType);
        result.put("scoreDistribution", distribution);

        return result;
    }
    
    /**
     * 根据科目构建分数段分布
     */
    private Map<String, Integer> buildScoreDistributionBySubject(List<Double> scoreList, Integer subjectType) {
        Map<String, Integer> distribution = new LinkedHashMap<>();
        
        if (isFullScore150(subjectType)) {
            // 150 分制（语文、数学、英语）
            distribution.put("120 分以上", countScores(scoreList, 120.0, null));
            distribution.put("105-119 分", countScores(scoreList, 105.0, 119.0));
            distribution.put("90-104 分", countScores(scoreList, 90.0, 104.0));
            distribution.put("75-89 分", countScores(scoreList, 75.0, 89.0));
            distribution.put("75 分以下", countScores(scoreList, null, 74.0));
        } else {
            // 100 分制（其他科目）
            distribution.put("90 分以上", countScores(scoreList, 90.0, null));
            distribution.put("80-89 分", countScores(scoreList, 80.0, 89.0));
            distribution.put("70-79 分", countScores(scoreList, 70.0, 79.0));
            distribution.put("60-69 分", countScores(scoreList, 60.0, 69.0));
            distribution.put("60 分以下", countScores(scoreList, null, 59.0));
        }
        
        return distribution;
    }
    
    /**
     * 统计分数段人数
     */
    private int countScores(List<Double> scoreList, Double min, Double max) {
        if (min == null && max == null) {
            return scoreList.size();
        }
        
        if (min == null) {
            return (int) scoreList.stream().filter(s -> s <= max).count();
        }
        
        if (max == null) {
            return (int) scoreList.stream().filter(s -> s >= min).count();
        }
        
        return (int) scoreList.stream().filter(s -> s >= min && s <= max).count();
    }
    
    /**
     * 判断科目是否为 150 分制
     */
    private boolean isFullScore150(Integer subjectType) {
        if (subjectType == null) {
            return false;
        }
        // 语文、数学、英语为 150 分制
        return subjectType == 10111001 || // 语文
               subjectType == 10111002 || // 数学
               subjectType == 10111003;   // 英语
    }

    /**
     * 构建分数段分布字符串
     */
    private String buildScoreDistribution(Map<String, Integer> distribution) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> entry : distribution.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append(" 人\n");
        }
        return sb.toString();
    }

    /**
     * 获取上次考试平均分
     */
    private Double getPreviousExamAverage(Long currentExamId, Integer grade) {
        // 简化实现：查找同年级的上一次考试
        // 实际应该根据考试时间排序
        List<ExamVO> exams = examMapper.selectAll();
        ExamVO currentExamVO = examMapper.selectById(currentExamId);
        if (currentExamVO == null) {
            return null;
        }
        
        List<ExamVO> sameGradeExams = exams.stream()
                .filter(e -> e.getGra().equals(grade) && e.getSubjectType().equals(currentExamVO.getSubjectType()))
                .filter(e -> !e.getExamId().equals(currentExamId))
                .collect(Collectors.toList());

        if (sameGradeExams.isEmpty()) {
            return null;
        }

        // 获取最近一次考试
        ExamVO previousExamVO = sameGradeExams.get(0);
        List<ScoreVO> previousScores = scoreService.getScoresByExamId(previousExamVO.getExamId());
        
        if (previousScores.isEmpty()) {
            return null;
        }

        return previousScores.stream()
                .mapToDouble(s -> s.getScore().doubleValue())
                .average()
                .orElse(0.0);
    }

    /**
     * 获取科目名称
     */
    private String getSubjectName(Integer subjectType) {
        Map<Integer, String> subjectMap = new HashMap<>();
        subjectMap.put(10111001, "语文");
        subjectMap.put(10111002, "数学");
        subjectMap.put(10111003, "英语");
        subjectMap.put(10111004, "物理");
        subjectMap.put(10111005, "历史");
        subjectMap.put(10111006, "化学");
        subjectMap.put(10111007, "生物");
        subjectMap.put(10111008, "地理");
        subjectMap.put(10111009, "政治");
        return subjectMap.getOrDefault(subjectType, "未知科目");
    }

    /**
     * 解析 AI 分析结果
     */
    private AiAnalysisVO parseAnalysisResult(String aiResult) {
        try {
            // 清理可能存在的 markdown 格式（反引号）
            String cleanJson = cleanJsonString(aiResult);
            
            JsonNode rootNode = objectMapper.readTree(cleanJson);
            AiAnalysisVO vo = new AiAnalysisVO();
            
            if (rootNode.has("classComment")) {
                vo.setClassComment(rootNode.get("classComment").asText());
            }
            if (rootNode.has("mainProblems")) {
                vo.setMainProblems(rootNode.get("mainProblems").asText());
            }
            if (rootNode.has("teachingSuggestions")) {
                List<String> suggestions = new ArrayList<>();
                JsonNode suggestionsNode = rootNode.get("teachingSuggestions");
                if (suggestionsNode.isArray()) {
                    for (JsonNode node : suggestionsNode) {
                        suggestions.add(node.asText());
                    }
                }
                vo.setTeachingSuggestions(suggestions);
            }
            if (rootNode.has("trend")) {
                if (vo.getExamComparison() == null) {
                    vo.setExamComparison(new AiAnalysisVO.ExamComparisonVO());
                }
                vo.getExamComparison().setTrend(rootNode.get("trend").asText());
            }
            
            return vo;
        } catch (Exception e) {
            // 如果解析失败，将原始结果作为 classComment 返回
            AiAnalysisVO vo = new AiAnalysisVO();
            vo.setClassComment(aiResult);
            vo.setMainProblems("AI 返回格式异常，请检查 API 响应");
            return vo;
        }
    }
    
    /**
     * 清理 JSON 字符串中的 markdown 格式
     */
    private String cleanJsonString(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        
        // 移除开头的 ```json 或 ```
        String cleaned = input.trim();
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7);
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3);
        }
        
        // 移除结尾的 ```
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3);
        }
        
        return cleaned.trim();
    }

    /**
     * 解析试卷分析结果
     */
    private AiAnalysisVO parsePaperAnalysisResult(String aiResult) {
        try {
            // 清理可能存在的 markdown 格式
            String cleanJson = cleanJsonString(aiResult);
            
            JsonNode rootNode = objectMapper.readTree(cleanJson);
            AiAnalysisVO vo = new AiAnalysisVO();
            
            // 解析总分
            if (rootNode.has("totalScore")) {
                double totalScore = rootNode.get("totalScore").asDouble();
                vo.setMaxScore(totalScore); // 使用 maxScore 字段存储总分
            }
            
            // 解析得分明细
            if (rootNode.has("scoreBreakdown")) {
                vo.setClassComment(rootNode.get("scoreBreakdown").asText());
            }
            
            // 解析优点
            List<String> advantages = new ArrayList<>();
            if (rootNode.has("advantages") && rootNode.get("advantages").isArray()) {
                for (JsonNode node : rootNode.get("advantages")) {
                    advantages.add(node.asText());
                }
            }
            
            // 解析薄弱点
            List<String> weaknesses = new ArrayList<>();
            if (rootNode.has("weaknesses") && rootNode.get("weaknesses").isArray()) {
                for (JsonNode node : rootNode.get("weaknesses")) {
                    weaknesses.add(node.asText());
                }
            }
            
            // 解析错误类型
            List<String> errorTypes = new ArrayList<>();
            if (rootNode.has("errorTypes") && rootNode.get("errorTypes").isArray()) {
                for (JsonNode node : rootNode.get("errorTypes")) {
                    errorTypes.add(node.asText());
                }
            }
            
            // 解析学习建议
            List<String> suggestions = new ArrayList<>();
            if (rootNode.has("learningSuggestions") && rootNode.get("learningSuggestions").isArray()) {
                for (JsonNode node : rootNode.get("learningSuggestions")) {
                    suggestions.add(node.asText());
                }
            }
            vo.setTeachingSuggestions(suggestions);
            
            // 解析表现等级
            if (rootNode.has("performanceLevel")) {
                vo.setMainProblems("表现等级：" + rootNode.get("performanceLevel").asText());
            }
            
            // 解析详细分析
            if (rootNode.has("detailedAnalysis")) {
                // 将详细分析放在 classComment 中
                String detailedAnalysis = rootNode.get("detailedAnalysis").asText();
                if (vo.getClassComment() != null && !vo.getClassComment().isEmpty()) {
                    vo.setClassComment(vo.getClassComment() + "\n\n" + detailedAnalysis);
                } else {
                    vo.setClassComment(detailedAnalysis);
                }
            }
            
            return vo;
        } catch (Exception e) {
            // 如果解析失败，将原始结果作为 classComment 返回
            AiAnalysisVO vo = new AiAnalysisVO();
            vo.setClassComment("AI 返回格式异常，原始结果：\n" + aiResult);
            vo.setMainProblems("解析失败：" + e.getMessage());
            return vo;
        }
    }

    /**
     * 构建班级排名
     */
    private List<AiAnalysisVO.StudentRankVO> buildClassRanking(List<ScoreVO> scores) {
        List<AiAnalysisVO.StudentRankVO> rankingList = new ArrayList<>();
        List<ScoreVO> sortedScores = scores.stream()
                .sorted(Comparator.comparingDouble(s -> -s.getScore().doubleValue()))
                .limit(10)
                .collect(Collectors.toList());
        
        int index = 1;
        for (ScoreVO score : sortedScores) {
            AiAnalysisVO.StudentRankVO rankVO = new AiAnalysisVO.StudentRankVO();
            rankVO.setRank(index++);
            rankVO.setStudentId(score.getStudentId());
            rankVO.setStudentName(score.getName());
            rankVO.setStudentCode(score.getStudentCode());
            rankVO.setScore(score.getScore().doubleValue());
            rankingList.add(rankVO);
        }
        
        return rankingList;
    }

    /**
     * 构建考试对比
     */
    private AiAnalysisVO.ExamComparisonVO buildExamComparison(Long examId, String examName, 
                                                              Double currentAverage, Double previousAverage) {
        AiAnalysisVO.ExamComparisonVO comparison = new AiAnalysisVO.ExamComparisonVO();
        comparison.setPreviousExamId(examId - 1);
        comparison.setPreviousExamName("上次考试");
        comparison.setPreviousAverageScore(previousAverage);
        comparison.setCurrentAverageScore(currentAverage);
        comparison.setAverageChange(currentAverage - previousAverage);
        comparison.setTrend(currentAverage > previousAverage ? "进步" : 
                           (currentAverage < previousAverage ? "退步" : "持平"));
        return comparison;
    }

    @Override
    public AiAnalysisVO analyzeStudentScore(Long scoreId, Long studentId) {
        // 1. 获取当前成绩记录
        ScoreVO currentScore = scoreService.getScoreById(scoreId);
        if (currentScore == null) {
            throw new RuntimeException("成绩记录不存在");
        }

        // 2. 获取学生信息
        StudentVO studentVO = studentMapper.selectById(currentScore.getStudentId());
        if (studentVO == null) {
            throw new RuntimeException("学生信息不存在");
        }

        // 3. 获取考试信息
        ExamVO examVO = examMapper.selectById(currentScore.getExamId());
        if (examVO == null) {
            throw new RuntimeException("考试信息不存在");
        }

        // 4. 获取该学生该科目的历史成绩（用于对比）
        List<ScoreVO> historyScores = scoreService.getScoresByStudentId(currentScore.getStudentId());
        ScoreVO previousScore = null;
        if (historyScores != null && !historyScores.isEmpty()) {
            // 找到同科目的上一次考试
            for (ScoreVO score : historyScores) {
                if (!score.getScoreId().equals(scoreId) && 
                    score.getSubjectType() != null && 
                    score.getSubjectType().equals(currentScore.getSubjectType())) {
                    // 按时间排序，取最近的一次
                    if (previousScore == null || 
                        (score.getExamTime() != null && previousScore.getExamTime() != null &&
                         score.getExamTime().isAfter(previousScore.getExamTime()))) {
                        previousScore = score;
                    }
                }
            }
        }

        // 5. 获取班级统计数据（用于对比）
        List<ScoreVO> classScores = scoreService.getScoresByExamId(currentScore.getExamId());
        double classAverage = 0.0;
        int classRank = 0;
        int gradeRank = 0;
        if (classScores != null && !classScores.isEmpty()) {
            classAverage = classScores.stream()
                    .mapToDouble(s -> s.getScore().doubleValue())
                    .average()
                    .orElse(0.0);
        }

        // 6. 判断满分值
        int fullScore = isFullScore150(examVO.getSubjectType()) ? 150 : 100;
        double studentScore = currentScore.getScore().doubleValue();

        // 7. 调用 AI 分析
        String aiResult = qwenAiClient.analyzeStudentScore(
                studentVO.getName(),
                getSubjectName(examVO.getSubjectType()),
                examVO.getExamName(),
                fullScore,
                studentScore,
                classAverage,
                currentScore.getClassRank(),
                currentScore.getGradeRank(),
                previousScore != null ? previousScore.getScore().doubleValue() : null,
                previousScore != null ? previousScore.getExamName() : null
        );

        // 8. 解析 AI 分析结果
        AiAnalysisVO result = parseStudentScoreAnalysis(aiResult);

        // 9. 填充基础数据
        result.setExamId(currentScore.getExamId());
        result.setExamName(examVO.getExamName());
        result.setSubjectType(examVO.getSubjectType());
        result.setSubjectName(getSubjectName(examVO.getSubjectType()));
        result.setGrade(examVO.getGra());
        result.setAverageScore(classAverage);
        result.setMaxScore((double) fullScore);

        return result;
    }

    /**
     * 解析学生成绩分析结果
     */
    private AiAnalysisVO parseStudentScoreAnalysis(String aiResult) {
        try {
            // 清理可能存在的 markdown 格式
            String cleanJson = cleanJsonString(aiResult);

            JsonNode rootNode = objectMapper.readTree(cleanJson);
            AiAnalysisVO vo = new AiAnalysisVO();

            // 解析总体评价
            if (rootNode.has("overallComment")) {
                vo.setClassComment(rootNode.get("overallComment").asText());
            }

            // 解析表现等级
            if (rootNode.has("performanceLevel")) {
                vo.setPerformanceLevel(rootNode.get("performanceLevel").asText());
            }

            // 解析优势
            List<String> advantages = new ArrayList<>();
            if (rootNode.has("advantages") && rootNode.get("advantages").isArray()) {
                for (JsonNode node : rootNode.get("advantages")) {
                    advantages.add(node.asText());
                }
            }
            vo.setAdvantages(advantages);

            // 解析薄弱点
            List<String> weaknesses = new ArrayList<>();
            if (rootNode.has("weaknesses") && rootNode.get("weaknesses").isArray()) {
                for (JsonNode node : rootNode.get("weaknesses")) {
                    weaknesses.add(node.asText());
                }
            }
            vo.setWeaknesses(weaknesses);

            // 解析学习建议
            List<String> suggestions = new ArrayList<>();
            if (rootNode.has("learningSuggestions") && rootNode.get("learningSuggestions").isArray()) {
                for (JsonNode node : rootNode.get("learningSuggestions")) {
                    suggestions.add(node.asText());
                }
            }
            vo.setTeachingSuggestions(suggestions);

            // 解析详细分析
            if (rootNode.has("detailedAnalysis")) {
                vo.setDetailedAnalysis(rootNode.get("detailedAnalysis").asText());
            }

            return vo;
        } catch (Exception e) {
            // 如果解析失败，将原始结果作为 classComment 返回
            AiAnalysisVO vo = new AiAnalysisVO();
            vo.setClassComment(aiResult);
            vo.setDetailedAnalysis("AI 返回格式异常，请检查 API 响应");
            return vo;
        }
    }
}
