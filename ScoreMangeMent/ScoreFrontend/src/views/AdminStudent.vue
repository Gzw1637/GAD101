<template>
  <div class="admin-student-content">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>学生管理</span>
          <div class="header-buttons">
            <el-button type="success" @click="handleDownloadTemplate">导入模板下载</el-button>
            <el-button type="warning" @click="handleImportDialog">批量导入学生</el-button>
            <el-button type="info" @click="handleExport">导出数据</el-button>
            <el-button type="primary" @click="handleAddStudent">添加学生</el-button>
          </div>
        </div>
      </template>
      
      <div class="search-bar">
        <el-input
          v-model="searchQuery"
          placeholder="请输入姓名"
          style="width: 200px; margin-right: 10px"
          clearable
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select
          v-model="filterGra"
          placeholder="选择年级"
          style="width: 140px; margin-right: 10px"
          clearable
        >
          <el-option
            v-for="item in graOptions"
            :key="item"
            :label="item + '级'"
            :value="String(item)"
          />
        </el-select>
        <el-select
          v-model="filterCla"
          placeholder="选择班级"
          style="width: 120px; margin-right: 10px"
          clearable
        >
          <el-option
            v-for="item in claOptions"
            :key="item"
            :label="item + '班'"
            :value="String(item)"
          />
        </el-select>
        <el-select
          v-model="filterStudentType"
          placeholder="学生类型"
          style="width: 140px; margin-right: 10px"
          clearable
        >
          <el-option label="普通学生" value="11011001" />
          <el-option label="艺体生" value="11011002" />
          <el-option label="尖子生" value="11011003" />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button type="info" @click="resetSearch">重置</el-button>
      </div>
      
      <el-table :data="studentList" style="width: 100%" border v-loading="loading">
        <el-table-column label="序号" width="60">
          <template #default="scope">
            {{ (currentPage - 1) * pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="userName" label="用户名" width="150" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column label="性别" width="80">
          <template #default="scope">
            <span>{{ scope.row.sex === 10031001 ? '男' : '女' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="studentCode" label="学号" width="120" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="phone" label="电话" width="130" />
        <el-table-column label="年级" width="80">
          <template #default="scope">
            <span>{{ scope.row.gra }}</span>
          </template>
        </el-table-column>
        <el-table-column label="班级" width="80">
          <template #default="scope">
            <span>{{ scope.row.cla }}班</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.studentStatus === 10011001 ? 'success' : 'warning'">
              {{ scope.row.studentStatus === 10011001 ? '就读' : scope.row.studentStatus === 10011002 ? '毕业' : scope.row.studentStatus === 10011003 ? '休学' : '留级' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="学生类型" width="100">
          <template #default="scope">
            <span>{{ scope.row.studentType === 11011001 ? '普通学生' : scope.row.studentType === 11011002 ? '艺体生' : '尖子生' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEditStudent(scope.row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDeleteStudent(scope.row.studentId)">删除</el-button>
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
    
    <!-- 添加/编辑学生对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '添加学生' : '编辑学生'"
      width="600px"
    >
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="用户名" prop="userName">
          <el-input v-model="formData.userName" placeholder="请输入用户名" :disabled="dialogType === 'edit'" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="formData.name" placeholder="请输入姓名" :disabled="dialogType === 'edit'" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="dialogType === 'add'">
          <el-input v-model="formData.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="性别" prop="sex">
          <el-select v-model="formData.sex" placeholder="请选择性别">
            <el-option label="男" value="10031001" />
            <el-option label="女" value="10031002" />
          </el-select>
        </el-form-item>
        <el-form-item label="学号" prop="studentCode">
          <el-input v-model="formData.studentCode" placeholder="请输入学号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入电话" />
        </el-form-item>
        <el-form-item label="年级" prop="gra">
          <el-input v-model="formData.gra" placeholder="请输入年级" />
        </el-form-item>
        <el-form-item label="班级" prop="cla">
          <el-input v-model="formData.cla" placeholder="请输入班级" />
        </el-form-item>
        <el-form-item label="状态" prop="studentStatus">
          <el-select v-model="formData.studentStatus" placeholder="请选择状态">
            <el-option label="就读" value="10011001" />
            <el-option label="毕业" value="10011002" />
            <el-option label="休学" value="10011003" />
            <el-option label="留级" value="10011004" />
          </el-select>
        </el-form-item>
        <el-form-item label="学生类型" prop="studentType">
          <el-select v-model="formData.studentType" placeholder="请选择类型">
            <el-option label="普通学生" value="11011001" />
            <el-option label="艺体生" value="11011002" />
            <el-option label="尖子生" value="11011003" />
          </el-select>
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
      title="批量导入学生"
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
              <el-descriptions-item label="用户名">
                唯一标识，用于登录系统<br/>
                <el-tag size="small" type="info">示例：zhangsan2024</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="姓名">
                学生真实姓名<br/>
                <el-tag size="small" type="info">示例：张三</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="密码">
                登录密码（至少 6 位）<br/>
                <el-tag size="small" type="info">示例：123456</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="性别">
                填写"男"或"女"<br/>
                <el-tag size="small" type="info">示例：男</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="学号">
                数字，唯一标识<br/>
                <el-tag size="small" type="info">示例：2024001</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="邮箱">
                有效的邮箱地址<br/>
                <el-tag size="small" type="info">示例：zhangsan@example.com</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="电话">
                11 位手机号码<br/>
                <el-tag size="small" type="info">示例：13800138000</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="年级">
                入学年份（数字）<br/>
                <el-tag size="small" type="info">示例：2024</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="班级">
                数字，如 1、2、3<br/>
                <el-tag size="small" type="info">示例：1</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="学生状态">
                就读/毕业/休学/留级<br/>
                <el-tag size="small" type="info">示例：就读</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="学生类型">
                普通学生/艺体生/尖子生<br/>
                <el-tag size="small" type="info">示例：普通学生</el-tag>
              </el-descriptions-item>
            </el-descriptions>
            <el-divider />
            <p><strong>注意事项：</strong></p>
            <ul>
              <li>只能上传 .xlsx 或 .xls 格式的 Excel 文件</li>
              <li>文件大小不超过 10MB</li>
              <li>第一行必须是表头（字段名），数据从第二行开始</li>
              <li>重复的用户名会导入失败</li>
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
import { ref, onMounted, reactive } from 'vue'
import { Search } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

// 搜索查询与筛选
const searchQuery = ref('')
const filterGra = ref('')
const filterCla = ref('')
const filterStudentType = ref('')

// 分页数据
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 学生列表（全部数据与当前页数据）
const allStudents = ref([])
const studentList = ref([])

// 年级、班级选项
const graOptions = ref([])
const claOptions = ref([])

// 加载状态
const loading = ref(true)

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)
const formData = reactive({
  studentId: '',
  userId: '',
  userName: '',
  name: '',
  password: '',
  sex: '',
  studentCode: '',
  email: '',
  phone: '',
  gra: '',
  cla: '',
  studentStatus: '',
  studentType: ''
})

// 表单验证规则
const rules = {
  userName: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  studentCode: [
    { required: true, message: '请输入学号', trigger: 'blur' }
  ],
  phone: [
    { 
      pattern: /^1[3-9]\d{9}$/, 
      message: '请输入正确的手机号码', 
      trigger: 'blur' 
    }
  ]
}

// 统一根据分页更新表格数据
const updateTableData = () => {
  const startIndex = (currentPage.value - 1) * pageSize.value
  const endIndex = startIndex + pageSize.value
  studentList.value = allStudents.value.slice(startIndex, endIndex)
}

// 从当前数据中生成年级、班级选项
const updateFilterOptions = () => {
  const graSet = new Set()
  const claSet = new Set()
  allStudents.value.forEach(student => {
    if (student.gra != null) {
      graSet.add(student.gra)
    }
    if (student.cla != null) {
      claSet.add(student.cla)
    }
  })
  graOptions.value = Array.from(graSet).sort((a, b) => a - b)
  claOptions.value = Array.from(claSet).sort((a, b) => a - b)
}

// 获取学生列表（默认全部）
const loadStudentList = async () => {
  loading.value = true
  try {
    const response = await request.get('/api/student/list')
    if (response.code === 200 && response.data) {
      // 先按用户名排序
      const sortedList = response.data.sort((a, b) => a.userName.localeCompare(b.userName))
      allStudents.value = sortedList
      total.value = sortedList.length
      updateFilterOptions()
      updateTableData()
    }
  } catch (error) {
    console.error('获取学生列表失败:', error)
    ElMessage.error('获取学生列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索学生（多条件复合查询）
const handleSearch = async () => {
  // 如果没有任何条件，回退到默认列表
  if (!searchQuery.value && !filterGra.value && !filterCla.value && !filterStudentType.value) {
    currentPage.value = 1
    await loadStudentList()
    return
  }

  loading.value = true
  try {
    // 构建查询参数
    const params = {}
    if (filterGra.value) params.gra = filterGra.value
    if (filterCla.value) params.cla = filterCla.value
    if (filterStudentType.value) params.studentType = filterStudentType.value
    if (searchQuery.value) params.name = searchQuery.value

    const response = await request.post('/api/student/search/advanced', null, { params })
    if (response.code === 200 && response.data) {
      const sortedList = response.data.sort((a, b) => a.userName.localeCompare(b.userName))
      allStudents.value = sortedList
      total.value = sortedList.length
      // 动态更新筛选项（基于搜索结果）
      updateFilterOptions()
      // 搜索后重置到第一页
      currentPage.value = 1
      updateTableData()

      if (studentList.value.length === 0) {
        ElMessage.info('未找到符合条件的学生')
      }
    }
  } catch (error) {
    console.error('搜索学生失败:', error)
    ElMessage.error('搜索学生失败')
  } finally {
    loading.value = false
  }
}

// 导出学生（按当前筛选条件导出）
const handleExport = async () => {
  try {
    const params = {}
    if (filterGra.value) params.gra = filterGra.value
    if (filterCla.value) params.cla = filterCla.value
    if (filterStudentType.value) params.studentType = filterStudentType.value
    if (searchQuery.value) params.name = searchQuery.value

    const response = await request.post('/api/student/export', null, {
      params,
      responseType: 'blob'
    })

    const blob = new Blob([response], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = '学生数据导出.xlsx'
    link.click()
    window.URL.revokeObjectURL(url)

    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

// 重置搜索
const resetSearch = () => {
  searchQuery.value = ''
  filterGra.value = ''
  filterCla.value = ''
  filterStudentType.value = ''
  currentPage.value = 1
  loadStudentList()
}

// 添加学生
const handleAddStudent = () => {
  dialogType.value = 'add'
  // 重置表单
  if (formRef.value) {
    formRef.value.resetFields()
  }
  // 重置表单数据
  Object.assign(formData, {
    studentId: '',
    userId: '',
    userName: '',
    name: '',
    password: '',
    sex: '',
    studentCode: '',
    email: '',
    phone: '',
    gra: '',
    cla: '',
    studentStatus: '',
    studentType: ''
  })
  dialogVisible.value = true
}

// 编辑学生
const handleEditStudent = (student) => {
  dialogType.value = 'edit'
  // 填充表单数据
  Object.assign(formData, {
    studentId: student.studentId,
    userId: student.userId,
    userName: student.userName,
    name: student.name,
    password: '********',
    sex: String(student.sex),
    studentCode: student.studentCode,
    email: student.email,
    phone: student.phone,
    gra: student.gra,
    cla: student.cla,
    studentStatus: String(student.studentStatus),
    studentType: String(student.studentType)
  })
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    // 复制表单数据
    const submitData = { ...formData }
    
    // 如果是编辑且密码未修改，删除密码字段
    if (dialogType.value === 'edit' && submitData.password === '********') {
      delete submitData.password
    }
    
    // 转换数据类型
    submitData.studentCode = Number(submitData.studentCode)
    submitData.phone = submitData.phone ? Number(submitData.phone) : null
    submitData.gra = submitData.gra ? Number(submitData.gra) : null
    submitData.cla = submitData.cla ? Number(submitData.cla) : null
    submitData.sex = submitData.sex ? Number(submitData.sex) : null
    submitData.studentStatus = submitData.studentStatus ? Number(submitData.studentStatus) : null
    submitData.studentType = submitData.studentType ? Number(submitData.studentType) : null
    
    if (dialogType.value === 'add') {
      // 添加学生
      await request.post('/api/student/add', submitData)
      ElMessage.success('添加学生成功')
    } else {
      // 编辑学生
      await request.put('/api/student/update', submitData)
      ElMessage.success('编辑学生成功')
    }
    
    dialogVisible.value = false
    loadStudentList()
  } catch (error) {
    console.error('提交失败:', error)
    if (error.name === 'Error') {
      ElMessage.error('操作失败，请稍后重试')
    }
  }
}

// 删除学生
const handleDeleteStudent = (studentId) => {
  ElMessageBox.confirm('确定要删除这个学生吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request.delete(`/api/student/delete/${studentId}`)
      ElMessage.success('删除成功')
      loadStudentList()
    } catch (error) {
      console.error('删除学生失败:', error)
      ElMessage.error('删除学生失败')
    }
  }).catch(() => {
    // 取消删除
  })
}

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size
  updateTableData()
}

const handleCurrentChange = (current) => {
  currentPage.value = current
  updateTableData()
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
    const response = await request.get('/api/student/download-template', {
      responseType: 'blob'
    })
    
    // 创建下载链接
    const blob = new Blob([response], { 
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' 
    })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = '学生导入模板.xlsx'
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
    const response = await request.post('/api/student/import', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    
    if (response.code === 200) {
      const successCount = response.data
      ElMessage.success(`导入成功 ${successCount} 条记录`)
      importDialogVisible.value = false
      loadStudentList()
    } else {
      ElMessage.error(response.message || '导入失败')
    }
  } catch (error) {
    console.error('导入学生失败:', error)
    ElMessage.error('导入失败：' + (error.response?.data?.message || error.message))
  } finally {
    importing.value = false
  }
}

// 组件挂载时加载数据
onMounted(() => {
  loadStudentList()
})
</script>

<style scoped>
.admin-student-content {
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
</style>
