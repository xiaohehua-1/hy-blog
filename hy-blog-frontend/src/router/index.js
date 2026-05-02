// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import adminRoutes from './modules/adminRoutes'
import frontRoutes from './modules/frontRoutes'

const router = createRouter({
  // 使用 import.meta.env.BASE_URL 确保在不同部署环境下路径正确
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // 前台路由 (通常包含首页 /)
    ...frontRoutes,
    
    // 后台路由 (包含 /admin 和 /login)
    ...adminRoutes,
    
    // 404 兜底路由：匹配所有未定义的路径
    { 
      path: '/:pathMatch(.*)*', 
      name: 'NotFound',
      redirect: '/404' // 假设你有一个 404 页面，或者直接重定向到首页 '/'
    }
  ],
  // 切换路由时自动滚动到页面顶部，体验更好
  scrollBehavior() {
    return { top: 0 }
  }
})

// ==========================================
// 🛡️ 核心修复：全局前置守卫
// ==========================================
router.beforeEach((to, from, next) => {
  // 1. 设置网页标题 (可选，让浏览器标签页显示当前页面名称)
  if (to.meta.title) {
    document.title = to.meta.title + ' - HyBlog'
  }

  // 2. 获取 Token (确保这里的 key 与你登录时存的一致，通常是 'token')
  const token = localStorage.getItem('token')

  // 3. 判断该路由是否需要登录权限
  // to.matched.some 能够检查父级路由的 meta，所以只要 /admin 加了 requiresAuth，下面所有子路由都会命中
  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (token) {
      // A. 有 Token -> 放行
      // 这里未来可以加进一步校验 Token 有效性的逻辑，目前先简单判断有无
      next()
    } else {
      // B. 无 Token -> 拦截，跳转去登录页
      // query: { redirect: to.fullPath } 的作用是：登录成功后，自动跳回刚才想访问的页面
      next({ 
        path: '/login', 
        query: { redirect: to.fullPath } 
      })
    }
  } 
  // 4. 特殊处理：如果已经登录了，还想去访问登录页
  else if (to.path === '/login' && token) {
    // 直接踢回后台首页，防止重复登录
    next('/admin/dashboard')
  } 
  // 5. 其他页面 (如前台首页、文章详情等) -> 直接放行
  else {
    next()
  }
})

export default router