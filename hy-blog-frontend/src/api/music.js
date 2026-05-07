/**
 * 背景音乐 API
 * 前台：已启用音乐列表
 * 后台：更新音乐信息
 */
import request from '@/utils/request'

/**
 * 获取已启用的背景音乐列表（前台播放器）
 */
export function getMusicList() {
  return request({
    url: '/front/music/list',
    method: 'get'
  })
}

/**
 * 更新音乐信息/启用状态（后台管理）
 */
export function updateMusic(data) {
  return request({
    url: '/admin/music',
    method: 'put',
    data
  })
}