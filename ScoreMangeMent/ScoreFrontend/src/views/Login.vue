<template>
  <div class="login-container">
    <!-- 背景动画 -->
    <div class="bg-shapes">
      <div class="shape shape-1"></div>
      <div class="shape shape-2"></div>
      <div class="shape shape-3"></div>
    </div>

    <div class="login-card">
      <!-- 左侧装饰 -->
      <div class="login-left">
        <div class="left-content">
          <div class="logo-icon">
            <el-icon :size="60"><Reading /></el-icon>
          </div>
          <h1 class="system-title">成绩管理系统</h1>
          <p class="system-desc">Score Management System</p>
          <div class="feature-list">
            <div class="feature-item">
              <el-icon><Check /></el-icon>
              <span>成绩管理</span>
            </div>
            <div class="feature-item">
              <el-icon><Check /></el-icon>
              <span>教学计划</span>
            </div>
            <div class="feature-item">
              <el-icon><Check /></el-icon>
              <span>意见箱</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧登录表单 -->
      <div class="login-right">
        <div class="login-header">
          <h2>欢迎回来</h2>
          <p>请登录您的账户</p>
        </div>

        <el-form
            :model="loginForm"
            :rules="rules"
            ref="loginFormRef"
            class="login-form"
            size="large"
        >
          <el-form-item prop="userName">
            <el-input
                v-model="loginForm.userName"
                placeholder="请输入用户名"
                prefix-icon="User"
                clearable
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                prefix-icon="Lock"
                show-password
                @keyup.enter="handleLogin"
            />
          </el-form-item>

          <div class="form-options">
            <el-checkbox v-model="rememberMe">记住我</el-checkbox>
          </div>

          <el-button
              type="primary"
              class="login-btn"
              @click="handleLogin"
              :loading="loading"
              :class="{ 'btn-loading': loading }"
          >
            <span v-if="!loading">登 录</span>
            <span v-else>登录中...</span>
          </el-button>
        </el-form>

        <div class="login-footer">
          <p class="copyright">© 2026 成绩管理系统</p>
        </div>
      </div>
    </div>

    <!-- 错误提示弹窗 -->
    <el-dialog
        v-model="errorDialogVisible"
        title="登录提示"
        width="400px"
        :close-on-click-modal="false"
        center
    >
      <div class="error-dialog-content">
        <el-icon class="error-icon" :size="48"><WarningFilled /></el-icon>
        <p>{{ errorMessage }}</p>
      </div>
      <template #footer>
        <el-button type="primary" @click="errorDialogVisible = false">知道了</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { Reading, Check, WarningFilled } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const errorMessage = ref('')
const errorDialogVisible = ref(false)
const rememberMe = ref(false)
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
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
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

      // 存储token和用户信息
      localStorage.setItem('token', userInfo.token)
      localStorage.setItem('userInfo', JSON.stringify({
        userId: userInfo.userId,
        userName: userInfo.userName,
        name: userInfo.name,
        role: userInfo.role,
        roleDesc: userInfo.roleDesc
      }))

      // 记住我功能
      if (rememberMe.value) {
        localStorage.setItem('rememberMe', 'true')
        localStorage.setItem('savedUserName', loginForm.userName)
      } else {
        localStorage.removeItem('rememberMe')
        localStorage.removeItem('savedUserName')
      }

      ElMessage.success('登录成功')

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
      }, 300)
    } else {
      showError(response.msg || '登录失败')
    }
  } catch (error) {
    let errorMsg = '登录失败，请稍后重试'

    if (error.msg) {
      errorMsg = error.msg
    } else if (error.response) {
      errorMsg = error.response.data?.msg || '登录失败'
    }

    showError(errorMsg)
  } finally {
    loading.value = false
  }
}

const showError = (msg) => {
  errorMessage.value = msg
  errorDialogVisible.value = true

  if (loginFormRef.value) {
    loginFormRef.value.clearValidate()
  }
  loginForm.password = ''
}

// 检查是否有记住的用户名
const checkRememberMe = () => {
  if (localStorage.getItem('rememberMe') === 'true') {
    const savedUserName = localStorage.getItem('savedUserName')
    if (savedUserName) {
      loginForm.userName = savedUserName
      rememberMe.value = true
    }
  }
}

checkRememberMe()
</script>

<style scoped>
/* 容器样式 */
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  position: relative;
  overflow: hidden;
}

/* 背景动画形状 */
.bg-shapes {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  pointer-events: none;
}

.shape {
  position: absolute;
  border-radius: 50%;
  opacity: 0.1;
}

.shape-1 {
  width: 400px;
  height: 400px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  top: -100px;
  left: -100px;
  animation: float 8s ease-in-out infinite;
}

.shape-2 {
  width: 300px;
  height: 300px;
  background: linear-gradient(135deg, #f093fb, #f5576c);
  bottom: -50px;
  right: -50px;
  animation: float 10s ease-in-out infinite reverse;
}

.shape-3 {
  width: 200px;
  height: 200px;
  background: linear-gradient(135deg, #4facfe, #00f2fe);
  top: 50%;
  right: 20%;
  animation: float 12s ease-in-out infinite;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-30px) rotate(10deg);
  }
}

/* 登录卡片 */
.login-card {
  display: flex;
  width: 900px;
  max-width: 95%;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.4);
  overflow: hidden;
  position: relative;
  z-index: 10;
  backdrop-filter: blur(10px);
}

/* 左侧装饰区 */
.login-left {
  flex: 1;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 50px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  color: white;
  position: relative;
  overflow: hidden;
}

.login-left::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 60%);
  animation: rotate 20s linear infinite;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.left-content {
  position: relative;
  z-index: 1;
  text-align: center;
}

.logo-icon {
  width: 100px;
  height: 100px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 24px;
  backdrop-filter: blur(5px);
  border: 2px solid rgba(255, 255, 255, 0.3);
}

.logo-icon .el-icon {
  color: white;
}

.system-title {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 8px;
  letter-spacing: 2px;
}

.system-desc {
  font-size: 14px;
  opacity: 0.8;
  margin-bottom: 40px;
  letter-spacing: 1px;
}

.feature-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.feature-item {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  font-size: 15px;
  opacity: 0.9;
}

.feature-item .el-icon {
  background: rgba(255, 255, 255, 0.2);
  padding: 6px;
  border-radius: 50%;
  font-size: 12px;
}

/* 右侧登录表单 */
.login-right {
  flex: 1;
  padding: 50px 45px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.login-header h2 {
  font-size: 28px;
  font-weight: 600;
  color: #1a1a2e;
  margin-bottom: 8px;
}

.login-header p {
  font-size: 14px;
  color: #999;
}

/* 表单样式 */
.login-form {
  margin-bottom: 30px;
}

.login-form :deep(.el-input__wrapper) {
  padding: 8px 12px;
  border-radius: 10px;
  box-shadow: 0 0 0 1px #e4e7ed inset;
  transition: all 0.3s;
}

.login-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #667eea inset;
}

.login-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #667eea inset;
}

.login-form :deep(.el-input__inner) {
  height: 40px;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 24px;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.form-options :deep(.el-checkbox__label) {
  color: #666;
}

/* 登录按钮 */
.login-btn {
  width: 100%;
  height: 48px;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 4px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

.btn-loading {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

/* 底部 */
.login-footer {
  text-align: center;
}

.copyright {
  font-size: 12px;
  color: #bbb;
}

/* 错误弹窗 */
.error-dialog-content {
  text-align: center;
  padding: 20px 0;
}

.error-icon {
  color: #f5576c;
  margin-bottom: 16px;
}

.error-dialog-content p {
  font-size: 15px;
  color: #333;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-card {
    flex-direction: column;
    width: 95%;
  }

  .login-left {
    padding: 30px 20px;
    display: none;
  }

  .login-right {
    padding: 40px 25px;
  }

  .system-title {
    font-size: 22px;
  }

  .login-header h2 {
    font-size: 24px;
  }
}
</style>