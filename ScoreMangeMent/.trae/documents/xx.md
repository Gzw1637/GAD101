## 为教师管理添加导入导出功能

### 需求分析
仿照学生管理功能，为教师管理添加：
1. **导入模板下载** - 下载 Excel 模板
2. **批量导入教师** - 批量导入教师数据
3. **导出数据** - 根据筛选条件导出教师数据（优化样式）

### 实现方案

#### 1. 后端 - 创建教师导入导出 VO 类
- `TeacherImportVO.java` - 用于 Excel 导入
- `TeacherExportVO.java` - 用于 Excel 导出（带样式优化）

#### 2. 后端 - 修改 TeacherService
- 添加 `importTeachers()` 方法
- 添加 `exportTeachers()` 方法

#### 3. 后端 - 修改 TeacherController
- 添加 `/download-template` 接口
- 添加 `/import` 接口
- 添加 `/export` 接口（带样式优化）

#### 4. 前端 - 修改 AdminTeacher.vue
- 添加三个按钮：导入模板下载、批量导入教师、导出数据
- 添加导入对话框
- 实现下载、导入、导出功能

### 样式优化重点
- 表头颜色：使用浅灰色（GREY_25_PERCENT），不要太深
- 列宽：使用 `LongestMatchColumnWidthStyleStrategy` 自动适配内容
- 内容样式：居中对齐，字体适中

### 文件清单
**后端新增：**
- `TeacherImportVO.java`
- `TeacherExportVO.java`

**后端修改：**
- `TeacherService.java`
- `TeacherServiceImpl.java`
- `TeacherController.java`

**前端修改：**
- `AdminTeacher.vue`