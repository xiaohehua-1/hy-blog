/**
 * 文章评论 API
 * 前台：评论列表查询、提交评论
 */
import request from '@/utils/request'

/**
 * 获取指定文章下的树形评论列表（含子回复）
 */
export function getCommentList(blogId, current, size) {
  return request({
    url: `/front/comment/list/${blogId}`,
    method: 'get',
    params: { current, size }
  })
}

/**
 * 提交评论（游客/管理员均可）
 */
export function saveComment(data) {
  return request({
    url: '/front/comment/save',
    method: 'post',
    data
  })
}