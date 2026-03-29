<template>
  <Layout :active-menu="currentActiveMenu" @menu-select="handleMenuSelect">
    <template #sidebar>
      <el-menu-item index="/teacher">
        <template #icon>
          <el-icon><House /></el-icon>
        </template>
        <span>首页</span>
      </el-menu-item>
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
      <el-menu-item index="exam-list">
        <template #icon>
          <el-icon><Document /></el-icon>
        </template>
        <span>考试列表</span>
      </el-menu-item>
      <el-menu-item index="analysis">
        <template #icon>
          <el-icon><TrendCharts /></el-icon>
        </template>
        <span>AI 成绩分析</span>
      </el-menu-item>
      <el-menu-item index="paper-upload">
        <template #icon>
          <el-icon><Picture /></el-icon>
        </template>
        <span>试卷识图</span>
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
    </template>

    <!-- 内容会通过 Layout 组件的 router-view 显示 -->
  </Layout>
</template>

<script setup>
import { useRouter, useRoute } from 'vue-router'
import { RouterView } from 'vue-router'
import Layout from '@/components/Layout.vue'
import { Document, DataAnalysis, School, Notebook, UserFilled, Lock, House, TrendCharts, Picture } from '@element-plus/icons-vue'
import { computed } from 'vue'

const router = useRouter()
const route = useRoute()

const currentActiveMenu = computed(() => {
  const path = route.path
  if (path === '/teacher' || path === '/teacher/home') return '/teacher'
  if (path === '/teacher/course') return 'course-list'
  if (path === '/teacher/score') return 'score-manage'
  if (path === '/teacher/exam-list') return 'exam-list'
  if (path === '/teacher/analysis') return 'analysis'
  if (path === '/teacher/paper-upload') return 'paper-upload'
  return '/teacher'
})

const handleMenuSelect = (key, keyPath) => {
  // 处理菜单选择
  console.log('Menu selected:', key, keyPath)
  switch (key) {
    case 'home':
      router.push('/teacher')
      break
    case 'course-list':
      router.push('/teacher/course')
      break
    case 'score-manage':
      router.push('/teacher/score')
      break
    case 'exam-list':
      router.push('/teacher/exam-list')
      break
    case 'analysis':
      router.push('/teacher/analysis')
      break
    case 'paper-upload':
      router.push('/teacher/paper-upload')
      break
    case 'student-list':
      // 学生列表路由
      break
    case 'teaching-plan':
      // 教学计划路由
      break
  }
}
</script>

