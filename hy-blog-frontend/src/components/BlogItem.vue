<template>
  <div class="blog-item transparent-card">
    
    <div class="blog-content">
      
      <h2 class="blog-title text-truncate">
        <router-link :to="`/article/${data.id}`" class="link-hover">
          {{ data.title }}
        </router-link>
      </h2>

      <p class="blog-desc">
        {{ data.description }}
      </p>

      <div class="blog-author-row">
        <router-link to="/about" class="author-link">
          {{ data.author }}
        </router-link>
      </div>

      <div class="blog-meta-row">
        <div class="meta-left">
          <span class="meta-date" v-if="data.createTime">
             <el-icon class="icon-small"><Clock /></el-icon>
             {{ data.createTime }}
          </span>
          
          <span class="meta-icon-box">
             <el-icon class="icon-small"><View /></el-icon> 
             {{ data.views || 0 }}
          </span>
          <span class="meta-icon-box">
             <el-icon class="icon-small"><ChatDotRound /></el-icon> 
             {{ data.commentCount || 0 }}
          </span>
        </div>

        <div class="meta-right">
           <span 
             v-for="tag in data.tagList" 
             :key="tag.id" 
             class="meta-tag"
             :style="getTagStyle(tag.id)" 
           >
             # {{ tag.name }}
           </span>
           
           <span class="copyright-badge" :class="data.isOriginal ? 'original' : 'reprint'">
             {{ data.isOriginal ? '原创' : '转载' }}
           </span>
        </div>
      </div>
    </div>

    <div class="blog-cover" v-if="data.firstPicture">
      <router-link :to="`/article/${data.id}`">
        <img :src="data.firstPicture" alt="cover" loading="lazy">
      </router-link>
    </div>

  </div>
</template>

<script setup>
import { Clock } from '@element-plus/icons-vue' // 引入时钟图标
defineProps({
  data: { type: Object, required: true, default: () => ({}) }
})

const tagColors = [
  { bg: '#eef4ff', text: '#597ef7' },
  { bg: '#f6ffed', text: '#73d13d' },
  { bg: '#fff7e6', text: '#fa8c16' },
  { bg: '#fff1f0', text: '#f5222d' },
  { bg: '#f9f0ff', text: '#722ed1' },
  { bg: '#e6fffb', text: '#13c2c2' },
]

const getTagStyle = (id) => {
  const index = id % tagColors.length
  const color = tagColors[index]
  return { backgroundColor: color.bg, color: color.text }
}
</script>

<style scoped>
/* ========== 全透明卡片 (无边框) ========== */
.transparent-card {
  display: flex;
  padding: 20px;
  margin-bottom: 25px;
  gap: 25px;
  width: 100%;
  align-items: stretch;
  
  /* 默认：全透明，无边框，无阴影 */
  background: transparent; 
  border: 1px solid transparent; /* 占位，防止hover时抖动 */
  border-radius: 8px;
  transition: all 0.3s ease;
}

.transparent-card:hover {
  /* 悬停：出现微弱背景和阴影 */
  background: rgba(255, 255, 255, 0.3); /* 30%白 */
  border-color: rgba(255, 255, 255, 0.5);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
  transform: translateY(-3px);
  backdrop-filter: blur(5px); /* 悬停时加一点磨砂 */
}

/* 内容布局 */
.blog-content {
  flex: 1; display: flex; flex-direction: column; justify-content: space-between; min-width: 0;
}

.blog-title {
  font-size: 1.4rem; font-weight: 700; margin: 0 0 10px 0; line-height: 1.4; text-align: left;
}
.link-hover { color: #333; text-decoration: none; transition: 0.2s; }
.link-hover:hover { color: var(--bs-primary); }
.text-truncate { white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }

.blog-desc {
  color: #444; font-size: 0.95rem; line-height: 1.6; margin: 0 0 12px 0; text-align: left;
  display: -webkit-box; -webkit-box-orient: vertical; -webkit-line-clamp: 2; overflow: hidden; text-overflow: ellipsis;
}

.blog-author-row { margin-bottom: 12px; text-align: left; }
.author-link {
  color: #555; font-size: 0.9rem; font-weight: 600; text-decoration: none;
  border-bottom: 2px solid #999; padding-bottom: 2px; transition: 0.3s;
}
.author-link:hover { color: var(--bs-primary); border-bottom-color: var(--bs-primary); }

.blog-meta-row { display: flex; justify-content: space-between; align-items: center; font-size: 0.85rem; color: #666; }
.meta-left { display: flex; align-items: center; gap: 15px; }
.meta-icon-box { display: flex; align-items: center; gap: 4px; }
.icon-small { font-size: 0.9rem; margin-right: 2px; }

/* 时间样式 */
.meta-date { 
  font-family: 'Helvetica Neue', Arial, sans-serif; 
  font-weight: 500; 
  color: #555; /* 加深颜色 */
  display: flex; 
  align-items: center; 
  gap: 4px; 
}

.meta-right { display: flex; align-items: center; gap: 8px; }
.meta-tag { padding: 2px 8px; border-radius: 4px; font-size: 0.8rem; font-weight: 500; }
.copyright-badge { padding: 2px 8px; border-radius: 4px; color: #fff; font-size: 0.75rem; }
.copyright-badge.original { background-color: #ff6b6b; }
.copyright-badge.reprint { background-color: #fca130; }

/* 右侧图片：修改为 280x180 */
.blog-cover {
  width: 280px; 
  height: 180px; 
  flex-shrink: 0; 
  border-radius: 6px; 
  overflow: hidden;
  box-shadow: 0 4px 10px rgba(0,0,0,0.1);
}
.blog-cover img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.5s ease; }
.blog-cover:hover img { transform: scale(1.05); }

@media (max-width: 768px) {
  .blog-item { flex-direction: column-reverse; height: auto; align-items: flex-start; }
  .blog-cover { width: 100%; height: 180px; }
  .blog-meta-row { flex-direction: column; align-items: flex-start; gap: 10px; width: 100%; }
  .meta-right { width: 100%; justify-content: flex-start; }
}
</style>