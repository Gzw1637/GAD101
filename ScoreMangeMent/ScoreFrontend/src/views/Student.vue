<template>
  <Layout active-menu="/student" @menu-select="handleMenuSelect">
    <template #sidebar>
      <el-menu-item index="/student">
        <template #icon>
          <el-icon><House /></el-icon>
        </template>
        <span>首页</span>
      </el-menu-item>
      <el-menu-item index="/student/score">
        <template #icon>
          <el-icon><DataAnalysis /></el-icon>
        </template>
        <span>成绩查询</span>
      </el-menu-item>
      <el-menu-item index="/student/analysis">
        <template #icon>
          <el-icon><Check /></el-icon>
        </template>
        <span>成绩分析</span>
      </el-menu-item>
      <el-sub-menu index="advice">
        <template #title>
          <el-icon><UserFilled /></el-icon>
          <span>意见箱</span>
        </template>
        <el-menu-item index="/student/advice/sent">发件箱</el-menu-item>
        <el-menu-item index="/student/advice/received">收件箱</el-menu-item>
      </el-sub-menu>
      <el-menu-item index="/student/change-password">
        <template #icon>
          <el-icon><Lock /></el-icon>
        </template>
        <span>修改密码</span>
      </el-menu-item>
    </template>
    
    <div class="student-content">
      <RouterView />
    </div>
  </Layout>
</template>

<script setup>
import { ref } from 'vue'
import Layout from '@/components/Layout.vue'
import {Document, DataAnalysis, Check, UserFilled, Lock, House} from '@element-plus/icons-vue'

const searchQuery = ref('')
const total = ref(9)
const currentPage = ref(1)

const courses = ref([
  { courseName: '分布式开发', teacher: '左宸坤', description: '一门课', credit: 6, startTime: '2025-11-01 00:00', endTime: '2025-11-27 00:00' },
  { courseName: 'J2EE高级框架设计', teacher: '左宸坤', description: '一门很重要的课程', credit: 5, startTime: '2025-11-01 00:00', endTime: '2025-11-27 00:00' },
  { courseName: '软件1', teacher: '陈彬', description: '1', credit: 1, startTime: '2025-11-01 00:00', endTime: '2025-11-20 00:00' },
  { courseName: '微服务', teacher: '左宸坤', description: '经典的一门课', credit: 6, startTime: '2025-11-01 00:00', endTime: '2025-11-11 00:00' },
  { courseName: 'J2EE框架', teacher: '左宸坤', description: '很基础的项目框架', credit: 8, startTime: '2025-11-02 00:00', endTime: '2025-11-27 00:00' }
])

const handleMenuSelect = (key, keyPath) => {
  // 处理菜单选择
  console.log('Menu selected:', key, keyPath)
}

const handleView = (row) => {
  // 查看课程详情
  console.log('View course:', row)
}

const handleCurrentChange = (current) => {
  currentPage.value = current
  // 分页查询
  console.log('Current page:', current)
}
</script>

<style scoped>
.student-content {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>