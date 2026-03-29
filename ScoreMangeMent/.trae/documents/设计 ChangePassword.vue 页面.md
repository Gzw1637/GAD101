# ChangePassword.vue 页面设计方案

## 1. 创建 ChangePassword.vue 组件

在 `src/views` 目录下创建 `ChangePassword.vue` 文件，包含以下内容：

- 表单结构：原密码、新密码、确认新密码输入框
- 表单验证：原密码不能为空，新密码长度至少6位，两次输入的新密码必须一致
- 提交按钮和取消按钮
- 响应式布局

## 2. 实现密码修改逻辑

- 导入必要的依赖：Vue 3 的 ref、reactive，Element Plus 的 ElMessage，以及 request 工具
- 实现表单验证规则
- 实现密码修改的 API 调用
- 处理成功和失败的回调

## 3. 配置路由

在 `src/router/index.js` 中添加修改密码的路由：

- 教师端路由：`/teacher/change-password`
- 学生端路由：`/student/change-password`

## 4. 更新菜单点击事件

- 在 `Teacher.vue` 中更新 `handleMenuSelect` 函数，添加修改密码的路由跳转
- 确保 `Student.vue` 中的修改密码菜单项能正确跳转到路由

## 5. 样式设计

- 使用 Element Plus 的表单样式
- 添加适当的间距和布局
- 确保页面美观且用户友好

## 6. 测试和优化

- 测试表单验证
- 测试密码修改功能
- 优化用户体验和错误提示

## 技术要点

- 使用 Vue 3 的 Composition API
- 使用 Element Plus 组件库
- 使用 request.js 进行 API 调用
- 实现表单验证和错误处理
- 确保路由配置正确