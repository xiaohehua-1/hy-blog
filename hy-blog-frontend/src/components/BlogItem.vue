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
             v-for="tag in data.tagList.filter(t => t)" 
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

    <!-- 右侧图片：有图才显示 -->
    <div class="blog-cover" v-if="data.firstPicture">
      <router-link :to="`/article/${data.id}`">
        <img :src="data.firstPicture" alt="cover" loading="lazy">
      </router-link>
    </div>

  </div>
</template>

<script setup>
/**
 * 博客文章卡片组件
 * 展示标题、描述、作者、元信息（日期/浏览/评论）、标签、封面图
 * 标签颜色通过 id 取模循环分配
 */
import { Clock } from '@element-plus/icons-vue'
defineProps({
  data: { type: Object, required: true, default: () => ({}) }
})

// 标签配色池，通过 id % 6 循环分配，保证同一标签颜色一致
const tagColors = [
  { bg: '#eef4ff', text: '#597ef7' },
  { bg: '#f6ffed', text: '#73d13d' },
  { bg: '#fff7e6', text: '#fa8c16' },
  { bg: '#fff1f0', text: '#f5222d' },
  { bg: '#f9f0ff', text: '#722ed1' },
  { bg: '#e6fffb', text: '#13c2c2' },
]

/** 根据标签 ID 取模返回配色，同一标签在不同文章间颜色稳定 */
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
  padding: var(--spacing-lg);
  margin-bottom: var(--spacing-lg);
  gap: var(--spacing-lg);
  width: 100%;
  align-items: stretch;
  
  /* 默认：全透明，无边框，无阴影 */
  background: transparent; 
  border: 1px solid transparent; /* 占位，防止hover时抖动 */
  border-radius: 12px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.transparent-card:hover {
  /* 悬停：出现微弱背景和阴影 */
  background: rgba(255, 255, 255, 0.4); /* 稍微增加不透明度 */
  border-color: rgba(255, 255, 255, 0.5);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  transform: translateY(-4px);
  backdrop-filter: blur(8px); 
}

/* 内容布局 */
.blog-content {
  flex: 1; display: flex; flex-direction: column; justify-content: space-between; min-width: 0;
}

.blog-title {
  font-size: 1.4rem; font-weight: 700; margin: 0 0 var(--spacing-sm) 0; line-height: 1.4; text-align: left;
  letter-spacing: var(--letter-spacing-base);
}
.link-hover { color: var(--bs-gray-900); text-decoration: none; transition: 0.2s; }
.link-hover:hover { color: var(--bs-primary); }
.text-truncate { white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }

.blog-desc {
  color: var(--bs-gray-700); font-size: 0.95rem; line-height: var(--line-height-base); margin: 0 0 var(--spacing-md) 0; text-align: left;
  display: -webkit-box; -webkit-box-orient: vertical; -webkit-line-clamp: 2; overflow: hidden; text-overflow: ellipsis;
  letter-spacing: var(--letter-spacing-base);
}

.blog-author-row { margin-bottom: var(--spacing-md); text-align: left; }
.author-link {
  color: var(--bs-gray-600); font-size: 0.9rem; font-weight: 600; text-decoration: none;
  border-bottom: 2px solid var(--bs-gray-300); padding-bottom: 2px; transition: 0.3s;
}
.author-link:hover { color: var(--bs-primary); border-bottom-color: var(--bs-primary); }

.blog-meta-row { display: flex; justify-content: space-between; align-items: center; font-size: 0.85rem; color: var(--bs-gray-500); }
.meta-left { display: flex; align-items: center; gap: var(--spacing-md); }
.meta-icon-box { display: flex; align-items: center; gap: 4px; }
.icon-small { font-size: 0.9rem; margin-right: 2px; }

/* 时间样式 */
.meta-date { 
  font-weight: 500; 
  color: var(--bs-gray-600); 
  display: flex; 
  align-items: center; 
  gap: 4px; 
}

.meta-right { display: flex; align-items: center; gap: var(--spacing-sm); }
.meta-tag { padding: 2px 8px; border-radius: 4px; font-size: 0.8rem; font-weight: 500; }
.copyright-badge { padding: 2px 8px; border-radius: 4px; color: #fff; font-size: 0.75rem; font-weight: 600; }
.copyright-badge.original { background-color: #ff6b6b; }
.copyright-badge.reprint { background-color: #fca130; }

/* 右侧图片：修改为 280x180 */
.blog-cover {
  width: 280px; 
  height: 180px; 
  flex-shrink: 0; 
  border-radius: 8px; 
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
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