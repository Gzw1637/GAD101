<template>
  <Layout :active-menu="currentActiveMenu" @menu-select="handleMenuSelect">
    <template #sidebar>
      <el-menu-item index="home">
        <template #icon>
          <el-icon><House /></el-icon>
        </template>
        <span>首页</span>
      </el-menu-item>
      <el-menu-item index="score">
        <template #icon>
          <el-icon><DataAnalysis /></el-icon>
        </template>
        <span>成绩查询</span>
      </el-menu-item>
      <!-- 成绩分析菜单暂时注释，因为对应的组件不存在 -->
      <!-- <el-menu-item index="analysis">
        <template #icon>
          <el-icon><Check /></el-icon>
        </template>
        <span>成绩分析</span>
      </el-menu-item> -->
      <el-sub-menu index="advice">
        <template #title>
          <el-icon><UserFilled /></el-icon>
          <span>意见箱</span>
        </template>
        <el-menu-item index="sent">发件箱</el-menu-item>
        <el-menu-item index="received">收件箱</el-menu-item>
      </el-sub-menu>
    </template>
    
    <router-view />
  </Layout>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Layout from '@/components/Layout.vue'
import { House, DataAnalysis, UserFilled } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const currentActiveMenu = computed(() => {
  const path = route.path
  if (path === '/student' || path === '/student/home') return 'home'
  if (path === '/student/score') return 'score'
  if (path === '/student/analysis') return 'analysis'
  if (path === '/student/advice/sent') return 'sent'
  if (path === '/student/advice/received') return 'received'
  return 'home'
})

const handleMenuSelect = (key, keyPath) => {
  console.log('Menu selected:', key, keyPath)
  if (key === 'home') {
    router.push('/student')
  } else if (key === 'score') {
    router.push('/student/score')
  } else if (key === 'analysis') {
    router.push('/student/analysis')
  } else if (key === 'sent') {
    router.push('/student/advice/sent')
  } else if (key === 'received') {
    router.push('/student/advice/received')
  }
}
</script>