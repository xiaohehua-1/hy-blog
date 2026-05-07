/**
 * 动态（朋友圈）API
 * 后台：增删改查、批量删除
 * 前台：公开列表、随机动态、点赞
 */
import request from '@/utils/request'

// ===== 后台管理 =====

/** 分页查询动态（含私密） */
export function getMomentList(params) {
  return request({ url: '/admin/moment/list', method: 'get', params })
}
/** 发布动态，不传 publishTime 则立即发布 */
export function saveMoment(data) {
  return request({ url: '/admin/moment/save', method: 'post', data })
}
/** 编辑动态 */
export function updateMoment(data) {
  return request({ url: '/admin/moment/update', method: 'put', data })
}
/** 删除单条动态 */
export function deleteMoment(id) {
  return request({ url: `/admin/moment/${id}`, method: 'delete' })
}
/** 批量删除动态 */
export function deleteMomentBatch(ids) {
  return request({ url: '/admin/moment/batch', method: 'delete', data: ids })
}
/** 获取动态详情（编辑回显） */
export function getMomentDetail(id) {
  return request({ url: `/admin/moment/${id}`, method: 'get' })
}

// ===== 前台展示 =====

/** 公开动态分页列表（排除私密） */
export function getFrontMomentList(params) {
  return request({ url: '/front/moment/list', method: 'get', params })
}
/** 随机一条公开动态 */
export function getRandomMoment() {
  return request({ url: '/front/moment/random', method: 'get' })
}
/** 点赞 +1 */
export function likeMoment(id) {
  return request({ url: `/front/moment/like/${id}`, method: 'post' })
}