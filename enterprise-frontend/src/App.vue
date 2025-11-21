<template>
  <div v-if="$route.path === '/login'" style="height: 100vh;">
    <router-view />
  </div>

  <div v-else class="common-layout">
    <el-container>

      <el-aside width="220px" class="custom-sidebar">

        <div class="sidebar-logo">
          <el-icon class="logo-icon"><Platform /></el-icon>
          <span class="logo-text">IoT 管理平台</span>
        </div>

        <div class="user-card">
          <div class="user-avatar">
            <el-icon><UserFilled /></el-icon>
          </div>
          <div class="user-info">
            <div class="username">{{ currentUser.username || 'Admin' }}</div>
            <div class="role-badge">
              {{ currentUser.roleType === 0 ? '超级管理员' : '数据管理员' }}
            </div>
          </div>
          <el-icon class="logout-btn" @click="logout" title="退出登录"><SwitchButton /></el-icon>
        </div>

        <el-menu
            background-color="#001529"
            text-color="#a6adb4"
            active-text-color="#fff"
            router
            :default-active="$route.path"
            class="custom-menu"
        >
          <el-menu-item index="/home">
            <el-icon><Odometer /></el-icon>
            <span>系统首页</span>
          </el-menu-item>

          <div class="menu-group-title">设备管理</div>

          <el-menu-item index="/station">
            <el-icon><OfficeBuilding /></el-icon><span>监测站点</span>
          </el-menu-item>
          <el-menu-item index="/gateway">
            <el-icon><Connection /></el-icon><span>网关设备</span>
          </el-menu-item>
          <el-menu-item index="/sensor">
            <el-icon><Cpu /></el-icon><span>传感器信息</span>
          </el-menu-item>

          <div class="menu-group-title">数据与运维</div>

          <el-menu-item index="/data">
            <el-icon><TrendCharts /></el-icon><span>实时监控</span>
          </el-menu-item>
          <el-menu-item index="/log">
            <el-icon><Document /></el-icon><span>维护日志</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <el-main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
// 引入需要的图标
import {
  Platform, UserFilled, SwitchButton,
  Odometer, OfficeBuilding, Connection, Cpu, TrendCharts, Document
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const currentUser = ref({})

const logout = () => {
  localStorage.removeItem('user')
  router.push('/login')
}

watch(() => route.path, (newPath) => {
  if (newPath !== '/login') {
    const userStr = localStorage.getItem('user')
    if (!userStr) {
      router.push('/login')
    } else {
      currentUser.value = JSON.parse(userStr)
    }
  }
}, { immediate: true })
</script>

<style>
/* 全局重置 */
body { margin: 0; padding: 0; background-color: #f0f2f5; }

.common-layout { height: 100vh; display: flex; }

/* --- 侧边栏样式 --- */
.custom-sidebar {
  background-color: #001529; /* 深夜蓝背景 */
  box-shadow: 2px 0 6px rgba(0,21,41,0.35);
  z-index: 10;
  display: flex;
  flex-direction: column;
  transition: width 0.3s;
  overflow-x: hidden;
}

/* 1. Logo 区域 */
.sidebar-logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #002140; /* 比侧边栏稍浅一点，做出层次 */
  color: white;
  font-size: 20px;
  font-weight: bold;
  letter-spacing: 1px;
}
.logo-icon {
  font-size: 24px;
  margin-right: 8px;
  color: #409EFF; /* 科技蓝图标 */
}
.logo-text {
  background: linear-gradient(to right, #fff, #b3d4fc);
  -webkit-background-clip: text;
  color: transparent;
}

/* 2. 用户卡片 */
.user-card {
  padding: 20px 15px;
  display: flex;
  align-items: center;
  border-bottom: 1px solid rgba(255,255,255,0.1);
  margin-bottom: 10px;
}
.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(255,255,255,0.2);
  display: flex;
  justify-content: center;
  align-items: center;
  color: white;
  margin-right: 12px;
}
.user-info {
  flex: 1;
}
.username {
  color: #fff;
  font-size: 14px;
  font-weight: 600;
}
.role-badge {
  display: inline-block;
  font-size: 10px;
  padding: 2px 6px;
  background: #409EFF;
  color: white;
  border-radius: 4px;
  margin-top: 4px;
  transform: scale(0.9);
  transform-origin: left;
}
.logout-btn {
  color: #909399;
  cursor: pointer;
  transition: color 0.3s;
}
.logout-btn:hover {
  color: #F56C6C; /* 退出变红 */
}

/* 3. 菜单样式微调 */
.custom-menu {
  border-right: none !important; /* 去掉 Element 默认的白边 */
}
.menu-group-title {
  padding: 10px 20px;
  font-size: 12px;
  color: #58636f;
  margin-top: 10px;
}

/* 菜单项悬停 */
.el-menu-item:hover {
  background-color: #001529 !important;
  color: #fff !important;
}

/* 菜单项选中态 (重点) */
.el-menu-item.is-active {
  background-color: #1890ff !important; /* 亮蓝色背景 */
  /* 或者用这种更现代的风格：背景透明，左边加条 */
  /* background-color: rgba(24, 144, 255, 0.15) !important; */
  /* border-right: 3px solid #1890ff; */
}

/* 右侧主体 */
.main-content {
  background-color: #f0f2f5; /* 浅灰底色，衬托卡片 */
  padding: 0; /* 让内部组件自己控制 padding */
  height: 100vh;
  overflow-y: auto;
}

/* 页面切换动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>