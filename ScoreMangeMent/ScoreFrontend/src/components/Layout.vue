<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '80px' : '220px'" class="layout-aside" :class="{ 'collapsed': isCollapse }">
      <!-- Logo区域 -->
      <div class="sidebar-header">
        <div class="logo-box">
          <img src="@/assets/re-data.png" alt="logo" class="logo-img">
          <span v-if="!isCollapse" class="logo-text">成绩管理系统</span>
        </div>
        <el-button
            type="text"
            class="collapse-btn"
            @click="isCollapse = !isCollapse"
        >
          <el-icon v-if="isCollapse"><DArrowRight /></el-icon>
          <el-icon v-else><DArrowLeft /></el-icon>
        </el-button>
      </div>

      <el-menu
          :default-active="activeMenu"
          class="layout-menu"
          :collapse="isCollapse"
          :collapse-transition="false"
          @select="handleMenuSelect"
          background-color="transparent"
          text-color="#a8abb2"
          active-text-color="#409eff"
      >
        <slot name="sidebar"></slot>
      </el-menu>
    </el-aside>

    <!-- 主内容区 -->
    <el-container>
      <!-- 顶部导航 -->
      <el-header class="layout-header">
        <div class="header-left">
          <breadcrumb class="header-breadcrumb" />
        </div>

        <div class="header-right">
          <!-- 欢迎语 -->
          <div class="welcome-text">
            <el-icon><User /></el-icon>
            <span>欢迎，{{ userName }}</span>
          </div>

          <el-divider direction="vertical" />

          <!-- 头像+退出 -->
          <div class="user-actions">
            <el-dropdown @command="handleCommand">
              <div class="user-avatar-box">
                <el-avatar :size="36" :src="userAvatar" class="user-avatar" />
                <span class="user-name">{{ userName }}</span>
                <el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">
                    <el-icon><User /></el-icon>
                    个人中心
                  </el-dropdown-item>
                  <el-dropdown-item command="password">
                    <el-icon><Lock /></el-icon>
                    修改密码
                  </el-dropdown-item>
                  <el-dropdown-item divided command="logout">
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </el-header>

      <!-- 内容区 -->
      <el-main class="layout-main">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { DArrowLeft, DArrowRight, User, Lock, SwitchButton, ArrowDown } from '@element-plus/icons-vue'
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
const userAvatar = computed(() => 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png')

const handleMenuSelect = (key, keyPath) => {
  if (key === 'logout') {
    handleLogout()
  } else if (key !== 'collapse') {
    emit('menu-select', key, keyPath)
  }
}

const handleCommand = (command) => {
  const role = userInfo.value.role
  switch (command) {
    case 'profile':
      // 根据用户角色跳转到对应的个人信息页面
      if (role === 14981003) {
        router.push('/admin/personal')
      } else if (role === 14981002) {
        router.push('/teacher/personal')
      } else if (role === 14981001) {
        router.push('/student/personal')
      } else {
        ElMessage.warning('未知角色，无法访问个人中心')
      }
      break
    case 'password':
      // 根据用户角色跳转到对应的修改密码页面
      if (role === 14981003) {
        router.push('/admin/change-password')
      } else if (role === 14981002) {
        router.push('/teacher/change-password')
      } else if (role === 14981001) {
        router.push('/student/change-password')
      } else {
        ElMessage.warning('未知角色，无法访问修改密码')
      }
      break
    case 'logout':
      handleLogout()
      break
  }
}

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    localStorage.removeItem('userInfo')
    localStorage.removeItem('token')
    localStorage.removeItem('rememberMe')
    router.push('/login')
    ElMessage.success('退出登录成功')
  }).catch(() => {})
}

import { ElMessageBox } from 'element-plus'

onMounted(() => {
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

/* 侧边栏 */
.layout-aside {
  background: linear-gradient(180deg, #1d1e23 0%, #232428 100%);
  border-right: 1px solid #2d2f33;
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 12px;
  border-bottom: 1px solid #2d2f33;
  height: 60px;
}

.logo-box {
  display: flex;
  align-items: center;
  gap: 10px;
  overflow: hidden;
}

.logo-img {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  flex-shrink: 0;
}

.logo-text {
  font-size: 16px;
  font-weight: 600;
  color: #fff;
  white-space: nowrap;
  letter-spacing: 1px;
}

.collapse-btn {
  color: #909399;
  padding: 4px 8px;
  border-radius: 4px;
  transition: all 0.3s;
}

.collapse-btn:hover {
  color: #409eff;
  background: rgba(64, 158, 255, 0.1);
}

/* 菜单 */
.layout-menu {
  border-right: none;
  flex: 1;
  padding: 8px 0;
}

.layout-menu :deep(.el-menu-item) {
  margin: 4px 8px;
  border-radius: 8px;
  height: 44px;
  line-height: 44px;
  transition: all 0.3s;
}

.layout-menu :deep(.el-menu-item:hover) {
  background: rgba(64, 158, 255, 0.1) !important;
}

.layout-menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.2) 0%, rgba(103, 58, 183, 0.1) 100%) !important;
  color: #409eff !important;
}

.layout-menu :deep(.el-menu-item.is-active::before) {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 20px;
  background: #409eff;
  border-radius: 0 3px 3px 0;
}

/* 顶部导航 */
.layout-header {
  background: linear-gradient(90deg, #fff 0%, #f8f9fa 100%);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  height: 60px;
  border-bottom: 1px solid #ebeef5;
}

.header-left {
  display: flex;
  align-items: center;
}

.header-breadcrumb {
  font-size: 14px;
}

.header-right {
  display: flex;
  align-items: center;
}

.welcome-text {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #606266;
  font-size: 14px;
}

.el-divider--vertical {
  height: 24px;
  margin: 0 16px;
  border-color: #e4e7ed;
}

.user-actions {
  display: flex;
  align-items: center;
}

.user-avatar-box {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 12px;
  border-radius: 20px;
  transition: all 0.3s;
}

.user-avatar-box:hover {
  background: rgba(64, 158, 255, 0.1);
}

.user-avatar {
  border: 2px solid #e4e7ed;
  transition: border-color 0.3s;
}

.user-avatar-box:hover .user-avatar {
  border-color: #409eff;
}

.user-name {
  color: #303133;
  font-size: 14px;
  font-weight: 500;
}

/* 内容区 */
.layout-main {
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8eb 100%);
  padding: 20px;
  overflow-y: auto;
  min-height: calc(100vh - 60px);
}

/* 过渡动画 */
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.3s ease;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* 响应式 */
@media (max-width: 768px) {
  .layout-aside {
    position: fixed;
    z-index: 100;
    height: 100%;
  }

  .logo-text {
    display: none;
  }

  .welcome-text span {
    display: none;
  }
}
</style>