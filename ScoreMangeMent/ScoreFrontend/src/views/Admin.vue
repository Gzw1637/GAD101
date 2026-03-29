<template>
  <Layout active-menu="/admin" @menu-select="handleMenuSelect">
    <template #sidebar>
      <el-menu-item index="/admin">
        <template #icon>
          <el-icon><House /></el-icon>
        </template>
        <span>主页</span>
      </el-menu-item>
      <el-menu-item index="/admin/user">
      <template #icon>
        <el-icon><House /></el-icon>
      </template>
      <span>用户管理</span>
    </el-menu-item>
      <el-menu-item index="/admin/student">
        <template #icon>
          <el-icon><UserFilled /></el-icon>
        </template>
        <span>学生管理</span>
      </el-menu-item>
      <el-menu-item index="/admin/teacher">
        <template #icon>
          <el-icon><School /></el-icon>
        </template>
        <span>教师管理</span>
      </el-menu-item>
      <el-menu-item index="/admin/exam">
        <template #icon>
          <el-icon><Document /></el-icon>
        </template>
        <span>考试管理</span>
      </el-menu-item>
      <el-menu-item index="/admin/score">
        <template #icon>
          <el-icon><Document /></el-icon>
        </template>
        <span>成绩管理</span>
      </el-menu-item>
      <el-menu-item index="/admin/subject">
        <template #icon>
          <el-icon><DataAnalysis /></el-icon>
        </template>
        <span>学科查询</span>
      </el-menu-item>
      <el-menu-item index="/admin/advice">
        <template #icon>
          <el-icon><Setting /></el-icon>
        </template>
        <span>意见管理</span>
      </el-menu-item>
    </template>
    
    <!-- 内容会通过 Layout 组件的 router-view 显示 -->
  </Layout>
</template>


<script setup>
import { useRouter } from 'vue-router'
import { RouterView } from 'vue-router'
import Layout from '@/components/Layout.vue'
import { House, UserFilled, School, Document, DataAnalysis, Setting } from '@element-plus/icons-vue'

const router = useRouter()

const handleMenuSelect = (key, keyPath) => {
  console.log('Menu selected:', key, keyPath)
  // 由于我们已经在el-menu-item中设置了正确的路由路径，并且el-menu有router属性，所以这里不需要手动处理路由跳转
  // 只需要处理一些特殊情况，比如退出登录等
}
</script>

<style scoped>
.admin-content {
  padding: 20px;
}
</style>