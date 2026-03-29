<template>
  <div class="teacher-analysis-content">
    <el-card shadow="hover" class="mb-20">
      <template #header>
        <div class="card-header">
          <span>📊 AI 成绩分析</span>
          <el-tag type="success" size="small">通义千问支持</el-tag>
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
        <el-table-column label="年级" width="80">
          <template #default="{ row }">
            <span>{{ row.gra }}级</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="负责人" width="100" />
        <el-table-column prop="examLvDesc" label="考试级别" width="100" />
        <el-table-column prop="examTime" label="考试时间" width="150">
          <template #default="{ row }">
            {{ formatDate(row.examTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="analyzeClass(row.examId)"
              :loading="analyzing && currentAnalyzingExamId === row.examId"
            >
              {{ analyzing && currentAnalyzingExamId === row.examId ? '分析中...' : '📈 成绩分析' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="examList.length === 0" description="暂无考试数据" />
    </el-card>

    <!-- AI 分析报告 -->
    <div v-if="analysisResult" class="analysis-result">
      <!-- 基础数据卡片 -->
      <el-row :gutter="20" class="mb-20">
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-item">
              <div class="stat-label">参考人数</div>
              <div class="stat-value">{{ analysisResult.totalStudents }}人</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-item">
              <div class="stat-label">平均分</div>
              <div class="stat-value" style="color: #409EFF">{{ analysisResult.averageScore?.toFixed(2) }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-item">
              <div class="stat-label">及格率</div>
              <div class="stat-value" style="color: #67C23A">{{ analysisResult.passRate?.toFixed(2) }}%</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-item">
              <div class="stat-label">优秀率</div>
              <div class="stat-value" style="color: #E6A23C">{{ analysisResult.excellentRate?.toFixed(2) }}%</div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 考试对比 -->
      <el-card v-if="analysisResult.examComparison" shadow="hover" class="mb-20">
        <template #header>
          <span>📈 考试对比</span>
        </template>
        <el-descriptions :column="3" border>
          <el-descriptions-item label="上次考试">
            {{ analysisResult.examComparison.previousExamName }}
          </el-descriptions-item>
          <el-descriptions-item label="上次平均分">
            {{ analysisResult.examComparison.previousAverageScore?.toFixed(2) }}
          </el-descriptions-item>
          <el-descriptions-item label="变化趋势">
            <el-tag :type="analysisResult.examComparison.trend === '进步' ? 'success' : 'danger'">
              {{ analysisResult.examComparison.trend }}
              {{ analysisResult.examComparison.averageChange > 0 ? '+' : '' }}
              {{ analysisResult.examComparison.averageChange?.toFixed(2) }}分
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- AI 评语 -->
      <el-card shadow="hover" class="mb-20">
        <template #header>
          <span>💬 AI 班级评语</span>
        </template>
        <el-alert
          type="info"
          :closable="false"
          show-icon
        >
          <template #title>
            <p class="ai-comment">{{ analysisResult.classComment }}</p>
          </template>
        </el-alert>
      </el-card>

      <!-- 主要问题 -->
      <el-card v-if="analysisResult.mainProblems" shadow="hover" class="mb-20">
        <template #header>
          <span>⚠️ 主要问题</span>
        </template>
        <p class="problem-text">{{ analysisResult.mainProblems }}</p>
      </el-card>

      <!-- 教学建议 -->
      <el-card v-if="analysisResult.teachingSuggestions && analysisResult.teachingSuggestions.length > 0" shadow="hover" class="mb-20">
        <template #header>
          <span>💡 教学建议</span>
        </template>
        <el-timeline>
          <el-timeline-item
            v-for="(suggestion, index) in analysisResult.teachingSuggestions"
            :key="index"
            :timestamp="`建议${index + 1}`"
            placement="top"
          >
            <el-card>
              <p>{{ suggestion }}</p>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </el-card>

      <!-- 分数分布图表 -->
      <el-card shadow="hover" class="mb-20">
        <template #header>
          <span>📊 分数段分布</span>
        </template>
        <div ref="scoreDistributionChart" style="height: 300px;"></div>
      </el-card>

      <!-- 年级排名 -->
      <el-card shadow="hover">
        <template #header>
          <span>🏆 年级排名（前 10 名）</span>
        </template>
        <el-table :data="analysisResult.classRanking || []" style="width: 100%" border>
          <el-table-column label="排名" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.rank <= 3 ? 'success' : 'primary'">
                第{{ scope.row.rank }}名
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="studentName" label="姓名" width="150" />
          <el-table-column prop="studentCode" label="学号" width="150" />
          <el-table-column label="分数" width="150">
            <template #default="scope">
              <span style="font-weight: bold; color: #409EFF">
                {{ scope.row.score?.toFixed(2) }}
              </span>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>

    <!-- 空状态 -->
    <el-empty v-else description="请选择考试并点击开始分析" />
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import request from '@/utils/request'

const examList = ref([])
const analyzing = ref(false)
const currentAnalyzingExamId = ref(null)
const analysisResult = ref(null)
const scoreDistributionChart = ref(null)
const loading = ref(false)

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

// 分析班级成绩
const analyzeClass = async (examId) => {
  if (!examId) {
    ElMessage.warning('请选择考试')
    return
  }

  analyzing.value = true
  currentAnalyzingExamId.value = examId
  
  try {
    const res = await request.post('/api/ai-analysis/class', null, {
      params: { examId: examId }
    })
    if (res.code === 200) {
      analysisResult.value = res.data
      ElMessage.success('AI 分析完成')
      
      // 滚动到分析结果
      await nextTick()
      document.querySelector('.analysis-result')?.scrollIntoView({ 
        behavior: 'smooth',
        block: 'start'
      })
      
      // 渲染图表
      await nextTick()
      renderScoreDistributionChart()
    } else {
      ElMessage.error(res.message || '分析失败')
    }
  } catch (error) {
    console.error('分析失败:', error)
    ElMessage.error('分析失败：' + (error.message || '未知错误'))
  } finally {
    analyzing.value = false
    currentAnalyzingExamId.value = null
  }
}

// 渲染分数分布图表
const renderScoreDistributionChart = () => {
  if (!scoreDistributionChart.value || !analysisResult.value?.scoreDistribution) return

  const chart = echarts.init(scoreDistributionChart.value)
  const distribution = analysisResult.value.scoreDistribution

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    xAxis: {
      type: 'category',
      data: Object.keys(distribution),
      axisLabel: {
        rotate: 30
      }
    },
    yAxis: {
      type: 'value',
      name: '人数'
    },
    series: [
      {
        name: '学生人数',
        type: 'bar',
        data: Object.values(distribution),
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#83bff6' },
            { offset: 0.5, color: '#188df0' },
            { offset: 1, color: '#188df0' }
          ])
        },
        label: {
          show: true,
          position: 'top'
        }
      }
    ]
  }

  chart.setOption(option)

  // 响应式调整
  window.addEventListener('resize', () => {
    chart.resize()
  })
}

onMounted(() => {
  fetchExamList()
})
</script>

<style scoped>
.teacher-analysis-content {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
}

.mb-20 {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
}

.stat-item {
  padding: 20px 0;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.analysis-result {
  animation: fadeIn 0.5s ease-in;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.ai-comment {
  font-size: 15px;
  line-height: 1.8;
  margin: 10px 0;
}

.problem-text {
  font-size: 14px;
  line-height: 1.8;
  color: #606266;
}
</style>
