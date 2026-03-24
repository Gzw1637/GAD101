<template>
  <div class="admin-teacher-content">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>教师管理</span>
          <div class="header-buttons">
            <el-button type="success" @click="handleDownloadTemplate">导入模板下载</el-button>
            <el-button type="warning" @click="handleImportDialog">批量导入教师</el-button>
            <el-button type="info" @click="handleExport">导出数据</el-button>
            <el-button type="primary" @click="handleAddTeacher">添加教师</el-button>
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
          v-model="filterTeachSubject"
          placeholder="教学科目"
          style="width: 140px; margin-right: 10px"
          clearable
        >
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
          v-model="filterTeacherType"
          placeholder="教师类型"
          style="width: 140px; margin-right: 10px"
          clearable
        >
          <el-option label="普通教师" value="11021001" />
          <el-option label="学科组长" value="11021002" />
          <el-option label="班主任" value="11021003" />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button type="info" @click="resetSearch">重置</el-button>
      </div>
      
      <el-table :data="teacherList" style="width: 100%" border v-loading="loading">
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
        <el-table-column prop="teacherCode" label="教师编号" width="120" />
        <el-table-column label="教学科目" width="120">
          <template #default="scope">
            <span>{{ getSubjectDesc(scope.row.teachSubject) }}</span>
          </template>
        </el-table-column>
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
            <el-tag :type="scope.row.teacherStatus === 10021001 ? 'success' : 'warning'">
              {{ scope.row.teacherStatus === 10021001 ? '在职' : scope.row.teacherStatus === 10021002 ? '离职' : scope.row.teacherStatus === 10021003 ? '休假' : '其他' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="教师类型" width="100">
          <template #default="scope">
            <span>{{ scope.row.teacherType === 11021001 ? '普通教师' : scope.row.teacherType === 11021002 ? '学科组长' : '班主任' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEditTeacher(scope.row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDeleteTeacher(scope.row.teacherId)">删除</el-button>
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
    
    <!-- 添加/编辑教师对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '添加教师' : '编辑教师'"
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
        <el-form-item label="教师编号" prop="teacherCode">
          <el-input v-model="formData.teacherCode" placeholder="请输入教师编号" />
        </el-form-item>
        <el-form-item label="教学科目" prop="teachSubject">
          <el-select v-model="formData.teachSubject" placeholder="请选择教学科目">
            <el-option label="语文" value="10111001" />
            <el-option label="数学" value="10111002" />
            <el-option label="英语" value="10111003" />
            <el-option label="物理" value="10111004" />
            <el-option label="化学" value="10111006" />
            <el-option label="生物" value="10111007" />
            <el-option label="历史" value="10111005" />
            <el-option label="地理" value="10111008" />
            <el-option label="政治" value="10111009" />
          </el-select>
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
        <el-form-item label="状态" prop="teacherStatus">
          <el-select v-model="formData.teacherStatus" placeholder="请选择状态">
            <el-option label="在职" value="10021001" />
            <el-option label="离职" value="10021002" />
            <el-option label="休假" value="10021003" />
          </el-select>
        </el-form-item>
        <el-form-item label="教师类型" prop="teacherType">
          <el-select v-model="formData.teacherType" placeholder="请选择类型">
            <el-option label="普通教师" value="11021001" />
            <el-option label="学科组长" value="11021002" />
            <el-option label="班主任" value="11021003" />
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
      title="批量导入教师"
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
                <el-tag size="small" type="info">示例：zhanglaoshi2024</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="姓名">
                教师真实姓名<br/>
                <el-tag size="small" type="info">示例：张老师</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="密码">
                登录密码（至少 6 位）<br/>
                <el-tag size="small" type="info">示例：123456</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="性别">
                填写"男"或"女"<br/>
                <el-tag size="small" type="info">示例：男</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="教师编号">
                数字，唯一标识<br/>
                <el-tag size="small" type="info">示例：10001</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="教学科目">
                语文/数学/英语/物理/化学/生物/地理/政治/历史<br/>
                <el-tag size="small" type="info">示例：数学</el-tag>
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
              <el-descriptions-item label="教师状态">
                在职/离职<br/>
                <el-tag size="small" type="info">示例：在职</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="教师类型">
                普通教师/学科组长/班主任<br/>
                <el-tag size="small" type="info">示例：普通教师</el-tag>
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
const filterTeachSubject = ref('')
const filterGra = ref('')
const filterCla = ref('')
const filterTeacherType = ref('')

// 分页数据
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 教师列表（全部数据与当前页数据）
const allTeachers = ref([])
const teacherList = ref([])

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
  teacherId: '',
  userId: '',
  userName: '',
  name: '',
  password: '',
  sex: '',
  teacherCode: '',
  teachSubject: '',
  teacherStatus: '',
  teacherType: '',
  phone: '',
  gra: '',
  cla: ''
})

// 表单验证规则
const rules = {
  userName: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  teacherCode: [
    { required: true, message: '请输入教师编号', trigger: 'blur' }
  ],
  phone: [
    { 
      pattern: /^1[3-9]\d{9}$/, 
      message: '请输入正确的手机号码', 
      trigger: 'blur' 
    }
  ]
}

// 获取科目描述
const getSubjectDesc = (subject) => {
  const subjectMap = {
    10111001: '语文',
    10111002: '数学',
    10111003: '英语',
    10111004: '物理',
    10111005: '历史',
    10111006: '化学',
    10111007: '生物',
    10111008: '地理',
    10111009: '政治'
  }
  return subjectMap[subject] || '未知'
}

// 统一根据分页更新表格数据
const updateTableData = () => {
  const startIndex = (currentPage.value - 1) * pageSize.value
  const endIndex = startIndex + pageSize.value
  teacherList.value = allTeachers.value.slice(startIndex, endIndex)
}

// 从当前数据中生成年级、班级选项
const updateFilterOptions = () => {
  const graSet = new Set()
  const claSet = new Set()
  allTeachers.value.forEach(teacher => {
    if (teacher.gra != null) {
      graSet.add(teacher.gra)
    }
    if (teacher.cla != null) {
      claSet.add(teacher.cla)
    }
  })
  graOptions.value = Array.from(graSet).sort((a, b) => a - b)
  claOptions.value = Array.from(claSet).sort((a, b) => a - b)
}

// 获取教师列表（默认全部）
const loadTeacherList = async () => {
  loading.value = true
  try {
    const response = await request.get('/api/teacher/list')
    if (response.code === 200 && response.data) {
      const sortedList = response.data.sort((a, b) => a.userName.localeCompare(b.userName))
      allTeachers.value = sortedList
      total.value = sortedList.length
      updateFilterOptions()
      updateTableData()
    }
  } catch (error) {
    console.error('获取教师列表失败:', error)
    ElMessage.error('获取教师列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索教师（多条件复合查询）
const handleSearch = async () => {
  // 如果没有任何条件，回退到默认列表
  if (!searchQuery.value && !filterTeachSubject.value && !filterGra.value && !filterCla.value && !filterTeacherType.value) {
    currentPage.value = 1
    await loadTeacherList()
    return
  }

  loading.value = true
  try {
    // 构建查询参数
    const params = {}
    if (filterTeachSubject.value) params.teachSubject = filterTeachSubject.value
    if (filterGra.value) params.gra = filterGra.value
    if (filterCla.value) params.cla = filterCla.value
    if (filterTeacherType.value) params.teacherType = filterTeacherType.value
    if (searchQuery.value) params.name = searchQuery.value

    const response = await request.post('/api/teacher/search/advanced', null, { params })
    if (response.code === 200 && response.data) {
      const sortedList = response.data.sort((a, b) => a.userName.localeCompare(b.userName))
      allTeachers.value = sortedList
      total.value = sortedList.length
      // 动态更新筛选项（基于搜索结果）
      updateFilterOptions()
      // 搜索后重置到第一页
      currentPage.value = 1
      updateTableData()

      if (teacherList.value.length === 0) {
        ElMessage.info('未找到符合条件的教师')
      }
    }
  } catch (error) {
    console.error('搜索教师失败:', error)
    ElMessage.error('搜索教师失败')
  } finally {
    loading.value = false
  }
}

// 重置搜索
const resetSearch = () => {
  searchQuery.value = ''
  filterTeachSubject.value = ''
  filterGra.value = ''
  filterCla.value = ''
  filterTeacherType.value = ''
  currentPage.value = 1
  loadTeacherList()
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
    const response = await request.get('/api/teacher/download-template', {
      responseType: 'blob'
    })
    
    // 创建下载链接
    const blob = new Blob([response], { 
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' 
    })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = '教师导入模板.xlsx'
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
    const response = await request.post('/api/teacher/import', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    
    if (response.code === 200) {
      const successCount = response.data
      ElMessage.success(`导入成功 ${successCount} 条记录`)
      importDialogVisible.value = false
      loadTeacherList()
    } else {
      ElMessage.error(response.message || '导入失败')
    }
  } catch (error) {
    console.error('导入教师失败:', error)
    ElMessage.error('导入失败：' + (error.response?.data?.message || error.message))
  } finally {
    importing.value = false
  }
}

// 导出数据
const handleExport = async () => {
  try {
    // 导出当前筛选条件下的所有数据
    const params = {}
    if (filterTeachSubject.value) params.teachSubject = filterTeachSubject.value
    if (filterGra.value) params.gra = filterGra.value
    if (filterCla.value) params.cla = filterCla.value
    if (filterTeacherType.value) params.teacherType = filterTeacherType.value
    if (searchQuery.value) params.name = searchQuery.value
    
    const response = await request.post('/api/teacher/export', params, {
      responseType: 'blob'
    })
    
    // 创建下载链接
    const blob = new Blob([response], { 
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' 
    })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = '教师数据.xlsx'
    link.click()
    window.URL.revokeObjectURL(url)
    
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出数据失败:', error)
    ElMessage.error('导出数据失败')
  }
}

// 添加教师
const handleAddTeacher = () => {
  dialogType.value = 'add'
  if (formRef.value) {
    formRef.value.resetFields()
  }
  Object.assign(formData, {
    teacherId: '',
    userId: '',
    userName: '',
    name: '',
    password: '',
    sex: '',
    teacherCode: '',
    teachSubject: '',
    teacherStatus: '',
    teacherType: '',
    phone: '',
    gra: '',
    cla: ''
  })
  dialogVisible.value = true
}

// 编辑教师
const handleEditTeacher = (teacher) => {
  dialogType.value = 'edit'
  Object.assign(formData, {
    teacherId: teacher.teacherId,
    userId: teacher.userId,
    userName: teacher.userName,
    name: teacher.name,
    password: '********',
    sex: String(teacher.sex),
    teacherCode: teacher.teacherCode,
    teachSubject: String(teacher.teachSubject),
    teacherStatus: String(teacher.teacherStatus),
    teacherType: String(teacher.teacherType),
    phone: teacher.phone,
    gra: teacher.gra,
    cla: teacher.cla
  })
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    const submitData = { ...formData }
    
    if (dialogType.value === 'edit' && submitData.password === '********') {
      delete submitData.password
    }
    
    submitData.teacherCode = submitData.teacherCode ? Number(submitData.teacherCode) : null
    submitData.phone = submitData.phone ? Number(submitData.phone) : null
    submitData.gra = submitData.gra ? Number(submitData.gra) : null
    submitData.cla = submitData.cla ? Number(submitData.cla) : null
    submitData.sex = submitData.sex ? Number(submitData.sex) : null
    submitData.teachSubject = submitData.teachSubject ? Number(submitData.teachSubject) : null
    submitData.teacherStatus = submitData.teacherStatus ? Number(submitData.teacherStatus) : null
    submitData.teacherType = submitData.teacherType ? Number(submitData.teacherType) : null
    
    if (dialogType.value === 'add') {
      await request.post('/api/teacher/add', submitData)
      ElMessage.success('添加教师成功')
    } else {
      await request.put('/api/teacher/update', submitData)
      ElMessage.success('编辑教师成功')
    }
    
    dialogVisible.value = false
    loadTeacherList()
  } catch (error) {
    console.error('提交失败:', error)
    if (error.name === 'Error') {
      ElMessage.error('操作失败，请稍后重试')
    }
  }
}

// 删除教师
const handleDeleteTeacher = (teacherId) => {
  ElMessageBox.confirm('确定要删除这个教师吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request.delete(`/api/teacher/${teacherId}`)
      ElMessage.success('删除成功')
      loadTeacherList()
    } catch (error) {
      console.error('删除教师失败:', error)
      ElMessage.error('删除教师失败')
    }
  }).catch(() => {
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

// 组件挂载时加载数据
onMounted(() => {
  loadTeacherList()
})
</script>

<style scoped>
.admin-teacher-content {
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
