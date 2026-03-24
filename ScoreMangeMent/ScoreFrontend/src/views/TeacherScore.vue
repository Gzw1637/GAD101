<template>
  <div class="teacher-score-content">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>成绩查询</span>
        </div>
      </template>
      
      <div class="search-bar">
        <el-select
          v-model="filterGra"
          placeholder="选择年级"
          style="width: 120px; margin-right: 10px"
          clearable
        >
          <el-option
            v-for="grade in gradeOptions"
            :key="grade.value"
            :label="grade.label"
            :value="grade.value"
          />
        </el-select>
        <el-select
          v-model="filterSubjectType"
          placeholder="选择学科"
          style="width: 150px; margin-right: 10px"
          clearable
        >
          <el-option
            v-for="subject in subjectOptions"
            :key="subject.value"
            :label="subject.label"
            :value="subject.value"
          />
        </el-select>
        <el-input
          v-model="filterExamName"
          placeholder="输入考试名称搜索"
          style="width: 220px; margin-right: 10px"
          clearable
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </div>
      
      <el-table :data="scoreList" style="width: 100%" border v-loading="loading">
        <el-table-column label="序号" width="80">
          <template #default="scope">
            {{ (currentPage - 1) * pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="name" label="学生姓名" width="120" />
        <el-table-column prop="studentCode" label="学号" width="150" />
        <el-table-column label="班级" width="100">
          <template #default="scope">
            <span>{{ scope.row.cla }}班</span>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="分数" width="100">
          <template #default="scope">
            <span style="font-weight: bold; color: #409EFF">{{ scope.row.score }}</span>
          </template>
        </el-table-column>
        <el-table-column label="班级排名" width="100">
          <template #default="scope">
            <span style="font-weight: bold; color: #F56C6C">
              {{ scope.row.classRank || '-' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="年级排名" width="100">
          <template #default="scope">
            <span style="font-weight: bold; color: #E6A23C">
              {{ scope.row.gradeRank || '-' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="examName" label="考试名称" width="200" />
        <el-table-column prop="examTime" label="考试时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.examTime) }}
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination" style="margin-top: 20px">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

// 筛选条件
const filterGra = ref(null)
const filterSubjectType = ref(null)
const filterExamName = ref('')

// 分页数据
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 成绩列表
const scoreList = ref([])

// 加载状态
const loading = ref(true)

// 年级选项
const gradeOptions = [
  { value: 2025, label: '2025级' },
  { value: 2026, label: '2026级' },
  { value: 2027, label: '2027级' }
]

// 学科选项
const subjectOptions = [
  { value: 10111001, label: '语文' },
  { value: 10111002, label: '数学' },
  { value: 10111003, label: '英语' },
  { value: 10111004, label: '物理' },
  { value: 10111005, label: '历史' },
  { value: 10111006, label: '化学' },
  { value: 10111007, label: '生物' },
  { value: 10111008, label: '地理' },
  { value: 10111009, label: '政治' }
]

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return d.toLocaleString('zh-CN', { 
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

// 获取成绩列表
const loadScoreList = async () => {
  loading.value = true
  try {
    const params = {
      subjectType: filterSubjectType.value,
      examName: filterExamName.value
    }
    
    const response = await request.get('/api/score/list', { params })
    if (response.code === 200) {
      let data = response.data || []
      
      // 如果选择了年级，进行前端过滤
      if (filterGra.value) {
        data = data.filter(score => score.gra === filterGra.value)
      }
      
      // 按考试时间排序
      const sortedList = data.sort((a, b) => {
        const timeA = a.examTime ? new Date(a.examTime).getTime() : 0
        const timeB = b.examTime ? new Date(b.examTime).getTime() : 0
        return timeB - timeA
      })
      
      // 设置总数
      total.value = sortedList.length
      
      // 前端分页
      const startIndex = (currentPage.value - 1) * pageSize.value
      const endIndex = startIndex + pageSize.value
      scoreList.value = sortedList.slice(startIndex, endIndex)
    }
  } catch (error) {
    console.error('获取成绩列表失败:', error)
    ElMessage.error('获取成绩列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  loadScoreList()
}

// 重置搜索
const resetSearch = () => {
  filterGra.value = null
  filterSubjectType.value = null
  filterExamName.value = ''
  currentPage.value = 1
  loadScoreList()
}

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size
  loadScoreList()
}

const handleCurrentChange = (current) => {
  currentPage.value = current
  loadScoreList()
}

// 组件挂载时加载数据
onMounted(async () => {
  await loadScoreList()
})
</script>

<style scoped>
.teacher-score-content {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-bar {
  margin-bottom: 20px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
}
</style>