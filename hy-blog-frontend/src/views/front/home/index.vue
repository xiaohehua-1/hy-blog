<template>
  <div class="home-wrapper">
    <transition name="fade">
      <nav v-show="!showGlobal" class="home-navbar">
        <div class="home-nav-container">
          <div class="home-left">
             <router-link to="/" class="home-brand"><strong>HeYi</strong></router-link>
          </div>
          <div class="home-right d-none d-md-flex">
            <router-link to="/" class="home-nav-item">首页</router-link>
            <router-link to="/tags" class="home-nav-item">标签</router-link>
            <router-link to="/archives" class="home-nav-item">归档</router-link>
            <router-link to="/message" class="home-nav-item">留言板</router-link>
            <router-link to="/friends" class="home-nav-item">友链</router-link>
            <router-link to="/about" class="home-nav-item">关于我</router-link>
          </div>
           <button class="home-menu-toggle d-md-none" @click="scrollToContent">☰</button>
        </div>
      </nav>
    </transition>

    <div class="hero-section">
      <div class="hero-content">
        <h1 class="hero-title">He Yi's Blog</h1>
        <p class="hero-subtitle">Welcome to my website ——by 何忆 （He Yi）</p>
      </div>
    </div>

    <div class="content-body" id="content-start">
      <el-row justify="center" class="main-container-row">
        <el-col :span="16" :xs="22">
          
          <div class="home-main-content">
            
            <MomentTicker />
            <HomeRecommend />

            <el-row :gutter="40"> 
              <el-col :md="18" :sm="24">
                <div class="blog-list-header">
                  <el-dropdown trigger="hover" @command="handleTypeChange">
                    <div class="section-title dropdown-title">
                      <span>
                        {{ currentTypeName === '最新文章' ? '博客列表' : currentTypeName }} 
                        <el-icon class="el-icon--right"><ArrowDown /></el-icon>
                      </span>
                    </div>
                    <template #dropdown>
                      <el-dropdown-menu>
                        <el-dropdown-item command="all">全部文章</el-dropdown-item>
                        <el-dropdown-item 
                          v-for="type in typeList" 
                          :key="type.id" 
                          :command="type.id"
                        >
                          {{ type.name }}
                        </el-dropdown-item>
                      </el-dropdown-menu>
                    </template>
                  </el-dropdown>
                </div>
                
                <div class="blog-list" v-if="blogList.length > 0">
                  <BlogItem 
                    v-for="item in blogList" 
                    :key="item.id" 
                    :data="item" 
                  />
                </div>
                <el-empty v-else description="暂无文章" />

                <div class="pagination-box mt-5" v-if="total > 0">
                  <el-pagination
                    background
                    layout="prev, pager, next"
                    :total="total"
                    :page-size="pageSize"
                    :current-page="currentPage"
                    @current-change="handlePageChange"
                    class="custom-pagination"
                    prev-text="上一页"
                    next-text="下一页"
                  />
                </div>

              </el-col>

              <el-col :md="6" :sm="24" class="hidden-sm-and-down">
                <div class="sidebar-sticky">
                  <SiteProfile />
                  <MusicCard />
                  <SiteStats />
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
/**
 * 首页
 * 全屏 Hero + 动态跑马灯 + 推荐文章 + 分类筛选博客列表 + 侧边栏
 */
import MomentTicker from './components/MomentTicker.vue'
import { ref, onMounted, onUnmounted } from 'vue'
import { getFrontBlogList, getFrontTypeList } from '@/api/blog'
import HomeRecommend from './components/HomeRecommend.vue'
import BlogItem from '@/components/BlogItem.vue'
import SiteProfile from './components/SiteProfile.vue'
import MusicCard from './components/MusicCard.vue'
import SiteStats from './components/SiteStats.vue'
import { ArrowDown } from '@element-plus/icons-vue'

const showGlobal = ref(false)
const handleScroll = () => {
  const threshold = window.innerHeight * 0.66
  showGlobal.value = window.scrollY > threshold
}
const scrollToContent = () => {
  const threshold = window.innerHeight * 0.7
  window.scrollTo({ top: threshold, behavior: 'smooth' })
}

const blogList = ref([])
const typeList = ref([]) 
const currentTypeName = ref("最新文章") 
const currentTypeId = ref(null) 

const fetchTypes = async () => {
  const res = await getFrontTypeList()
  if (res.success) {
    typeList.value = res.data.list
  }
}

// 前端 Vue3 首页分页加载核心逻辑
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)

const fetchBlogList = async () => {
  loading.value = true
  try {
    // 携带分页参数与分类ID发起后端请求
    const res = await getFrontBlogList(currentPage.value, pageSize.value, currentTypeId.value)
    if (res.success) {
      blogList.value = res.data.records
      total.value = res.data.total
    }
  } finally {
    loading.value = false
  }
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchBlogList()
  // 提升用户体验：分页后平滑滚动至列表顶部
  const listTop = document.getElementById('content-start')
  if (listTop) listTop.scrollIntoView({ behavior: 'smooth' })
}

const handleTypeChange = (command) => {
  currentPage.value = 1
  if (command === 'all') {
    currentTypeId.value = null
    currentTypeName.value = "最新文章"
  } else {
    currentTypeId.value = command
    const target = typeList.value.find(t => t.id === command)
    currentTypeName.value = target ? target.name : "分类文章"
  }
  fetchBlogList()
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
  handleScroll()
  fetchTypes()
  fetchBlogList()
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
/* 1. 修复导航栏间距 */
.home-right {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg); /* 使用规范间距 */
}

/* 2. 侧边栏固定 */
.sidebar-sticky { 
  position: -webkit-sticky;
  position: sticky; 
  top: 104px; /* 72px navbar + 32px spacing */
  z-index: 10;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg); /* 侧边栏组件间距 */
}

/* 3. 分页组件文字适配 */
:deep(.el-pagination.is-background .el-pager li),
:deep(.el-pagination.is-background .btn-prev),
:deep(.el-pagination.is-background .btn-next) {
  margin: 0 !important;
  border-radius: 0 !important;
  border: 1px solid var(--bs-gray-300);
  background-color: #fff !important;
  color: var(--bs-gray-600);
  font-weight: normal;
  height: 40px; /* 调整为 8 的倍数 */
  line-height: 38px;
  min-width: 40px;
}

/* 让上一页/下一页按钮宽度自适应 */
:deep(.el-pagination.is-background .btn-prev),
:deep(.el-pagination.is-background .btn-next) {
  padding: 0 var(--spacing-md) !important;
  width: auto !important;
}

/* 处理边框重叠 */
:deep(.el-pagination.is-background .el-pager li + li),
:deep(.el-pagination.is-background .el-pager li),
:deep(.el-pagination.is-background .btn-next) {
  border-left: none; 
}
:deep(.el-pagination.is-background .btn-prev) {
  border-radius: 6px 0 0 6px !important;
  border-right: 1px solid var(--bs-gray-300);
}
:deep(.el-pagination.is-background .el-pager li) {
  border-left: none !important;
}
:deep(.el-pagination.is-background .btn-next) {
  border-left: none !important;
  border-radius: 0 6px 6px 0 !important;
}

/* 标题样式微调 */
.dropdown-title { cursor: pointer; display: block; }
.dropdown-title span { display: flex; align-items: center; gap: var(--spacing-xs); } 
.dropdown-title:hover { color: var(--bs-primary); }

/* 其他原有样式 */
.home-navbar { position: fixed; top: 0; left: 0; width: 100%; height: 80px; z-index: 1001; display: flex; justify-content: center; transition: opacity 0.5s ease; background: transparent; }
.home-nav-container { width: 1140px; max-width: 100%; padding: 0 var(--spacing-lg); display: flex; justify-content: space-between; align-items: center; height: 100%; }
.home-brand { font-size: 1.8rem; color: #fff; text-decoration: none; font-weight: bold; letter-spacing: var(--letter-spacing-wide); text-shadow: 0 2px 4px rgba(0,0,0,0.5); }
.home-nav-item { color: #fff; text-decoration: none; font-size: 1.1rem; font-weight: 500; text-shadow: 0 2px 4px rgba(0,0,0,0.5); letter-spacing: var(--letter-spacing-base); transition: opacity 0.3s; }
.home-nav-item:hover { opacity: 0.8; }

.hero-section { position: relative; width: 100%; height: 100vh; background-image: url('@/assets/images/homepage.jpg'); background-size: cover; background-position: center center; display: flex; justify-content: center; align-items: center; background-attachment: fixed; }
.hero-section::before { content: ''; position: absolute; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0, 0, 0, 0.2); }
.hero-content { position: relative; text-align: center; color: #fff; z-index: 2; padding: 0 var(--spacing-lg); }
.hero-title { font-size: 4.5rem; font-weight: 700; margin-bottom: var(--spacing-lg); text-shadow: 0 10px 20px rgba(0, 0, 0, 0.8); letter-spacing: var(--letter-spacing-wide); }
.hero-subtitle { font-size: 1.5rem; font-weight: 300; margin-top: var(--spacing-md); font-family: 'ZhuqueFangsong', serif; text-shadow: 0 5px 15px rgba(0, 0, 0, 0.8); letter-spacing: var(--letter-spacing-base); }
.content-body { width: 100%; min-height: 100vh; background-image: url('@/assets/images/bg_01.png'); background-repeat: repeat; background-color: #f5f5f5; }
.home-main-content { padding-top: var(--spacing-2xl); padding-bottom: var(--spacing-2xl); }
.pagination-box { display: flex; justify-content: center; margin-top: var(--spacing-2xl); }

@media (max-width: 768px) {
  .hero-title { font-size: 2.5rem; }
  .hidden-sm-and-down { display: none; }
}
.fade-enter-active, .fade-leave-active { transition: opacity 0.5s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>