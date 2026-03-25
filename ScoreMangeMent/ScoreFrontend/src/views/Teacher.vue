<template>
  <Layout active-menu="course-list" @menu-select="handleMenuSelect">
    <template #sidebar>
      <el-menu-item index="course-list">
        <template #icon>
          <el-icon><Document /></el-icon>
        </template>
        <span>我的课程</span>
      </el-menu-item>
      <el-menu-item index="score-manage">
        <template #icon>
          <el-icon><DataAnalysis /></el-icon>
        </template>
        <span>成绩管理</span>
      </el-menu-item>
      <el-menu-item index="student-list">
        <template #icon>
          <el-icon><School /></el-icon>
        </template>
        <span>学生列表</span>
      </el-menu-item>
      <el-menu-item index="teaching-plan">
        <template #icon>
          <el-icon><Notebook /></el-icon>
        </template>
        <span>教学计划</span>
      </el-menu-item>
      <el-menu-item index="advice-received">
        <template #icon>
          <el-icon><Message /></el-icon>
        </template>
        <span>收件箱</span>
      </el-menu-item>
      <el-menu-item index="advice-sent">
        <template #icon>
          <el-icon><Promotion /></el-icon>
        </template>
        <span>发件箱</span>
      </el-menu-item>
      <el-menu-item index="personal-info">
        <template #icon>
          <el-icon><UserFilled /></el-icon>
        </template>
        <span>个人信息</span>
      </el-menu-item>
      <el-menu-item index="change-password">
        <template #icon>
          <el-icon><Lock /></el-icon>
        </template>
        <span>修改密码</span>
      </el-menu-item>
    </template>
    
    <div class="teacher-content">
      <RouterView />
    </div>
  </Layout>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { RouterView } from 'vue-router'
import Layout from '@/components/Layout.vue'
import { Document, DataAnalysis, School, Notebook, UserFilled, Lock, Message, Promotion } from '@element-plus/icons-vue'

const router = useRouter()

const handleMenuSelect = (key, keyPath) => {
  // 处理菜单选择
  console.log('Menu selected:', key, keyPath)
  switch (key) {
    case 'course-list':
      router.push('/teacher')
      break
    case 'score-manage':
      router.push('/teacher/score')
      break
    case 'student-list':
      // 学生列表路由
      break
    case 'teaching-plan':
      // 教学计划路由
      break
    case 'advice-received':
      router.push('/teacher/advice/received')
      break
    case 'advice-sent':
      router.push('/teacher/advice/sent')
      break
    case 'personal-info':
      // 个人信息路由
      break
    case 'change-password':
      // 修改密码路由
      break
  }
}
</script>

<style scoped>
.teacher-content {
  padding: 20px;
}
</style>