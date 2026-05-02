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
import { ref, onMounted, onUnmounted } from 'vue'

const readingProgress = ref(0)
const showVerticalProgress = ref(false)
const progressTrackRef = ref(null)
let isDragging = false

const handleScroll = () => {
  const scrollTop = window.pageYOffset || document.documentElement.scrollTop
  const scrollHeight = document.documentElement.scrollHeight
  const clientHeight = document.documentElement.clientHeight
  
  // 降低显示门槛：只要能滚动（内容高度 > 窗口高度），就显示侧边条
  showVerticalProgress.value = scrollHeight > clientHeight

  // 增加 5px 的容错缓冲区
  // 有些浏览器滚动到底部时，scrollTop + clientHeight 可能会比 scrollHeight 小一点点
  const distanceToBottom = scrollHeight - clientHeight - scrollTop

  if (distanceToBottom <= 5) {
    readingProgress.value = 100
  } else {
    // 正常计算百分比
    const percentage = Math.floor((scrollTop / (scrollHeight - clientHeight)) * 100)
    readingProgress.value = Math.min(100, Math.max(0, percentage))
  }
}

const handleTrackClick = (e) => updateScrollFromEvent(e)

const startDrag = (e) => {
  isDragging = true
  document.addEventListener('mousemove', onDrag)
  document.addEventListener('mouseup', stopDrag)
  e.preventDefault()
}
const onDrag = (e) => { if (isDragging) updateScrollFromEvent(e) }
const stopDrag = () => {
  isDragging = false
  document.removeEventListener('mousemove', onDrag)
  document.removeEventListener('mouseup', stopDrag)
}

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
  // 初始化一次，防止页面刚加载时不显示
  handleScroll()
})
onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
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