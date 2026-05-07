<template>
  <div class="article-container">

    <FloatingActionBar @click-comment="scrollToComment" />

    <div class="global-bg"></div>

    <div class="main-wrapper">
      
      <el-row class="header-row mb-5">
        <el-col :span="7" :xs="24" :offset="4" class="header-left">
          <div class="header-content">
            <h1 class="article-title">{{ blog.title }}</h1>
            
            <div class="article-desc">
              <span class="prefix"># 描述</span>
              <p>{{ blog.description || '暂无描述' }}</p>
            </div>

            <div class="article-tags">
              <span v-for="tag in tags" :key="tag.id" class="tag-item" @click="handleTagClick(tag.id)">
                # {{ tag.name }}
              </span>
            </div>

            <div class="author-box">
              <el-avatar :size="50" :src="avatarUrl" class="author-avatar"></el-avatar>
              <div class="author-info">
                <div class="author-name">{{ blog.author || 'HeYi' }}</div>
                <div class="article-time">
                  <el-icon><Calendar /></el-icon> {{ blog.createTime }}
                  <span class="ms-2"><el-icon><View /></el-icon> {{ blog.views || 0 }} 阅读</span>
                </div>
              </div>
            </div>
          </div>
        </el-col>

        <el-col :span="9" :xs="24" class="header-right hidden-xs-only">
          <div class="header-cover-box" v-if="blog.firstPicture">
            <img :src="blog.firstPicture" alt="cover" class="header-cover">
          </div>
        </el-col>
      </el-row>

      <el-row class="content-row mt-5" :gutter="40">
        
        <el-col :span="12" :xs="24" :offset="4">
          <div class="article-card">
            <div class="markdown-body js-toc-content" v-html="contentHtml"></div>

            <div class="copyright-card mt-5">
              <div class="copyright-item">
                <span class="label">文章作者：</span>
                <span class="value">{{ blog.author || 'HeYi' }}</span>
              </div>
              <div class="copyright-item">
                <span class="label">文章链接：</span>
                <a :href="currentUrl" class="value link">{{ currentUrl }}</a>
              </div>
              <div class="copyright-item">
                <span class="label">版权声明：</span>
                <span class="value">
                  本博客所有文章除特别声明外，均采用 
                  <a href="https://creativecommons.org/licenses/by-nc-sa/4.0/" target="_blank">CC BY-NC-SA 4.0</a> 
                  许可协议。转载请注明来自 <strong>{{ blog.author || 'HeYi' }}</strong>！
                </span>
              </div>
              <div class="copyright-bg-icon">
                <el-icon><Connection /></el-icon>
              </div>
            </div>
          </div>

          <div class="recommend-section mt-4" v-if="recommendList.length > 0">
            <h3 class="section-title"><el-icon><StarFilled /></el-icon> 相关推荐</h3>
            <div class="recommend-grid">
              <div v-for="item in recommendList" :key="item.id" class="recommend-card-grid" @click="goDetail(item.id)">
                <div class="grid-img-box">
                  <img :src="item.firstPicture || defaultCover" alt="cover" loading="lazy">
                </div>
                <div class="grid-info">
                  <div class="grid-title" :title="item.title">{{ item.title }}</div>
                  <div class="grid-date">{{ item.createTime?.split(' ')[0] }}</div>
                </div>
              </div>
            </div>
          </div>

          <div class="comment-section mt-5" ref="commentSectionRef" v-if="blog.id">
            <h3 class="section-title mb-4"><el-icon><EditPen /></el-icon> 发表评论</h3>
            <MessageForm ref="msgFormRef" module="blog" :target-id="blog.id" title="发表评论" @success="refreshList" />
          </div>

          <div class="comment-list-wrapper mt-5" v-if="blog.id">
            <h3 class="section-title mb-4"><el-icon><ChatLineSquare /></el-icon> 评论列表</h3>
            <MessageList :key="refreshKey" module="blog" :target-id="blog.id" @reply="onReplyComment" />
          </div>

        </el-col>

        <el-col :span="4" :xs="0" class="hidden-xs-only">
          <div class="sidebar-sticky">
            <div class="toc-card">
              <h3 class="toc-title">目录</h3>
              <div class="toc-body custom-scrollbar">
                <div class="js-toc"></div>
              </div>
            </div>
          </div>
        </el-col>

      </el-row>
    </div>
  </div>
</template>

<script setup>
/**
 * 文章详情页
 * Markdown 渲染 + 代码高亮 + Tocbot 目录导航 + 评论区 + 相关推荐 + WebSocket 实时刷新
 */
import { ref, onMounted, onUnmounted, nextTick, watch, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getBlogDetail, getFrontBlogList } from '@/api/blog'
import { Calendar, View, StarFilled, EditPen, ChatLineSquare, Connection } from '@element-plus/icons-vue'
import SocketService from '@/utils/websocket'

import FloatingActionBar from '@/components/FloatingActionBar.vue'
import MessageForm from '@/views/front/message/components/MessageForm.vue'
import MessageList from '@/views/front/message/components/MessageList.vue'

import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'
import markdownItAnchor from 'markdown-it-anchor'
import * as tocbot from 'tocbot'
import 'github-markdown-css/github-markdown-light.css' 
import 'highlight.js/styles/github.css' 
import 'tocbot/dist/tocbot.css' 

import avatarImg from '@/assets/images/me.jpg'
import defaultCoverImg from '@/assets/images/overview.jpg'

const route = useRoute()
const router = useRouter()
const blog = ref({})
const tags = ref([])
const contentHtml = ref('')
const recommendList = ref([])

// 新增绑定的变量
const msgFormRef = ref(null)
const refreshKey = ref(0)
const commentSectionRef = ref(null)

const avatarUrl = avatarImg
const defaultCover = defaultCoverImg

const currentUrl = computed(() => window.location.origin + route.path)

const md = new MarkdownIt({
  html: true, linkify: true, typographer: true,
  highlight: function (str, lang) {
    if (lang && hljs.getLanguage(lang)) {
      try { return `<pre class="hljs"><code>${hljs.highlight(str, { language: lang, ignoreIllegals: true }).value}</code></pre>` } catch (__) {}
    }
    return `<pre class="hljs"><code>${md.utils.escapeHtml(str)}</code></pre>`
  }
}).use(markdownItAnchor, { permalink: false, slugify: (s) => encodeURIComponent(String(s).trim().toLowerCase().replace(/\s+/g, '-')) })

const fetchDetail = async (id) => {
  try {
    const res = await getBlogDetail(id)
    if (res.success) {
      blog.value = res.data.data
      tags.value = res.data.tags || []
      if (blog.value.content) {
        contentHtml.value = md.render(blog.value.content)
        nextTick(() => { initToc() })
      }
      fetchRecommend()
    }
  } catch (err) { console.error("加载详情失败", err) }
}

const fetchRecommend = async () => {
  try {
    const res = await getFrontBlogList(1, 3)
    if (res.success) {
      recommendList.value = res.data.records.filter(b => b.id !== blog.value.id).slice(0, 3)
    }
  } catch (e) {}
}

// === 修复1：列表刷新逻辑 ===
const refreshList = () => {
  // 设置 300 毫秒延迟，确保后端数据库已经完全把数据存进去了
  setTimeout(() => {
    refreshKey.value += 1 
  }, 300)
}

// === 修复2：点击回复逻辑 ===
const onReplyComment = (comment) => {
  if (msgFormRef.value) {
    // 告诉表单组件，现在是处于回复状态
    msgFormRef.value.setReply(comment)
    // 自动滚动到表单位置
    scrollToComment()
  }
}

const initToc = () => {
  tocbot.init({ 
    tocSelector: '.js-toc', 
    contentSelector: '.js-toc-content', 
    headingSelector: 'h1, h2, h3', 
    scrollSmooth: true, scrollSmoothOffset: -80, headingsOffset: 80, hasInnerContainers: true 
  })
}

const scrollToComment = () => {
  if (commentSectionRef.value) {
    const top = commentSectionRef.value.getBoundingClientRect().top + window.pageYOffset - 100
    window.scrollTo({ top, behavior: 'smooth' })
  }
}

const handleTagClick = (tagId) => { router.push({ path: '/tags', query: { id: tagId } }) }
const goDetail = (id) => { router.push(`/article/${id}`) }

watch(() => route.params.id, (newId) => {
  if (newId) { 
    tocbot.destroy(); 
    fetchDetail(newId); 
    window.scrollTo(0, 0)
  }
})

onMounted(() => {
  if (route.params.id) fetchDetail(route.params.id)
  
  // 注册 WebSocket 实时刷新回调
  SocketService.getInstance.registerCallBack('blog_comment', (data) => {
    if (data === 'refresh_blog_comment') {
      refreshList()
    }
  })
})

onUnmounted(() => {
  tocbot.destroy()
  // 取消注册回调，防止内存泄漏
  SocketService.getInstance.unRegisterCallBack('blog_comment')
})
</script>

<style scoped>
.global-bg { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background-image: url('@/assets/images/bg_01.png'); background-repeat: repeat; background-position: top left; z-index: -1; }
.main-wrapper { padding-top: 104px; padding-bottom: var(--spacing-3xl); min-height: 100vh; }
.header-row { display: flex; align-items: center; }
.header-content { color: var(--bs-gray-900); padding-right: var(--spacing-lg); }
.article-title { font-size: 2.8rem; font-weight: 800; margin-bottom: var(--spacing-lg); line-height: 1.2; color: #000; letter-spacing: var(--letter-spacing-base); }
.article-desc { margin-bottom: var(--spacing-lg); background: transparent; }
.prefix { font-weight: bold; font-size: 1.1rem; color: var(--bs-primary); display: block; margin-bottom: var(--spacing-xs); }
.article-desc p { font-size: 1.05rem; color: var(--bs-gray-700); line-height: var(--line-height-base); margin: 0; }
.article-tags { margin-bottom: var(--spacing-lg); }
.tag-item { display: inline-block; margin-right: var(--spacing-md); font-size: 0.95rem; color: var(--bs-primary); font-weight: 500; cursor: pointer; transition: all 0.3s; }
.tag-item:hover { text-decoration: underline; transform: translateY(-2px); }
.author-box { display: flex; align-items: center; gap: var(--spacing-md); }
.author-avatar { border: 2px solid #fff; box-shadow: 0 2px 8px rgba(0,0,0,0.15); }
.author-name { font-size: 1.1rem; font-weight: bold; color: var(--bs-gray-900); }
.article-time { font-size: 0.85rem; color: var(--bs-gray-600); display: flex; align-items: center; gap: var(--spacing-xs); margin-top: 2px; }
.header-cover-box { width: 100%; height: 320px; border-radius: 12px; overflow: hidden; box-shadow: 0 8px 24px rgba(0,0,0,0.1); }
.header-cover { width: 100%; height: 100%; object-fit: cover; }
.content-row { margin-top: var(--spacing-2xl); }
.article-card { background: #fff; padding: var(--spacing-2xl); border-radius: 12px; box-shadow: 0 4px 20px rgba(0,0,0,0.05); min-height: 500px; border: 1px solid var(--bs-gray-200); }
.copyright-card { background-color: var(--bs-gray-100); border: 1px dashed var(--bs-gray-400); border-radius: 8px; padding: var(--spacing-lg); position: relative; transition: all 0.3s; }
.copyright-card:hover { box-shadow: 0 4px 12px rgba(0,0,0,0.05); border-color: var(--bs-primary); }
.copyright-item { margin-bottom: var(--spacing-sm); font-size: 0.95rem; line-height: var(--line-height-base); z-index: 1; position: relative; }
.copyright-item:last-child { margin-bottom: 0; }
.copyright-item .label { font-weight: bold; color: var(--bs-gray-900); margin-right: var(--spacing-sm); }
.copyright-item .value { color: var(--bs-gray-700); }
.copyright-item .link { color: var(--bs-gray-500); text-decoration: none; border-bottom: 1px dashed var(--bs-gray-400); transition: color 0.3s; }
.copyright-item .link:hover { color: var(--bs-primary); border-bottom-color: var(--bs-primary); }
.copyright-bg-icon { position: absolute; top: 10px; right: 15px; font-size: 3rem; color: var(--bs-gray-200); z-index: 0; transform: rotate(-15deg); }
.sidebar-sticky { position: sticky; top: 104px; display: flex; flex-direction: column; gap: var(--spacing-lg); }
.toc-card { background: #fff; padding: var(--spacing-lg); border-radius: 12px; box-shadow: 0 4px 20px rgba(0,0,0,0.05); border: 1px solid var(--bs-gray-200); }
.toc-title { font-size: 1.1rem; font-weight: bold; margin-bottom: var(--spacing-md); border-bottom: none; padding-bottom: var(--spacing-sm); }
.toc-body { max-height: calc(100vh - 200px); overflow-y: auto; padding-right: var(--spacing-xs); }
:deep(.toc-list) { list-style: none !important; padding-left: var(--spacing-sm); }
:deep(.toc-list-item) { list-style: none !important; }
:deep(.toc-link) { color: var(--bs-gray-600); font-size: 0.95rem; display: block; padding: var(--spacing-xs) 0; transition: all 0.2s; text-decoration: none; line-height: 1.5; height: auto !important; }
:deep(.is-active-link) { color: var(--bs-primary); font-weight: bold; background-color: rgba(0,0,0,0.02); border-left: 3px solid var(--bs-primary); padding-left: var(--spacing-sm); border-radius: 0 4px 4px 0; }
.custom-scrollbar::-webkit-scrollbar { width: 4px; }
.custom-scrollbar::-webkit-scrollbar-thumb { background: var(--bs-gray-300); border-radius: 4px; }
.custom-scrollbar::-webkit-scrollbar-thumb:hover { background: var(--bs-gray-400); }
.custom-scrollbar::-webkit-scrollbar-track { background: transparent; }
.section-title { font-size: 1.3rem; font-weight: bold; margin-bottom: var(--spacing-lg); display: flex; align-items: center; gap: var(--spacing-sm); }
.recommend-section { background: transparent; }
.recommend-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: var(--spacing-md); }
.recommend-card-grid { background: #fff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.05); cursor: pointer; transition: transform 0.3s, box-shadow 0.3s; }
.recommend-card-grid:hover { transform: translateY(-4px); box-shadow: 0 12px 24px rgba(0,0,0,0.12); }
.grid-img-box { width: 100%; height: 120px; overflow: hidden; }
.grid-img-box img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.5s; }
.recommend-card-grid:hover .grid-img-box img { transform: scale(1.1); }
.grid-info { padding: var(--spacing-md); }
.grid-title { font-size: 0.95rem; font-weight: bold; color: var(--bs-gray-900); margin-bottom: var(--spacing-xs); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.grid-date { font-size: 0.8rem; color: var(--bs-gray-500); }

@media (max-width: 768px) {
  .header-left { text-align: center; }
  .article-title { font-size: 2rem; }
  .author-box { justify-content: center; }
  .article-card { padding: 20px; }
  .hidden-xs-only { display: none; }
  .recommend-grid { grid-template-columns: 1fr; } 
  .grid-img-box { height: 160px; }
}
</style>