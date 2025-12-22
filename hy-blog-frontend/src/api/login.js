import request from '@/utils/request'

// 登录接口
export function login(data) {
  return request({
    url: '/login', // 对应后端 PostMapping("/login")
    method: 'post',
    data
  })
}

// 获取用户信息接口（登录后通常需要）
export function getInfo() {
  return request({
    url: '/user/info', 
    method: 'get'
  })
}