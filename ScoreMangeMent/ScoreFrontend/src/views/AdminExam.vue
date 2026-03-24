<template>
  <div class="admin-exam-content">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>考试管理</span>
          <div class="header-buttons">
            <el-button type="success" @click="handleDownloadTemplate">导入模板下载</el-button>
            <el-button type="warning" @click="handleImportDialog">批量导入考试</el-button>
            <el-button type="info" @click="handleExport">导出数据</el-button>
            <el-button type="primary" @click="handleAddExam">添加考试</el-button>
          </div>
        </div>
      </template>
      
      <div class="search-bar">
        <el-input v-model="searchQuery" placeholder="请输入考试名称" style="width: 200px; margin-right: 10px" clearable>
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select v-model="filterGra" placeholder="选择年级" style="width: 120px; margin-right: 10px" clearable>
          <el-option
            v-for="option in gradeOptions"
            :key="option.value"
            :label="option.label"
            :value="option.value"
          />
        </el-select>
        <el-select v-model="filterSubject" placeholder="选择科目" style="width: 100px; margin-right: 10px" clearable>
          <el-option label="语文" value="10111001" />
          <el-option label="数学" value="10111002" />
          <el-option label="英语" value="10111003" />
          <el-option label="物理" value="10111004" />
          <el-option label="历史" value="10111005" />
          <el-option label="化学" value="10111006" />
          <el-option label="生物" value="10111007" />
          <el-option label="地理" value="10111008" />
          <el-option label="政治" value="10111009" />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button type="info" @click="resetSearch">重置</el-button>
      </div>
      
      <el-table :data="examList" style="width: 100%" border v-loading="loading">
        <el-table-column label="序号" width="60">
          <template #default="scope">
            {{ (currentPage - 1) * pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="examCode" label="考试代码" width="120" />
        <el-table-column prop="examName" label="考试名称" width="200" />
        <el-table-column label="年级" width="80">
          <template #default="scope">
            <span>{{ scope.row.gra }}级</span>
          </template>
        </el-table-column>
        <el-table-column label="科目类型" width="100">
          <template #default="scope">
            <span>{{ getSubjectTypeDesc(scope.row.subjectType) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="考试级别" width="100">
          <template #default="scope">
            <span>{{ getExamLvDesc(scope.row.examLv) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="examTime" label="考试时间" width="160">
          <template #default="scope">
            <span>{{ formatDateTime(scope.row.examTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="负责人姓名" width="100" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEditExam(scope.row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDeleteExam(scope.row.examCode)">删除</el-button>
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
    
    <!-- 添加/编辑考试对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '添加考试' : '编辑考试'"
      width="600px"
    >
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="考试代码" prop="examCode">
          <el-input v-model="formData.examCode" placeholder="请输入考试代码" :disabled="dialogType === 'edit'" />
        </el-form-item>
        <el-form-item label="考试名称" prop="examName">
          <el-input v-model="formData.examName" placeholder="请输入考试名称" />
        </el-form-item>
        <el-form-item label="年级" prop="gra">
          <el-select v-model="formData.gra" placeholder="请选择入学年份" @change="handleGradeChange">
            <el-option
              v-for="option in gradeOptions"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="负责人" prop="userId">
          <el-select v-model="formData.userId" placeholder="请先选择科目类型和年级" style="width: 100%">
            <el-option
              v-for="teacher in filteredUserList"
              :key="teacher.userId"
              :label="teacher.name"
              :value="teacher.userId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="科目类型" prop="subjectType">
          <el-select v-model="formData.subjectType" placeholder="请选择科目类型" @change="handleSubjectTypeChange">
            <el-option label="语文" value="10111001" />
            <el-option label="数学" value="10111002" />
            <el-option label="英语" value="10111003" />
            <el-option label="物理" value="10111004" />
            <el-option label="历史" value="10111005" />
            <el-option label="化学" value="10111006" />
            <el-option label="生物" value="10111007" />
            <el-option label="地理" value="10111008" />
            <el-option label="政治" value="10111009" />
          </el-select>
        </el-form-item>
        <el-form-item label="考试级别" prop="examLv">
          <el-select v-model="formData.examLv" placeholder="请选择考试级别">
            <el-option label="D" value="10121001" />
            <el-option label="C" value="10121002" />
            <el-option label="B" value="10121003" />
            <el-option label="A" value="10121004" />
            <el-option label="S" value="10121005" />
          </el-select>
        </el-form-item>
        <el-form-item label="考试时间" prop="examTime">
          <el-date-picker
            v-model="formData.examTime"
            type="datetime"
            placeholder="请选择考试时间"
            style="width: 100%"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
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
      title="批量导入考试"
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
              <el-descriptions-item label="考试代码">
                数字，唯一标识<br/>
                <el-tag size="small" type="info">示例：20240901</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="考试名称">
                考试名称<br/>
                <el-tag size="small" type="info">示例：2024 年秋季期末考试</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="科目">
                语文/数学/英语/物理/化学/生物/地理/政治/历史<br/>
                <el-tag size="small" type="info">示例：数学</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="考试级别">
                D/C/B/A/S<br/>
                <el-tag size="small" type="info">示例：D</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="考试时间">
                格式：yyyy-MM-dd HH:mm:ss<br/>
                <el-tag size="small" type="info">示例：2024-07-15 09:00:00</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="年级">
                入学年份（数字）<br/>
                <el-tag size="small" type="info">示例：2024</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="负责人姓名">
                教师姓名（必须已存在于系统中）<br/>
                <el-tag size="small" type="info">示例：张老师</el-tag>
              </el-descriptions-item>
            </el-descriptions>
            <el-divider />
            <p><strong>注意事项：</strong></p>
            <ul>
              <li>只能上传 .xlsx 或 .xls 格式的 Excel 文件</li>
              <li>文件大小不超过 10MB</li>
              <li>第一行必须是表头（字段名），数据从第二行开始</li>
              <li>负责人姓名必须是系统中已存在的教师用户</li>
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

// 搜索查询
const searchQuery = ref('')
// 筛选条件
const filterGra = ref('')
const filterSubject = ref('')

// 分页数据
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 考试列表
const examList = ref([])

// 所有学科组长列表（原始数据）
const allSubjectLeaders = ref([])

// 加载状态
const loading = ref(true)

// 生成年份选项（当前年份往前推 3 年到当前年）
const currentYear = new Date().getFullYear()
const gradeOptions = ref([])
for (let i = 0; i <= 3; i++) {
  gradeOptions.value.push({
    label: `${currentYear - i}级`,
    value: String(currentYear - i)
  })
}

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)
const formData = reactive({
  examId: '',
  userId: '',
  examCode: '',
  examName: '',
  gra: '',
  subjectType: '',
  examLv: '',
  examTime: ''
})

// 计算属性：根据科目类型和年级过滤后的负责人列表
const filteredUserList = computed(() => {
  if (!formData.subjectType || !formData.gra) {
    return allSubjectLeaders.value
  }
  // 过滤出任教科目和年级都匹配的学科组长
  return allSubjectLeaders.value.filter(
    teacher => 
      teacher.teachSubject === Number(formData.subjectType) &&
      teacher.gra === Number(formData.gra)
  )
})

// 表单验证规则
const rules = {
  examCode: [
    { required: true, message: '请输入考试代码', trigger: 'blur' }
  ],
  examName: [
    { required: true, message: '请输入考试名称', trigger: 'blur' }
  ],
  gra: [
    { required: true, message: '请选择年级', trigger: 'change' }
  ],
  userId: [
    { required: true, message: '请选择负责人', trigger: 'change' }
  ],
  subjectType: [
    { required: true, message: '请选择科目类型', trigger: 'change' }
  ],
  examLv: [
    { required: true, message: '请选择考试级别', trigger: 'change' }
  ],
  examTime: [
    { required: true, message: '请选择考试时间', trigger: 'change' }
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

// 获取考试级别描述
const getExamLvDesc = (examLv) => {
  const lvMap = {
    10121001: 'D',
    10121002: 'C',
    10121003: 'B',
    10121004: 'A',
    10121005: 'S'
  }
  return lvMap[examLv] || '未知'
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

// 获取学科组长列表（只有学科组长才能发布考试）
const loadUserList = async () => {
  try {
    const response = await request.get('/api/teacher/subject-leaders/list')
    if (response.code === 200 && response.data) {
      console.log('获取到的学科组长列表:', response.data)
      allSubjectLeaders.value = response.data
    }
  } catch (error) {
    console.error('获取学科组长列表失败:', error)
  }
}

// 科目类型变化时，清空负责人选择并重置验证
const handleSubjectTypeChange = () => {
  formData.userId = ''
  if (formRef.value) {
    formRef.value.clearValidate('userId')
  }
}

// 年级变化时，清空负责人选择并重置验证
const handleGradeChange = () => {
  formData.userId = ''
  if (formRef.value) {
    formRef.value.clearValidate('userId')
  }
}

// 获取考试列表
const loadExamList = async () => {
  loading.value = true
  try {
    const response = await request.get('/api/exam/list')
    if (response.code === 200 && response.data) {
      console.log('获取到的考试列表:', response.data)
      // 排序
      const sortedList = response.data.sort((a, b) => a.examCode - b.examCode)
      // 设置总数
      total.value = sortedList.length
      // 根据当前页和每页数量进行切片（前端分页）
      const startIndex = (currentPage.value - 1) * pageSize.value
      const endIndex = startIndex + pageSize.value
      examList.value = sortedList.slice(startIndex, endIndex)
    }
  } catch (error) {
    console.error('获取考试列表失败:', error)
    ElMessage.error('获取考试列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索考试（多条件复合查询）
const handleSearch = async () => {
  loading.value = true
  try {
    const response = await request.get('/api/exam/list')
    if (response.code === 200 && response.data) {
      // 前端过滤
      let filteredList = response.data
      
      // 根据考试名称过滤
      if (searchQuery.value) {
        filteredList = filteredList.filter(exam => 
          exam.examName && exam.examName.includes(searchQuery.value)
        )
      }
      
      // 根据年级过滤
      if (filterGra.value) {
        filteredList = filteredList.filter(exam => 
          exam.gra && String(exam.gra) === String(filterGra.value)
        )
      }
      
      // 根据科目过滤
      if (filterSubject.value) {
        filteredList = filteredList.filter(exam => 
          exam.subjectType && String(exam.subjectType) === String(filterSubject.value)
        )
      }
      
      // 排序
      const sortedList = filteredList.sort((a, b) => a.examCode - b.examCode)
      // 设置总数
      total.value = sortedList.length
      // 重置到第一页
      currentPage.value = 1
      // 根据当前页和每页数量进行切片（前端分页）
      const startIndex = (currentPage.value - 1) * pageSize.value
      const endIndex = startIndex + pageSize.value
      examList.value = sortedList.slice(startIndex, endIndex)
      
      if (examList.value.length === 0) {
        ElMessage.info('未找到符合条件的考试')
      }
    }
  } catch (error) {
    console.error('搜索考试失败:', error)
    ElMessage.error('搜索考试失败')
  } finally {
    loading.value = false
  }
}

// 重置搜索
const resetSearch = () => {
  searchQuery.value = ''
  filterGra.value = ''
  filterSubject.value = ''
  currentPage.value = 1
  loadExamList()
}

// 批量导入相关
const importDialogVisible = ref(false)
const importFormRef = ref(null)
const uploadRef = ref(null)
const importing = ref(false)
const importForm = reactive({
  file: null
})

// 下载模板
const handleDownloadTemplate = async () => {
  try {
    const response = await request.get('/api/exam/download-template', {
      responseType: 'blob'
    })
    
    // 创建下载链接
    const blob = new Blob([response], { 
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' 
    })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = '考试导入模板.xlsx'
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
    const response = await request.post('/api/exam/import', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    
    if (response.code === 200) {
      const successCount = response.data
      ElMessage.success(`导入成功 ${successCount} 条记录`)
      importDialogVisible.value = false
      loadExamList()
    } else {
      ElMessage.error(response.message || '导入失败')
    }
  } catch (error) {
    console.error('导入考试失败:', error)
    ElMessage.error('导入失败：' + (error.response?.data?.message || error.message))
  } finally {
    importing.value = false
  }
}

// 导出数据
const handleExport = async () => {
  try {
    // 导出当前筛选条件下的所有数据
    let examsToExport = examList.value
    // 如果是搜索状态，则导出搜索结果
    if (searchQuery.value) {
      const response = await request.get('/api/exam/list')
      if (response.code === 200 && response.data) {
        examsToExport = response.data.filter(exam => 
          exam.examName && exam.examName.includes(searchQuery.value)
        )
      }
    }
    
    const response = await request.post('/api/exam/export', examsToExport, {
      responseType: 'blob'
    })
    
    // 创建下载链接
    const blob = new Blob([response], { 
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' 
    })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = '考试数据.xlsx'
    link.click()
    window.URL.revokeObjectURL(url)
    
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出数据失败:', error)
    ElMessage.error('导出数据失败')
  }
}

// 添加考试
const handleAddExam = () => {
  dialogType.value = 'add'
  if (formRef.value) {
    formRef.value.resetFields()
  }
  Object.assign(formData, {
    examId: '',
    userId: '',
    examCode: '',
    examName: '',
    gra: '',
    subjectType: '',
    examLv: '',
    examTime: ''
  })
  dialogVisible.value = true
}

// 编辑考试
const handleEditExam = (exam) => {
  dialogType.value = 'edit'
  console.log('编辑的考试数据:', exam)
  
  // 先填充科目类型和年级，这样负责人列表会自动过滤
  Object.assign(formData, {
    examId: exam.examId,
    userId: exam.userId,
    examCode: exam.examCode,
    examName: exam.examName,
    gra: exam.gra ? String(exam.gra) : '',
    subjectType: String(exam.subjectType),
    examLv: String(exam.examLv),
    examTime: exam.examTime
  })
  
  console.log('填充后的表单数据:', formData)
  console.log('过滤后的负责人列表:', filteredUserList.value)
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    const submitData = { ...formData }
    
    console.log('提交的考试数据:', submitData)
    
    submitData.examCode = Number(submitData.examCode)
    submitData.userId = Number(submitData.userId)
    submitData.gra = submitData.gra ? Number(submitData.gra) : null
    submitData.subjectType = submitData.subjectType ? Number(submitData.subjectType) : null
    submitData.examLv = submitData.examLv ? Number(submitData.examLv) : null
    
    console.log('转换后的考试数据:', submitData)
    
    if (dialogType.value === 'add') {
      await request.post('/api/exam/add', submitData)
      ElMessage.success('添加考试成功')
    } else {
      await request.put('/api/exam/update', submitData)
      ElMessage.success('编辑考试成功')
    }
    
    dialogVisible.value = false
    loadExamList()
  } catch (error) {
    console.error('提交失败:', error)
    if (error.name === 'Error') {
      ElMessage.error('操作失败，请稍后重试')
    }
  }
}

// 删除考试
const handleDeleteExam = (examCode) => {
  ElMessageBox.confirm('确定要删除这个考试吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const exam = examList.value.find(e => e.examCode === examCode)
      if (exam && exam.examId) {
        await request.delete(`/api/exam/delete/${exam.examId}`)
        ElMessage.success('删除成功')
        loadExamList()
      }
    } catch (error) {
      console.error('删除考试失败:', error)
      ElMessage.error('删除考试失败')
    }
  }).catch(() => {
  })
}

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size
  loadExamList()
}

const handleCurrentChange = (current) => {
  currentPage.value = current
  loadExamList()
}

// 组件挂载时加载数据
onMounted(() => {
  loadUserList()
  loadExamList()
})
</script>

<style scoped>
.admin-exam-content {
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
  flex-wrap: wrap;
  gap: 10px;
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
