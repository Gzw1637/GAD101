<template>
  <div class="student-personal-content">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>个人信息</span>
        </div>
      </template>
      
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="用户名" prop="userName">
          <el-input v-model="formData.userName" placeholder="请输入用户名" disabled />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="formData.name" placeholder="请输入姓名" disabled />
        </el-form-item>
        <el-form-item label="性别" prop="sex">
          <el-input v-model="sexText" placeholder="请选择性别" disabled />
        </el-form-item>
        <el-form-item label="学号" prop="studentCode">
          <el-input v-model="formData.studentCode" placeholder="请输入学号" disabled />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入电话" />
        </el-form-item>
        <el-form-item label="年级" prop="gra">
          <el-input v-model="formData.gra" placeholder="请输入年级" disabled />
        </el-form-item>
        <el-form-item label="班级" prop="cla">
          <el-input v-model="formData.cla" placeholder="请输入班级" disabled />
        </el-form-item>
        <el-form-item label="状态" prop="studentStatus">
          <el-select v-model="formData.studentStatus" placeholder="请选择状态" disabled>
            <el-option label="就读" value="10011001" />
            <el-option label="毕业" value="10011002" />
            <el-option label="休学" value="10011003" />
            <el-option label="留级" value="10011004" />
          </el-select>
        </el-form-item>
        <el-form-item label="学生类型" prop="studentType">
          <el-select v-model="formData.studentType" placeholder="请选择类型" disabled>
            <el-option label="普通学生" value="11011001" />
            <el-option label="艺体生" value="11011002" />
            <el-option label="尖子生" value="11011003" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit">保存修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

// 表单数据
const formData = reactive({
  studentId: '',
  userId: '',
  userName: '',
  name: '',
  sex: '',
  sexDesc: '',
  studentCode: '',
  email: '',
  phone: '',
  gra: '',
  cla: '',
  studentStatus: '',
  studentType: ''
})

// 性别文本计算属性
const sexText = computed(() => {
  return formData.sexDesc || ''
})

// 表单验证规则
const rules = {
  email: [
    {
      type: 'email',
      message: '请输入正确的邮箱地址',
      trigger: 'blur'
    }
  ],
  phone: [
    {
      pattern: /^1[3-9]\d{9}$/,
      message: '请输入正确的手机号码',
      trigger: 'blur'
    }
  ]
}

// 表单引用
const formRef = ref(null)

// 加载状态
const loading = ref(false)

// 获取当前学生信息
const loadStudentInfo = async () => {
  loading.value = true
  try {
    // 首先获取当前用户信息
    const userInfoResponse = await request.get('/api/userInfo')
    if (userInfoResponse.code === 200 && userInfoResponse.data) {
      const userId = userInfoResponse.data.userId
      
      // 然后根据 userId 获取学生详细信息
      const studentResponse = await request.get(`/api/student/user/${userId}`)
      if (studentResponse.code === 200 && studentResponse.data) {
        const student = studentResponse.data
        Object.assign(formData, {
          studentId: student.studentId,
          userId: student.userId,
          userName: student.userName,
          name: student.name,
          sex: String(student.sex),
          sexDesc: student.sexDesc,
          studentCode: student.studentCode,
          email: student.email,
          phone: student.phone,
          gra: student.gra,
          cla: student.cla,
          studentStatus: String(student.studentStatus),
          studentType: String(student.studentType)
        })
      } else {
        ElMessage.error('获取学生信息失败')
      }
    } else {
      ElMessage.error('获取用户信息失败')
    }
  } catch (error) {
    console.error('获取学生信息失败:', error)
    ElMessage.error('网络错误，请检查网络连接')
  } finally {
    loading.value = false
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    const submitData = { ...formData }
    
    // 转换数据类型
    submitData.studentCode = submitData.studentCode ? Number(submitData.studentCode) : null
    submitData.phone = submitData.phone ? Number(submitData.phone) : null
    submitData.gra = submitData.gra ? Number(submitData.gra) : null
    submitData.cla = submitData.cla ? Number(submitData.cla) : null
    submitData.sex = submitData.sex ? Number(submitData.sex) : null
    submitData.studentStatus = submitData.studentStatus ? Number(submitData.studentStatus) : null
    submitData.studentType = submitData.studentType ? Number(submitData.studentType) : null
    
    // 调用后端接口更新学生信息
    const response = await request.put('/api/student/update', submitData)
    if (response.code === 200) {
      ElMessage.success('保存成功')
    } else {
      ElMessage.error(response.message || '保存失败')
    }
  } catch (error) {
    console.error('提交失败:', error)
    if (error.name === 'Error') {
      ElMessage.error('操作失败，请稍后重试')
    }
  }
}

// 组件挂载时加载数据
onMounted(() => {
  loadStudentInfo()
})
</script>

<style scoped>
.student-personal-content {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>