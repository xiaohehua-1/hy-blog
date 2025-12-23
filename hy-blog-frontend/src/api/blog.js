import request from '@/utils/request'

// 获取前台文章列表 (支持分类筛选)
export function getFrontBlogList(current = 1, size = 10, typeId = null) {
  return request({
    url: '/front/blog/list',
    method: 'get',
    params: { current, size, typeId }
  })
}

// 获取所有分类
export function getFrontTypeList() {
  return request({
    url: '/front/type/list', // 对应刚刚新建的 Controller
    method: 'get'
  })
}

// 获取推荐文章列表
export function getFrontRecommendList() {
  return request({
    url: '/front/blog/recommend',
    method: 'get'
  })
}

// 获取博文详情
export function getBlogDetail(id) {
  return request({
    url: `/front/blog/detail/${id}`,
    method: 'get'
  })
}