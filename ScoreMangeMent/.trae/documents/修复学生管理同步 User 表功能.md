## 修复计划

### 问题
添加/修改学生时，只操作了 `zw_student` 表，没有同步操作 `g_user` 表，导致用户名、姓名等信息无法保存。

### 解决方案

#### 1. 修改 Student 实体类
- 添加 `userName`、`name`、`password` 字段（用于接收前端传递的用户信息）

#### 2. 修改 StudentMapper.xml
- 添加 `<insert>` 操作 g_user 表的 SQL 语句
- 添加 `<update>` 操作 g_user 表的 SQL 语句

#### 3. 修改 StudentServiceImpl.java
- 添加 `@Transactional` 注解保证事务一致性
- 在 `addStudent()` 方法中：先插入 g_user 表，获取生成的 userId，再插入 zw_student 表
- 在 `updateStudent()` 方法中：同时更新 g_user 表和 zw_student 表

#### 4. 修改 StudentMapper.java
- 添加操作 User 表的方法声明

### 涉及文件
- `Student.java` - 添加用户信息字段
- `StudentMapper.java` - 添加 User 表操作方法
- `StudentMapper.xml` - 添加 User 表 SQL 语句
- `StudentServiceImpl.java` - 实现事务性操作

### 预期效果
添加/修改学生时，用户名、姓名、密码等信息会同步保存到 g_user 表，学生详细信息保存到 zw_student 表。