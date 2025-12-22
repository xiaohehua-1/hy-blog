// src/router/modules/adminRoutes.js
import Layout from '@/layout/admin/index.vue' // 注意引入路径变了

const adminRoutes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '后台登录', hidden: true }
  },
  {
    path: '/admin',
    component: Layout,
    redirect: '/admin/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/admin/dashboard/index.vue'),
        meta: { title: '首页', icon: 'Odometer' }
      },
      {
        path: 'tag',
        name: 'Tag',
        component: () => import('@/views/admin/tag/index.vue'),
        meta: { title: '标签管理', icon: 'PriceTag' }
      },
      {
        path: 'type',
        name: 'Type',
        component: () => import('@/views/admin/type/index.vue'),
        meta: { title: '分类管理', icon: 'Folder' }
      },
      {
        path: 'article', // 访问路径：/admin/article
        name: 'Article',
        component: () => import('@/views/admin/blog/index.vue'),
        meta: { title: '文章管理', icon: 'Document' }
      },
      {
        path: 'article/publish', // 访问路径：/admin/article/publish
        name: 'ArticlePublish',
        component: () => import('@/views/admin/blog/publish.vue'),
        meta: { title: '发布文章', hidden: true } // hidden:true 表示不在侧边栏显示
      },
      {
        path: 'article/edit/:id', // 访问路径：/admin/article/edit/1
        name: 'ArticleEdit',
        component: () => import('@/views/admin/blog/publish.vue'), // 复用 publish.vue
        meta: { title: '编辑文章', hidden: true }
      },
      {
        path: 'friend',
        name: 'Friend',
        component: () => import('@/views/admin/friend/index.vue'),
        meta: { title: '友链管理', icon: 'Link' }
      },
      {
        path: 'music',
        name: 'Music',
        component: () => import('@/views/admin/music/index.vue'),
        meta: { title: '音乐管理', icon: 'Headset' }
      },
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/admin/user/index.vue'),
        meta: { title: '用户管理', icon: 'User' }
      },
      {
        path: 'interaction',
        name: 'Interaction',
        component: () => import('@/views/admin/interaction/index.vue'),
        meta: { title: '互动管理', icon: 'ChatDotRound' }
      },
      {
        path: 'config',
        name: 'Config',
        component: () => import('@/views/admin/config/index.vue'),
        meta: { title: '系统配置管理', icon: 'Setting' }
      },
      {
        path: 'log',
        name: 'SysLog',
        component: () => import('@/views/admin/log/index.vue'),
        meta: { title: '系统日志管理', icon: 'Tickets' }
      },
      {
        path: 'blacklist',
        name: 'Blacklist',
        component: () => import('@/views/admin/log/blacklist.vue'),
        meta: { title: '黑名单IP管理', icon: 'Lock' }
      }
      // ... 其他后台路由
    ]
  }
]

export default adminRoutes