<template>
  <div class="teacher-personal-content">
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
        <el-form-item label="教师编号" prop="teacherCode">
          <el-input v-model="formData.teacherCode" placeholder="请输入教师编号" disabled />
        </el-form-item>
        <el-form-item label="教学科目" prop="teachSubject">
          <el-select v-model="formData.teachSubject" placeholder="请选择教学科目" disabled>
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
          <el-input v-model="formData.gra" placeholder="请输入年级" disabled />
        </el-form-item>
        <el-form-item label="班级" prop="cla">
          <el-input v-model="formData.cla" placeholder="请输入班级" disabled />
        </el-form-item>
        <el-form-item label="状态" prop="teacherStatus">
          <el-select v-model="formData.teacherStatus" placeholder="请选择状态" disabled>
            <el-option label="在职" value="10021001" />
            <el-option label="离职" value="10021002" />
            <el-option label="休假" value="10021003" />
          </el-select>
        </el-form-item>
        <el-form-item label="教师类型" prop="teacherType">
          <el-select v-model="formData.teacherType" placeholder="请选择类型" disabled>
            <el-option label="普通教师" value="11021001" />
            <el-option label="学科组长" value="11021002" />
            <el-option label="班主任" value="11021003" />
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
  teacherId: '',
  userId: '',
  userName: '',
  name: '',
  sex: '',
  sexDesc: '',
  teacherCode: '',
  teachSubject: '',
  teacherStatus: '',
  teacherType: '',
  phone: '',
  gra: '',
  cla: ''
})

// 性别文本计算属性
const sexText = computed(() => {
  return formData.sexDesc || ''
})

// 表单验证规则
const rules = {
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

// 获取当前教师信息
const loadTeacherInfo = async () => {
  loading.value = true
  try {
    // 首先获取当前用户信息
    const userInfoResponse = await request.get('/api/userInfo')
    if (userInfoResponse.code === 200 && userInfoResponse.data) {
      const userId = userInfoResponse.data.userId
      
      // 然后根据 userId 获取教师详细信息
      const teacherResponse = await request.get(`/api/teacher/user/${userId}`)
      if (teacherResponse.code === 200 && teacherResponse.data) {
        const teacher = teacherResponse.data
        Object.assign(formData, {
          teacherId: teacher.teacherId,
          userId: teacher.userId,
          userName: teacher.userName,
          name: teacher.name,
          sex: String(teacher.sex),
          sexDesc: teacher.sexDesc,
          teacherCode: teacher.teacherCode,
          teachSubject: String(teacher.teachSubject),
          teacherStatus: String(teacher.teacherStatus),
          teacherType: String(teacher.teacherType),
          phone: teacher.phone,
          gra: teacher.gra,
          cla: teacher.cla
        })
      } else {
        ElMessage.error('获取教师信息失败')
      }
    } else {
      ElMessage.error('获取用户信息失败')
    }
  } catch (error) {
    console.error('获取教师信息失败:', error)
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
    submitData.teacherCode = submitData.teacherCode ? Number(submitData.teacherCode) : null
    submitData.phone = submitData.phone ? Number(submitData.phone) : null
    submitData.gra = submitData.gra ? Number(submitData.gra) : null
    submitData.cla = submitData.cla ? Number(submitData.cla) : null
    submitData.sex = submitData.sex ? Number(submitData.sex) : null
    submitData.teachSubject = submitData.teachSubject ? Number(submitData.teachSubject) : null
    submitData.teacherStatus = submitData.teacherStatus ? Number(submitData.teacherStatus) : null
    submitData.teacherType = submitData.teacherType ? Number(submitData.teacherType) : null
    
    // 调用后端接口更新教师信息
    const response = await request.put('/api/teacher/update', submitData)
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
  loadTeacherInfo()
})
</script>

<style scoped>
.teacher-personal-content {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>