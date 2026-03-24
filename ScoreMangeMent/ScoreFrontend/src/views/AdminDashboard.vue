<template>
  <div class="admin-dashboard">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>管理员控制台</span>
        </div>
      </template>
      
      <div class="dashboard-stats">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-card class="stat-card">
              <div class="stat-content">
                <div class="stat-number">{{ loading ? '加载中...' : stats.studentCount }}</div>
                <div class="stat-label">学生总数</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="stat-card">
              <div class="stat-content">
                <div class="stat-number">{{ loading ? '加载中...' : stats.teacherCount }}</div>
                <div class="stat-label">教师总数</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="stat-card">
              <div class="stat-content">
                <div class="stat-number">{{ loading ? '加载中...' : stats.examCount }}</div>
                <div class="stat-label">考试总数</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="stat-card">
              <div class="stat-content">
                <div class="stat-number">{{ loading ? '加载中...' : stats.scoreCount }}</div>
                <div class="stat-label">成绩记录</div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
      
      <div class="recent-activities">
        <h3 class="section-title">最近活动</h3>
        <el-table :data="recentActivities" style="width: 100%">
          <el-table-column prop="time" label="时间" width="180" />
          <el-table-column prop="user" label="用户" width="120" />
          <el-table-column prop="action" label="操作" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.status === '成功' ? 'success' : 'danger'">
                {{ scope.row.status }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'

const recentActivities = ref([
  { time: '2026-02-28 15:30', user: '张三', action: '添加了新学生', status: '成功' },
  { time: '2026-02-28 14:20', user: '李四', action: '更新了课程信息', status: '成功' },
  { time: '2026-02-28 13:15', user: '王五', action: '删除了学生记录', status: '成功' },
  { time: '2026-02-28 12:00', user: '赵六', action: '导入了成绩数据', status: '失败' },
  { time: '2026-02-28 11:30', user: '孙七', action: '修改了教师信息', status: '成功' }
])

// 统计数据
const stats = ref({
  studentCount: 0,
  teacherCount: 0,
  examCount: 0,
  scoreCount: 0
})

// 加载状态
const loading = ref(true)

// 获取学生总数
const getStudentCount = async () => {
  try {
    const response = await request.get('/api/student/list')
    if (response.code === 200 && response.data) {
      stats.value.studentCount = response.data.length
    }
  } catch (error) {
    console.error('获取学生总数失败:', error)
  }
}

// 获取教师总数
const getTeacherCount = async () => {
  try {
    const response = await request.get('/api/teacher/list')
    if (response.code === 200 && response.data) {
      stats.value.teacherCount = response.data.length
    }
  } catch (error) {
    console.error('获取教师总数失败:', error)
  }
}

// 获取考试总数
const getExamCount = async () => {
  try {
    const response = await request.get('/api/exam/list')
    if (response.code === 200 && response.data) {
      stats.value.examCount = response.data.length
    }
  } catch (error) {
    console.error('获取考试总数失败:', error)
  }
}

// 获取成绩记录数
const getScoreCount = async () => {
  try {
    const response = await request.get('/api/score/list')
    if (response.code === 200 && response.data) {
      stats.value.scoreCount = response.data.length
    }
  } catch (error) {
    console.error('获取成绩记录数失败:', error)
  }
}

// 初始化数据
const initData = async () => {
  loading.value = true
  try {
    await Promise.all([
      getStudentCount(),
      getTeacherCount(),
      getExamCount(),
      getScoreCount()
    ])
  } finally {
    loading.value = false
  }
}

// 组件挂载时初始化数据
onMounted(() => {
  initData()
})
</script>

<style scoped>
.admin-dashboard {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dashboard-stats {
  margin-bottom: 30px;
}

.stat-card {
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.stat-content {
  text-align: center;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #606266;
}

.section-title {
  font-size: 18px;
  font-weight: bold;
  margin: 20px 0;
  color: #303133;
}

.recent-activities {
  margin-top: 30px;
}
</style>