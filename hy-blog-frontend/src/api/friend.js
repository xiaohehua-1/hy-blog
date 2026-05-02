import request from '@/utils/request'

// 获取友链列表
export function getFriendList() {
  return request({
    url: '/front/friend/list',
    method: 'get'
  })
}

// 申请友链
export function applyFriendLink(data) {
  return request({
    url: '/front/friend/apply',
    method: 'post',
    data
  })
}