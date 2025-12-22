// src/router/modules/frontRoutes.js
import FrontLayout from '@/layout/front/index.vue' // 引入前台布局

const frontRoutes = [
  {
    path: '/',
    component: FrontLayout,
    children: [
      {
        path: '', // 空路径表示默认子路由
        name: 'Home',
        component: () => import('@/views/front/Home.vue'),
        meta: { title: '首页' }
      },
      // ... 其他前台路由
    ]
  }
]

export default frontRoutes