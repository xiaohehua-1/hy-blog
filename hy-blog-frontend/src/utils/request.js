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
      // Sa-Token 默认读取 header 里的 satoken 字段
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
      ElMessage.error(res.message || '系统错误')
      return Promise.reject(new Error(res.message || 'Error'))
    }
    
    return res // 直接返回后端的数据主体
  },
  (error) => {
    console.error('请求错误:', error)
    ElMessage.error(error.message || '请求失败')
    return Promise.reject(error)
  }
)

export default service