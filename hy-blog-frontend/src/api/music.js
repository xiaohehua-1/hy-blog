import request from '@/utils/request'

// 获取前台音乐列表
export function getMusicList() {
  return request({
    url: '/front/music/list',
    method: 'get'
  })
}

// 修改或新增：更新音乐接口
export function updateMusic(data) {
  return request({
    url: '/admin/music', // 注意：这里地址要和你后端的 @RequestMapping 一致，通常是 /admin/music
    method: 'put',
    data
  })
}