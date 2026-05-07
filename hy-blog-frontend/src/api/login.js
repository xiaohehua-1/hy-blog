/**
 * 登录认证 API
 * 管理员登录、获取当前用户信息
 */
import request from '@/utils/request'

/**
 * 管理员登录，验证用户名密码后返回 Sa-Token
 */
export function login(data) {
  return request({
    url: '/login',
    method: 'post',
    data
  })
}

/**
 * 获取当前登录用户信息（密码已由后端脱敏）
 */
export function getInfo() {
  return request({
    url: '/user/info',
    method: 'get'
  })
}