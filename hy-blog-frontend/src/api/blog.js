/**
 * 博客文章相关 API
 * 前台：列表（分类/标签/关键词筛选）、推荐、随机、详情
 */
import request from '@/utils/request'

/**
 * 前台文章分页列表，支持分类、多标签、关键词筛选
 */
export function getFrontBlogList(current = 1, size = 10, typeId = null, tagIds = null, keyword = null) {
  return request({
    url: '/front/blog/list',
    method: 'get',
    params: { current, size, typeId, tagIds, keyword}
  })
}

/**
 * 获取全部分类（前台页脚/导航）
 */
export function getFrontTypeList() {
  return request({
    url: '/front/type/list',
    method: 'get'
  })
}

/**
 * 推荐文章（首页置顶展示，后端 Redis 缓存）
 */
export function getFrontRecommendList() {
  return request({
    url: '/front/blog/recommend',
    method: 'get'
  })
}

/**
 * 文章详情，访问时 Redis 浏览量 +1
 */
export function getBlogDetail(id) {
  return request({
    url: `/front/blog/detail/${id}`,
    method: 'get'
  })
}

/**
 * 随机一篇文章 ID，用于"随便看看"入口
 */
export function getFrontRandomBlog() {
  return request({
    url: '/front/blog/random',
    method: 'get'
  })
}