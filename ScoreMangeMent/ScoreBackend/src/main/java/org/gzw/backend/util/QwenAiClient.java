package org.gzw.backend.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * DeepSeek AI 客户端工具类
 * 使用 HTTP 方式调用 DeepSeek API
 */
@Component
public class QwenAiClient {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ai.deepseek.api-key}")
    private String apiKey;

    @Value("${ai.deepseek.base-url}")
    private String baseUrl;

    @Value("${ai.deepseek.model}")
    private String model;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 调用 DeepSeek API 生成文本
     *
     * @param prompt 提示词
     * @return AI 生成的文本
     */
    public String generateText(String prompt) {
        try {
            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            
            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);
            messages.add(message);
            
            requestBody.put("messages", messages);
            requestBody.put("stream", false);
            requestBody.put("temperature", 0.7);

            // 创建 HTTP 请求
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            String url = baseUrl + "/v1/chat/completions";

            // 发送请求
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                // 解析响应
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                JsonNode choicesNode = rootNode.path("choices");
                
                if (choicesNode.isArray() && choicesNode.size() > 0) {
                    return choicesNode.get(0).path("message").path("content").asText();
                }
            }
            
            throw new RuntimeException("DeepSeek API 调用失败，状态码：" + response.getStatusCode());
        } catch (Exception e) {
            throw new RuntimeException("调用 DeepSeek API 失败：" + e.getMessage(), e);
        }
    }

    /**
     * 生成班级成绩分析报告
     */
    public String analyzeClassPerformance(String examName, String subjectName, Integer grade, Integer className,
                                         Double averageScore, Double maxScore, Double minScore,
                                         Double passRate, Double excellentRate, String scoreDistribution,
                                         Double previousAverage) {
        // 判断满分值
        int fullScore = isFullScore150(subjectName) ? 150 : 100;
        
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一位经验丰富的中学教师，请根据以下班级成绩数据进行分析：\n\n");
        prompt.append("【考试信息】\n");
        prompt.append("考试名称：").append(examName).append("\n");
        prompt.append("科目：").append(subjectName).append("\n");
        prompt.append("满分：").append(fullScore).append(" 分\n");
        prompt.append("年级：").append(grade).append(" 年级").append(className).append(" 班\n\n");

        prompt.append("【成绩数据】\n");
        prompt.append("平均分：").append(String.format("%.2f", averageScore)).append("\n");
        prompt.append("最高分：").append(String.format("%.2f", maxScore)).append("\n");
        prompt.append("最低分：").append(String.format("%.2f", minScore)).append("\n");
        prompt.append("及格率：").append(String.format("%.2f", passRate)).append("%\n");
        prompt.append("优秀率：").append(String.format("%.2f", excellentRate)).append("%\n\n");

        prompt.append("【分数段分布】\n");
        prompt.append(scoreDistribution).append("\n\n");

        if (previousAverage != null) {
            double change = averageScore - previousAverage;
            String trend = change > 0 ? "进步" : (change < 0 ? "退步" : "持平");
            prompt.append("【历次考试对比】\n");
            prompt.append("上次考试平均分：").append(String.format("%.2f", previousAverage)).append("\n");
            prompt.append("变化：").append(String.format("%.2f", change)).append(" 分（").append(trend).append("）\n\n");
        }

        prompt.append("请生成一份分析报告，返回 JSON 格式，包含以下字段：\n");
        prompt.append("{\n");
        prompt.append("  \"classComment\": \"班级整体表现评价（100 字以内）\",\n");
        prompt.append("  \"mainProblems\": \"指出主要问题和薄弱环节\",\n");
        prompt.append("  \"teachingSuggestions\": [\"建议 1\", \"建议 2\", \"建议 3\"],\n");
        prompt.append("  \"trend\": \"进步/退步/持平\"\n");
        prompt.append("}\n\n");
        prompt.append("重要提示：\n");
        prompt.append("1. 本考试满分为 ").append(fullScore).append(" 分，请根据满分值合理评价成绩水平\n");
        if (fullScore == 150) {
            prompt.append("2. 150 分制评分标准：优秀≥120 分，良好≥105 分，及格≥90 分\n");
            prompt.append("3. 评价时不要使用 100 分制的标准（如 90 分以上为优秀）\n");
        } else {
            prompt.append("2. 100 分制评分标准：优秀≥80 分，良好≥70 分，及格≥60 分\n");
        }
        prompt.append("3. 平均分评价示例：150 分制下，平均分 120 分以上为优秀，90-120 为良好\n\n");
        prompt.append("要求：语言简洁、专业，适合教师阅读。直接返回 JSON，不要其他内容。");

        return generateText(prompt.toString());
    }
    
    /**
     * 判断科目是否为 150 分制（按科目名称）
     */
    private boolean isFullScore150(String subjectName) {
        if (subjectName == null) {
            return false;
        }
        return subjectName.contains("语文") || 
               subjectName.contains("数学") || 
               subjectName.contains("英语");
    }
    
    /**
     * 判断科目是否为 150 分制（按科目编码）
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
     * 分析学生试卷作答情况
     */
    public String analyzeStudentPaper(String studentName, String subjectName, Integer subjectType, String ocrContent) {
        // 判断满分值
        int fullScore = isFullScore150(subjectType) ? 150 : 100;
        
        // 科目编码映射说明
        String subjectCodeMap = "科目编码说明：10111001=语文 (150 分), 10111002=数学 (150 分), 10111003=英语 (150 分), " +
                               "10111004=物理 (100 分), 10111005=历史 (100 分), 10111006=化学 (100 分), " +
                               "10111007=生物 (100 分), 10111008=地理 (100 分), 10111009=政治 (100 分)";
        
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一位经验丰富的中学教师，请根据以下学生答题卡的 OCR 识别结果进行分析。\n\n");
        prompt.append("【学生信息】\n");
        prompt.append("姓名：").append(studentName).append("\n");
        prompt.append("科目：").append(subjectName).append("（满分").append(fullScore).append(" 分）\n");
        prompt.append(subjectCodeMap).append("\n\n");

        prompt.append("【答题卡 OCR 识别内容】\n");
        prompt.append(ocrContent).append("\n\n");

        prompt.append("【任务要求】\n");
        prompt.append("1. 从 OCR 内容中准确提取各题得分，计算总分\n");
        prompt.append("2. 分析学生答题情况\n");
        prompt.append("3. 生成详细分析报告\n\n");

        prompt.append("【评分标准】\n");
        if (fullScore == 150) {
            prompt.append("- 满分：150 分\n");
            prompt.append("- 优秀：120-150 分（80% 及以上）\n");
            prompt.append("- 良好：105-119 分（70%-79%）\n");
            prompt.append("- 及格：90-104 分（60%-69%）\n");
            prompt.append("- 不及格：90 分以下\n");
        } else {
            prompt.append("- 满分：100 分\n");
            prompt.append("- 优秀：80-100 分（80% 及以上）\n");
            prompt.append("- 良好：70-79 分（70%-79%）\n");
            prompt.append("- 及格：60-69 分（60%-69%）\n");
            prompt.append("- 不及格：60 分以下\n");
        }
        
        prompt.append("\n【重要提示】\n");
        prompt.append("1. 必须从 OCR 识别内容中提取实际的得分，不要凭空猜测分数\n");
        prompt.append("2. 如果 OCR 内容中已经包含各题得分或总分，请直接使用这些分数，不要重新计算\n");
        prompt.append("3. 如果 OCR 内容中包含得分点（如'+5'、'-3'、'得分：X'等），请累加计算总分\n");
        prompt.append("4. 如果无法从 OCR 内容中准确计算分数，请说明原因并给出预估分数范围\n");
        prompt.append("5. 注意识别选择题答案（如 A、B、C、D）和非选择题的得分点\n");
        prompt.append("6. 全部使用中文回复，不要出现任何英文\n");
        prompt.append("7. 分数计算要准确，这是分析的核心依据\n");
        prompt.append("8. 如果 OCR 识别结果中包含'总分'、'客观题得分'、'主观题得分'等字段，请优先采用\n\n");
        
        prompt.append("【返回格式】（严格按照以下 JSON 格式，不要包含 markdown 符号）\n");
        prompt.append("{\n");
        prompt.append("  \"totalScore\": 实际计算的总分（数字类型）,\n");
        prompt.append("  \"scoreBreakdown\": \"各题得分明细，如：选择题 60 分，填空题 20 分，解答题 64 分\",\n");
        prompt.append("  \"advantages\": [\"优点 1\", \"优点 2\", \"优点 3\"],\n");
        prompt.append("  \"weaknesses\": [\"薄弱点 1\", \"薄弱点 2\", \"薄弱点 3\"],\n");
        prompt.append("  \"errorTypes\": [\"错误类型 1\", \"错误类型 2\"],\n");
        prompt.append("  \"learningSuggestions\": [\"建议 1\", \"建议 2\", \"建议 3\"],\n");
        prompt.append("  \"performanceLevel\": \"优秀/良好/及格/不及格\",\n");
        prompt.append("  \"detailedAnalysis\": \"详细的文字分析报告，100-200 字\"\n");
        prompt.append("}\n\n");
        
        prompt.append("【示例】\n");
        prompt.append("{\n");
        prompt.append("  \"totalScore\": 144,\n");
        prompt.append("  \"scoreBreakdown\": \"选择题 45 分，填空题 18 分，解答题 81 分\",\n");
        prompt.append("  \"advantages\": [\"基础知识扎实\", \"解题思路清晰\", \"计算准确\"],\n");
        prompt.append("  \"weaknesses\": [\"压轴题得分不高\", \"时间分配不合理\"],\n");
        prompt.append("  \"errorTypes\": [\"计算失误\", \"审题不清\"],\n");
        prompt.append("  \"learningSuggestions\": [\"加强压轴题训练\", \"进行限时训练\", \"注重审题\"],\n");
        prompt.append("  \"performanceLevel\": \"优秀\",\n");
        prompt.append("  \"detailedAnalysis\": \"该生本次考试表现优秀，总分 144 分。基础知识掌握扎实，选择题和填空题几乎全对。解答题步骤清晰，计算准确。主要不足在于压轴题得分不高，建议加强难题训练。\"\n");
        prompt.append("}");

        return generateText(prompt.toString());
    }

    /**
     * 分析学生个人成绩
     */
    public String analyzeStudentScore(String studentName, String subjectName, String examName,
                                     int fullScore, double studentScore, double classAverage,
                                     Integer classRank, Integer gradeRank,
                                     Double previousScore, String previousExamName) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一位经验丰富的中学教师，请根据以下学生成绩数据进行分析：\n\n");

        prompt.append("【学生信息】\n");
        prompt.append("姓名：").append(studentName).append("\n");
        prompt.append("科目：").append(subjectName).append("\n");
        prompt.append("考试：").append(examName).append("\n\n");

        prompt.append("【成绩数据】\n");
        prompt.append("满分：").append(fullScore).append(" 分\n");
        prompt.append("学生得分：").append(String.format("%.2f", studentScore)).append(" 分\n");
        prompt.append("班级平均分：").append(String.format("%.2f", classAverage)).append(" 分\n");
        if (classRank != null) {
            prompt.append("班级排名：第 ").append(classRank).append(" 名\n");
        }
        if (gradeRank != null) {
            prompt.append("年级排名：第 ").append(gradeRank).append(" 名\n");
        }

        // 计算与班级平均分的差距
        double diffFromAverage = studentScore - classAverage;
        prompt.append("与班级平均分差距：").append(String.format("%.2f", diffFromAverage)).append(" 分\n");

        if (previousScore != null && previousExamName != null) {
            prompt.append("\n【历史对比】\n");
            prompt.append("上次考试：").append(previousExamName).append("\n");
            prompt.append("上次得分：").append(String.format("%.2f", previousScore)).append(" 分\n");
            double progress = studentScore - previousScore;
            String trend = progress > 0 ? "进步" : (progress < 0 ? "退步" : "持平");
            prompt.append("变化：").append(String.format("%.2f", Math.abs(progress))).append(" 分（").append(trend).append("）\n");
        }

        prompt.append("\n【评分标准】\n");
        if (fullScore == 150) {
            prompt.append("- 满分：150 分\n");
            prompt.append("- 优秀：120-150 分（80% 及以上）\n");
            prompt.append("- 良好：105-119 分（70%-79%）\n");
            prompt.append("- 及格：90-104 分（60%-69%）\n");
            prompt.append("- 不及格：90 分以下\n");
        } else {
            prompt.append("- 满分：100 分\n");
            prompt.append("- 优秀：80-100 分（80% 及以上）\n");
            prompt.append("- 良好：70-79 分（70%-79%）\n");
            prompt.append("- 及格：60-69 分（60%-69%）\n");
            prompt.append("- 不及格：60 分以下\n");
        }

        prompt.append("\n请生成一份学生个人成绩分析报告，返回 JSON 格式，包含以下字段：\n");
        prompt.append("{\n");
        prompt.append("  \"overallComment\": \"总体评价，50字以内\",\n");
        prompt.append("  \"performanceLevel\": \"优秀/良好/及格/不及格\",\n");
        prompt.append("  \"advantages\": [\"优势1\", \"优势2\"],\n");
        prompt.append("  \"weaknesses\": [\"薄弱点1\", \"薄弱点2\"],\n");
        prompt.append("  \"learningSuggestions\": [\"建议1\", \"建议2\", \"建议3\"],\n");
        prompt.append("  \"detailedAnalysis\": \"详细分析，100-150字，包含与班级平均分的对比、排名分析、进步/退步分析等\"\n");
        prompt.append("}\n\n");

        prompt.append("重要提示：\n");
        prompt.append("1. 本考试满分为 ").append(fullScore).append(" 分，请根据满分值合理评价成绩水平\n");
        if (fullScore == 150) {
            prompt.append("2. 150 分制评价标准：120 分以上为优秀，105-119 为良好，90-104 为及格\n");
        } else {
            prompt.append("2. 100 分制评价标准：80 分以上为优秀，70-79 为良好，60-69 为及格\n");
        }
        prompt.append("3. 分析要结合班级平均分、排名等数据进行综合评价\n");
        prompt.append("4. 如果有历史成绩对比，要分析进步或退步的原因\n");
        prompt.append("5. 语言要亲切、鼓励性，适合学生和家长阅读\n\n");
        prompt.append("要求：直接返回 JSON，不要其他内容。");

        return generateText(prompt.toString());
    }
}
