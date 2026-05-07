/**
 * 留言板 API
 * 前台：树形留言列表查询、提交留言
 */
import request from '@/utils/request'

/**
 * 分页获取树形留言列表（一级留言 + 子回复装配）
 */
export function getMessageList(current, size) {
  return request({
    url: '/front/message/list',
    method: 'get',
    params: { current, size }
  })
}

/**
 * 提交留言（游客，默认非管理员）
 */
export function saveMessage(data) {
  return request({
    url: '/front/message/save',
    method: 'post',
    data
  })
}