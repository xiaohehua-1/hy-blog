import request from '@/utils/request'

// 获取前台音乐列表
export function getMusicList() {
  return request({
    url: '/front/music/list',
    method: 'get'
  })
}

// 更新音乐接口
export function updateMusic(data) {
  return request({
    url: '/admin/music', 
    method: 'put',
    data
  })
}