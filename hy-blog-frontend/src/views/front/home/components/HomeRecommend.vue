<template>
  <div class="home-recommend-container mb-5" v-if="recommendList.length > 0">
    <h3 class="section-title"><span>最近推荐</span></h3>

    <el-row :gutter="20">
      
      <el-col :md="13" :xs="24">
        <el-carousel trigger="click" height="420px" :interval="5000" indicator-position="outside" arrow="always">
          <el-carousel-item v-for="item in carouselList" :key="item.id">
            
            <div class="recommend-carousel-card transparent-card">
              
              <div class="carousel-img-wrapper">
                <div class="carousel-img" v-if="item.firstPicture">
                  <router-link :to="`/article/${item.id}`">
                    <img :src="item.firstPicture" alt="cover">
                  </router-link>
                </div>
                <div class="carousel-img-placeholder" v-else></div>
              </div>
              
              <div class="carousel-info">
                <h2 class="rec-title text-truncate">
                  <router-link :to="`/article/${item.id}`">{{ item.title }}</router-link>
                </h2>
                
                <p class="rec-desc">{{ item.description }}</p>
                
                <div class="rec-author">
                  <router-link to="/about" class="author-link">{{ item.author }}</router-link>
                </div>
                
                <div class="rec-meta">
                  <span class="meta-date">
                    {{ formatDateCN(item.createTime) }}
                  </span>
                  <span class="meta-icon">
                    <el-icon><View /></el-icon> {{ item.views || 0 }}
                  </span>
                  <span class="meta-icon">
                    <el-icon><ChatDotRound /></el-icon> {{ item.commentCount || 0 }}
                  </span>
                </div>
              </div>

            </div>
          </el-carousel-item>
        </el-carousel>
      </el-col>

      <el-col :md="11" :xs="24">
        <div class="recommend-side-list">
          
          <div v-for="item in sideList" :key="item.id" class="side-card transparent-card">
            
            <div class="side-content">
              <h3 class="side-title">
                <router-link :to="`/article/${item.id}`">{{ item.title }}</router-link>
              </h3>
              
              <div class="side-author">
                <router-link to="/about" class="author-link">{{ item.author }}</router-link>
              </div>
              
              <div class="side-meta">
                <span class="meta-date-small">
                  {{ formatDateCN(item.createTime) }}
                </span>
                <span class="meta-icon">
                  <el-icon><View /></el-icon> {{ item.views || 0 }}
                </span>
                <span class="meta-icon">
                  <el-icon><ChatDotRound /></el-icon> {{ item.commentCount || 0 }}
                </span>
              </div>
            </div>

            <div class="side-img" v-if="item.firstPicture">
              <router-link :to="`/article/${item.id}`">
                <img :src="item.firstPicture" alt="cover">
              </router-link>
            </div>

          </div>

        </div>
      </el-col>

    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { getFrontRecommendList } from '@/api/blog'
import { View, ChatDotRound } from '@element-plus/icons-vue'

const recommendList = ref([])

// 计算属性：前3篇轮播，后4篇列表
const carouselList = computed(() => recommendList.value.slice(0, 3)) 
const sideList = computed(() => recommendList.value.slice(3, 7))     

const fetchData = async () => {
  try {
    const res = await getFrontRecommendList()
    if (res.success) {
      recommendList.value = res.data.list
    }
  } catch (err) {
    console.error("获取推荐文章失败", err)
  }
}

// 时间格式化：2025-10-10 17:42:14 -> 2025年10月10日 17:42:14
const formatDateCN = (timeStr) => {
  if (!timeStr) return ''
  const datePart = timeStr.split(' ')[0]
  const timePart = timeStr.split(' ')[1]
  if(datePart && timePart) {
    const [y, m, d] = datePart.split('-')
    return `${y}年${m}月${d}日 ${timePart}`
  }
  return timeStr
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
/* ========== 全透明卡片通用样式 ========== */
.transparent-card {
  background: transparent;
  border: 1px solid transparent;
  border-radius: 8px;
  transition: all 0.3s ease;
  overflow: hidden;
}
/* 悬停效果：轻微浮起，加一点点磨砂背景 */
.transparent-card:hover {
  background: rgba(255, 255, 255, 0.2);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
  backdrop-filter: blur(5px);
  transform: translateY(-2px);
}

a { text-decoration: none; color: inherit; transition: color 0.2s; }
a:hover { color: var(--bs-primary); }

.author-link {
  font-size: 0.9rem;
  color: #555;
  border-bottom: 2px solid #ccc; 
  padding-bottom: 1px;
}
.author-link:hover { color: var(--bs-primary); border-bottom-color: var(--bs-primary); }

.rec-meta, .side-meta {
  display: flex; align-items: center; font-size: 0.85rem; color: #666; gap: 12px;
}
.meta-date, .meta-date-small { font-family: "Helvetica Neue", Arial, sans-serif; letter-spacing: 0.5px; }
.meta-icon { display: flex; align-items: center; gap: 4px; }

/* ================= 左侧轮播样式 ================= */
.recommend-carousel-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

/* 图片区域容器 */
.carousel-img-wrapper {
  height: 260px; /* 固定图片区域高度 */
  width: 100%;
  flex-shrink: 0; /* 防止被压缩 */
}

/* 有图时的样式 */
.carousel-img {
  height: 100%;
  width: 100%;
  overflow: hidden;
  border-radius: 8px;
}
.carousel-img img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.5s; }
.recommend-carousel-card:hover .carousel-img img { transform: scale(1.05); }

/* 无图时的占位符 (透明，占位用) */
.carousel-img-placeholder {
  height: 100%;
  width: 100%;
  /* 如果你想让没图的时候显示一个默认底图，可以在这里加 background */
}

/* 内容区域 */
.carousel-info {
  flex: 1;
  padding: 15px 10px;
  display: flex;
  flex-direction: column;
  justify-content: space-evenly; /* 均匀分布 */
}

.rec-title { font-size: 1.4rem; font-weight: bold; color: #333; margin: 0; }

/* 描述：限制2行 */
.rec-desc { 
  color: #555; font-size: 0.95rem; margin: 0; 
  display: -webkit-box; -webkit-box-orient: vertical; -webkit-line-clamp: 2; overflow: hidden;
}
.rec-author { text-align: left; }

/* ================= 右侧列表样式 ================= */
.recommend-side-list {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: 420px; /* 这里的 420px 必须和 carousel 高度匹配 */
}

.side-card {
  flex: 1;
  display: flex;
  align-items: center;
  padding: 5px 10px;
  gap: 15px;
  margin-bottom: 10px;
}
.side-card:last-child { margin-bottom: 0; }

.side-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 8px;
}
.side-title { 
  font-size: 1.05rem; font-weight: bold; color: #333; margin: 0; 
  display: -webkit-box; -webkit-line-clamp: 1; -webkit-box-orient: vertical; overflow: hidden;
}
.side-author { text-align: left; }

.side-img {
  width: 120px;
  height: 80px;
  border-radius: 6px;
  overflow: hidden;
  flex-shrink: 0;
}
.side-img img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.5s; }
.side-card:hover .side-img img { transform: scale(1.05); }

/* 响应式 */
@media (max-width: 768px) {
  .carousel-img-wrapper { height: 200px; }
  .recommend-side-list { height: auto; gap: 15px; margin-top: 20px; }
}
</style>