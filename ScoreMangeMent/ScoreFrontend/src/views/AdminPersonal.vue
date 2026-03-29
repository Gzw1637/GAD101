<template>
  <div class="admin-personal-content">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>个人信息</span>
        </div>
      </template>
      
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="状态" prop="userStatus">
          <el-input v-model="formData.userStatus" placeholder="请选择状态" disabled />
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'

// 表单数据
const formData = reactive({
  userStatus: ''
})

// 加载状态
const loading = ref(false)

// 获取当前用户信息
const loadUserInfo = async () => {
  loading.value = true
  try {
    // 直接设置状态为有效，不需要调用API
    formData.userStatus = '有效'
  } catch (error) {
    console.error('获取用户信息失败:', error)
    ElMessage.error('网络错误，请检查网络连接')
  } finally {
    loading.value = false
  }
}

// 组件挂载时加载数据
onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.admin-personal-content {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>