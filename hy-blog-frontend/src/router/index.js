// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import adminRoutes from './modules/adminRoutes'
//import frontRoutes from './modules/frontRoutes'

const router = createRouter({
  history: createWebHistory(),
  // 使用解构赋值 [...A, ...B] 合并数组
  routes: [
    { path: '/', redirect: '/admin/dashboard' },
    // ...frontRoutes,  // 放在前面
    ...adminRoutes,  // 放在后面
    // 404 页面通常放在最后
    { 
      path: '/:pathMatch(.*)*', 
      redirect: '/' 
    }
  ]
})

// 路由守卫保持不变...
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else {
    next()
  }
})
export default router