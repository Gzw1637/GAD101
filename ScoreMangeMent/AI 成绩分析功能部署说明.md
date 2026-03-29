# AI 成绩分析功能部署说明

## 📋 功能概述

基于**DeepSeek AI**和**百度 OCR**的成绩分析功能，为教师端提供：
1. **班级成绩 AI 分析** - 分析班级整体表现、进步趋势、教学建议
2. **试卷 OCR 识别** - 上传学生试卷，自动识别并分析作答情况
3. **可视化报告** - 生成直观的成绩分析报告

**成本预估**：20-50 元（毕设使用量，DeepSeek 1 元/百万 tokens + 百度 OCR 免费额度）

---

## 🔧 部署步骤

### 第一步：申请 DeepSeek API Key

1. **访问 DeepSeek 官网**
   - 网址：https://www.deepseek.com/
   - 注册账号

2. **获取 API Key**
   - 登录后进入控制台
   - 找到"API Key 管理"
   - 创建并复制 API Key

3. **充值**
   - 建议先充值 50 元（足够完成毕设）
   - DeepSeek 价格：约 1 元/百万 tokens

---

### 第二步：申请百度 OCR API Key

1. **访问百度智能云**
   - 网址：https://console.bce.baidu.com/
   - 注册账号

2. **创建应用**
   - 进入"文字识别 OCR"服务
   - 创建新应用
   - 获取 API Key 和 Secret Key

3. **领取免费额度**
   - 新用户有免费识别额度
   - 足够毕设使用

---

### 第三步：配置环境变量

**Windows 系统**：

1. 右键"此电脑" → "属性" → "高级系统设置"
2. 点击"环境变量"
3. 在"系统变量"中点击"新建"
4. 添加以下变量：

```
变量名：DEEPSEEK_API_KEY
变量值：你的 DeepSeek API Key

变量名：BAIDU_OCR_API_KEY
变量值：你的百度 OCR API Key

变量名：BAIDU_OCR_SECRET_KEY
变量值：你的百度 OCR Secret Key
```

5. 点击"确定"保存

**或者直接在 application.yml 中配置**：

```yaml
ai:
    deepseek:
        api-key: 你的实际 DeepSeek APIKey
    baidu-ocr:
        api-key: 你的实际百度 APIKey
        secret-key: 你的实际百度 SecretKey
```

---

### 第三步：创建数据库表

1. 打开 MySQL 数据库管理工具（如 Navicat、MySQL Workbench）

2. 连接到 `score_management` 数据库

3. 执行 SQL 脚本：
   ```sql
   -- 文件位置：ScoreBackend/src/main/resources/sql/ai_analysis_tables.sql
   ```

4. 确认创建成功：
   - `ai_analysis_record` 表
   - `student_exam_paper` 表

---

### 第四步：启动后端服务

1. **打开项目**
   ```
   cd ScoreBackend
   ```

2. **Maven 构建**
   ```bash
   mvn clean install
   ```

3. **启动服务**
   - 方式 1：IDEA 中运行 `ScoreBackendApplication.java`
   - 方式 2：命令行启动
     ```bash
     mvn spring-boot:run
     ```

4. **验证启动成功**
   - 访问：http://localhost:8080/api/ai-analysis/report/1
   - 查看控制台日志

---

### 第五步：启动前端服务

1. **打开项目**
   ```
   cd ScoreFrontend
   ```

2. **安装依赖**（首次运行）
   ```bash
   npm install
   ```

3. **启动开发服务器**
   ```bash
   npm run dev
   ```

4. **访问系统**
   - 浏览器打开：http://localhost:5173
   - 使用教师账号登录

---

## 📖 使用指南

### 1. 班级成绩 AI 分析

1. 登录教师账号
2. 点击左侧菜单 **"AI 成绩分析"**
3. 选择要分析的考试
4. 点击 **"开始分析"**
5. 等待 AI 生成报告（约 3-5 秒）
6. 查看分析结果：
   - 基础统计数据
   - 考试对比趋势
   - AI 班级评语
   - 主要问题诊断
   - 教学建议
   - 分数分布图表
   - 班级排名

### 2. 试卷 OCR 识别分析

1. 点击左侧菜单 **"试卷识图"**
2. 选择考试和学生
3. 上传学生试卷图片（支持拖拽）
4. 点击 **"上传并分析"**
5. 等待 OCR 识别和 AI 分析（约 5-10 秒）
6. 查看分析结果：
   - 预估分数
   - 优点分析
   - 薄弱点诊断
   - 错误类型归类
   - 学习建议

---

## 🎯 配置说明

### application.yml 配置项

```yaml
ai:
    # DeepSeek AI 配置
    deepseek:
        api-key: ${DEEPSEEK_API_KEY}  # DeepSeek API Key
        base-url: https://api.deepseek.com
        model: deepseek-chat          # 使用 deepseek-chat 模型
    
    # 百度 OCR 配置
    baidu-ocr:
        api-key: ${BAIDU_OCR_API_KEY}      # 百度 API Key
        secret-key: ${BAIDU_OCR_SECRET_KEY} # 百度 Secret Key
        base-url: https://aip.baidubce.com/rest/2.0/ocr/v1

# 文件上传配置
aliyun:
    upload:
        exam-papers-path: C:/uploads/exam-papers  # 试卷图片存储路径
        exam-papers-base-url: /api/exam/papers    # 访问 URL 前缀
```

### 模型选择建议

**DeepSeek 模型**：
- `deepseek-chat`：推荐，性价比高，适合文本分析
- `deepseek-coder`：适合代码相关任务

**百度 OCR 服务**：
- `general_basic`：通用文字识别（已配置）
- `handwriting`：手写体识别（如需更高精度可切换）

---

## 🔍 常见问题

### 1. API Key 无效

**错误信息**：`调用通义千问 API 失败：Invalid API Key`

**解决方案**：
- 检查环境变量是否配置正确
- 确认 API Key 格式正确（`sk-` 开头）
- 重启后端服务

### 2. 数据库表不存在

**错误信息**：`Table 'ai_analysis_record' doesn't exist`

**解决方案**：
- 执行 SQL 脚本创建表
- 确认数据库名称正确

### 3. 文件上传失败

**错误信息**：`上传试卷失败：目录不存在`

**解决方案**：
- 手动创建上传目录：`C:/uploads/exam-papers`
- 确保目录有写入权限

### 4. OCR 识别失败

**错误信息**：`OCR API 调用失败`

**解决方案**：
- 检查 OCR API Key 是否配置
- 确认图片格式正确（jpg/png）
- 检查图片大小（不超过 10MB）

---

## 💰 费用说明

### DeepSeek 价格

| 模型 | 价格 | 说明 |
|------|------|------|
| deepseek-chat | 约 1 元/百万 tokens | 性价比最高，推荐使用 |

### 百度 OCR 价格

| 服务 | 免费额度 | 超出后价格 |
|------|---------|----------|
| 通用文字识别 | 500 次/月 | 0.006 元/次 |

### 月度成本估算

| 使用场景 | 频次 | 月费用 |
|---------|------|--------|
| 班级成绩分析 | 4 个班 × 2 次 | 约 0.1 元 |
| 试卷 OCR 识别 | 50 张 | 0 元（免费额度内） |
| **总计** | - | **约 20-50 元（毕设）** |

**说明**：
- DeepSeek：一次分析约 1000-2000 tokens，费用约 0.001-0.002 元
- 百度 OCR：新用户免费额度 500 次/月，足够毕设使用
- **总成本 20-50 元** 可以完成整个毕设

---

## 📚 API 接口文档

### 1. 班级成绩分析

**接口**：`POST /api/ai-analysis/class`

**参数**：
```json
{
  "examId": 1
}
```

**返回**：
```json
{
  "code": 200,
  "data": {
    "examId": 1,
    "examName": "期中考试",
    "averageScore": 78.5,
    "totalStudents": 50,
    "passRate": 85.0,
    "excellentRate": 32.0,
    "classComment": "班级整体表现良好...",
    "teachingSuggestions": ["建议 1", "建议 2"]
  }
}
```

### 2. 上传试卷

**接口**：`POST /api/ai-analysis/paper/upload`

**参数**：`multipart/form-data`
- `file`: 试卷图片
- `studentId`: 学生 ID
- `examId`: 考试 ID

### 3. 分析试卷

**接口**：`POST /api/ai-analysis/paper/analyze`

**参数**：
```json
{
  "paperId": 1
}
```

---

## 🎯 后续优化建议

1. **性能优化**
   - 添加 Redis 缓存，避免重复分析
   - 异步处理，上传后后台分析

2. **功能扩展**
   - 批量上传试卷
   - 导出 PDF 报告
   - 历史分析对比

3. **体验优化**
   - 添加加载进度提示
   - 优化错误提示
   - 支持更多科目

---

## 📚 技术支持

- DeepSeek 官方文档：https://platform.deepseek.com/api-docs/
- 百度 OCR 文档：https://cloud.baidu.com/doc/OCR/index.html
- DeepSeek API 调用示例：https://help.deepseek.com/api

---

## 🎉 快速开始（5 分钟）

### 1. 获取 API Keys

```bash
# DeepSeek（约 5 分钟）
1. 访问 https://www.deepseek.com/
2. 注册并登录
3. 进入控制台获取 API Key
4. 充值 50 元

# 百度 OCR（约 5 分钟）
1. 访问 https://console.bce.baidu.com/
2. 注册并登录
3. 进入"文字识别 OCR"
4. 创建应用获取 API Key 和 Secret Key
```

### 2. 配置环境变量

```bash
# Windows PowerShell（管理员身份运行）
[System.Environment]::SetEnvironmentVariable("DEEPSEEK_API_KEY", "你的 DeepSeek API Key", "User")
[System.Environment]::SetEnvironmentVariable("BAIDU_OCR_API_KEY", "你的百度 API Key", "User")
[System.Environment]::SetEnvironmentVariable("BAIDU_OCR_SECRET_KEY", "你的百度 Secret Key", "User")
```

### 3. 创建数据库表

```bash
# 打开 MySQL，执行
mysql -u root -p score_management < ScoreBackend/src/main/resources/sql/ai_analysis_tables.sql
```

### 4. 启动项目

```bash
# 后端
cd ScoreBackend
mvn spring-boot:run

# 前端
cd ScoreFrontend
npm run dev
```

### 5. 测试功能

1. 访问 http://localhost:5173
2. 使用教师账号登录
3. 点击"AI 成绩分析"菜单
4. 选择考试，点击"开始分析"

---

## ✅ 完成！

现在你可以：
- ✅ 分析班级成绩（DeepSeek AI）
- ✅ 上传试卷图片（百度 OCR）
- ✅ 查看 AI 分析报告
- ✅ 导出分析结果

**总成本**：20-50 元（DeepSeek 充值 50 元 + 百度 OCR 免费）

**如有问题，请查看控制台日志或联系技术支持。**
