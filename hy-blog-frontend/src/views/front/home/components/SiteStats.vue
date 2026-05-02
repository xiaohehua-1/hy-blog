<template>
  <div class="stats-wrapper mb-5">
    
    <h3 class="section-title"><span>站点统计</span></h3>

    <div class="stats-card transparent-card">
      
      <div class="stats-item">
        <div class="stats-left">
          <el-icon class="stats-icon"><View /></el-icon>
          <span class="stats-num">{{ stats.totalViews || 0 }}</span>
        </div>
        <div class="stats-right">
          <span class="stats-label">访问总数</span>
          <span class="today-tag" v-if="stats.todayViews > 0">
            {{ stats.todayViews }} <el-icon><Top /></el-icon>
          </span>
        </div>
      </div>

      <div class="stats-item">
        <div class="stats-left">
          <el-icon class="stats-icon"><Document /></el-icon>
          <span class="stats-num">{{ stats.totalBlogs || 0 }}</span>
        </div>
        <div class="stats-right">
          <span class="stats-label">博文数量</span>
          <span class="today-tag" v-if="stats.todayBlogs > 0">
            {{ stats.todayBlogs }} <el-icon><Top /></el-icon>
          </span>
        </div>
      </div>

      <div class="stats-item">
        <div class="stats-left">
          <el-icon class="stats-icon"><ChatLineSquare /></el-icon>
          <span class="stats-num">{{ stats.totalComments || 0 }}</span>
        </div>
        <div class="stats-right">
          <span class="stats-label">评论总数</span>
          <span class="today-tag" v-if="stats.todayComments > 0">
            {{ stats.todayComments }} <el-icon><Top /></el-icon>
          </span>
        </div>
      </div>

      <div class="stats-item">
        <div class="stats-left">
          <el-icon class="stats-icon"><ChatDotSquare /></el-icon>
          <span class="stats-num">{{ stats.totalMessages || 0 }}</span>
        </div>
        <div class="stats-right">
          <span class="stats-label">留言总数</span>
          <span class="today-tag" v-if="stats.todayMessages > 0">
            {{ stats.todayMessages }} <el-icon><Top /></el-icon>
          </span>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getSiteStats, reportVisit } from '@/api/statistics'
import { View, Document, ChatLineSquare, ChatDotSquare, Top } from '@element-plus/icons-vue'

const stats = ref({})

const initData = async () => {
  try {
    // 1. 上报一次访问 (让 Redis +1)
    await reportVisit()
    // 2. 获取数据
    const res = await getSiteStats()
    if (res.success) {
      stats.value = res.data.data
    }
  } catch (err) {
    console.error("获取统计失败", err)
  }
}

onMounted(() => {
  initData()
})
</script>

<style scoped>
/* 全透明卡片容器 */
.transparent-card {
  background: transparent;
  padding: 10px 15px;
  border-radius: 8px;
  /* 只有悬停时才显示微弱背景，平时完全透明 */
  transition: all 0.3s ease;
}
.transparent-card:hover {
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(5px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
}

.stats-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  /* 虚线分隔 */
  /* border-bottom: 1px dashed rgba(0,0,0,0.1); */ 
}

/* 左侧：图标 + 数字 */
.stats-left {
  display: flex;
  align-items: center;
  width: 120px; /* 固定宽度对齐 */
  gap: 10px;
}
.stats-icon { font-size: 1.1rem; color: #333; }
.stats-num { 
  font-size: 1.1rem; 
  font-family: 'Helvetica Neue', Arial, sans-serif; 
  color: #333; 
  letter-spacing: 1px;
}

/* 右侧：文字 + 绿色箭头 */
.stats-right {
  display: flex;
  align-items: center;
  gap: 8px;
}
.stats-label { font-size: 0.95rem; color: #444; }

/* 今日新增提示 (仿图中的绿色小数字) */
.today-tag {
  font-size: 0.8rem;
  color: #10b981; /* 清新的绿色 */
  display: flex;
  align-items: center;
  font-weight: bold;
}
</style>