import axios from 'axios'
import { ElMessage } from 'element-plus'

// 1. 创建 axios 实例
const service = axios.create({
  baseURL: '/api', // 这里会触发 vite.config.js 的代理
  timeout: 5000 // 请求超时时间
})

// 2. 请求拦截器 (自动带上 Token)
service.interceptors.request.use(
  (config) => {
    // 从 localStorage 获取 token
    const token = localStorage.getItem('token')
    if (token) {
      // 【关键保留】你的后端使用的是 satoken 字段，千万别改
      config.headers['satoken'] = token
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 3. 响应拦截器 (统一处理结果)
service.interceptors.response.use(
  (response) => {
    const res = response.data
    
    // 如果后端返回的 code 不是 20000，说明有错误
    if (res.code !== 20000) {
      
      // 检测 Token 是否无效 ===
      // 这里我加了几个判断条件，只要命中其中一个，就强制踢出
      // 1. code 为 401 或 403 (常见权限错误码)
      // 2. 错误信息里包含 "token" 和 "无效" (模糊匹配你之前遇到的提示)
      const isTokenInvalid = 
          res.code === 401 || 
          res.code === 403 || 
          (res.message && res.message.includes('token') && res.message.includes('无效')) ||
          (res.message && res.message.includes('过期'));

      if (isTokenInvalid) {
        ElMessage.error('登录状态已失效，请重新登录')
        
        // 1. 清除本地缓存
        localStorage.removeItem('token')
        localStorage.removeItem('user') // 如果有的话
        
        // 2. 强制跳转回登录页 (延迟一下让用户看清提示)
        setTimeout(() => {
           window.location.href = '/login'
        }, 1000)
        
        return Promise.reject(new Error('Token Invalid'))
      }

      // 普通业务错误 (比如密码输错)
      ElMessage.error(res.message || '系统错误')
      return Promise.reject(new Error(res.message || 'Error'))
    }
    
    return res // 直接返回后端的数据主体
  },
  (error) => {
    console.error('请求错误:', error)
    // 处理 HTTP 状态码层面的 401 ===
    // 有时候后端不会返回 JSON，而是直接返回 HTTP 401
    if (error.response && error.response.status === 401) {
      ElMessage.error('登录已过期，请重新登录')
      localStorage.removeItem('token')
      setTimeout(() => {
         window.location.href = '/login'
      }, 1000)
    } else {
      ElMessage.error(error.message || '请求失败')
    }
    
    return Promise.reject(error)
  }
)

export default service