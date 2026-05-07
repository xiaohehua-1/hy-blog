/**
 * 友链 API
 * 前台：已审核通过的友链列表、友链申请
 */
import request from '@/utils/request'

/**
 * 获取审核通过的友链列表
 */
export function getFriendList() {
  return request({
    url: '/front/friend/list',
    method: 'get'
  })
}

/**
 * 提交友链申请，初始状态为待审核
 */
export function applyFriendLink(data) {
  return request({
    url: '/front/friend/apply',
    method: 'post',
    data
  })
}