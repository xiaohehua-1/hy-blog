<template>
  <div class="login-container">
    <div class="bubbles">
      <div v-for="n in 10" :key="n" class="bubble"></div>
    </div>

    <div class="login-card">
      <div class="login-header">
        <img src="@/assets/images/avatar.png" alt="Logo" class="logo" />
        <h2 class="title">HyBlog Admin</h2>
        <p class="subtitle">后台管理系统登录</p>
      </div>

      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="rules"
        class="login-form"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            :prefix-icon="User"
            size="large"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            show-password
            size="large"
          />
        </el-form-item>

        <el-button
          :loading="loading"
          type="primary"
          size="large"
          class="login-btn"
          @click="handleLogin"
        >
          {{ loading ? '登录中...' : '立即登录' }}
        </el-button>
      </el-form>
      
      <div class="login-footer">
        <p>Copyright © 2026 HyBlog</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { User, Lock } from '@element-plus/icons-vue'
// 【关键修正 1】 改回引入你封装的 request 工具
import request from '@/utils/request' 
import { ElMessage } from 'element-plus'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute() // 引入 route 为了处理 redirect
const loginFormRef = ref(null)
const loading = ref(false)

// 表单数据 (保持你原本的结构)
const loginForm = reactive({
  username: '',
  password: ''
})

// 表单校验规则 (保持你原本的规则)
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

// 登录处理函数 (逻辑完全基于你提供的代码进行微调优化)
const handleLogin = () => {
  loginFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    loading.value = true
    try {
      // 【关键修正 2】 路径改回你代码里的 '/admin/login'
      const res = await request.post('/admin/login', loginForm)
      
      // 注意：根据 request.js 的拦截器，如果 code=20000，res 就是后端返回的完整数据对象
      // 你的后端 R 类结构是 { code, message, data }
      
      // 【关键修正 3】 按照你原本的写法获取 Token
      // 如果后端返回 data: { token: 'xxx' }，那么这里就是 res.data.token
      const token = res.data.token
      
      if (token) {
        localStorage.setItem('token', token)
        
        // 顺便存一下用户信息，方便展示 (如果后端 login 接口没返回 user，这行可以删掉)
        // localStorage.setItem('user', JSON.stringify(res.data.user)) 

        ElMessage.success('登录成功，欢迎回来！')
        
        // 优化：如果有重定向地址就去重定向地址，没有就去 dashboard
        const redirect = route.query.redirect || '/admin/dashboard'
        router.push(redirect)
      } else {
        // 防御性编程：万一 token 是空的
        ElMessage.warning('登录异常：未获取到令牌')
      }
      
    } catch (error) {
      console.error('登录报错:', error)
      // request.js 拦截器通常会弹出错误提示，这里不用重复弹
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
/* ==============================================
   以下是样式部分 (Glassmorphism 风格)
   只改变外观，不影响上面 Script 的逻辑功能
   ============================================== */

/* 容器：背景渐变 + 防止滚动 */
.login-container {
  position: relative;
  width: 100%;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  /* 这里的背景色可以根据喜好改 */
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

/* 登录卡片：磨砂玻璃效果 */
.login-card {
  position: relative;
  z-index: 10;
  width: 400px; /* 和你原本的宽度一致 */
  padding: 40px 35px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.15); /* 半透明白 */
  backdrop-filter: blur(20px); /* 模糊背景 */
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
  color: #fff;
  transition: transform 0.3s ease;
}

.login-card:hover {
  transform: translateY(-5px);
}

/* 头部内容 */
.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.logo {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  border: 4px solid rgba(255, 255, 255, 0.3);
  margin-bottom: 15px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.title {
  font-size: 26px;
  font-weight: 600;
  margin-bottom: 8px;
  color: #fff;
}

.subtitle {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
}

/* --- 覆盖 Element Plus 输入框样式，让它变透明 --- */
:deep(.el-input__wrapper) {
  background-color: rgba(255, 255, 255, 0.1) !important;
  border: 1px solid rgba(255, 255, 255, 0.2) !important;
  box-shadow: none !important;
  border-radius: 8px;
}

:deep(.el-input__wrapper.is-focus) {
  background-color: rgba(255, 255, 255, 0.2) !important;
  border-color: rgba(255, 255, 255, 0.5) !important;
}

:deep(.el-input__inner) {
  color: #fff !important;
  height: 40px;
}

/* 占位符颜色 */
:deep(.el-input__inner::placeholder) {
  color: rgba(255, 255, 255, 0.6);
}

:deep(.el-input__prefix-inner) {
  color: rgba(255, 255, 255, 0.8);
}

/* 按钮样式 */
.login-btn {
  width: 100%;
  height: 45px;
  font-size: 16px;
  letter-spacing: 2px;
  border-radius: 8px;
  background: linear-gradient(90deg, #00c6fb 0%, #005bea 100%);
  border: none;
  margin-top: 10px;
}

.login-btn:hover {
  opacity: 0.9;
  transform: scale(1.02);
}

.login-footer {
  margin-top: 30px;
  text-align: center;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
}

/* --- 动态气泡动画 (可选，觉得花哨可以删掉 .bubbles 整个 div) --- */
.bubbles {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  z-index: 1;
}

.bubble {
  position: absolute;
  bottom: -100px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  animation: rise 15s infinite ease-in;
}

.bubble:nth-child(1) { width: 40px; height: 40px; left: 10%; animation-duration: 8s; }
.bubble:nth-child(2) { width: 20px; height: 20px; left: 20%; animation-duration: 5s; animation-delay: 1s; }
.bubble:nth-child(3) { width: 50px; height: 50px; left: 35%; animation-duration: 10s; animation-delay: 2s; }
.bubble:nth-child(4) { width: 80px; height: 80px; left: 50%; animation-duration: 11s; animation-delay: 0s; }
.bubble:nth-child(6) { width: 45px; height: 45px; left: 65%; animation-duration: 8s; animation-delay: 3s; }
.bubble:nth-child(7) { width: 90px; height: 90px; left: 70%; animation-duration: 12s; animation-delay: 2s; }
.bubble:nth-child(9) { width: 15px; height: 15px; left: 85%; animation-duration: 5s; animation-delay: 1s; }

@keyframes rise {
  0% { bottom: -100px; transform: translateX(0); }
  50% { transform: translateX(100px); }
  100% { bottom: 1080px; transform: translateX(-200px); }
}
</style>