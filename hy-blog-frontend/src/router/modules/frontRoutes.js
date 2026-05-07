/**
 * 前台路由配置
 * 所有前台页面共用 FrontLayout（NavBar + 内容区 + Footer）
 * 文章详情路由为 /article/:id，动态参数由页面组件消费
 */
import FrontLayout from '@/layout/front/index.vue'

const frontRoutes = [
    {
        path: '/',
        component: FrontLayout,
        children: [
            {
                path: '',
                name: 'Home',
                component: () => import('@/views/front/home/index.vue'),
                meta: { title: '首页' }
            },
            {
                path: 'tags',
                name: 'Tags',
                component: () => import('@/views/front/tags/index.vue'),
                meta: { title: '标签' }
            },
            {
                path: 'archives',
                name: 'Archives',
                component: () => import('@/views/front/archives/index.vue'),
                meta: { title: '归档' }
            },
            {
                path: 'message',
                name: 'Message',
                component: () => import('@/views/front/message/index.vue'),
                meta: { title: '留言板' }
            },
            {
                path: 'friends',
                name: 'Friends',
                component: () => import('@/views/front/friends/index.vue'),
                meta: { title: '友链' }
            },
            {
                path: 'about',
                name: 'About',
                component: () => import('@/views/front/about/index.vue'),
                meta: { title: '关于我' }
            },
            {
                path: 'article/:id',
                name: 'ArticleDetail',
                component: () => import('@/views/front/article/index.vue'),
                meta: { title: '文章详情' }
            },
            {
                path: '/moments',
                name: 'FrontMoments',
                component: () => import('@/views/front/moment/index.vue'),
                meta: { title: '生活动态' }
            }
        ]
    }
]

export default frontRoutes