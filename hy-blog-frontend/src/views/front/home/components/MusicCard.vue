<template>
  <div class="music-wrapper mb-5" v-if="musicList.length > 0">
    
    <h3 class="section-title"><span>音乐推荐</span></h3>

    <div class="music-player transparent-card">
      
      <audio 
        ref="audioRef" 
        :src="currentMusic.filePath" 
        @timeupdate="onTimeUpdate" 
        @ended="onEnded"
        @loadedmetadata="onLoadedMetadata"
        @canplay="onCanPlay"
        @error="onError"
      ></audio>

      <div class="music-info-row">
        <div class="music-cover" :class="{ 'playing': isPlaying && !isError }">
          <img :src="currentMusic.coverPath || defaultCover" alt="cover">
          <div class="cover-center"></div>
        </div>
        
        <div class="music-text">
          <div class="music-name text-truncate" :title="currentMusic.title">
            {{ currentMusic.title }}
          </div>
          <div class="music-artist text-truncate" :title="currentMusic.artist">
            {{ currentMusic.artist || '未知艺术家' }}
          </div>
        </div>
      </div>

      <div class="music-progress-row">
        <span class="time-text">{{ formatTime(currentTime) }}</span>
        
        <el-slider 
          v-model="currentTime" 
          :max="duration" 
          :show-tooltip="false"
          @change="onSliderChange"
          @input="onSliderInput"
          class="custom-slider"
          :disabled="isError"
        />
        
        <span class="time-text">{{ formatTime(duration) }}</span>
      </div>

      <div class="music-control-row">
        
        <div class="volume-box">
          <el-icon class="control-btn" v-if="volume > 0" @click="toggleMute"><Microphone /></el-icon>
          <el-icon class="control-btn" v-else @click="toggleMute"><Mute /></el-icon>
          
          <div class="volume-slider-wrapper">
             <el-slider v-model="volume" vertical height="60px" @input="onVolumeChange" />
          </div>
        </div>

        <div class="play-actions">
           <el-icon class="control-btn small" @click="playPrev"><ArrowLeftBold /></el-icon>
           
           <div class="play-toggle" @click="togglePlay" :class="{ 'disabled': isError }">
             <el-icon v-if="!isPlaying"><VideoPlay /></el-icon>
             <el-icon v-else><VideoPause /></el-icon>
           </div>
           
           <el-icon class="control-btn small" @click="playNext"><ArrowRightBold /></el-icon>
        </div>

        <div class="dynamic-icon">
          <div class="bar" :class="{ 'animating': isPlaying && !isError }"></div>
          <div class="bar" :class="{ 'animating': isPlaying && !isError }"></div>
          <div class="bar" :class="{ 'animating': isPlaying && !isError }"></div>
          <div class="bar" :class="{ 'animating': isPlaying && !isError }"></div>
        </div>

      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import { getMusicList } from '@/api/music'
import { 
  VideoPlay, VideoPause, ArrowLeftBold, ArrowRightBold, 
  Microphone, Mute 
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const audioRef = ref(null)
const musicList = ref([])
const currentIndex = ref(0)
const isPlaying = ref(false)
const currentTime = ref(0)
const duration = ref(0)
const isDragging = ref(false) // 是否正在拖拽进度条
const volume = ref(50)
const isError = ref(false) // 是否加载出错
const defaultCover = 'https://picsum.photos/200/200?music'

// 当前音乐对象
const currentMusic = computed(() => {
  return musicList.value[currentIndex.value] || {}
})

// 监听播放状态：这是解决"点击不播放"最稳妥的方法
watch(isPlaying, (val) => {
  nextTick(() => {
    if (!audioRef.value) return
    if (val) {
      const playPromise = audioRef.value.play()
      if (playPromise !== undefined) {
        playPromise.catch(err => {
          console.error("播放失败:", err)
          isPlaying.value = false
        })
      }
    } else {
      audioRef.value.pause()
    }
  })
})

const fetchMusic = async () => {
  try {
    const res = await getMusicList()
    if (res.success && res.data.list.length > 0) {
      musicList.value = res.data.list
      // 初始化音量
      nextTick(() => {
        if(audioRef.value) audioRef.value.volume = volume.value / 100
      })
    }
  } catch (err) {
    console.error(err)
  }
}

// 切换播放/暂停
const togglePlay = () => {
  if (isError.value) return
  isPlaying.value = !isPlaying.value
}

// 切歌
const changeMusic = (index) => {
  isPlaying.value = false // 先暂停
  isError.value = false
  currentTime.value = 0
  duration.value = 0
  currentIndex.value = index
  // 这里的自动播放会在 audio 加载完 metadata 后通过 watch 触发吗？
  // 不，建议在 onCanPlay 中处理，或者简单的延迟播放
  setTimeout(() => {
    isPlaying.value = true
  }, 500)
}

const playPrev = () => {
  let index = (currentIndex.value - 1 + musicList.value.length) % musicList.value.length
  changeMusic(index)
}

const playNext = () => {
  let index = (currentIndex.value + 1) % musicList.value.length
  changeMusic(index)
}

// Audio 事件钩子
const onTimeUpdate = (e) => {
  // 只有在非拖拽状态下才更新进度条，解决"进度条跳动"问题
  if (!isDragging.value) {
    currentTime.value = e.target.currentTime
  }
}

const onLoadedMetadata = (e) => {
  duration.value = e.target.duration
  isError.value = false
}

const onCanPlay = () => {
  // 音频准备好可以播放了
}

const onError = () => {
  console.error("音频加载失败")
  isError.value = true
  isPlaying.value = false
  // 自动切下一首（可选）
  // playNext()
}

const onEnded = () => {
  playNext()
}

// 进度条拖拽中
const onSliderInput = (val) => {
  isDragging.value = true
  currentTime.value = val
}

// 进度条拖拽结束（松手）
const onSliderChange = (val) => {
  isDragging.value = false
  if(audioRef.value) {
    audioRef.value.currentTime = val
  }
}

const onVolumeChange = (val) => {
  if(audioRef.value) audioRef.value.volume = val / 100
}

const toggleMute = () => {
  volume.value = volume.value > 0 ? 0 : 50
  onVolumeChange(volume.value)
}

const formatTime = (time) => {
  if (!time || isNaN(time)) return '00:00'
  const minutes = Math.floor(time / 60)
  const seconds = Math.floor(time % 60)
  return `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
}

onMounted(() => {
  fetchMusic()
})
</script>

<style scoped>
/* 全透明卡片复用 */
.transparent-card {
  background: transparent;
  border: 1px solid transparent;
  border-radius: 8px;
  padding: 15px;
  transition: all 0.3s ease;
}
.transparent-card:hover {
  background: rgba(255, 255, 255, 0.2);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
  backdrop-filter: blur(5px);
  border-color: rgba(255, 255, 255, 0.3);
}

.music-info-row { display: flex; align-items: center; gap: 15px; margin-bottom: 15px; }

/* 封面 */
.music-cover {
  width: 70px; height: 70px; border-radius: 6px; overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1); position: relative; flex-shrink: 0;
  transition: border-radius 0.5s; /* 圆角过渡 */
}
.music-cover img { width: 100%; height: 100%; object-fit: cover; }
.music-cover.playing { border-radius: 50%; animation: rotate 10s linear infinite; }
.cover-center {
  position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%);
  width: 15px; height: 15px; background: #000; border-radius: 50%; border: 2px solid #fff;
  opacity: 0; transition: opacity 0.3s;
}
.music-cover.playing .cover-center { opacity: 0.8; }

@keyframes rotate { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }

/* 信息 */
.music-text { flex: 1; overflow: hidden; }
.music-name { font-size: 1.05rem; font-weight: bold; color: #333; margin-bottom: 4px; }
.music-artist { font-size: 0.85rem; color: #666; }

/* 进度条 */
.music-progress-row { display: flex; align-items: center; gap: 10px; margin-bottom: 10px; }
.time-text { font-size: 0.75rem; color: #666; font-family: monospace; width: 35px; text-align: center; }
.custom-slider { flex: 1; }
:deep(.el-slider__runway) { margin: 0; height: 4px; background-color: rgba(0,0,0,0.1); }
:deep(.el-slider__bar) { height: 4px; background-color: var(--bs-primary); }
:deep(.el-slider__button) { width: 10px; height: 10px; border: 2px solid var(--bs-primary); }

/* 控制区 */
.music-control-row { display: flex; justify-content: space-between; align-items: center; }

.play-actions { display: flex; align-items: center; gap: 15px; }
.control-btn { font-size: 1.2rem; cursor: pointer; color: #555; transition: color 0.2s; }
.control-btn:hover { color: var(--bs-primary); }
.play-toggle {
  width: 36px; height: 36px; border-radius: 50%; background: var(--bs-primary);
  color: #fff; display: flex; align-items: center; justify-content: center; font-size: 1.2rem;
  cursor: pointer; box-shadow: 0 2px 8px rgba(13, 110, 253, 0.3); transition: transform 0.2s;
}
.play-toggle:hover { transform: scale(1.1); }
.play-toggle.disabled { background: #ccc; cursor: not-allowed; }

.volume-box { position: relative; display: flex; align-items: center; }
.volume-slider-wrapper {
  position: absolute; bottom: 25px; left: -5px; height: 80px; width: 24px;
  background: #fff; border-radius: 4px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  display: none; padding: 10px 0; justify-content: center; z-index: 10;
}
.volume-box:hover .volume-slider-wrapper { display: flex; }

/* 修复点：动态线条样式
  1. 强制 box-shadow: none 和 border: none 去除黑线
  2. 确保高度固定
*/
.dynamic-icon { display: flex; align-items: flex-end; gap: 3px; height: 16px; padding-bottom: 2px; overflow: hidden; }
.bar {
  width: 3px; 
  background-color: var(--bs-primary); /* 强制使用主题色 */
  border-radius: 2px; 
  height: 2px; 
  transition: height 0.2s;
  /* 关键修复：去除可能的边框和阴影 */
  border: none !important;
  box-shadow: none !important;
}
@keyframes dance { 0%, 100% { height: 2px; } 50% { height: 12px; } }
.bar.animating:nth-child(1) { animation: dance 0.6s infinite 0.1s; }
.bar.animating:nth-child(2) { animation: dance 0.6s infinite 0.3s; }
.bar.animating:nth-child(3) { animation: dance 0.6s infinite 0.0s; }
.bar.animating:nth-child(4) { animation: dance 0.6s infinite 0.2s; }
</style>