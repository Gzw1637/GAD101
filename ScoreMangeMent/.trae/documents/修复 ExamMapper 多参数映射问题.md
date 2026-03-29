## 修复计划

### 问题分析
1. `selectByUserGradeAndSubjects` 方法 XML 中有多余的 `parameterType="map"`
2. `searchByExamNameAndSubject` 方法也有两个参数，但缺少 `@Param` 注解

### 需要修改的文件

**1. ExamMapper.java**
- 为 `searchByExamNameAndSubject` 方法添加 `@Param` 注解

**2. ExamMapper.xml**
- 移除 `selectByUserGradeAndSubjects` 的 `parameterType="map"` 属性
- 移除 `searchByExamNameAndSubject` 的 `parameterType` 属性 (如果有)

### 修改后你需要做的
1. 在 IDEA 中执行 `Maven → Reimport`
2. 执行 `Build → Rebuild Project`
3. 重新启动应用测试