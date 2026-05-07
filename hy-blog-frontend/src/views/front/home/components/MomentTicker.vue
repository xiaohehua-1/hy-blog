<template>
  <div 
    class="moment-ticker-wrapper" 
    @mouseenter="stopAutoPlay" 
    @mouseleave="startAutoPlay"
    @click="goToList"
  >
    <div class="ticker-content">
      <div class="ticker-label">
        <span class="pulse-dot"></span>
        <span>动态</span>
      </div>

      <div class="ticker-scroll-box">
        <transition name="slide-up" mode="out-in">
          <div :key="currentIndex" class="current-msg" v-if="currentMoment">
            
            <span class="msg-text">{{ truncate(currentMoment.content || '分享图片', 35) }}</span>
            
            <div class="media-indicators">
              <el-tooltip content="包含图片" placement="top" v-if="hasImage(currentMoment)">
                <el-icon class="indicator-icon"><Picture /></el-icon>
              </el-tooltip>
              <el-tooltip content="包含链接" placement="top" v-if="currentMoment.extraUrl">
                <el-icon class="indicator-icon"><Link /></el-icon>
              </el-tooltip>
            </div>

          </div>
        </transition>
      </div>

      <div class="ticker-arrow">
        <el-icon><ArrowRight /></el-icon>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * 动态跑马灯组件
 * 轮播最新 10 条动态内容，悬停暂停，点击跳转动态列表页
 */
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { Picture, Link, ArrowRight } from '@element-plus/icons-vue'
import { getFrontMomentList } from '@/api/moment'
import { useRouter } from 'vue-router'

const router = useRouter()
const momentList = ref([])
const currentIndex = ref(0)
let timer = null

/** 当前展示的动态 */
const currentMoment = computed(() => {
  if (momentList.value.length === 0) return null
  return momentList.value[currentIndex.value]
})

const hasImage = (item) => item && item.images && item.images.length > 0

/** 文字截断，超出 len 追加 ... */
const truncate = (str, len) => {
  if (!str) return ''
  return str.length > len ? str.substring(0, len) + '...' : str
}

const goToList = () => router.push('/moments')

/** 启动轮播，4000ms 切换一次 */
const startAutoPlay = () => {
  if (timer) clearInterval(timer)
  timer = setInterval(() => {
    if (momentList.value.length > 1) {
      currentIndex.value = (currentIndex.value + 1) % momentList.value.length
    }
  }, 4000)
}

/** 暂停轮播（鼠标悬停） */
const stopAutoPlay = () => {
  if (timer) clearInterval(timer)
}

onMounted(async () => {
  try {
    // 获取最新的 10 条动态来轮播
    const res = await getFrontMomentList({ current: 1, size: 10 })
    if (res.data && res.data.page && res.data.page.records.length > 0) {
      momentList.value = res.data.page.records
      startAutoPlay()
    } else {
      // 兜底数据
      momentList.value = [{ content: '暂无动态，快去发布第一条吧！' }]
    }
  } catch (e) {
    console.error(e)
  }
})

onUnmounted(() => stopAutoPlay())
</script>

<style scoped>
.moment-ticker-wrapper {
  width: 100%;
  max-width: 1200px;
  height: 60px; /* 增加高度 */
  margin: 30px auto; /* 增加间距 */
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border-radius: 50px;
  display: flex;
  align-items: center;
  padding: 0 25px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.06);
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #fff;
}

.moment-ticker-wrapper:hover {
  transform: translateY(-3px) scale(1.01);
  box-shadow: 0 12px 25px rgba(64, 158, 255, 0.15);
  border-color: #409EFF;
}

.ticker-content {
  display: flex;
  align-items: center;
  width: 100%;
  height: 100%;
  position: relative;
}

/* 左侧标签 */
.ticker-label {
  display: flex;
  align-items: center;
  gap: 8px;
  background: linear-gradient(135deg, #409EFF 0%, #79bbff 100%);
  color: #fff;
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: bold;
  margin-right: 20px;
  box-shadow: 0 4px 10px rgba(64, 158, 255, 0.3);
}

.pulse-dot {
  width: 8px;
  height: 8px;
  background-color: #fff;
  border-radius: 50%;
  animation: pulse 1.5s infinite;
}

/* 中间滚动文字 */
.ticker-scroll-box {
  flex: 1;
  height: 100%;
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center; /* 文字居中 */
}

.current-msg {
  position: absolute;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  width: 100%;
}

.msg-text {
  font-size: 16px; /* 字体加大 */
  font-weight: 600; /* 加粗 */
  color: #333;
  letter-spacing: 0.5px;
  transition: color 0.3s;
}

.moment-ticker-wrapper:hover .msg-text {
  color: #409EFF; /* 悬停变色 */
}

.media-indicators {
  display: flex;
  gap: 8px;
}

.indicator-icon {
  background: #f0f2f5;
  padding: 4px;
  border-radius: 4px;
  font-size: 14px;
  color: #909399;
}

.ticker-arrow {
  color: #C0C4CC;
  font-size: 18px;
  margin-left: 15px;
}

/* 动画定义 */
@keyframes pulse {
  0% { box-shadow: 0 0 0 0 rgba(255, 255, 255, 0.7); }
  70% { box-shadow: 0 0 0 6px rgba(255, 255, 255, 0); }
  100% { box-shadow: 0 0 0 0 rgba(255, 255, 255, 0); }
}

/* 滚动切换动画 (Vue Transition) */
.slide-up-enter-active,
.slide-up-leave-active {
  transition: all 0.5s ease;
}

.slide-up-enter-from {
  opacity: 0;
  transform: translateY(20px);
}

.slide-up-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}
</style>