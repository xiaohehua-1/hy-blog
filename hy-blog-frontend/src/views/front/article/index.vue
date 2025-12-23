<template>
  <div class="article-container">
    
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
              <span 
                v-for="tag in tags" 
                :key="tag.id" 
                class="tag-item"
                @click="handleTagClick(tag.id)"
              >
                # {{ tag.name }}
              </span>
            </div>

            <div class="author-box">
              <el-avatar :size="50" :src="avatarUrl" class="author-avatar"></el-avatar>
              <div class="author-info">
                <div class="author-name">{{ blog.author || 'HeYi' }}</div>
                <div class="article-time">
                  <el-icon><Calendar /></el-icon> {{ blog.createTime }}
                  <span class="ms-2"><el-icon><View /></el-icon> {{ blog.views || 0 }}</span>
                </div>
              </div>
            </div>
          </div>
        </el-col>

        <el-col :span="9" :xs="24" class="header-right hidden-xs-only">
          <div class="header-cover-box" v-if="blog.coverPath">
            <img :src="blog.coverPath" alt="cover" class="header-cover">
          </div>
        </el-col>
      </el-row>


      <el-row class="content-row" :gutter="40">
        
        <el-col :span="12" :xs="24" :offset="4">
          <div class="article-card">
            <div 
              class="markdown-body js-toc-content" 
              v-html="contentHtml"
            ></div>
          </div>
        </el-col>

        <el-col :span="4" :xs="0" class="hidden-xs-only">
          <div class="sidebar-sticky">
            <div class="toc-card">
              <h3 class="toc-title">目录</h3>
              <div class="js-toc"></div>
            </div>
          </div>
        </el-col>

      </el-row>


      <el-row class="recommend-row mt-5">
        <el-col :span="16" :xs="24" :offset="4">
          <div class="recommend-box">
            <h3 class="recommend-title"><el-icon><StarFilled /></el-icon> 相关推荐</h3>
            
            <el-row :gutter="20">
              <el-col 
                :span="24" 
                v-for="item in recommendList" 
                :key="item.id"
              >
                <div class="recommend-card-new" @click="goDetail(item.id)">
                  <div class="rec-img-box">
                     <img :src="item.coverPath || defaultCover" alt="rec">
                  </div>
                  <div class="rec-info-box">
                    <div class="rec-title-new" :title="item.title">{{ item.title }}</div>
                    <div class="rec-meta-new">
                      <span class="rec-date-new">
                        <el-icon><Calendar /></el-icon> {{ item.createTime?.split(' ')[0] }}
                      </span>
                    </div>
                  </div>
                </div>
              </el-col>
            </el-row>

          </div>
        </el-col>
      </el-row>

    </div>
  </div>
</template>

<script setup>
/* 脚本部分保持不变 */
import { ref, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getBlogDetail, getFrontBlogList } from '@/api/blog'
import { Calendar, View, StarFilled } from '@element-plus/icons-vue'

import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'
import markdownItAnchor from 'markdown-it-anchor'
import * as tocbot from 'tocbot'
import 'github-markdown-css/github-markdown-light.css' 
import 'highlight.js/styles/github.css' 
import 'tocbot/dist/tocbot.css' 

import avatarImg from '@/assets/images/me.jpg'
// 修改点：这里只用于默认推荐图，全局背景在CSS里
import defaultCoverImg from '@/assets/images/overview.jpg'

const route = useRoute()
const router = useRouter()
const blog = ref({})
const tags = ref([])
const contentHtml = ref('')
const recommendList = ref([])

const avatarUrl = avatarImg
const defaultCover = defaultCoverImg

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

const initToc = () => {
  tocbot.init({ tocSelector: '.js-toc', contentSelector: '.js-toc-content', headingSelector: 'h1, h2, h3', scrollSmooth: true, scrollSmoothOffset: -80, headingsOffset: 80 })
}

const handleTagClick = (tagId) => { router.push({ path: '/tags', query: { id: tagId } }) }
const goDetail = (id) => { router.push(`/article/${id}`) }

watch(() => route.params.id, (newId) => {
  if (newId) { tocbot.destroy(); fetchDetail(newId); window.scrollTo(0, 0) }
})

onMounted(() => { if (route.params.id) fetchDetail(route.params.id) })
onUnmounted(() => { tocbot.destroy() })
</script>

<style scoped>
/* 修改点1：更换背景图，并设置为平铺 */
.global-bg {
  position: fixed;
  top: 0; left: 0; width: 100%; height: 100%;
  background-image: url('@/assets/images/bg_01.png'); /* 替换为 bg_01.png */
  background-repeat: repeat; /* 设置为平铺 */
  background-position: top left;
  z-index: -1;
  /* background-color: #f5f5f5; 可选：底色 */
}

/* 修改点2：减小 padding-top，让内容上移 */
.main-wrapper {
  padding-top: 85px; /* 从 100px 调整为 85px (约等于导航栏高度) */
  padding-bottom: 60px;
  min-height: 100vh;
}

/* Header 样式 (保持不变) */
.header-row { display: flex; align-items: center; }
.header-content { color: #333; padding-right: 20px; }
.article-title { font-size: 2.8rem; font-weight: 800; margin-bottom: 25px; line-height: 1.2; color: #000; /* 移除阴影，假设新背景较亮 */ }
.article-desc { margin-bottom: 25px; background: transparent; }
.prefix { font-weight: bold; font-size: 1.1rem; color: var(--bs-primary); display: block; margin-bottom: 5px; }
.article-desc p { font-size: 1.05rem; color: #444; line-height: 1.6; margin: 0; }
.article-tags { margin-bottom: 25px; }
.tag-item { display: inline-block; margin-right: 15px; font-size: 0.95rem; color: var(--bs-primary); font-weight: 500; cursor: pointer; transition: all 0.3s; }
.tag-item:hover { text-decoration: underline; transform: translateY(-2px); }
.author-box { display: flex; align-items: center; gap: 15px; }
.author-avatar { border: 2px solid #fff; box-shadow: 0 2px 8px rgba(0,0,0,0.15); }
.author-name { font-size: 1.1rem; font-weight: bold; color: #333; }
.article-time { font-size: 0.85rem; color: #666; display: flex; align-items: center; gap: 5px; margin-top: 2px; }
.header-cover-box { width: 100%; height: 320px; border-radius: 12px; overflow: hidden; box-shadow: 0 8px 20px rgba(0,0,0,0.1); transform: rotate(2deg); transition: transform 0.3s; }
.header-cover-box:hover { transform: rotate(0deg) scale(1.02); }
.header-cover { width: 100%; height: 100%; object-fit: cover; }

/* 内容与目录卡片 */
/* 修改点3：正文卡片去掉透明度，增加质感 */
.article-card {
  background: #fff; /* 纯白背景 */
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 2px 15px rgba(0,0,0,0.05);
  min-height: 500px;
  border: 1px solid #eee; /* 增加细边框 */
}
.sidebar-sticky { position: sticky; top: 100px; }
.toc-card {
  background: #fff; /* 纯白背景 */
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 15px rgba(0,0,0,0.05);
  border: 1px solid #eee;
}
.toc-title { font-size: 1.1rem; font-weight: bold; margin-bottom: 15px; border-bottom: 1px solid #ddd; padding-bottom: 10px; }

/* 推荐板块容器 */
.recommend-box {
  background: #fff;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
}
.recommend-title { font-size: 1.3rem; font-weight: bold; margin-bottom: 20px; display: flex; align-items: center; gap: 8px; }

/* === 修改点4：全新的左图右文推荐卡片样式 === */
.recommend-card-new {
  display: flex;
  align-items: center;
  gap: 20px; /* 图片和文字的间距 */
  padding: 15px;
  background: #fff;
  border-radius: 10px;
  transition: all 0.3s ease;
  cursor: pointer;
  border-bottom: 1px solid #f5f5f5; /* 列表分割线 */
}
.recommend-card-new:last-child { border-bottom: none; } /* 最后一个去掉分割线 */
.recommend-card-new:hover {
  background: #fcfcfc;
  transform: translateX(5px); /* 悬停轻微右移 */
}

.rec-img-box {
  width: 140px; /* 固定宽度 */
  height: 95px; /* 固定高度 */
  border-radius: 6px;
  overflow: hidden;
  flex-shrink: 0; /* 防止挤压 */
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}
.rec-img-box img {
  width: 100%; height: 100%; object-fit: cover; transition: transform 0.5s;
}
.recommend-card-new:hover .rec-img-box img { transform: scale(1.08); }

.rec-info-box {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  height: 95px;
}

.rec-title-new {
  font-size: 1.1rem;
  font-weight: bold;
  color: #333;
  line-height: 1.4;
  margin-bottom: 10px;
  /* CSS多行省略 (限制2行) */
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.rec-meta-new { display: flex; align-items: center; font-size: 0.9rem; color: #888; }
.rec-date-new { display: flex; align-items: center; gap: 6px; }

/* 响应式适配 */
@media (max-width: 768px) {
  .header-left { text-align: center; }
  .article-title { font-size: 2rem; }
  .author-box { justify-content: center; }
  .article-card { padding: 20px; }
  .hidden-xs-only { display: none; }
  /* 移动端推荐卡片调整 */
  .recommend-card-new { flex-direction: column; align-items: flex-start; gap: 10px; }
  .rec-img-box { width: 100%; height: 180px; }
  .rec-info-box { height: auto; }
}
</style>