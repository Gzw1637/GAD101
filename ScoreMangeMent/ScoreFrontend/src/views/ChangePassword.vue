<template>
  <div class="change-password-container">
    <h2>修改密码</h2>
    <el-form
      :model="form"
      :rules="rules"
      ref="formRef"
      label-width="100px"
      class="change-password-form"
    >
      <el-form-item label="原密码" prop="oldPassword">
        <el-input
          v-model="form.oldPassword"
          type="password"
          placeholder="请输入原密码"
          show-password
        />
      </el-form-item>
      <el-form-item label="新密码" prop="newPassword">
        <el-input
          v-model="form.newPassword"
          type="password"
          placeholder="请输入新密码（至少6位）"
          show-password
        />
      </el-form-item>
      <el-form-item label="确认新密码" prop="confirmPassword">
        <el-input
          v-model="form.confirmPassword"
          type="password"
          placeholder="请确认新密码"
          show-password
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSubmit" :loading="loading">提交</el-button>
        <el-button @click="handleCancel">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { useRouter } from 'vue-router'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

// 获取当前用户信息
const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')

const form = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const rules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '新密码长度至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== form.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const response = await request.post('/api/user/change-password', {
          oldPassword: form.oldPassword,
          newPassword: form.newPassword
        })
        
        if (response.code === 200) {
          ElMessage.success('密码修改成功')
          // 重置表单
          formRef.value.resetFields()
          // 根据用户角色跳转到相应页面
          setTimeout(() => {
            if (userInfo.role === 14981002) {
              // 教师跳转到个人信息页
              router.push('/teacher/personal')
            } else if (userInfo.role === 14981001) {
              // 学生跳转到学生首页
              router.push('/student')
            } else if (userInfo.role === 14981003) {
              // 管理员跳转到管理员首页
              router.push('/admin')
            }
          }, 1500)
        } else {
          ElMessage.error(response.msg || '密码修改失败')
        }
      } catch (error) {
        ElMessage.error('网络错误，请稍后重试')
      } finally {
        loading.value = false
      }
    }
  })
}

const handleCancel = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  // 可以选择返回上一页
  router.back()
}
</script>

<style scoped>
.change-password-container {
  max-width: 500px;
  margin: 0 auto;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  margin-top: 40px;
}

.change-password-container h2 {
  text-align: center;
  margin-bottom: 20px;
  color: #333;
}

.change-password-form {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
}

.el-form-item {
  margin-bottom: 20px;
}

.el-form-item__label {
  font-weight: 500;
}

.el-form-item__content {
  width: 100%;
}

.el-button {
  margin-right: 10px;
}
</style>