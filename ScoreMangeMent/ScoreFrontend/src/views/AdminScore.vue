<template>
  <div class="admin-score-content">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>成绩管理</span>
          <div class="header-buttons">
            <el-button type="success" @click="handleDownloadTemplate">导入模板下载</el-button>
            <el-button type="warning" @click="handleImportDialog">批量导入成绩</el-button>
            <el-button type="info" @click="handleExport">导出数据</el-button>
            <el-button type="primary" @click="handleAddScore">添加成绩</el-button>
          </div>
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
        <el-table-column label="科目类型" width="100">
          <template #default="scope">
            <span>{{ getSubjectTypeDesc(scope.row.subjectType) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="studentCode" label="学号" width="120" />
        <el-table-column prop="name" label="姓名" width="100" />
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
        <el-table-column prop="score" label="分数" width="80">
          <template #default="scope">
            <span style="font-weight: bold; color: #409EFF">{{ scope.row.score }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEditScore(scope.row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDeleteScore(scope.row.scoreId)">删除</el-button>
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
    
    <!-- 添加/编辑成绩对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '添加成绩' : '编辑成绩'"
      width="700px"
    >
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="考试" prop="examId">
          <el-select v-model="formData.examId" placeholder="请选择考试" style="width: 100%" :disabled="dialogType === 'edit'">
            <el-option
              v-for="exam in examList"
              :key="exam.examId"
              :label="exam.examName + ' - ' + getSubjectTypeDesc(exam.subjectType)"
              :value="exam.examId"
            />
          </el-select>
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="年级" prop="gra">
              <el-select v-model="formData.gra" placeholder="请选择年级" style="width: 100%" @change="handleGradeChange" :disabled="dialogType === 'edit'">
                <el-option
                  v-for="option in gradeOptions"
                  :key="option.value"
                  :label="option.label"
                  :value="option.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="班级" prop="cla">
              <el-select v-model="formData.cla" placeholder="请先选择年级" style="width: 100%" @change="handleClassChange" :disabled="dialogType === 'edit'">
                <el-option
                  v-for="classOption in classOptions"
                  :key="classOption.value"
                  :label="classOption.label"
                  :value="classOption.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学号" prop="studentCode">
              <el-select v-model="formData.studentCode" placeholder="请先选择班级" style="width: 100%" @change="handleStudentCodeChange" :disabled="dialogType === 'edit'">
                <el-option
                  v-for="student in filteredStudentsByClass"
                  :key="student.studentCode"
                  :label="student.studentCode"
                  :value="student.studentCode"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="studentName">
              <el-input v-model="formData.studentName" placeholder="自动显示" readonly style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="分数" prop="score">
          <el-input-number v-model="formData.score" :min="0" :max="150" :precision="1" :step="0.5" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 批量导入对话框 -->
    <el-dialog
      v-model="importDialogVisible"
      title="批量导入成绩"
      width="700px"
    >
      <el-alert
        title="填写说明"
        type="info"
        :closable="false"
        show-icon
        style="margin-bottom: 20px"
      >
        <template #default>
          <div class="import-instructions">
            <p><strong>必填字段及填写要求：</strong></p>
            <el-descriptions :column="2" border size="small">
              <el-descriptions-item label="考试名称">
                必须与系统中已存在的考试名称一致<br/>
                <el-tag size="small" type="info">示例：2019-2020 第一学期第一次月考</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="科目">
                语文/数学/英语/物理/化学/生物/地理/政治/历史<br/>
                <el-tag size="small" type="info">示例：数学</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="学号">
                数字，学生的唯一标识<br/>
                <el-tag size="small" type="info">示例：2019010001</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="姓名">
                学生姓名（必须与学号匹配）<br/>
                <el-tag size="small" type="info">示例：郭振伟</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="分数">
                数字，支持 1 位小数<br/>
                <el-tag size="small" type="info">示例：90.5</el-tag>
              </el-descriptions-item>
            </el-descriptions>
            <el-divider />
            <p><strong>注意事项：</strong></p>
            <ul>
              <li>只能上传 .xlsx 或 .xls 格式的 Excel 文件</li>
              <li>文件大小不超过 10MB</li>
              <li>第一行必须是表头（字段名），数据从第二行开始</li>
              <li>考试名称和科目必须与系统中已存在的考试匹配</li>
              <li>学号和姓名必须与系统中已存在的学生匹配</li>
              <li>如果成绩已存在则会更新，不存在则新增</li>
              <li>建议先下载模板，按照模板格式填写</li>
            </ul>
          </div>
        </template>
      </el-alert>
      
      <el-form :model="importForm" ref="importFormRef" label-width="100px">
        <el-form-item label="Excel 文件" required>
          <el-upload
            ref="uploadRef"
            action="#"
            :auto-upload="false"
            :limit="1"
            :on-change="handleFileChange"
            accept=".xlsx,.xls"
          >
            <el-button type="primary">选择文件</el-button>
            <template #tip>
              <div class="el-upload__tip">
                只能上传 xlsx/xls 文件，且不超过 10MB
              </div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="importDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleImportSubmit" :loading="importing">
            开始导入
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, computed } from 'vue'
import { Search } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

// 筛选条件
const filterExamName = ref('')
const filterSubjectType = ref(null)

// 分页数据
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 成绩列表
const scoreList = ref([])

// 考试列表
const examList = ref([])

// 学生列表
const studentList = ref([])

// 加载状态
const loading = ref(true)

// 科目选项
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

// 批量导入相关
const importDialogVisible = ref(false)
const importFormRef = ref(null)
const uploadRef = ref(null)
const importing = ref(false)
const importForm = reactive({
  file: null
})

// 年级选项（根据学生数据动态生成）
const gradeOptions = computed(() => {
  const grades = [...new Set(studentList.value
    .map(s => s.gra)
    .filter(g => g !== null && g !== undefined && g !== '')
  )].sort((a, b) => b - a) // 降序排列，新的年级在前
  return grades.map(g => ({
    label: `${g}级`,
    value: String(g)
  }))
})

// 班级选项（根据所选年级动态生成）
const classOptions = computed(() => {
  if (!formData.gra) return []
  const classes = [...new Set(
    studentList.value
      .filter(s => String(s.gra) === String(formData.gra))
      .map(s => s.cla)
      .filter(c => c !== null && c !== undefined && c !== '')
  )].sort((a, b) => a - b)
  return classes.map(c => ({
    label: `${c}班`,
    value: String(c)
  }))
})

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)
const formData = reactive({
  scoreId: '',
  examId: '',
  studentId: '',
  gra: '',
  cla: '',
  studentCode: '',
  studentName: '',
  score: 0
})

// 计算属性：根据年级和班级过滤后的学生列表
const filteredStudentsByClass = computed(() => {
  if (!formData.gra || !formData.cla) {
    return []
  }
  return studentList.value.filter(
    student => String(student.gra) === String(formData.gra) && String(student.cla) === String(formData.cla)
  )
})

// 表单验证规则
const rules = {
  examId: [
    { required: true, message: '请选择考试', trigger: 'change' }
  ],
  gra: [
    { required: true, message: '请选择年级', trigger: 'change' }
  ],
  cla: [
    { required: true, message: '请选择班级', trigger: 'change' }
  ],
  studentCode: [
    { required: true, message: '请选择学号', trigger: 'change' }
  ],
  score: [
    { required: true, message: '请输入分数', trigger: 'blur' }
  ]
}

// 获取科目类型描述
const getSubjectTypeDesc = (subjectType) => {
  const subjectMap = {
    10111001: '语文',
    10111002: '数学',
    10111003: '英语',
    10111004: '物理',
    10111005: '化学',
    10111006: '生物',
    10111007: '政治',
    10111008: '历史',
    10111009: '地理'
  }
  return subjectMap[subjectType] || '未知'
}

// 获取考试列表
const loadExamList = async () => {
  try {
    const response = await request.get('/api/exam/list')
    if (response.code === 200 && response.data) {
      examList.value = response.data
    }
  } catch (error) {
    console.error('获取考试列表失败:', error)
  }
}

// 获取学生列表
const loadStudentList = async () => {
  try {
    const response = await request.get('/api/student/list')
    if (response.code === 200 && response.data) {
      studentList.value = response.data
    }
  } catch (error) {
    console.error('获取学生列表失败:', error)
  }
}

// 获取成绩列表
const loadScoreList = async () => {
  loading.value = true
  try {
    const params = {}
    if (filterExamName.value) {
      params.examName = filterExamName.value
    }
    if (filterSubjectType.value) {
      params.subjectType = filterSubjectType.value
    }
    
    const response = await request.get('/api/score/list', { params })
    
    if (response.code === 200 && response.data) {
      // 排序（按学号）
      const sortedList = response.data.sort((a, b) => a.studentCode - b.studentCode)
      // 设置总数
      total.value = sortedList.length
      // 根据当前页和每页数量进行切片（前端分页）
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
  filterExamName.value = ''
  filterSubjectType.value = null
  currentPage.value = 1
  loadScoreList()
}

// 年级变化时，清空班级和学号选择
const handleGradeChange = () => {
  formData.cla = ''
  formData.studentCode = ''
  formData.studentId = ''
  formData.studentName = ''
  if (formRef.value) {
    formRef.value.clearValidate(['cla', 'studentCode'])
  }
}

// 班级变化时，清空学号选择
const handleClassChange = () => {
  formData.studentCode = ''
  formData.studentId = ''
  formData.studentName = ''
  if (formRef.value) {
    formRef.value.clearValidate('studentCode')
  }
}

// 学号变化时，自动填充学生 ID 和姓名
const handleStudentCodeChange = (studentCode) => {
  const student = filteredStudentsByClass.value.find(s => s.studentCode === studentCode)
  if (student) {
    formData.studentId = student.studentId
    formData.studentName = student.name
  }
}

// 添加成绩
const handleAddScore = () => {
  dialogType.value = 'add'
  if (formRef.value) {
    formRef.value.resetFields()
  }
  Object.assign(formData, {
    scoreId: '',
    examId: '',
    studentId: '',
    gra: '',
    cla: '',
    studentCode: '',
    studentName: '',
    score: 0
  })
  dialogVisible.value = true
}

// 编辑成绩
const handleEditScore = (score) => {
  dialogType.value = 'edit'
  
  // 查找对应的学生信息
  const student = studentList.value.find(s => s.studentId === score.studentId)
  if (student) {
    Object.assign(formData, {
      scoreId: score.scoreId,
      examId: score.examId,
      studentId: score.studentId,
      gra: student.gra ? String(student.gra) : '',
      cla: student.cla ? String(student.cla) : '',
      studentCode: student.studentCode ? String(student.studentCode) : '',
      studentName: student.name || '',
      score: score.score
    })
  }
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    const submitData = { ...formData }
    submitData.examId = Number(submitData.examId)
    submitData.studentId = Number(submitData.studentId)
    submitData.score = Number(submitData.score)
    
    console.log('提交的成绩数据:', submitData)
    
    if (dialogType.value === 'add') {
      await request.post('/api/score/add', submitData)
      ElMessage.success('添加成绩成功')
    } else {
      await request.put('/api/score/update', submitData)
      ElMessage.success('编辑成绩成功')
    }
    
    dialogVisible.value = false
    loadScoreList()
  } catch (error) {
    console.error('提交失败:', error)
    if (error.name === 'Error') {
      ElMessage.error('操作失败，请稍后重试')
    }
  }
}

// 删除成绩
const handleDeleteScore = (scoreId) => {
  ElMessageBox.confirm('确定要删除这个成绩吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request.delete(`/api/score/delete/${scoreId}`)
      ElMessage.success('删除成功')
      loadScoreList()
    } catch (error) {
      console.error('删除成绩失败:', error)
      ElMessage.error('删除成绩失败')
    }
  }).catch(() => {
  })
}

// 下载模板
const handleDownloadTemplate = async () => {
  try {
    const response = await request.get('/api/score/download-template', {
      responseType: 'blob'
    })
    
    const blob = new Blob([response], { 
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' 
    })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = '成绩导入模板.xlsx'
    link.click()
    window.URL.revokeObjectURL(url)
    
    ElMessage.success('模板下载成功')
  } catch (error) {
    console.error('下载模板失败:', error)
    ElMessage.error('下载模板失败')
  }
}

// 打开导入对话框
const handleImportDialog = () => {
  importDialogVisible.value = true
  importForm.file = null
  if (uploadRef.value) {
    uploadRef.value.clearFiles()
  }
}

// 文件选择变化
const handleFileChange = (file) => {
  importForm.file = file.raw
}

// 提交导入
const handleImportSubmit = async () => {
  if (!importForm.file) {
    ElMessage.warning('请选择要导入的 Excel 文件')
    return
  }
  
  importing.value = true
  const formData = new FormData()
  formData.append('file', importForm.file)
  
  try {
    const response = await request.post('/api/score/import', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    
    if (response.code === 200) {
      const successCount = response.data
      ElMessage.success(`导入成功 ${successCount} 条记录`)
      importDialogVisible.value = false
      loadScoreList()
    } else {
      ElMessage.error(response.message || '导入失败')
    }
  } catch (error) {
    console.error('导入成绩失败:', error)
    ElMessage.error('导入失败：' + (error.response?.data?.message || error.message))
  } finally {
    importing.value = false
  }
}

// 导出数据
const handleExport = async () => {
  try {
    // 构建查询参数
    const params = {}
    if (filterExamName.value) params.examName = filterExamName.value
    if (filterSubjectType.value) params.subjectType = filterSubjectType.value
    
    const response = await request.post('/api/score/export', null, {
      params,
      responseType: 'blob'
    })
    
    const blob = new Blob([response], { 
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' 
    })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = '成绩数据.xlsx'
    link.click()
    window.URL.revokeObjectURL(url)
    
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出数据失败:', error)
    ElMessage.error('导出数据失败')
  }
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
onMounted(() => {
  loadExamList()
  loadStudentList()
  loadScoreList()
})
</script>

<style scoped>
.admin-score-content {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-buttons {
  display: flex;
  gap: 10px;
}

.import-instructions {
  font-size: 13px;
  line-height: 1.6;
}

.import-instructions p {
  margin: 10px 0;
}

.import-instructions ul {
  margin: 10px 0;
  padding-left: 20px;
}

.import-instructions ul li {
  margin: 5px 0;
}

.search-bar {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}

.pagination {
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
