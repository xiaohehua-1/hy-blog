/**
 * 标签 API
 * 前台：标签云列表（含各标签文章计数）
 */
import request from '@/utils/request'

/**
 * 获取标签列表，后端 Redis 缓存 1 小时
 */
export function getFrontTagList() {
  return request({
    url: '/front/tag/list',
    method: 'get'
  })
}