<template>
  <div class="exam-list-content">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>📋 考试列表</span>
          <el-tag type="primary" size="small">点击右侧按钮进行试卷分析</el-tag>
        </div>
      </template>

      <el-table :data="examList" style="width: 100%" border v-loading="loading">
        <el-table-column prop="examName" label="考试名称" min-width="180" />
        <el-table-column prop="subjectTypeDesc" label="科目" width="100">
          <template #default="{ row }">
            <el-tag :type="getSubjectTagType(row.subjectType)">
              {{ row.subjectTypeDesc }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="examLvDesc" label="考试级别" width="100" />
        <el-table-column prop="examTime" label="考试时间" width="120">
          <template #default="{ row }">
            {{ formatDate(row.examTime) }}
          </template>
        </el-table-column>
        <el-table-column label="年级" width="100">
          <template #default="{ row }">
            <span>{{ row.gra }}级</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="负责人" width="120" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="handlePaperAnalysis(row)"
            >
              📷 试卷分析
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="examList.length === 0" description="暂无考试数据" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()
const loading = ref(false)
const examList = ref([])

// 获取科目标签颜色
const getSubjectTagType = (subjectType) => {
  const tagMap = {
    10111001: '', // 语文
    10111002: 'success', // 数学
    10111003: 'primary', // 英语
    10111004: 'warning', // 物理
    10111005: 'danger', // 历史
    10111006: 'info', // 化学
    10111007: 'success', // 生物
    10111008: 'warning', // 地理
    10111009: 'danger' // 政治
  }
  return tagMap[subjectType] || ''
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

// 获取考试列表
const fetchExamList = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/exam/list')
    if (res.code === 200) {
      examList.value = res.data || []
    } else {
      ElMessage.error('获取考试列表失败')
    }
  } catch (error) {
    console.error('获取考试列表失败:', error)
    ElMessage.error('获取考试列表失败：' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 处理试卷分析按钮点击
const handlePaperAnalysis = (exam) => {
  router.push({
    path: '/teacher/paper-upload',
    query: {
      examId: exam.examId,
      examName: exam.examName,
      subjectType: exam.subjectType,
      subjectName: exam.subjectTypeDesc
    }
  })
}

onMounted(() => {
  fetchExamList()
})
</script>

<style scoped>
.exam-list-content {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
}

:deep(.el-table) {
  font-size: 14px;
}

:deep(.el-table th) {
  background-color: #f5f7fa;
  color: #606266;
  font-weight: 600;
}
</style>
