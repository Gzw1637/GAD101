<template>
  <div class="student-score-content">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>我的成绩查询</span>
        </div>
      </template>

      <div class="search-bar">
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
        <el-select
            v-model="filterSubjectType"
            placeholder="选择科目"
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
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </div>

      <el-table :data="scoreList" style="width: 100%" border v-loading="loading">
        <el-table-column label="序号" width="60">
          <template #default="scope">
            {{ (currentPage - 1) * pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="examName" label="考试名称" width="200" />
        <el-table-column label="科目" width="100">
          <template #default="scope">
            {{ getSubjectName(scope.row.subjectType) }}
          </template>
        </el-table-column>
        <el-table-column prop="score" label="分数" width="100">
          <template #default="scope">
            <span style="font-weight: bold; color: #409EFF">{{ scope.row.score }}</span>
          </template>
        </el-table-column>
        <el-table-column label="年级" width="80">
          <template #default="scope">
            <span>{{ scope.row.gra }}级</span>
          </template>
        </el-table-column>
        <el-table-column label="班级" width="80">
          <template #default="scope">
            <span>{{ scope.row.cla }}班</span>
          </template>
        </el-table-column>
        <el-table-column prop="examTime" label="考试时间" width="180" />
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
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button type="text" @click="handleAnalyze(scope.row)">
              成绩分析
            </el-button>
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
        />
      </div>
    </el-card>

    <!-- 成绩分析弹窗 -->
    <el-dialog
        v-model="analysisDialogVisible"
        title="AI 成绩分析报告"
        width="800px"
        append-to-body
        destroy-on-close
    >
      <div v-if="analyzing" style="text-align:center; padding: 40px">
        <el-icon class="loading-icon" size="48"><Loading /></el-icon>
        <p>正在生成AI分析报告...</p>
      </div>

      <div v-else-if="analysisResult" class="analysis-content">
        <!-- 基本信息卡片 -->
        <el-row :gutter="20" class="mb-20">
          <el-col :span="8">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-item">
                <div class="stat-label">考试</div>
                <div class="stat-value">{{ analysisResult.examName }}</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-item">
                <div class="stat-label">科目</div>
                <div class="stat-value" style="color: #409EFF">{{ analysisResult.subjectName }}</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-item">
                <div class="stat-label">表现等级</div>
                <div class="stat-value" :style="{ color: getPerformanceColor(analysisResult.performanceLevel) }">
                  {{ analysisResult.performanceLevel || '-' }}
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <!-- 成绩对比 -->
        <el-card shadow="hover" class="mb-20">
          <template #header>
            <span>📊 成绩概况</span>
          </template>
          <el-descriptions :column="3" border>
            <el-descriptions-item label="学生得分">
              <span style="font-weight: bold; color: #409EFF; font-size: 18px">
                {{ currentScore?.score }}
              </span>
              <span style="color: #909399; margin-left: 5px">/ {{ analysisResult.maxScore }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="班级平均分">
              {{ analysisResult.averageScore?.toFixed(2) }}
            </el-descriptions-item>
            <el-descriptions-item label="与班级平均差距">
              <el-tag :type="getDiffTagType(getDiffFromAverage())" size="small">
                {{ getDiffFromAverage() > 0 ? '+' : '' }}{{ getDiffFromAverage().toFixed(2) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="班级排名" v-if="currentScore?.classRank">
              第 {{ currentScore.classRank }} 名
            </el-descriptions-item>
            <el-descriptions-item label="年级排名" v-if="currentScore?.gradeRank">
              第 {{ currentScore.gradeRank }} 名
            </el-descriptions-item>
          </el-descriptions>
        </el-card>

        <!-- 总体评价 -->
        <el-card shadow="hover" class="mb-20">
          <template #header>
            <span>💬 总体评价</span>
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

        <!-- 优势与薄弱点 -->
        <el-row :gutter="20" class="mb-20">
          <el-col :span="12">
            <el-card shadow="hover">
              <template #header>
                <span style="color: #67C23A">👍 优势</span>
              </template>
              <ul class="feature-list">
                <li v-for="(item, index) in analysisResult.advantages" :key="index" class="advantage-item">
                  {{ item }}
                </li>
              </ul>
              <el-empty v-if="!analysisResult.advantages || analysisResult.advantages.length === 0" description="暂无数据" />
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card shadow="hover">
              <template #header>
                <span style="color: #F56C6C">⚠️ 薄弱点</span>
              </template>
              <ul class="feature-list">
                <li v-for="(item, index) in analysisResult.weaknesses" :key="index" class="weakness-item">
                  {{ item }}
                </li>
              </ul>
              <el-empty v-if="!analysisResult.weaknesses || analysisResult.weaknesses.length === 0" description="暂无数据" />
            </el-card>
          </el-col>
        </el-row>

        <!-- 学习建议 -->
        <el-card v-if="analysisResult.teachingSuggestions && analysisResult.teachingSuggestions.length > 0" shadow="hover" class="mb-20">
          <template #header>
            <span>💡 学习建议</span>
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

        <!-- 详细分析 -->
        <el-card v-if="analysisResult.detailedAnalysis" shadow="hover">
          <template #header>
            <span>📝 详细分析</span>
          </template>
          <p class="detailed-text">{{ analysisResult.detailedAnalysis }}</p>
        </el-card>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { Search, Loading } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

// 筛选
const filterExamName = ref('')
const filterSubjectType = ref(null)

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 数据
const scoreList = ref([])
const allData = ref([])
const loading = ref(false)

// 弹窗
const analysisDialogVisible = ref(false)
const analyzing = ref(false)
const analysisResult = ref(null)
const currentScore = ref(null)

// 科目
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

const getSubjectName = (type) => {
  const item = subjectOptions.find(s => s.value === type)
  return item ? item.label : '未知'
}

// ====================== 加载成绩列表（只请求一次，不卡死）
const loadAllData = async () => {
  if (allData.value.length) return
  loading.value = true
  try {
    const res = await request.get('/api/score/my')
    if (res.code === 200) {
      allData.value = res.data || []
      doSearch()
    }
  } catch (e) {
    ElMessage.error('加载成绩失败')
  } finally {
    loading.value = false
  }
}

// ====================== 搜索过滤
const doSearch = () => {
  let list = [...allData.value]
  if (filterExamName.value) list = list.filter(i => i.examName?.includes(filterExamName.value))
  if (filterSubjectType.value) list = list.filter(i => i.subjectType === filterSubjectType.value)
  list = [...list].sort((a, b) => (new Date(b.examTime) - new Date(a.examTime)))
  total.value = list.length
  const start = (currentPage.value - 1) * pageSize.value
  scoreList.value = list.slice(start, start + pageSize.value)
}

const handleSearch = () => { currentPage.value = 1; doSearch() }
const resetSearch = () => {
  filterExamName.value = ''
  filterSubjectType.value = null
  currentPage.value = 1
  doSearch()
}

// ====================== 成绩分析
const handleAnalyze = async (row) => {
  analysisDialogVisible.value = true
  analyzing.value = true
  analysisResult.value = null
  currentScore.value = row

  try {
    const res = await request.post(`/api/ai-analysis/student-score/${row.scoreId}`)
    if (res.code === 200) {
      analysisResult.value = res.data
    } else {
      ElMessage.error(res.message || '分析失败')
    }
  } catch (err) {
    ElMessage.error('AI分析失败：' + (err.message || '未知错误'))
  } finally {
    analyzing.value = false
  }
}

// 获取表现等级颜色
const getPerformanceColor = (level) => {
  const colorMap = {
    '优秀': '#67C23A',
    '良好': '#409EFF',
    '及格': '#E6A23C',
    '不及格': '#F56C6C'
  }
  return colorMap[level] || '#909399'
}

// 计算与班级平均分的差距
const getDiffFromAverage = () => {
  if (!currentScore.value || !analysisResult.value) return 0
  const studentScore = parseFloat(currentScore.value.score) || 0
  const classAverage = analysisResult.value.averageScore || 0
  return studentScore - classAverage
}

// 获取差距标签类型
const getDiffTagType = (diff) => {
  if (diff > 0) return 'success'
  if (diff < 0) return 'danger'
  return 'info'
}

// 监听分页
watch([currentPage, pageSize], doSearch)
onMounted(() => loadAllData())
</script>

<style scoped>
.student-score-content { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.search-bar { margin-bottom: 20px; display: flex; flex-wrap: wrap; gap: 10px; }
.pagination { display: flex; justify-content: flex-end; }

/* 分析弹窗样式 */
.analysis-content {
  max-height: 600px;
  overflow-y: auto;
}

.mb-20 {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
}

.stat-item {
  padding: 15px 0;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
}

.ai-comment {
  font-size: 15px;
  line-height: 1.8;
  margin: 10px 0;
}

.feature-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.feature-list li {
  padding: 8px 0;
  border-bottom: 1px solid #EBEEF5;
  font-size: 14px;
  line-height: 1.6;
}

.feature-list li:last-child {
  border-bottom: none;
}

.advantage-item {
  color: #67C23A;
  padding-left: 20px;
  position: relative;
}

.advantage-item::before {
  content: '✓';
  position: absolute;
  left: 0;
  color: #67C23A;
  font-weight: bold;
}

.weakness-item {
  color: #F56C6C;
  padding-left: 20px;
  position: relative;
}

.weakness-item::before {
  content: '!';
  position: absolute;
  left: 0;
  color: #F56C6C;
  font-weight: bold;
}

.detailed-text {
  font-size: 14px;
  line-height: 1.8;
  color: #606266;
  text-indent: 2em;
}

.loading-icon {
  animation: rotate 1s linear infinite;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>