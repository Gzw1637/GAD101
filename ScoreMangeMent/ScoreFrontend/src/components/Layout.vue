<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '80px' : '180px'"  class="layout-aside" :class="{ 'collapsed': isCollapse }" >
      <div class="sidebar-header">
        <el-button
            type="text"
            class="collapse-btn"
            @click="isCollapse = !isCollapse"
        >
          <el-icon v-if="isCollapse"><ArrowRight /></el-icon>
          <el-icon v-else><ArrowLeft /></el-icon>
        </el-button>
      </div>
      <el-menu
          :default-active="activeMenu"
          class="layout-menu"
          :collapse="isCollapse"
          @select="handleMenuSelect"
          router
      >
        <slot name="sidebar"></slot>
<!--        <el-menu-item index="logout">-->
<!--          <template #icon>-->
<!--            <el-icon><SwitchButton /></el-icon>-->
<!--          </template>-->
<!--          <span v-if="!isCollapse">退出</span>-->
<!--        </el-menu-item>-->
      </el-menu>
    </el-aside>

    <!-- 主内容区 -->
    <el-container>
      <!-- 页眉 -->
      <el-header class="layout-header">
        <div class="header-left">
          <img src="@/assets/re-data.png" alt="" style="width: 40px;height: 40px;margin-right: 10px">
          <span class="logo-text">教育管理系统</span>
        </div>
        <div class="header-right">
          <div class="user-info">
            <el-avatar :size="32" :src="userAvatar"></el-avatar>
            <span class="user-name">{{ userName }}</span>
          </div>
          <el-button type="text" style="color: #f0f2f5" @click="handleLogout">退出登录</el-button>
        </div>
      </el-header>

      <!-- 内容区 -->
      <el-main class="layout-main">
        <slot></slot>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft, ArrowRight, SwitchButton } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  activeMenu: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['menu-select'])

const router = useRouter()
const isCollapse = ref(false)
const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))

const userName = computed(() => userInfo.value.name || '用户')
const userAvatar = computed(() => 'https://img.icons8.com/color/48/000000/user.png')

const handleMenuSelect = (key, keyPath) => {
  if (key === 'logout') {
    handleLogout()
  } else if (key !== 'collapse') {
    emit('menu-select', key, keyPath)
  }
}

const handleLogout = () => {
  localStorage.removeItem('userInfo')
  router.push('/login')
  ElMessage.success('退出登录成功')
}

onMounted(() => {
  // 检查用户是否登录
  if (!userInfo.value.userName) {
    router.push('/login')
  }
})
</script>

<style scoped>
.layout-container {
  height: 100vh;
  overflow: hidden;
}

.layout-aside {
  background-color: #f0f2f5;
  border-right: 1px solid #e4e7ed;
  transition: width 0.3s ease;
}

.sidebar-header {
  display: flex;
  justify-content: center;
  padding: 10px 0;
  border-bottom: 1px solid #e4e7ed;
}

.collapse-btn {
  color: #333;
  font-size: 16px;
  padding: 8px;
}

.layout-menu {
  margin-top: 10px;
}

.layout-header {
  background-color: #409eff;
  color: white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  height: 60px;
}

.header-left {
  display: flex;
  align-items: center;
}

.logo-text {
  margin-left: 12px;
  font-size: 18px;
  font-weight: bold;
  color: white;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  margin-right: 20px;
}

.user-name {
  margin-left: 10px;
  font-size: 14px;
  color: white;
}

.layout-main {
  background-color: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .layout-aside {
    width: 64px !important;
  }

  .logo-text {
    display: none;
  }
}
</style>