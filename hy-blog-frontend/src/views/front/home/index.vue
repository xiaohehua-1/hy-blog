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
            <a href="https://www.travellings.cn/go.html" target="_blank" class="home-nav-item">🚇开往</a>
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
/* 脚本保持不变，为了节省篇幅略去，逻辑与你之前的一致 */
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

const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)

const fetchTypes = async () => {
  const res = await getFrontTypeList()
  if (res.success) {
    typeList.value = res.data.list
  }
}

const fetchBlogList = async () => {
  loading.value = true
  try {
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
  gap: 25px; /* 这里添加间距 */
}

/* 2. 侧边栏固定 */
.sidebar-sticky { 
  position: -webkit-sticky;
  position: sticky; 
  top: 100px; /* 距离顶部 100px 吸附 */
  z-index: 10;
}

/* 3. 分页组件文字适配 */
:deep(.el-pagination.is-background .el-pager li),
:deep(.el-pagination.is-background .btn-prev),
:deep(.el-pagination.is-background .btn-next) {
  margin: 0 !important;
  border-radius: 0 !important;
  border: 1px solid #ccc;
  background-color: #fff !important;
  color: #666;
  font-weight: normal;
  height: 36px;
  line-height: 34px;
  min-width: 36px;
}

/* 让上一页/下一页按钮宽度自适应 */
:deep(.el-pagination.is-background .btn-prev),
:deep(.el-pagination.is-background .btn-next) {
  padding: 0 15px !important; /* 增加内边距 */
  width: auto !important;
}

/* 处理边框重叠 */
:deep(.el-pagination.is-background .el-pager li + li),
:deep(.el-pagination.is-background .el-pager li),
:deep(.el-pagination.is-background .btn-next) {
  border-left: none; 
}
:deep(.el-pagination.is-background .btn-prev) {
  border-radius: 4px 0 0 4px !important;
  border-right: 1px solid #ccc;
}
:deep(.el-pagination.is-background .el-pager li) {
  border-left: none !important;
}
:deep(.el-pagination.is-background .btn-next) {
  border-left: none !important;
  border-radius: 0 4px 4px 0 !important;
}

/* 激活状态 */
:deep(.el-pagination.is-background .el-pager li:not(.is-disabled).is-active) {
  background-color: var(--bs-primary) !important;
  color: #fff;
  border-color: var(--bs-primary);
  position: relative;
  z-index: 1;
}

/* 悬停状态 */
:deep(.el-pagination.is-background .el-pager li:hover),
:deep(.el-pagination.is-background .btn-prev:hover),
:deep(.el-pagination.is-background .btn-next:hover) {
  color: var(--bs-primary);
  background-color: #f0f9ff !important;
}

/* 标题样式微调 */
.dropdown-title { cursor: pointer; display: block; }
.dropdown-title span { display: flex; align-items: center; gap: 5px; } /* 让图标和文字对齐 */
.dropdown-title:hover { color: var(--bs-primary); }

/* 其他原有样式 */
.home-navbar { position: fixed; top: 0; left: 0; width: 100%; height: 85px; z-index: 1001; display: flex; justify-content: center; transition: opacity 0.5s ease; background: transparent; }
.home-nav-container { width: 1110px; max-width: 100%; padding: 0 20px; display: flex; justify-content: space-between; align-items: center; height: 100%; }
.home-brand { font-size: 1.8rem; color: #fff; text-decoration: none; font-weight: bold; letter-spacing: 1px; text-shadow: 0 2px 4px rgba(0,0,0,0.5); }
.home-nav-item { color: #fff; text-decoration: none; font-size: 1.1rem; font-weight: 500; text-shadow: 0 2px 4px rgba(0,0,0,0.5); }
.hero-section { position: relative; width: 100%; height: 100vh; background-image: url('@/assets/images/homepage.jpg'); background-size: cover; background-position: center center; display: flex; justify-content: center; align-items: center; background-attachment: fixed; }
.hero-section::before { content: ''; position: absolute; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0, 0, 0, 0.2); }
.hero-content { position: relative; text-align: center; color: #fff; z-index: 2; padding: 0 20px; }
.hero-title { font-size: 4rem; font-weight: 700; margin-bottom: 1.5rem; text-shadow: 0 10px 20px rgba(0, 0, 0, 0.8); }
.hero-subtitle { font-size: 1.5rem; font-weight: 300; margin-top: 10px; font-family: 'ZhuqueFangsong', serif; text-shadow: 0 5px 15px rgba(0, 0, 0, 0.8); }
.content-body { width: 100%; min-height: 100vh; background-image: url('@/assets/images/bg_01.png'); background-repeat: repeat; background-color: #f5f5f5; }
.home-main-content { padding-top: 60px; padding-bottom: 60px; }
.pagination-box { display: flex; justify-content: center; margin-top: 40px; }
@media (max-width: 768px) {
  .hero-title { font-size: 2.5rem; }
  .hidden-sm-and-down { display: none; }
}
.fade-enter-active, .fade-leave-active { transition: opacity 0.5s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>