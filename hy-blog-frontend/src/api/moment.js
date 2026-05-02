import request from '@/utils/request'

// === Admin APIs ===
export function getMomentList(params) {
  return request({ url: '/admin/moment/list', method: 'get', params })
}
export function saveMoment(data) {
  return request({ url: '/admin/moment/save', method: 'post', data })
}
export function updateMoment(data) {
  return request({ url: '/admin/moment/update', method: 'put', data })
}
export function deleteMoment(id) {
  return request({ url: `/admin/moment/${id}`, method: 'delete' })
}
export function deleteMomentBatch(ids) {
  return request({ url: '/admin/moment/batch', method: 'delete', data: ids })
}
export function getMomentDetail(id) {
  return request({ url: `/admin/moment/${id}`, method: 'get' })
}

// === Front APIs ===
export function getFrontMomentList(params) {
  return request({ url: '/front/moment/list', method: 'get', params })
}
export function getRandomMoment() {
  return request({ url: '/front/moment/random', method: 'get' })
}
export function likeMoment(id) {
  return request({ url: `/front/moment/like/${id}`, method: 'post' })
}