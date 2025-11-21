<template>
  <div class="login-container">

    <div class="bg-carousel">
      <div
          v-for="(img, index) in bgList"
          :key="index"
          class="bg-slide"
          :style="{ backgroundImage: `url(${img})` }"
          :class="{ active: index === currentBgIndex }"
      ></div>
    </div>

    <div class="login-overlay"></div>

    <div class="login-wrapper">
      <div class="login-box">
        <div class="login-header">
          <div class="logo-icon">
            <el-icon><DataLine /></el-icon>
          </div>
          <h2 class="glow-text">智慧水质监测云平台</h2>
          <p>IoT Water Quality Analyzer</p>
        </div>

        <el-tabs v-model="activeTab" class="dark-tabs" stretch>
          <el-tab-pane label="账号登录" name="login">
            <el-form :model="loginForm" size="large" class="login-form">
              <el-form-item>
                <el-input v-model="loginForm.username" placeholder="请输入账号" :prefix-icon="User" class="dark-input"/>
              </el-form-item>
              <el-form-item>
                <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" :prefix-icon="Lock" show-password class="dark-input"/>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" class="neon-btn" @click="handleLogin" :loading="loading">进入系统</el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>

          <el-tab-pane label="注册新用户" name="register">
            <el-form :model="registerForm" size="large" class="login-form">
              <el-form-item>
                <el-input v-model="registerForm.username" placeholder="设置账号" :prefix-icon="User" class="dark-input"/>
              </el-form-item>
              <el-form-item>
                <el-input v-model="registerForm.password" type="password" placeholder="设置密码" :prefix-icon="Lock" show-password class="dark-input"/>
              </el-form-item>
              <el-form-item>
                <el-radio-group v-model="registerForm.roleType" class="role-radio">
                  <el-radio :label="0" border class="dark-radio">超级管理员</el-radio>
                  <el-radio :label="1" border class="dark-radio">数据管理员</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item>
                <el-button type="success" class="neon-btn register-btn" @click="handleRegister">确认注册</el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { User, Lock, DataLine } from '@element-plus/icons-vue'

// --- 导入图片 (Vite 方式) ---
// 使用 new URL(..., import.meta.url).href 是 Vite 中动态引用的标准写法
// 或者直接 import 也可以，这里为了稳妥用相对路径 import
import bg1 from '../assets/bg1.jpg'
import bg2 from '../assets/bg2.jpg'
import bg3 from '../assets/bg3.jpg'
import bg4 from '../assets/bg4.jpg'

const router = useRouter()
const activeTab = ref('login')
const loading = ref(false)
const loginForm = ref({ username: '', password: '' })
const registerForm = ref({ username: '', password: '', roleType: 1 })

// --- 背景轮播逻辑 ---
const bgList = [bg1, bg2, bg3, bg4]
const currentBgIndex = ref(0)
let timer = null

const startCarousel = () => {
  timer = setInterval(() => {
    // 循环切换索引: 0 -> 1 -> 2 -> 3 -> 0 ...
    currentBgIndex.value = (currentBgIndex.value + 1) % bgList.length
  }, 6000) // 6秒切一次
}

// --- 登录注册逻辑 (保持不变) ---
const handleLogin = () => {
  if(!loginForm.value.username || !loginForm.value.password) { ElMessage.warning('请输入账号密码'); return }
  loading.value = true
  setTimeout(() => {
    axios.post('http://localhost:8080/user/login', loginForm.value).then(res => {
      loading.value = false
      if (res.data.code === 200) {
        ElMessage.success('连接成功，正在进入...')
        localStorage.setItem('user', JSON.stringify(res.data.data))
        router.push('/home')
      } else { ElMessage.error(res.data.msg) }
    }).catch(() => { loading.value = false; ElMessage.error('服务器未响应') })
  }, 800)
}

const handleRegister = () => {
  axios.post('http://localhost:8080/user/register', registerForm.value).then(res => {
    if (res.data.code === 200) { ElMessage.success('注册成功'); activeTab.value = 'login' }
    else { ElMessage.error(res.data.msg) }
  })
}

// 生命周期
onMounted(() => {
  startCarousel()
})

onUnmounted(() => {
  if (timer) clearInterval(timer) // 离开页面销毁定时器，防止内存泄漏
})
</script>

<style scoped>
/* 1. 容器布局 */
.login-container {
  height: 100vh;
  width: 100%;
  position: relative;
  overflow: hidden; /* 防止图片放大溢出 */
  display: flex;
  justify-content: flex-end; /* 内容靠右 */
  align-items: center;
}

/* 2. 背景轮播层 (绝对定位铺满屏幕) */
.bg-carousel {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: -2; /* 最底层 */
}

.bg-slide {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  opacity: 0; /* 默认隐藏 */
  transition: opacity 1.5s ease-in-out, transform 6s ease-in-out; /* 核心动画参数 */
  transform: scale(1);
}

/* 激活状态：显示并放大 */
.bg-slide.active {
  opacity: 1;
  transform: scale(1.05); /* 缓慢放大到 1.05倍，营造呼吸感 */
  z-index: 1;
}

/* 3. 遮罩层 (加深一点) */
.login-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.3); /* 30% 黑色遮罩 */
  z-index: -1; /* 在背景之上，内容之下 */
  backdrop-filter: blur(2px);
}

/* 4. 登录框定位与样式 (复用之前的) */
.login-wrapper { margin-right: 10%; width: 400px; position: relative; z-index: 10; }

.login-box {
  padding: 40px;
  border-radius: 16px;
  background: rgba(13, 25, 48, 0.65);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(0, 255, 255, 0.15);
  box-shadow: 0 0 40px rgba(0, 140, 255, 0.15);
  animation: fadeInRight 1s ease-out;
}

@keyframes fadeInRight {
  from { opacity: 0; transform: translateX(50px); }
  to { opacity: 1; transform: translateX(0); }
}

/* 头部样式 */
.login-header { text-align: center; margin-bottom: 30px; }
.logo-icon { font-size: 48px; color: #00f2ff; margin-bottom: 10px; filter: drop-shadow(0 0 10px rgba(0, 242, 255, 0.6)); }
.login-header h2 { margin: 0; font-size: 24px; color: #ffffff; letter-spacing: 2px; text-shadow: 0 0 10px rgba(0, 242, 255, 0.3); }
.login-header p { margin: 8px 0 0; color: #00f2ff; font-size: 12px; opacity: 0.8; letter-spacing: 3px; }

/* 输入框样式 */
.dark-input :deep(.el-input__wrapper) { background-color: rgba(0, 0, 0, 0.3) !important; border: 1px solid rgba(0, 255, 255, 0.2); box-shadow: none; color: white; }
.dark-input :deep(.el-input__inner) { color: white !important; }
.dark-input :deep(.el-input__wrapper.is-focus) { border-color: #00f2ff; box-shadow: 0 0 10px rgba(0, 242, 255, 0.2) !important; }

/* 按钮样式 */
.neon-btn { width: 100%; height: 44px; font-weight: bold; letter-spacing: 2px; border: none; background: linear-gradient(90deg, #00c6ff, #0072ff); box-shadow: 0 0 20px rgba(0, 198, 255, 0.4); transition: all 0.3s; }
.neon-btn:hover { transform: scale(1.05); box-shadow: 0 0 30px rgba(0, 198, 255, 0.6); }
.register-btn { background: linear-gradient(90deg, #bc4e9c, #f80759); box-shadow: 0 0 20px rgba(248, 7, 89, 0.4); }

/* 其他控件 */
.dark-tabs :deep(.el-tabs__item) { color: rgba(255, 255, 255, 0.6); }
.dark-tabs :deep(.el-tabs__item.is-active) { color: #00f2ff; }
.dark-tabs :deep(.el-tabs__active-bar) { background-color: #00f2ff; box-shadow: 0 0 10px #00f2ff; }
.dark-radio { background: transparent !important; border-color: rgba(0, 255, 255, 0.2) !important; }
.dark-radio :deep(.el-radio__label) { color: white !important; }
.dark-radio :deep(.el-radio__input.is-checked .el-radio__inner) { border-color: #00f2ff; background: #00f2ff; }
.dark-radio :deep(.el-radio__input.is-checked + .el-radio__label) { color: #00f2ff !important; }
.role-radio { width: 100%; display: flex; justify-content: space-between; }
.role-radio :deep(.el-radio) { margin-right: 0; width: 48%; border-radius: 10px; }
</style>