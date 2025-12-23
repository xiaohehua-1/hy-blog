import request from '@/utils/request'

// 获取站点配置详情
export function getSiteConfig() {
  return request({
    url: '/front/config/detail',
    method: 'get'
  })
}