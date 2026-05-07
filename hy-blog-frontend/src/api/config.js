/**
 * 站点配置 API
 * 前台：全局配置、关于我页面（复用同一接口）
 */
import request from '@/utils/request'

/**
 * 获取站点全局配置（站长信息、社交链接等）
 */
export function getSiteConfig() {
  return request({
    url: '/front/config/detail',
    method: 'get'
  })
}

/**
 * 获取"关于我"页面内容，与站点配置共用后端接口
 */
export function getAboutMe() {
  return request({
    url: '/front/config/detail',
    method: 'get'
  })
}