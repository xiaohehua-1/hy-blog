/**
 * 路由入口
 * 合并前台/后台路由模块，注册全局前置守卫（登录鉴权 + 页面标题）
 */
import { createRouter, createWebHistory } from 'vue-router'
import adminRoutes from './modules/adminRoutes'
import frontRoutes from './modules/frontRoutes'

const router = createRouter({
  // import.meta.env.BASE_URL 确保不同部署环境（子路径/根路径）下路由正确
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // 前台路由（首页 /）
    ...frontRoutes,

    // 后台路由（/admin 和 /login）
    ...adminRoutes,

    // 404 兜底：匹配所有未定义路径，重定向到 /404
    {
      path: '/:pathMatch(.*)*',
      name: 'NotFound',
      redirect: '/404'
    }
  ],
  /** 路由切换后自动滚动到页面顶部 */
  scrollBehavior() {
    return { top: 0 }
  }
})

/**
 * 全局前置守卫：登录鉴权 + 动态标题
 * to.matched.some 向上穿透父级路由 meta，/admin 的 requiresAuth 自动应用到所有子路由
 */
router.beforeEach((to, from, next) => {
  // 动态设置浏览器标签页标题
  if (to.meta.title) {
    document.title = to.meta.title + ' - HyBlog'
  }

  const token = localStorage.getItem('token')

  // 需要登录权限的页面：无 token 则拦截并记住目标路径，登录后回跳
  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (token) {
      next()
    } else {
      // query.redirect 记住来源路径，登录成功后自动跳回
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
    }
  }
  // 已登录用户访问登录页 → 直接踢回后台首页
  else if (to.path === '/login' && token) {
    next('/admin/dashboard')
  }
  // 前台页面直接放行
  else {
    next()
  }
})

export default router