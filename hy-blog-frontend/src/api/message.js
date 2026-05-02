import request from '@/utils/request'

// 获取留言列表
export function getMessageList(current, size) {
  return request({
    url: '/front/message/list',
    method: 'get',
    params: { current, size }
  })
}

// 提交留言
export function saveMessage(data) {
  return request({
    url: '/front/message/save',
    method: 'post',
    data
  })
}