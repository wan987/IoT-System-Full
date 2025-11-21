<template>
  <div class="login-container">
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
                <el-input
                    v-model="loginForm.username"
                    placeholder="请输入账号"
                    :prefix-icon="User"
                    class="dark-input"
                />
              </el-form-item>
              <el-form-item>
                <el-input
                    v-model="loginForm.password"
                    type="password"
                    placeholder="请输入密码"
                    :prefix-icon="Lock"
                    show-password
                    class="dark-input"
                />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" class="neon-btn" @click="handleLogin" :loading="loading">
                  进入系统
                </el-button>
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
                <el-button type="success" class="neon-btn register-btn" @click="handleRegister">
                  确认注册
                </el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { User, Lock, DataLine } from '@element-plus/icons-vue'

const router = useRouter()
const activeTab = ref('login')
const loading = ref(false)

const loginForm = ref({ username: '', password: '' })
const registerForm = ref({ username: '', password: '', roleType: 1 })

const handleLogin = () => {
  if(!loginForm.value.username || !loginForm.value.password) {
    ElMessage.warning('请输入账号密码')
    return
  }
  loading.value = true
  setTimeout(() => {
    axios.post('http://localhost:8080/user/login', loginForm.value).then(res => {
      loading.value = false
      if (res.data.code === 200) {
        ElMessage.success('连接成功，正在进入...')
        localStorage.setItem('user', JSON.stringify(res.data.data))
        router.push('/home')
      } else {
        ElMessage.error(res.data.msg)
      }
    }).catch(() => { loading.value = false; ElMessage.error('服务器未响应') })
  }, 800)
}

const handleRegister = () => {
  axios.post('http://localhost:8080/user/register', registerForm.value).then(res => {
    if (res.data.code === 200) {
      ElMessage.success('注册成功')
      activeTab.value = 'login'
    } else { ElMessage.error(res.data.msg) }
  })
}
</script>

<style scoped>
/* 1. 背景图适配 */
.login-container {
  height: 100vh;
  width: 100%;
  /* 引用你的新图片 */
  background-image: url('../assets/bg.jpg');
  background-size: cover;
  background-position: center;
  display: flex;
  /* 关键：把内容推到右边 */
  justify-content: flex-end;
  align-items: center;
}

/* 2. 登录框定位 */
.login-wrapper {
  margin-right: 10%; /* 距离右边 10% 的位置，让出左边给水滴 */
  width: 400px;
}

/* 3. 深色磨砂玻璃核心 */
.login-box {
  padding: 40px;
  border-radius: 16px;
  /* 深蓝黑色半透明背景 */
  background: rgba(13, 25, 48, 0.65);
  /* 模糊滤镜 */
  backdrop-filter: blur(12px);
  /* 青色微光边框 */
  border: 1px solid rgba(0, 255, 255, 0.15);
  box-shadow: 0 0 40px rgba(0, 140, 255, 0.15);
  animation: fadeInRight 1s ease-out;
}

@keyframes fadeInRight {
  from { opacity: 0; transform: translateX(50px); }
  to { opacity: 1; transform: translateX(0); }
}

/* 4. 文字与图标发光效果 */
.login-header {
  text-align: center;
  margin-bottom: 30px;
}
.logo-icon {
  font-size: 48px;
  color: #00f2ff; /* 青色 */
  margin-bottom: 10px;
  filter: drop-shadow(0 0 10px rgba(0, 242, 255, 0.6)); /* 发光 */
}
.login-header h2 {
  margin: 0;
  font-size: 24px;
  color: #ffffff;
  letter-spacing: 2px;
  text-shadow: 0 0 10px rgba(0, 242, 255, 0.3);
}
.login-header p {
  margin: 8px 0 0;
  color: #00f2ff;
  font-size: 12px;
  opacity: 0.8;
  letter-spacing: 3px;
}

/* 5. 暗黑模式输入框 */
/* 穿透 element-plus 样式 */
.dark-input :deep(.el-input__wrapper) {
  background-color: rgba(0, 0, 0, 0.3) !important;
  border: 1px solid rgba(0, 255, 255, 0.2);
  box-shadow: none;
  color: white;
}
.dark-input :deep(.el-input__inner) {
  color: white !important;
}
.dark-input :deep(.el-input__wrapper.is-focus) {
  border-color: #00f2ff;
  box-shadow: 0 0 10px rgba(0, 242, 255, 0.2) !important;
}

/* 6. 霓虹按钮 */
.neon-btn {
  width: 100%;
  height: 44px;
  font-weight: bold;
  letter-spacing: 2px;
  border: none;
  /* 青色渐变 */
  background: linear-gradient(90deg, #00c6ff, #0072ff);
  box-shadow: 0 0 20px rgba(0, 198, 255, 0.4);
  transition: all 0.3s;
}
.neon-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 0 30px rgba(0, 198, 255, 0.6);
}

/* 7. 注册按钮颜色微调 (紫色系) */
.register-btn {
  background: linear-gradient(90deg, #bc4e9c, #f80759);
  box-shadow: 0 0 20px rgba(248, 7, 89, 0.4);
}

/* 8. 暗色 Tabs */
.dark-tabs :deep(.el-tabs__item) {
  color: rgba(255, 255, 255, 0.6);
}
.dark-tabs :deep(.el-tabs__item.is-active) {
  color: #00f2ff;
}
.dark-tabs :deep(.el-tabs__active-bar) {
  background-color: #00f2ff;
  box-shadow: 0 0 10px #00f2ff;
}

/* 9. 单选框适配 */
.dark-radio {
  background: transparent !important;
  border-color: rgba(0, 255, 255, 0.2) !important;
}
.dark-radio :deep(.el-radio__label) {
  color: white !important;
}
.dark-radio :deep(.el-radio__input.is-checked .el-radio__inner) {
  border-color: #00f2ff;
  background: #00f2ff;
}
.dark-radio :deep(.el-radio__input.is-checked + .el-radio__label) {
  color: #00f2ff !important;
}
</style>