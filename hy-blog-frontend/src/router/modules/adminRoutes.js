import Layout from '@/layout/admin/index.vue'

const adminRoutes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login.vue'),
    meta: { title: '后台登录', hidden: true }
  },
  {
    path: '/admin',
    component: Layout,
    // redirect: '/admin/dashboard', // 移除重定向，防止无限循环
    meta: { title: '后台管理', requiresAuth: true },
    children: [
      // 1. 仪表盘 (独立，没有子菜单)
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/admin/dashboard/index.vue'),
        meta: { title: '仪表盘', icon: 'Odometer' }
      },

      // 2. 博客内容管理 (父菜单)
      {
        path: 'content',
        name: 'Content',
        meta: { title: '博客管理', icon: 'Document' },
        // component: undefined, // 父菜单不需要组件，只需要 children
        children: [
          {
            path: 'article',
            name: 'Article',
            component: () => import('@/views/admin/blog/index.vue'),
            meta: { title: '博文管理' } // 子菜单图标可以省略，或者也加
          },
          {
            path: 'article/publish',
            name: 'PublishArticle',
            component: () => import('@/views/admin/blog/publish.vue'),
            meta: { title: '发布文章', hidden: true } // 隐藏路由，不显示在菜单
          },
          {
            path: 'article/edit/:id',
            name: 'EditArticle',
            component: () => import('@/views/admin/blog/publish.vue'),
            meta: { title: '编辑文章', hidden: true }
          },
          {
            path: 'moment',
            name: 'Moment',
            component: () => import('@/views/admin/moment/index.vue'),
            meta: { title: '动态管理' }
          },
          {
            path: 'moment/publish',
            name: 'PublishMoment',
            component: () => import('@/views/admin/moment/publish.vue'),
            meta: { title: '发布动态', hidden: true }
          },
          {
            path: 'moment/edit/:id',
            name: 'EditMoment',
            component: () => import('@/views/admin/moment/publish.vue'),
            meta: { title: '编辑动态', hidden: true }
          },
          {
            path: 'type',
            name: 'Type',
            component: () => import('@/views/admin/type/index.vue'),
            meta: { title: '分类管理' }
          },
          {
            path: 'tag',
            name: 'Tag',
            component: () => import('@/views/admin/tag/index.vue'),
            meta: { title: '标签管理' }
          }
        ]
      },

      // 3. 网站运营管理 (父菜单)
      {
        path: 'operation',
        name: 'Operation',
        meta: { title: '网站运营', icon: 'Connection' },
        children: [
          {
            path: 'interaction',
            name: 'Interaction',
            component: () => import('@/views/admin/interaction/index.vue'),
            meta: { title: '互动管理' } // 包含留言和评论
          },
          {
            path: 'friend',
            name: 'Friend',
            component: () => import('@/views/admin/friend/index.vue'),
            meta: { title: '友链管理' }
          },
          {
            path: 'music',
            name: 'Music',
            component: () => import('@/views/admin/music/index.vue'),
            meta: { title: '音乐管理' }
          }
        ]
      },

      // 4. 系统管理 (父菜单)
      {
        path: 'system',
        name: 'System',
        meta: { title: '系统管理', icon: 'Setting' },
        children: [
          {
            path: 'user',
            name: 'User',
            component: () => import('@/views/admin/user/index.vue'),
            meta: { title: '用户管理' }
          },
          {
            path: 'config',
            name: 'Config',
            component: () => import('@/views/admin/config/index.vue'),
            meta: { title: '网站配置' }
          },
          {
            path: 'log',
            name: 'SysLog',
            component: () => import('@/views/admin/log/index.vue'),
            meta: { title: '操作日志' }
          },
          {
            path: 'blacklist',
            name: 'BlackList',
            component: () => import('@/views/admin/log/blacklist.vue'),
            meta: { title: '黑名单' }
          }
        ]
      }
    ]
  }
]

export default adminRoutes