-- AI 成绩分析功能数据库表
-- 创建时间：2026-03-25

-- 1. AI 分析记录表
CREATE TABLE IF NOT EXISTS `ai_analysis_record` (
  `ANALYSIS_ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分析记录 ID',
  `EXAM_ID` bigint(20) NOT NULL COMMENT '考试 ID',
  `TEACHER_ID` bigint(20) NOT NULL COMMENT '教师 ID',
  `ANALYSIS_TYPE` int(11) NOT NULL COMMENT '分析类型：1=成绩趋势，2=试卷识图，3=综合报告',
  `ANALYSIS_CONTENT` text COMMENT 'AI 生成的分析内容（JSON 格式）',
  `STUDENT_ID` bigint(20) DEFAULT NULL COMMENT '学生 ID（试卷识图分析时用）',
  `IMAGE_URL` varchar(500) DEFAULT NULL COMMENT '试卷图片 URL（识图分析时用）',
  `CREATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`ANALYSIS_ID`),
  KEY `idx_exam_id` (`EXAM_ID`),
  KEY `idx_teacher_id` (`TEACHER_ID`),
  KEY `idx_student_id` (`STUDENT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 分析记录表';

-- 2. 学生试卷表
CREATE TABLE IF NOT EXISTS `student_exam_paper` (
  `PAPER_ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '试卷记录 ID',
  `STUDENT_ID` bigint(20) NOT NULL COMMENT '学生 ID',
  `EXAM_ID` bigint(20) NOT NULL COMMENT '考试 ID',
  `PAPER_IMAGE_URL` varchar(500) NOT NULL COMMENT '试卷图片存储路径',
  `OCR_RESULT` text COMMENT 'OCR 识别结果',
  `AI_ANALYSIS` text COMMENT 'AI 分析结果',
  `UPLOAD_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  PRIMARY KEY (`PAPER_ID`),
  KEY `idx_student_id` (`STUDENT_ID`),
  KEY `idx_exam_id` (`EXAM_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生试卷表';

-- 说明：
-- 1. ai_analysis_record 表用于存储 AI 分析的历史记录
--    - ANALYSIS_TYPE: 1=班级成绩分析，2=试卷识图分析，3=综合报告
--    - ANALYSIS_CONTENT: 存储 AI 生成的 JSON 格式分析结果
--
-- 2. student_exam_paper 表用于存储学生试卷图片和识别结果
--    - PAPER_IMAGE_URL: 存储试卷图片的访问路径
--    - OCR_RESULT: 存储 OCR 识别的原始文本
--    - AI_ANALYSIS: 存储 AI 对试卷的分析结果
--
-- 使用步骤：
-- 1. 在 MySQL 数据库中执行此 SQL 脚本
-- 2. 确保数据库为 score_management
-- 3. 执行完成后重启后端服务
