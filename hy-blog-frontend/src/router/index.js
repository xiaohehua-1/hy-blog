// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import adminRoutes from './modules/adminRoutes'
import frontRoutes from './modules/frontRoutes'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL), // 建议加上 base url
  routes: [
    // 1. 删除原本在这里的 { path: '/', redirect: '/admin/dashboard' }
    // 让 frontRoutes 里的 '/' 生效，显示前台首页
    
    ...frontRoutes,  // 前台路由 (包含 path: '/')
    ...adminRoutes,  // 后台路由
    
    // 404 页面
    { 
      path: '/:pathMatch(.*)*', 
      redirect: '/404' // 或者跳转到首页 '/'
    }
  ],
  // 切换路由时滚动到顶部
  scrollBehavior() {
    return { top: 0 }
  }
})

// 路由守卫修改：只拦截后台管理页面
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  
  // 判断逻辑：
  // 1. 如果去的是后台 (/admin 开头)
  if (to.path.startsWith('/admin')) {
    if (token) {
      // 有 token，放行
      next()
    } else {
      // 没 token，去登录页
      next('/login')
    }
  } 
  // 2. 如果去的是前台 (首页、文章页等) 或 登录页
  else {
    // 直接放行，不需要 token 也能看博客
    next()
  }
})

export default router