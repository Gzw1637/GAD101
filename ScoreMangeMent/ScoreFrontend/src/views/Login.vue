<template>
  <div class="login-container">
    <div class="login-form-wrapper">
      <h2 class="login-title">登录系统</h2>
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef" label-width="80px">
        <el-form-item label="用户名" prop="userName">
          <el-input v-model="loginForm.userName" placeholder="请输入用户名" prefix-icon="UserFilled" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" prefix-icon="Lock" show-password @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="login-button" @click="handleLogin" :loading="loading">登录</el-button>
        </el-form-item>
        <el-form-item v-if="errorMessage" class="error-message">
          <el-alert :title="errorMessage" type="error" show-icon :closable="false" />
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const errorMessage = ref('')
const loginFormRef = ref(null)

const loginForm = reactive({
  userName: '',
  password: ''
})

const rules = {
  userName: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  try {
    await loginFormRef.value.validate()
    loading.value = true
    errorMessage.value = ''
    
    const response = await request.post('/api/login', loginForm)
    
    if (response.code === 200) {
      const userInfo = response.data
      localStorage.setItem('token', userInfo.token)
      localStorage.setItem('userInfo', JSON.stringify({
        userId: userInfo.userId,
        userName: userInfo.userName,
        name: userInfo.name,
        role: userInfo.role,
        roleDesc: userInfo.roleDesc
      }))
      
      ElMessage.success('登录成功，正在跳转...')
      
      setTimeout(() => {
        switch (userInfo.role) {
          case 14981001:
            router.push('/student')
            break
          case 14981002:
            router.push('/teacher')
            break
          case 14981003:
            router.push('/admin')
            break
          default:
            router.push('/')
        }
      }, 500)
    } else {
      showError(response.msg)
    }
  } catch (error) {
    let errorMsg = '登录失败'
    
    if (error.msg) {
      errorMsg = error.msg
    } else if (error.response) {
      errorMsg = error.response.data.msg || '登录失败'
    } else {
      errorMsg = '网络错误，请检查网络连接'
    }
    
    showError(errorMsg)
  } finally {
    loading.value = false
  }
}

const showError = (msg) => {
  errorMessage.value = msg
  ElMessage.error({
    message: msg,
    duration: 5000,
    showClose: true
  })
  
  if (loginFormRef.value) {
    loginFormRef.value.clearValidate()
  }
  loginForm.password = ''
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-form-wrapper {
  background: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

.login-title {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
  font-size: 24px;
  font-weight: 600;
}

.login-button {
  width: 100%;
  padding: 12px;
  font-size: 16px;
}

.error-message {
  margin-top: 15px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-form-wrapper {
    padding: 30px;
    margin: 0 20px;
  }
}
</style>