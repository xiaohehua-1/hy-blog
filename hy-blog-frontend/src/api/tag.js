import request from '@/utils/request'

export function getFrontTagList() {
  return request({
    url: '/front/tag/list',
    method: 'get'
  })
}