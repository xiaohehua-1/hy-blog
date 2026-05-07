/**
 * 站点统计 API
 * 前台：获取统计概览、上报页面访问量
 */
import request from '@/utils/request'

/**
 * 获取站点统计（浏览量来自 Redis，文章/评论/留言数来自 DB）
 */
export function getSiteStats() {
  return request({
    url: '/front/statistics/info',
    method: 'get'
  })
}

/**
 * 上报 PV +1（总访问量和今日访问量各 +1，Redis 原子自增）
 */
export function reportVisit() {
  return request({
    url: '/front/statistics/visit',
    method: 'post'
  })
}