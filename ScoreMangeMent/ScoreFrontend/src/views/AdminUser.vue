<template>
  <div class="admin-user-content">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
        </div>
      </template>
      
      <div class="search-bar">
        <el-input v-model="searchQuery" placeholder="请输入姓名" style="width: 200px; margin-right: 10px" clearable>
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select v-model="filterRole" placeholder="选择角色" style="width: 120px; margin-right: 10px" clearable>
          <el-option label="学生" value="14981001" />
          <el-option label="教师" value="14981002" />
        </el-select>
        <el-select v-model="filterStatus" placeholder="选择状态" style="width: 100px; margin-right: 10px" clearable>
          <el-option label="有效" value="10001001" />
          <el-option label="无效" value="10001002" />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button type="info" @click="resetSearch">重置</el-button>
      </div>
      
      <el-table :data="userList" style="width: 100%" border v-loading="loading">
        <el-table-column label="序号" width="80">
          <template #default="scope">
            {{ (currentPage - 1) * pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
<!--        <el-table-column prop="userId" label="用户ID" width="100" />-->
        <el-table-column prop="userName" label="用户名" width="180" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column label="角色" width="120">
          <template #default="scope">
            {{ getRoleName(scope.row.role) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.userStatus === 10001001 ? 'success' : 'danger'">
              {{ scope.row.userStatus === 10001001 ? '有效' : '无效' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEditUser(scope.row)">编辑</el-button>
            <el-button 
              v-if="scope.row.loginFailCount >= 5 || scope.row.lockTime" 
              type="warning" 
              size="small" 
              @click="handleUnlockUser(scope.row)"
            >
              解封
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
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    
    <!-- 编辑用户对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="编辑用户"
      width="500px"
    >
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="用户名" prop="userName">
          <el-input v-model="formData.userName" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="formData.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="formData.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="formData.role" placeholder="请选择角色" disabled>
            <el-option label="学生" value="14981001" />
            <el-option label="教师" value="14981002" />
            <el-option label="管理员" value="14981003" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="userStatus">
          <el-select v-model="formData.userStatus" placeholder="请选择状态">
            <el-option label="有效" value="10001001" />
            <el-option label="无效" value="10001002" />
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
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { Search } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

// 搜索查询
const searchQuery = ref('')
// 筛选条件
const filterRole = ref('')
const filterStatus = ref('')

// 分页数据
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 用户列表
const userList = ref([])

// 加载状态
const loading = ref(true)

// 对话框相关
const dialogVisible = ref(false)
const formRef = ref(null)
const formData = reactive({
  userId: '',
  userName: '',
  name: '',
  password: '',
  role: '',
  userStatus: ''
})

// 表单验证规则
const rules = {
  userName: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ],
  userStatus: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 获取用户列表
const loadUserList = async () => {
  loading.value = true
  try {
    const response = await request.get('/api/user/list')
    if (response.code === 200 && response.data) {
      // 过滤掉管理员用户并按用户名排序
      const sortedList = response.data.filter(user => user.role !== 14981003).sort((a, b) => a.userName.localeCompare(b.userName))
      // 设置总数
      total.value = sortedList.length
      // 根据当前页和每页数量进行切片（前端分页）
      const startIndex = (currentPage.value - 1) * pageSize.value
      const endIndex = startIndex + pageSize.value
      userList.value = sortedList.slice(startIndex, endIndex)
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索用户（多条件复合查询）
const handleSearch = async () => {
  loading.value = true
  try {
    // 构建查询参数
    const params = {}
    if (filterRole.value) params.role = filterRole.value
    if (filterStatus.value) params.userStatus = filterStatus.value
    if (searchQuery.value) params.name = searchQuery.value
    
    const response = await request.post('/api/user/search/advanced', null, { params })
    if (response.code === 200 && response.data) {
      // 按用户名排序
      const sortedList = response.data.sort((a, b) => a.userName.localeCompare(b.userName))
      // 设置总数
      total.value = sortedList.length
      // 重置到第一页
      currentPage.value = 1
      // 根据当前页和每页数量进行切片（前端分页）
      const startIndex = (currentPage.value - 1) * pageSize.value
      const endIndex = startIndex + pageSize.value
      userList.value = sortedList.slice(startIndex, endIndex)
      
      if (userList.value.length === 0) {
        ElMessage.info('未找到符合条件的用户')
      }
    }
  } catch (error) {
    console.error('搜索用户失败:', error)
    ElMessage.error('搜索用户失败')
  } finally {
    loading.value = false
  }
}

// 重置搜索
const resetSearch = () => {
  searchQuery.value = ''
  filterRole.value = ''
  filterStatus.value = ''
  loadUserList()
}

// 编辑用户
const handleEditUser = (user) => {
  // 填充表单数据
  Object.assign(formData, {
    userId: user.userId,
    userName: user.userName,
    name: user.name,
    password: '********', // 密码以小黑点形式显示
    role: String(user.role), // 转换为字符串类型，确保正确显示角色名称
    userStatus: String(user.userStatus) // 转换为字符串类型，确保正确显示状态名称
  })
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    // 复制表单数据，避免修改原始数据
    const submitData = { ...formData }
    
    // 如果是编辑用户且密码没有修改，不发送密码字段
    if (submitData.password === '********') {
      delete submitData.password
    }
    
    // 编辑用户
    await request.put('/api/user/update', submitData)
    ElMessage.success('编辑用户成功')
    
    dialogVisible.value = false
    loadUserList()
  } catch (error) {
    console.error('提交失败:', error)
    if (error.name === 'Error') {
      ElMessage.error('操作失败，请稍后重试')
    }
  }
}

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size
  loadUserList()
}

const handleCurrentChange = (current) => {
  currentPage.value = current
  loadUserList()
}

// 获取角色名称
const getRoleName = (role) => {
  const roleMap = {
    '14981001': '学生',
    '14981002': '教师',
    '14981003': '管理员'
  }
  return roleMap[role] || '未知'
}

// 解封用户
const handleUnlockUser = async (user) => {
  try {
    await ElMessageBox.confirm(
      `确定要解封用户"${user.userName}"吗？解封后该用户可以重新登录。`,
      '解封确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await request.put(`/api/user/unlock/${user.userId}`)
    ElMessage.success('用户解封成功')
    loadUserList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('解封用户失败:', error)
      ElMessage.error('解封用户失败')
    }
  }
}

// 组件挂载时加载数据
onMounted(() => {
  loadUserList()
})
</script>

<style scoped>
.admin-user-content {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-bar {
  margin-bottom: 20px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
}
</style>