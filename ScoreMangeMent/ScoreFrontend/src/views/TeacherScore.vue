<template>
  <div class="teacher-score-content">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>成绩查询</span>
        </div>
      </template>
      
      <div class="search-bar">
        <el-input
          v-model="filterExamName"
          placeholder="输入考试名称搜索"
          style="width: 200px; margin-right: 10px"
          clearable
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-input
          v-model="filterClass"
          placeholder="输入班级搜索"
          style="width: 150px; margin-right: 10px"
          clearable
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-input
          v-model="filterStudentName"
          placeholder="输入学生姓名搜索"
          style="width: 180px; margin-right: 10px"
          clearable
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select
          v-model="sortBy"
          placeholder="排序方式"
          style="width: 150px; margin-right: 10px"
          clearable
        >
          <el-option label="按年级排名升序" value="gradeRankAsc" />
          <el-option label="按年级排名降序" value="gradeRankDesc" />
          <el-option label="按班级排名升序" value="classRankAsc" />
          <el-option label="按班级排名降序" value="classRankDesc" />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
        <el-button type="success" @click="handleExport">导出数据</el-button>
      </div>
      
      <el-table :data="scoreList" style="width: 100%" border v-loading="loading" empty-text="暂无数据，请检查筛选条件或联系管理员">
        <el-table-column label="序号" width="80">
          <template #default="scope">
            {{ (currentPage - 1) * pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="examName" label="考试名称" width="200" />
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
const filterExamName = ref('')
const filterClass = ref('')
const filterStudentName = ref('')
const sortBy = ref('')

// 分页数据
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 成绩列表
const scoreList = ref([])

// 加载状态
const loading = ref(true)



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
      examName: filterExamName.value
    }
    
    const response = await request.get('/api/score/list', { params })
    if (response.code === 200) {
      let data = response.data || []
      
      // 前端过滤
      if (filterClass.value) {
        data = data.filter(score => score.cla.toString().includes(filterClass.value))
      }
      if (filterStudentName.value) {
        data = data.filter(score => score.name.includes(filterStudentName.value))
      }
      
      // 排序处理
      let sortedList = data
      if (sortBy.value) {
        sortedList = data.sort((a, b) => {
          if (sortBy.value === 'gradeRankAsc') {
            const rankA = a.gradeRank || 999999
            const rankB = b.gradeRank || 999999
            return rankA - rankB
          } else if (sortBy.value === 'gradeRankDesc') {
            const rankA = a.gradeRank || 999999
            const rankB = b.gradeRank || 999999
            return rankB - rankA
          } else if (sortBy.value === 'classRankAsc') {
            const rankA = a.classRank || 999999
            const rankB = b.classRank || 999999
            return rankA - rankB
          } else if (sortBy.value === 'classRankDesc') {
            const rankA = a.classRank || 999999
            const rankB = b.classRank || 999999
            return rankB - rankA
          }
          return 0
        })
      } else {
        // 默认按考试时间排序
        sortedList = data.sort((a, b) => {
          const timeA = a.examTime ? new Date(a.examTime).getTime() : 0
          const timeB = b.examTime ? new Date(b.examTime).getTime() : 0
          return timeB - timeA
        })
      }
      
      // 设置总数
      total.value = sortedList.length
      
      // 前端分页
      const startIndex = (currentPage.value - 1) * pageSize.value
      const endIndex = startIndex + pageSize.value
      scoreList.value = sortedList.slice(startIndex, endIndex)
      
      // 如果没有数据，显示提示
      if (sortedList.length === 0) {
        ElMessage.info('未找到符合条件的成绩数据')
      }
    } else {
      // 处理后端返回的错误
      ElMessage.error(response.msg || '获取成绩失败')
    }
  } catch (error) {
    console.error('获取成绩列表失败:', error)
    ElMessage.error('网络错误，请检查网络连接')
  } finally {
    loading.value = false
  }
}

// 导出数据
const handleExport = async () => {
  try {
    // 构建查询参数
    const params = {}
    if (filterExamName.value) params.examName = filterExamName.value
    if (sortBy.value) params.sortBy = sortBy.value
    
    const response = await request.post('/api/teacher/export-scores', null, {
      params,
      responseType: 'blob'
    })
    
    const blob = new Blob([response], { 
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' 
    })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = '教师成绩数据.xlsx'
    link.click()
    window.URL.revokeObjectURL(url)
    
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出数据失败:', error)
    ElMessage.error('导出数据失败')
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  loadScoreList()
}

// 重置搜索
const resetSearch = () => {
  filterExamName.value = ''
  filterClass.value = ''
  filterStudentName.value = ''
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