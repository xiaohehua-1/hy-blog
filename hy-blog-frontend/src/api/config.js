import request from '@/utils/request'

// 获取站点配置详情
export function getSiteConfig() {
  return request({
    url: '/front/config/detail',
    method: 'get'
  })
}
// 获取关于我信息 (复用同一个接口)
export function getAboutMe() {
  return request({
    url: '/front/config/detail',
    method: 'get'
  })
}