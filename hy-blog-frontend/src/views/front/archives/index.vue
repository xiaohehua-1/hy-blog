<template>
  <div class="archive-container">
    
    <div class="content-bg"></div>

    <div class="archive-hero">
      <img src="@/assets/images/archive.JPG" alt="archive-bg" class="hero-img">
      <div class="hero-text">
        <div class="hero-title">- 💾归档 -</div>
        <div class="hero-subtitle">Welcome to my website ——by 何忆 （He Yi）</div>
      </div>
    </div>

    <div class="main-content">
      <el-row justify="center">
        <el-col :span="14" :xs="22">
          
          <div class="timeline-box" v-if="blogList.length > 0">
            <div class="center-line"></div>

            <div 
              v-for="(item, index) in blogList" 
              :key="item.id" 
              class="timeline-item"
              :class="index % 2 === 0 ? 'left-item' : 'right-item'"
              @click="goDetail(item.id)"
            >
              <div class="timeline-dot"></div>

              <div class="timeline-card">
                <div class="card-inner" :class="{ 'no-cover': !item.firstPicture }">
                  
                  <div class="card-img-box" v-if="item.firstPicture">
                    <img :src="item.firstPicture" alt="cover" loading="lazy">
                  </div>

                  <div class="card-text-box">
                    <div class="card-date">{{ formatDate(item.createTime) }}</div>
                    <div class="card-title text-truncate">{{ item.title }}</div>
                    <div class="card-desc">{{ item.description || '暂无描述...' }}</div>
                  </div>

                </div>
              </div>
            </div>
          </div>

          <el-empty v-else description="暂无归档文章" />

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
import { useRouter } from 'vue-router'
import { getFrontBlogList } from '@/api/blog'

const router = useRouter()
const blogList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

const fetchBlogs = async () => {
  try {
    const res = await getFrontBlogList(currentPage.value, pageSize.value)
    if (res.success) {
      blogList.value = res.data.records
      total.value = res.data.total
    }
  } catch (err) { console.error(err) }
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchBlogs()
  window.scrollTo({ top: 350, behavior: 'smooth' })
}

const goDetail = (id) => { router.push(`/article/${id}`) }

// 格式化时间函数
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  // 假设后端返回的是标准的 yyyy-MM-dd HH:mm:ss 或 ISO 时间
  const date = new Date(dateStr)
  if (isNaN(date.getTime())) return dateStr // 如果解析失败直接返回原字符串
  
  const y = date.getFullYear()
  const m = (date.getMonth() + 1).toString().padStart(2, '0')
  const d = date.getDate().toString().padStart(2, '0')
  const hh = date.getHours().toString().padStart(2, '0')
  const mm = date.getMinutes().toString().padStart(2, '0')
  const ss = date.getSeconds().toString().padStart(2, '0')
  
  return `${y}年${m}月${d}日 ${hh}:${mm}:${ss}`
}

onMounted(() => { fetchBlogs() })
</script>

<style scoped>
.archive-container { position: relative; min-height: 100vh; }
.content-bg {
  position: fixed; top: 0; left: 0; width: 100%; height: 100%;
  background-image: url('@/assets/images/bg_01.png');
  background-repeat: repeat; z-index: -2;
}

/* Hero */
.archive-hero {
  position: relative; width: 100%; height: 400px;
  overflow: hidden; display: flex; align-items: center; justify-content: center;
  margin-top: 0;
}
.hero-img { position: absolute; width: 100%; height: 100%; object-fit: cover; z-index: -1; filter: brightness(0.8); }
.hero-text { text-align: center; color: #fff; z-index: 1; text-shadow: 0 2px 10px rgba(0,0,0,0.5); }
.hero-title { font-size: 2.5rem; font-weight: bold; margin-bottom: 10px; }
.hero-subtitle { font-size: 1.2rem; opacity: 0.9; }

.main-content {
  padding-bottom: 60px;
  position: relative;
  margin-top: 40px; 
}

/* === 时间轴容器 === */
.timeline-box { position: relative; padding: 20px 0; }

.center-line {
  position: absolute;
  left: 50%; top: 0; bottom: 0;
  width: 4px;
  background-color: #000;
  transform: translateX(-50%);
  border-radius: 2px;
}

.timeline-item {
  position: relative; width: 50%; padding-bottom: 40px;
  box-sizing: border-box; cursor: pointer;
}

/* 布局微调：增加内边距
  为了让卡片视觉上接近 "6格" (约 25% 宽度)，我们在 50% 的基础上增加 padding
*/
.left-item { left: 0; padding-right: 70px; text-align: right; }
.right-item { left: 50%; padding-left: 70px; text-align: left; }

/* 圆点 */
.timeline-dot {
  position: absolute; top: 20px; 
  width: 18px; height: 18px; 
  background: #000; border-radius: 50%; z-index: 2; transition: all 0.3s;
  border: 3px solid #fff; box-shadow: 0 0 0 2px #000;
}
/* 圆点定位修正 */
.left-item .timeline-dot { right: -11px; } 
.right-item .timeline-dot { left: -11px; }

.timeline-item:hover .timeline-dot {
  transform: scale(1.2);
  background: #FF4D4F; /* 红色 */
  box-shadow: 0 0 0 2px #FF4D4F;
}

/* === 卡片样式 === */
.timeline-card {
  background: #F0F0F0;
  border-radius: 6px;
  border-bottom: 3px solid #000;
  transition: all 0.3s ease;
  overflow: visible; 
  position: relative;
  box-shadow: 0 2px 5px rgba(0,0,0,0.05);
}
.timeline-card:hover { transform: translateY(-5px); box-shadow: 0 8px 15px rgba(0,0,0,0.15); }

/* 三角形 (靠近轴的那侧) */
.timeline-card::before {
  content: ''; position: absolute; top: 24px; width: 0; height: 0;
  border-top: 10px solid transparent; border-bottom: 10px solid transparent; z-index: 1;
}
.left-item .timeline-card::before { right: -10px; border-left: 10px solid #F0F0F0; }
.right-item .timeline-card::before { left: -10px; border-right: 10px solid #F0F0F0; }

/* 卡片内部布局 
  align-items: center 实现垂直居中
*/
.card-inner {
  display: flex;
  height: 140px; /* 固定高度 */
  overflow: hidden;
  border-radius: 6px 6px 0 0;
  align-items: center; /* 垂直居中 */
}

/* 左侧卡片：row (图左 文右) -> 图片在外侧 */
.left-item .card-inner { flex-direction: row; }

/* 右侧卡片：row-reverse (图右 文左) -> 图片在外侧 */
.right-item .card-inner { flex-direction: row-reverse; }

/* 图片样式优化：
  1. 固定大小正方形
  2. 增加 margin，不贴边 (露出背景)
  3. 圆角处理
*/
.card-img-box {
  width: 110px; 
  height: 110px;
  flex-shrink: 0;
  position: relative;
  margin: 15px; /* 增加四周间距，不挡背景 */
  border-radius: 6px; /* 图片圆角 */
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}
.card-img-box img {
  width: 100%; height: 100%; object-fit: cover;
  transition: transform 0.5s;
}
.timeline-card:hover .card-img-box img { transform: scale(1.1); }

/* 文字盒子 */
.card-text-box {
  flex: 1;
  padding: 15px 20px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  text-align: left !important;
  min-width: 0;
}

/* 如果没图，文字铺满 */
.card-inner.no-cover .card-text-box { width: 100%; padding-left: 30px; }

.card-date { font-size: 0.85rem; color: #666; margin-bottom: 5px; }
.card-title { font-size: 1.1rem; font-weight: bold; color: #333; margin-bottom: 8px; line-height: 1.4; }
.card-desc { font-size: 0.9rem; color: #555; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }

/* === 响应式适配 === */
@media (max-width: 768px) {
  .center-line { left: 20px; }
  .timeline-item { width: 100%; left: 0; padding-left: 50px; padding-right: 0; }
  .timeline-item .timeline-card::before { left: -10px !important; right: auto !important; border-right: 10px solid #F0F0F0 !important; border-left: none !important; }
  .timeline-dot { left: 11px !important; right: auto !important; }
  .card-inner { flex-direction: row !important; }
  .card-img-box { width: 90px; height: 90px; margin: 10px; }
}

/* 分页复用样式 */
.pagination-box { display: flex; justify-content: center; }
:deep(.merged-pagination .el-pager li), :deep(.merged-pagination .btn-prev), :deep(.merged-pagination .btn-next) {
  margin: 0 !important; border-radius: 0 !important; border: 1px solid #ddd; border-left: none;
  background-color: #fff !important; color: #666; height: 36px; line-height: 34px;
}
:deep(.merged-pagination .el-pager li:first-child) { border-left: 1px solid #ddd; }
:deep(.merged-pagination .btn-prev) { border-radius: 4px 0 0 4px !important; border-right: none; padding: 0 15px !important; width: auto !important; }
:deep(.merged-pagination .btn-next) { border-radius: 0 4px 4px 0 !important; border-left: none; padding: 0 15px !important; width: auto !important; }
:deep(.merged-pagination .el-pager li:not(.is-disabled).is-active) { background-color: var(--bs-primary) !important; color: #fff; border-color: var(--bs-primary); position: relative; z-index: 2; }
:deep(.merged-pagination .el-pager li:hover) { z-index: 1; color: var(--bs-primary); background-color: #f5f7fa !important; }
</style>