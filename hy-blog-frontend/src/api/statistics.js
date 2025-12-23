import request from '@/utils/request'

// 获取统计数据
export function getSiteStats() {
  return request({
    url: '/front/statistics/info',
    method: 'get'
  })
}

// 上报一次访问 (PV)
export function reportVisit() {
  return request({
    url: '/front/statistics/visit',
    method: 'post'
  })
}