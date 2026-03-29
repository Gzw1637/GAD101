<template>
  <div class="paper-upload-content">
    <el-card shadow="hover" class="mb-20">
      <template #header>
        <div class="card-header">
          <span>📷 试卷上传与分析</span>
          <el-tag type="warning" size="small">OCR 识别 + AI 分析</el-tag>
        </div>
      </template>

      <el-form :model="uploadForm" label-width="120px">
        <el-form-item label="考试名称">
          <el-input
            v-model="uploadForm.examName"
            placeholder="请输入考试名称（如：期中考试）"
            style="width: 300px"
            clearable
            @change="handleExamSearch"
          >
            <template #prefix>
              <el-icon><search /></el-icon>
            </template>
            <template #append>
              <el-button @click="handleExamSearch">搜索</el-button>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="科目">
          <el-select v-model="uploadForm.subjectType" placeholder="请选择科目" style="width: 200px">
            <el-option label="语文" :value="10111001" />
            <el-option label="数学" :value="10111002" />
            <el-option label="英语" :value="10111003" />
            <el-option label="物理" :value="10111004" />
            <el-option label="历史" :value="10111005" />
            <el-option label="化学" :value="10111006" />
            <el-option label="生物" :value="10111007" />
            <el-option label="地理" :value="10111008" />
            <el-option label="政治" :value="10111009" />
          </el-select>
        </el-form-item>

        <el-form-item label="学生学号">
          <el-input
            v-model="uploadForm.studentCode"
            placeholder="请输入学生学号（如：ST2026010001）"
            style="width: 300px"
            clearable
            @blur="handleStudentSearch"
            @keyup.enter="handleStudentSearch"
          >
            <template #prefix>
              <el-icon><user /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="学生姓名">
          <el-input
            v-model="studentNameDisplay"
            placeholder="输入学号验证后自动显示"
            style="width: 300px"
            disabled
          >
            <template #prefix>
              <el-icon v-if="studentVerified"><circle-check /></el-icon>
              <el-icon v-else><lock /></el-icon>
            </template>
            <template #append>
              <el-tag :type="studentVerified ? 'success' : 'info'" size="small">
                {{ studentVerified ? '验证成功' : '已锁定' }}
              </el-tag>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="上传试卷">
          <el-upload
            ref="uploadRef"
            drag
            :auto-upload="false"
            :on-change="handleFileChange"
            :on-remove="handleFileRemove"
            :limit="1"
            accept="image/*"
            class="upload-demo"
          >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">
              拖拽文件到此处或<em>点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                支持 jpg/png 格式，文件大小不超过 10MB
              </div>
            </template>
          </el-upload>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleUpload" :loading="uploading">
            {{ uploading ? '上传分析中...' : '上传并分析' }}
          </el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 分析结果 -->
    <div v-if="analysisResult" class="analysis-result">
      <el-card shadow="hover" class="mb-20">
        <template #header>
          <div class="score-header">
            <span>📊 AI 试卷分析结果</span>
            <el-tag :type="getScoreTag(analysisResult.maxScore)" size="large">
              总分：{{ analysisResult.maxScore?.toFixed(1) }} / {{ fullScore }}
            </el-tag>
          </div>
        </template>
        
        <!-- 得分明细 -->
        <el-descriptions :column="2" border v-if="analysisResult.classComment">
          <el-descriptions-item label="得分明细" :span="2">
            {{ analysisResult.classComment }}
          </el-descriptions-item>
        </el-descriptions>
        
        <!-- 表现等级 -->
        <el-alert
          v-if="analysisResult.mainProblems"
          :title="analysisResult.mainProblems"
          :type="getAlertType(analysisResult.mainProblems)"
          show-icon
          class="mt-20"
        />
      </el-card>
      
      <!-- 优点 -->
      <el-card v-if="analysisResult.advantages && analysisResult.advantages.length > 0" shadow="hover" class="mb-20">
        <template #header>
          <span>✅ 优点</span>
        </template>
        <el-timeline>
          <el-timeline-item
            v-for="(advantage, index) in analysisResult.advantages"
            :key="index"
            :timestamp="`优点${index + 1}`"
            placement="top"
          >
            <el-card>
              <p>{{ advantage }}</p>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </el-card>
      
      <!-- 薄弱点 -->
      <el-card v-if="analysisResult.weaknesses && analysisResult.weaknesses.length > 0" shadow="hover" class="mb-20">
        <template #header>
          <span>⚠️ 薄弱点</span>
        </template>
        <el-timeline>
          <el-timeline-item
            v-for="(weakness, index) in analysisResult.weaknesses"
            :key="index"
            :timestamp="`薄弱点${index + 1}`"
            placement="top"
          >
            <el-card>
              <p>{{ weakness }}</p>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </el-card>
      
      <!-- 错误类型 -->
      <el-card v-if="analysisResult.errorTypes && analysisResult.errorTypes.length > 0" shadow="hover" class="mb-20">
        <template #header>
          <span>❌ 错误类型</span>
        </template>
        <el-space wrap>
          <el-tag
            v-for="(errorType, index) in analysisResult.errorTypes"
            :key="index"
            type="danger"
            effect="plain"
          >
            {{ errorType }}
          </el-tag>
        </el-space>
      </el-card>
      
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
        <el-alert
          type="info"
          :closable="false"
          show-icon
        >
          <template #title>
            <p class="detailed-text">{{ analysisResult.detailedAnalysis }}</p>
          </template>
        </el-alert>
      </el-card>
      
      <!-- 原始 AI 分析结果（调试用） -->
      <!-- 
      <el-card shadow="hover" class="mt-20">
        <template #header>
          <span>🔍 调试信息</span>
        </template>
        <pre>{{ analysisResult }}</pre>
      </el-card>
      -->
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { UploadFilled, Search, User, Lock, CircleCheck } from '@element-plus/icons-vue'
import request from '@/utils/request'

const route = useRoute()
const uploadRef = ref(null)
const uploading = ref(false)
const selectedFile = ref(null)
const analysisResult = ref(null)
const studentVerified = ref(false)
const studentNameDisplay = ref('')
const fullScore = ref(150) // 默认满分值

const uploadForm = ref({
  examName: '',
  subjectType: null,
  studentCode: '',
  examId: null,
  studentId: null
})

// 根据科目设置满分值
const setFullScore = (subjectType) => {
  // 语文、数学、英语为 150 分，其他为 100 分
  if (subjectType === 10111001 || subjectType === 10111002 || subjectType === 10111003) {
    fullScore.value = 150
  } else {
    fullScore.value = 100
  }
}

// 根据分数获取标签类型
const getScoreTag = (score) => {
  if (!score) return 'info'
  const rate = score / fullScore.value
  if (rate >= 0.8) return 'success'
  if (rate >= 0.7) return 'warning'
  if (rate >= 0.6) return 'primary'
  return 'danger'
}

// 根据表现等级获取警告框类型
const getAlertType = (mainProblems) => {
  if (!mainProblems) return 'info'
  if (mainProblems.includes('优秀')) return 'success'
  if (mainProblems.includes('良好')) return 'warning'
  if (mainProblems.includes('及格')) return 'primary'
  return 'error'
}

// 处理 URL 参数
const handleUrlParams = () => {
  const { examId, examName, subjectType, subjectName } = route.query
  
  if (examId && examName && subjectType) {
    uploadForm.value.examId = examId
    uploadForm.value.examName = examName
    uploadForm.value.subjectType = parseInt(subjectType)
    
    ElMessage.success(`已选择考试：${examName}（${subjectName || '科目'}）`)
  }
}

// 搜索考试
const searchExam = async () => {
  if (!uploadForm.value.examName || !uploadForm.value.subjectType) {
    return
  }
  
  try {
    const res = await request.get('/api/exam/search', {
      params: {
        examName: uploadForm.value.examName,
        subjectType: uploadForm.value.subjectType
      }
    })
    if (res.code === 200 && res.data && res.data.length > 0) {
      uploadForm.value.examId = res.data[0].examId
      ElMessage.success(`找到考试：${res.data[0].examName}`)
    } else {
      uploadForm.value.examId = null
      ElMessage.warning('未找到匹配的考试，请检查输入')
    }
  } catch (error) {
    console.error('搜索考试失败:', error)
    ElMessage.error('搜索考试失败：' + (error.message || '未知错误'))
  }
}

// 搜索学生（失去焦点或按回车时触发）
const handleStudentSearch = async () => {
  if (!uploadForm.value.studentCode) {
    studentVerified.value = false
    studentNameDisplay.value = ''
    return
  }
  
  try {
    const res = await request.get('/api/student/search-by-student-code', {
      params: { studentCode: uploadForm.value.studentCode }
    })
    if (res.code === 200 && res.data) {
      // 验证成功
      uploadForm.value.studentId = res.data.studentId
      studentNameDisplay.value = res.data.name
      studentVerified.value = true
      
      ElMessage.success(`验证成功：${res.data.name}（学号：${res.data.studentCode}）`)
    } else {
      // 未找到学生
      studentVerified.value = false
      studentNameDisplay.value = ''
      uploadForm.value.studentId = null
      ElMessage.error('未找到该学生')
    }
  } catch (error) {
    console.error('搜索学生失败:', error)
    studentVerified.value = false
    studentNameDisplay.value = ''
    uploadForm.value.studentId = null
    ElMessage.error('验证失败：' + (error.message || '未知错误'))
  }
}

// 处理考试搜索（手动触发）
const handleExamSearch = () => {
  if (uploadForm.value.examName && uploadForm.value.subjectType) {
    searchExam()
  }
}

// 处理文件变化
const handleFileChange = (file) => {
  selectedFile.value = file.raw
}

// 处理文件移除
const handleFileRemove = () => {
  selectedFile.value = null
}

// 上传并分析
const handleUpload = async () => {
  if (!uploadForm.value.examName || !uploadForm.value.subjectType) {
    ElMessage.warning('请输入考试名称并选择科目')
    return
  }
  if (!uploadForm.value.studentCode) {
    ElMessage.warning('请输入学生学号')
    return
  }
  if (!uploadForm.value.examId) {
    ElMessage.warning('请先搜索考试')
    return
  }
  if (!uploadForm.value.studentId) {
    ElMessage.warning('未找到该学生')
    return
  }
  if (!selectedFile.value) {
    ElMessage.warning('请选择试卷文件')
    return
  }

  uploading.value = true
  try {
    const formData = new FormData()
    formData.append('file', selectedFile.value)
    formData.append('examId', uploadForm.value.examId)
    formData.append('studentId', uploadForm.value.studentId)

    const res = await request.post('/api/ai-analysis/paper/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })

    if (res.code === 200) {
      ElMessage.success('上传成功，正在分析...')
      
      // 获取分析结果
      const paperId = res.data.paperId
      setTimeout(async () => {
        const analysisRes = await request.post('/api/ai-analysis/paper/analyze', null, {
          params: { paperId }
        })
        if (analysisRes.code === 200) {
          analysisResult.value = analysisRes.data
          // 根据科目设置满分值
          setFullScore(uploadForm.value.subjectType)
          ElMessage.success('AI 分析完成')
        }
      }, 3000)
    } else {
      ElMessage.error(res.message || '上传失败')
    }
  } catch (error) {
    console.error('上传失败:', error)
    ElMessage.error('上传失败：' + (error.message || '未知错误'))
  } finally {
    uploading.value = false
  }
}

// 重置表单
const resetForm = () => {
  uploadForm.value = {
    examName: '',
    subjectType: null,
    studentCode: '',
    examId: null,
    studentId: null
  }
  studentVerified.value = false
  studentNameDisplay.value = ''
  selectedFile.value = null
  analysisResult.value = null
  if (uploadRef.value) {
    uploadRef.value.clearFiles()
  }
}

// 初始化
onMounted(() => {
  handleUrlParams()
})
</script>

<style scoped>
.paper-upload-content {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
}

.score-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.mb-20 {
  margin-bottom: 20px;
}

.mt-20 {
  margin-top: 20px;
}

.detailed-text {
  line-height: 1.8;
  font-size: 15px;
}

.upload-demo {
  width: 100%;
}

.analysis-result {
  margin-top: 20px;
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

.analysis-report {
  padding: 10px 0;
}

.report-content {
  line-height: 2;
  font-size: 15px;
  color: #303133;
}

.report-content strong {
  color: #409EFF;
  font-weight: 600;
}
</style>
