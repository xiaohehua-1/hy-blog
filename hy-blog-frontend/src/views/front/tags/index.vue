<template>
  <div class="tags-container">
    
    <div class="tags-hero">
      <img src="@/assets/images/label.JPG" alt="label-bg" class="hero-img">
      <div class="hero-text">
        <div class="hero-title">- 🎨文章分类 -</div>
        <div class="hero-subtitle">Welcome to my website ——by 何忆 （He Yi）</div>
      </div>
    </div>

    <div class="content-bg"></div>

    <div class="main-content">
      
      <el-row justify="center" class="tag-cloud-row">
        <el-col :span="14" :xs="22">
          <div class="tag-cloud-box">
            <div class="tags-wrapper">
              <span 
                v-for="tag in tagList" 
                :key="tag.id"
                class="tag-item"
                :class="{ 'active': selectedTagIds.includes(tag.id) }"
                @click="toggleTag(tag.id)"
              >
                <span class="tag-symbol">#</span>
                <span class="tag-name">{{ tag.name }}</span>
                <span class="tag-count">{{ tag.count }}</span>
              </span>
            </div>
          </div>
        </el-col>
      </el-row>

      <el-row justify="center" class="article-list-row">
        <el-col :span="13" :xs="22">
          
          <div class="blog-list" v-loading="loading">
            <template v-if="blogList.length > 0">
              <BlogItem 
                v-for="blog in blogList" 
                :key="blog.id" 
                :data="blog" 
              />
            </template>
            <el-empty v-else description="暂无相关文章" />
          </div>

          <div class="pagination-box mt-5" v-if="total > 0">
            <el-pagination
              background
              layout="prev, pager, next"
              :total="total"
              :page-size="pageSize"
              :current-page="currentPage"
              @current-change="handlePageChange"
              class="merged-pagination"
              prev-text="上一页"
              next-text="下一页"
            />
          </div>

        </el-col>
      </el-row>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getFrontTagList } from '@/api/tag'
import { getFrontBlogList } from '@/api/blog'
import BlogItem from '@/components/BlogItem.vue' 

const route = useRoute()

const tagList = ref([])
const selectedTagIds = ref([]) 
const blogList = ref([])
const loading = ref(false)

const currentPage = ref(1)
const pageSize = ref(8) 
const total = ref(0)

const fetchTags = async () => {
  try {
    const res = await getFrontTagList()
    if (res.success) {
      tagList.value = res.data.list
    }
  } catch (err) { console.error(err) }
}

const fetchBlogs = async () => {
  loading.value = true
  try {
    const tagIdsStr = selectedTagIds.value.join(',')
    // 确保 api/blog.js 已经添加了 tagIds 参数
    const res = await getFrontBlogList(currentPage.value, pageSize.value, null, tagIdsStr)
    if (res.success) {
      blogList.value = res.data.records
      total.value = res.data.total
    }
  } finally { loading.value = false }
}

const toggleTag = (id) => {
  const index = selectedTagIds.value.indexOf(id)
  if (index > -1) selectedTagIds.value.splice(index, 1)
  else selectedTagIds.value.push(id)
  currentPage.value = 1
  fetchBlogs()
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchBlogs()
  window.scrollTo({ top: 400, behavior: 'smooth' })
}

onMounted(async () => {
  await fetchTags()
  if (route.query.id) {
    const queryId = parseInt(route.query.id)
    if (!isNaN(queryId)) selectedTagIds.value.push(queryId)
  }
  fetchBlogs()
})
</script>

<style scoped>
.tags-container { position: relative; min-height: 100vh; }

/* === 2. 大图 Hero 区域 === */
.tags-hero {
  position: relative;
  width: 100%;
  height: 35vh;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  /* 关键：确保没有外边距，紧贴顶部 */
  margin-top: 0; 
  padding-top: 0;
}
.hero-img {
  position: absolute;
  width: 100%; height: 100%;
  object-fit: cover;
  z-index: -1;
  filter: brightness(0.8);
}
.hero-text { text-align: center; color: #fff; z-index: 1; text-shadow: 0 2px 10px rgba(0,0,0,0.5); }
.hero-title { font-size: 2.5rem; font-weight: bold; margin-bottom: 10px; }
.hero-subtitle { font-size: 1.2rem; opacity: 0.9; }

/* === 背景 === */
.content-bg {
  position: fixed;
  top: 0; left: 0; width: 100%; height: 100%;
  background-image: url('@/assets/images/bg_01.png');
  background-repeat: repeat;
  z-index: -2;
}

.main-content {
  position: relative;
  margin-top: -60px; 
  z-index: 2;
  padding-bottom: 60px;
}

/* === 标签云 === */
.tag-cloud-row { margin-bottom: 40px; }
.tag-cloud-box {
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(10px);
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.05);
  border: 1px solid rgba(255,255,255,0.5);
}

.tags-wrapper { display: flex; flex-wrap: wrap; gap: 15px; justify-content: center; }

/* 单个标签样式 */
.tag-item {
  display: inline-flex;
  align-items: baseline;
  padding: 8px 15px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid transparent;
  background: transparent;
}
.tag-item:hover { background: rgba(0, 0, 0, 0.05); }

.tag-symbol { font-weight: bold; margin-right: 4px; color: #333; font-size: 18px; }

/* 修改点：文字大小 18px，加粗 */
.tag-name {
  font-size: 18px; 
  font-weight: bold; 
  color: #333;
}

/* 修改点：数量样式调整 (这里设为14px，比文字略小) */
.tag-count {
  font-size: 14px; 
  color: #999;
  font-weight: bold;
  margin-left: 6px;
}

/* 选中状态 */
.tag-item.active .tag-name,
.tag-item.active .tag-symbol,
.tag-item.active .tag-count {
  color: #0DCAF0 !important;
}
.tag-item.active { background: rgba(13, 202, 240, 0.05); }

/* === 分页样式 (合并连接版) === */
.pagination-box { display: flex; justify-content: center; margin-top: 40px; }

/* 强制去除间距和圆角，使其连接 */
:deep(.merged-pagination .el-pager li) {
  margin: 0 !important;
  border-radius: 0 !important;
  border: 1px solid #ddd;
  border-left: none; /* 去除中间的左边框防止重叠变粗 */
  background-color: #fff !important;
  color: #666;
  height: 36px;
  line-height: 34px;
}
/* 第一个页码需要左边框 */
:deep(.merged-pagination .el-pager li:first-child) {
  border-left: 1px solid #ddd;
}

/* 上一页按钮 */
:deep(.merged-pagination .btn-prev) {
  margin: 0 !important;
  border-radius: 4px 0 0 4px !important;
  border: 1px solid #ddd;
  border-right: none; /* 连接页码 */
  background-color: #fff !important;
  color: #666;
  height: 36px;
  padding: 0 15px !important;
  width: auto !important;
}

/* 下一页按钮 */
:deep(.merged-pagination .btn-next) {
  margin: 0 !important;
  border-radius: 0 4px 4px 0 !important;
  border: 1px solid #ddd;
  border-left: none; /* 连接页码 */
  background-color: #fff !important;
  color: #666;
  height: 36px;
  padding: 0 15px !important;
  width: auto !important;
}

/* 激活状态 */
:deep(.merged-pagination .el-pager li:not(.is-disabled).is-active) {
  background-color: var(--bs-primary) !important;
  color: #fff;
  border-color: var(--bs-primary);
  position: relative; 
  z-index: 2; /* 浮起，遮住边框 */
}

/* 悬停状态 */
:deep(.merged-pagination .el-pager li:hover),
:deep(.merged-pagination .btn-prev:hover),
:deep(.merged-pagination .btn-next:hover) {
  color: var(--bs-primary);
  background-color: #f5f7fa !important;
  z-index: 1;
}
</style>