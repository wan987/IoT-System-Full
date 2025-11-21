<template>
  <div class="global-bg">
    <div
        v-for="(img, index) in bgList"
        :key="index"
        class="bg-slide"
        :style="{ backgroundImage: `url(${img})` }"
        :class="{ active: index === currentBgIndex }"
    ></div>
    <div class="bg-overlay"></div>
  </div>

  <div v-if="$route.path === '/login'" class="full-screen-view">
    <router-view />
  </div>

  <div v-else class="common-layout">
    <el-container>

      <el-aside width="220px" class="custom-sidebar glass-sidebar">
        <div class="sidebar-logo">
          <el-icon class="logo-icon"><Platform /></el-icon>
          <span class="logo-text">IoT 智能中枢</span>
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
            background-color="transparent"
            text-color="#ccc"
            active-text-color="#409EFF"
            router
            :default-active="$route.path"
            class="custom-menu"
        >
          <el-menu-item index="/home">
            <el-icon><Odometer /></el-icon><span>系统首页</span>
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

          <div class="menu-group-title">数据运维</div>
          <el-menu-item index="/data">
            <el-icon><TrendCharts /></el-icon><span>实时监控</span>
          </el-menu-item>
          <el-menu-item index="/log">
            <el-icon><Document /></el-icon><span>维护日志</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <el-main class="main-content transparent-main">
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
import { ref, watch, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
// 引入图标
import {
  Platform, UserFilled, SwitchButton,
  Odometer, OfficeBuilding, Connection, Cpu, TrendCharts, Document
} from '@element-plus/icons-vue'

// --- 引入背景图 (确保你的 src/assets 下有这些图片) ---
// 如果没有 bg6.jpg，请把你的月球图放进去并重命名
import bg1 from './assets/bg1.jpg'
import bg2 from './assets/bg2.jpg'
import bg3 from './assets/bg3.jpg'
import bg4 from './assets/bg4.jpg'
import bg6 from './assets/bg6.jpg'

const router = useRouter()
const route = useRoute()
const currentUser = ref({})

// --- 背景轮播逻辑 ---
const bgList = [bg6, bg3, bg2, bg4, bg1] // 月球图放在第一个
const currentBgIndex = ref(0)
let timer = null

onMounted(() => {
  timer = setInterval(() => {
    currentBgIndex.value = (currentBgIndex.value + 1) % bgList.length
  }, 8000) // 8秒切换一次
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})

// --- 用户逻辑 ---
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
body { margin: 0; padding: 0; overflow: hidden; font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', '微软雅黑', Arial, sans-serif; }

/* --- 核心：全局背景层 --- */
.global-bg { position: fixed; top: 0; left: 0; width: 100%; height: 100%; z-index: -1; background-color: #000; }
.bg-slide {
  position: absolute; top: 0; left: 0; width: 100%; height: 100%;
  background-size: cover; background-position: center;
  opacity: 0; transition: opacity 2s ease-in-out, transform 10s linear;
  transform: scale(1);
}
.bg-slide.active { opacity: 1; transform: scale(1.1); z-index: 1; }
.bg-overlay {
  position: absolute; top: 0; left: 0; width: 100%; height: 100%;
  background: rgba(15, 23, 42, 0.6); /* 深蓝灰色遮罩 */
  backdrop-filter: blur(3px); z-index: 2;
}

.full-screen-view { height: 100vh; position: relative; z-index: 10; }
.common-layout { height: 100vh; display: flex; position: relative; z-index: 10; }

/* --- 侧边栏 (磨砂玻璃) --- */
.glass-sidebar {
  background-color: rgba(0, 21, 41, 0.75) !important; /* 半透明黑蓝 */
  backdrop-filter: blur(15px);
  border-right: 1px solid rgba(255, 255, 255, 0.08);
}

.sidebar-logo {
  height: 64px; display: flex; align-items: center; justify-content: center;
  background-color: rgba(255, 255, 255, 0.05); /* 微亮 */
  color: #fff; font-size: 18px; font-weight: bold; letter-spacing: 1px;
}
.logo-icon { margin-right: 8px; color: #409EFF; font-size: 22px; }

.user-card {
  padding: 20px 15px; display: flex; align-items: center;
  border-bottom: 1px solid rgba(255,255,255,0.1); margin-bottom: 10px;
}
.user-avatar {
  width: 36px; height: 36px; border-radius: 50%; background: rgba(255,255,255,0.15);
  display: flex; justify-content: center; align-items: center; color: #fff; margin-right: 10px;
}
.user-info { flex: 1; }
.username { color: #fff; font-size: 14px; font-weight: 600; }
.role-badge {
  display: inline-block; font-size: 10px; padding: 1px 5px;
  background: #409EFF; color: white; border-radius: 3px; margin-top: 2px;
}
.logout-btn { color: #aaa; cursor: pointer; transition: 0.3s; }
.logout-btn:hover { color: #F56C6C; }

/* 菜单样式 */
.custom-menu { border-right: none !important; background: transparent !important; }
.menu-group-title { padding: 10px 20px; font-size: 12px; color: #6b778c; margin-top: 5px; }
.el-menu-item:hover { background-color: rgba(255, 255, 255, 0.1) !important; color: #fff !important; }
.el-menu-item.is-active {
  background: linear-gradient(90deg, rgba(64, 158, 255, 0.2), transparent) !important;
  border-left: 3px solid #409EFF; color: #409EFF !important;
}

/* 右侧透明 */
.transparent-main { background: transparent !important; padding: 0; overflow-y: auto; }

/* 动画 */
.fade-enter-active, .fade-leave-active { transition: opacity 0.3s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>