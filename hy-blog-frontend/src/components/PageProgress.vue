<template>
  <div class="page-progress-container">
    <div class="reading-progress-bar" :style="{ width: readingProgress + '%' }"></div>
    
    <div class="vertical-progress-box hidden-xs-only" v-show="showVerticalProgress">
      <div class="progress-track" ref="progressTrackRef" @click="handleTrackClick">
        <div class="progress-thumb" :style="{ height: readingProgress + '%' }" @mousedown="startDrag">
          <div class="thumb-grip"></div>
        </div>
      </div>
      <div class="progress-text">{{ readingProgress }}%</div>
    </div>
  </div>
</template>

<script setup>
/**
 * 阅读进度条组件
 * 顶部细条 + 右侧垂直进度条（支持点击跳转和拖拽），内容不足以滚动时自动隐藏
 */
import { ref, onMounted, onUnmounted } from 'vue'

const readingProgress = ref(0)
const showVerticalProgress = ref(false)
const progressTrackRef = ref(null)
let isDragging = false

/** 计算阅读百分比，底部 5px 容错避免浏览器精度导致无法到达 100% */
const handleScroll = () => {
  const scrollTop = window.pageYOffset || document.documentElement.scrollTop
  const scrollHeight = document.documentElement.scrollHeight
  const clientHeight = document.documentElement.clientHeight
  
  showVerticalProgress.value = scrollHeight > clientHeight

  // 5px 容错：浏览器滚动到底部时 scrollTop + clientHeight 可能略小于 scrollHeight
  const distanceToBottom = scrollHeight - clientHeight - scrollTop

  if (distanceToBottom <= 5) {
    readingProgress.value = 100
  } else {
    // 正常计算百分比
    const percentage = Math.floor((scrollTop / (scrollHeight - clientHeight)) * 100)
    readingProgress.value = Math.min(100, Math.max(0, percentage))
  }
}

/** 点击进度条跳转 */
const handleTrackClick = (e) => updateScrollFromEvent(e)

/** 开始拖拽进度条滑块 */
const startDrag = (e) => {
  isDragging = true
  document.addEventListener('mousemove', onDrag)
  document.addEventListener('mouseup', stopDrag)
  e.preventDefault()
}
const onDrag = (e) => { if (isDragging) updateScrollFromEvent(e) }
/** 停止拖拽，清理全局事件监听 */
const stopDrag = () => {
  isDragging = false
  document.removeEventListener('mousemove', onDrag)
  document.removeEventListener('mouseup', stopDrag)
}

/** 根据鼠标位置计算百分比并跳转到对应滚动位置 */
const updateScrollFromEvent = (e) => {
  if (!progressTrackRef.value) return
  const trackRect = progressTrackRef.value.getBoundingClientRect()
  const clickY = e.clientY - trackRect.top
  const trackHeight = trackRect.height
  
  let percentage = clickY / trackHeight
  percentage = Math.max(0, Math.min(1, percentage))

  const scrollHeight = document.documentElement.scrollHeight
  const clientHeight = document.documentElement.clientHeight
  window.scrollTo({ top: percentage * (scrollHeight - clientHeight), behavior: 'auto' })
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
  handleScroll() // 首屏初始化
})
onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
  // 清理拖拽监听，避免内存泄漏
  document.removeEventListener('mousemove', onDrag)
  document.removeEventListener('mouseup', stopDrag)
})
</script>

<style scoped>
.reading-progress-bar {
  position: fixed; top: 0; left: 0; height: 3px;
  background: linear-gradient(90deg, #ff7e5f, #feb47b);
  z-index: 9999; transition: width 0.1s ease;
}
.vertical-progress-box {
  position: fixed; right: 20px; top: 50%; transform: translateY(-50%);
  height: 300px; display: flex; flex-direction: column; align-items: center;
  gap: 10px; z-index: 990;
}
.progress-track {
  width: 6px; height: 100%; background-color: rgba(0,0,0,0.1);
  border-radius: 10px; position: relative; cursor: pointer; overflow: hidden; 
}
.progress-track:hover { width: 10px; transition: width 0.2s; }
.progress-thumb {
  width: 100%; background: linear-gradient(to bottom, #ff7e5f, #feb47b);
  border-radius: 10px; position: absolute; top: 0; left: 0;
  transition: height 0.1s linear;
}
.thumb-grip {
  position: absolute; bottom: 0; left: 50%; transform: translateX(-50%);
  width: 12px; height: 12px; background: #fff; border: 2px solid #ff7e5f;
  border-radius: 50%; cursor: grab; opacity: 0; transition: opacity 0.2s;
}
.progress-track:hover .thumb-grip, .progress-thumb:active .thumb-grip { opacity: 1; }
.progress-text {
  font-size: 12px; color: #999; font-weight: bold;
  writing-mode: vertical-rl; text-orientation: mixed;
}
@media (max-width: 992px) {
  .hidden-xs-only { display: none !important; }
}
</style>