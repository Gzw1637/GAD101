## 修复方案

修改文件：[ScoreServiceImpl.java](file:///c:\Users\26868\Desktop\ScoreMangeMent\ScoreBackend\src\main\java\org\gzw\backend\service\Impl\ScoreServiceImpl.java)

**第 142-143 行**，将：
```java
List<ScoreVO> existingScores = scoreMapper.selectByStudentIdAndFilters(
    student.getStudentId(), exam.getExamId(), exam.getSubjectType());
```

修改为：
```java
List<ScoreVO> existingScores = scoreMapper.selectByStudentIdAndFilters(
    student.getStudentId(), exam.getExamId(), exam.getSubjectType(), null);
```

**原因**：添加第 4 个参数 `null`，因为这里检查已存在成绩时不需要按考试名称模糊查询。