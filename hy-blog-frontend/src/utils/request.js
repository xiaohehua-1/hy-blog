/**
 * Axios 请求封装
 * - 请求拦截器：自动从 localStorage 读取 token 注入 satoken 请求头
 * - 响应拦截器：统一处理业务错误（code !== 20000）和 HTTP 401，token 失效自动踢回登录页
 */
import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建 axios 实例，baseURL 通过 Vite 代理转发到后端
const service = axios.create({
  baseURL: '/api',
  timeout: 5000
})

// 请求拦截器：自动附带 token
service.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      // 后端 Sa-Token 框架要求请求头字段名为 satoken，不能改
      config.headers['satoken'] = token
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器：统一错误处理 + token 失效检测
service.interceptors.response.use(
  (response) => {
    const res = response.data

    if (res.code !== 20000) {
      // token 失效判定：HTTP 错误码 + 后端消息关键字双重匹配
      const isTokenInvalid =
          res.code === 401 ||
          res.code === 403 ||
          (res.message && res.message.includes('token') && res.message.includes('无效')) ||
          (res.message && res.message.includes('过期'));

      if (isTokenInvalid) {
        ElMessage.error('登录状态已失效，请重新登录')
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        // 延迟 1s 跳转，让用户看清提示
        setTimeout(() => {
           window.location.href = '/login'
        }, 1000)
        return Promise.reject(new Error('Token Invalid'))
      }

      // 普通业务错误（如密码错误、参数校验失败）
      ElMessage.error(res.message || '系统错误')
      return Promise.reject(new Error(res.message || 'Error'))
    }

    return res
  },
  (error) => {
    console.error('请求错误:', error)
    // HTTP 状态码层面的 401：后端直接返回 401 而非 JSON 时的兜底处理
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