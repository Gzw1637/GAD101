# 修复路由配置中的双重 Student 组件问题

## 问题原因
路由配置中，子路由的空路径 `''` 使用了 `component: Student`，导致访问 `/student` 时：
1. 主路由渲染 Student.vue（包含 Layout）
2. 子路由空路径又在 RouterView 中渲染 Student.vue（再次包含 Layout）

## 解决方案
将子路由空路径的 component 改为 Student 本身（保持不变），但确保 Student.vue 中的 RouterView 正确工作。

或者更好的方案：创建一个单独的 StudentHome.vue 作为首页内容。

## 简化方案
保持 Student.vue 作为 Layout 容器，将首页内容放到 Student.vue 的模板中，而不是通过 RouterView 渲染。