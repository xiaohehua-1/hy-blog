import request from '@/utils/request'

export function getCommentList(blogId, current, size) {
  return request({
    url: `/front/comment/list/${blogId}`,
    method: 'get',
    params: { current, size }
  })
}

export function saveComment(data) {
  return request({
    url: '/front/comment/save',
    method: 'post',
    data
  })
}