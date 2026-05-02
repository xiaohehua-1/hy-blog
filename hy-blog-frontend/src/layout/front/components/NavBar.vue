<template>
  <nav class="hy-navbar fixed" :class="{ 'scrolled': isScrolled }" v-show="!isHome || showGlobal">
    <div class="hy-container">
      
      <div class="nav-left">
        <el-tooltip content="返回博客首页" placement="bottom" effect="light">
          <router-link to="/" class="brand-capsule">
            <span class="brand-text">HeYi</span>
            <div class="brand-icon">
              <el-icon><HomeFilled /></el-icon>
            </div>
          </router-link>
        </el-tooltip>
      </div>

      <div class="nav-center d-none d-md-flex">
        <router-link to="/" class="hy-nav-item" :class="{ active: route.path === '/' }">首页</router-link>
        <router-link to="/tags" class="hy-nav-item" active-class="active">标签</router-link>
        <router-link to="/archives" class="hy-nav-item" active-class="active">归档</router-link>
        <router-link to="/message" class="hy-nav-item" active-class="active">留言板</router-link>
        <router-link to="/friends" class="hy-nav-item" active-class="active">友链</router-link>
        <router-link to="/about" class="hy-nav-item" active-class="active">关于我</router-link>
      </div>

      <div class="nav-right d-flex align-items-center">
        
        <el-popover
          placement="bottom"
          :width="240" 
          trigger="click"
          popper-class="music-popover"
        >
          <template #reference>
            <div class="icon-btn" :class="{ 'music-playing': isPlaying }" title="音乐播放器">
              <el-icon size="20"><Headset /></el-icon>
            </div>
          </template>
          
          <div class="mini-player">
            <div v-if="playlist.length > 0">
              <div class="song-info">
                <span class="song-name">{{ currentSong?.name || '未知歌名' }}</span>
                <span class="song-artist">{{ currentSong?.artist || 'HeYi' }}</span>
              </div>
              
              <div class="player-controls">
                <el-icon class="ctrl-btn" @click="prevSong"><CaretLeft /></el-icon>
                
                <el-icon class="ctrl-btn play-btn" @click="togglePlay">
                  <VideoPause v-if="isPlaying" />
                  <VideoPlay v-else />
                </el-icon>
                
                <el-icon class="ctrl-btn" @click="nextSong"><CaretRight /></el-icon>
              </div>
              
              <div class="progress-wrapper">
                <el-slider 
                  v-model="progress" 
                  size="small"
                  :show-tooltip="false"
                  @input="onSliderInput"
                  @change="onSliderChange"
                  class="music-slider"
                />
              </div>
            </div>

            <div v-else class="no-music-tip">
              <span>暂无音乐</span>
            </div>
          </div>
        </el-popover>

        <el-tooltip content="站内搜索" placement="bottom" effect="light">
          <div class="icon-btn" @click="openSearch">
            <el-icon size="20"><Search /></el-icon>
          </div>
        </el-tooltip>

        <el-tooltip content="随机前往一个开往项目网站" placement="bottom" effect="light">
          <a href="https://www.travellings.cn/go.html" target="_blank" class="icon-btn">
             <svg viewBox="0 0 1024 1024" width="20" height="20" fill="currentColor">
              <path d="M864 640a32 32 0 0 1 32-32v-96a32 32 0 0 0-32-32h-32V256c0-106-86-192-192-192H384c-106 0-192 86-192 192v224h-32a32 32 0 0 0-32 32v96a32 32 0 0 1 32 32v128c0 17.6 14.4 32 32 32h32c17.6 0 32-14.4 32-32v-64h512v64c0 17.6 14.4 32 32 32h32c17.6 0 32-14.4 32-32V640zM256 256c0-70.6 57.4-128 128-128h256c70.6 0 128 57.4 128 128v224H256V256zm64 640a64 64 0 1 1 128 0 64 64 0 0 1-128 0zm448 0a64 64 0 1 1 128 0 64 64 0 0 1-128 0z" />
            </svg>
          </a>
        </el-tooltip>

        <el-tooltip content="随机一篇文章" placement="bottom" effect="light">
          <div class="icon-btn" @click="handleRandomArticle">
            <el-icon size="20"><Place /></el-icon>
          </div>
        </el-tooltip>

        <el-tooltip content="碎碎念" placement="bottom" effect="light">
          <router-link to="/moments" class="icon-btn">
            <el-icon size="20"><ChatLineRound /></el-icon>
          </router-link>
        </el-tooltip>

        <button class="hy-menu-toggle d-md-none" @click="toggleMobileMenu">
          <span v-if="!mobileMenuOpen">☰</span>
          <span v-else>✕</span>
        </button>
      </div>
    </div>

    <audio 
      v-if="playlist.length > 0"
      ref="audioRef" 
      :src="currentSong?.url" 
      @ended="nextSong"
      @timeupdate="onTimeUpdate"
    ></audio>

    <transition name="slide-down">
      <div v-show="mobileMenuOpen" class="hy-mobile-menu">
        <router-link to="/" class="mobile-link" @click="closeMenu">首页</router-link>
        <router-link to="/tags" class="mobile-link" @click="closeMenu">标签</router-link>
        <router-link to="/archives" class="mobile-link" @click="closeMenu">归档</router-link>
        <router-link to="/moments" class="mobile-link" @click="closeMenu">碎碎念</router-link>
        <router-link to="/message" class="mobile-link" @click="closeMenu">留言板</router-link>
        <router-link to="/friends" class="mobile-link" @click="closeMenu">友链</router-link>
        <router-link to="/about" class="mobile-link" @click="closeMenu">关于我</router-link>
      </div>
    </transition>

    <div v-if="searchModalVisible" class="search-overlay" @click.self="closeSearch">
      <div class="search-modal">
        <div class="search-header">
          <span class="search-title">搜索</span>
          <span class="close-icon" @click="closeSearch">✕</span>
        </div>
        <div class="search-input-box">
          <div class="search-icon-wrapper">
             <el-icon size="18"><Search /></el-icon>
          </div>
          <input 
            ref="searchInputRef"
            type="text" 
            v-model="keyword" 
            class="search-input" 
            placeholder="输入关键字..." 
            @keyup.enter="handleSearch"
          >
        </div>
        <div class="search-results">
          <div v-if="loading" class="result-tip">搜索中...</div>
          <div v-else-if="hasSearched && blogList.length === 0" class="result-tip">未找到相关文章</div>
          <ul v-else class="result-list">
            <li v-for="blog in blogList" :key="blog.id" @click="goToDetail(blog.id)">
              <span class="result-title">{{ blog.title }}</span>
              <span class="result-date">{{ formatDate(blog.createTime) }}</span>
            </li>
          </ul>
        </div>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed, nextTick, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { 
  Search, Place, HomeFilled, ChatLineRound, 
  Headset, VideoPlay, VideoPause, CaretLeft, CaretRight 
} from '@element-plus/icons-vue'
import { getFrontBlogList, getFrontRandomBlog } from '@/api/blog'
import { getMusicList } from '@/api/music'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const showGlobal = ref(false)
const isScrolled = ref(false)
const mobileMenuOpen = ref(false)

const isHome = computed(() => route.path === '/')

// === 🎵 音乐播放器逻辑 ===
const audioRef = ref(null)
const isPlaying = ref(false)
const currentIndex = ref(0)
const progress = ref(0)
const playlist = ref([]) 
const isDragging = ref(false) // 新增：是否正在拖动进度条

const currentSong = computed(() => {
  if (playlist.value.length === 0) return null
  return playlist.value[currentIndex.value]
})

const initMusicPlayer = async () => {
  try {
    const res = await getMusicList()
    // 移除 console.log
    
    let rawList = []
    if (res.data && Array.isArray(res.data.list)) {
      rawList = res.data.list 
    } else if (res.data && Array.isArray(res.data)) {
      rawList = res.data
    } else if (res.data && res.data.records && Array.isArray(res.data.records)) {
      rawList = res.data.records
    } else if (res.rows && Array.isArray(res.rows)) {
      rawList = res.rows
    }

    if (rawList.length === 0) return

    playlist.value = rawList.map(item => ({
      name: item.title || item.name || item.songName || '未知歌名',
      artist: item.artist || item.singer || 'HeYi',
      url: item.filePath || item.file_path || item.url || '' 
    })).filter(item => item.url)

  } catch (error) {
    // 移除错误 log，保持清爽，除非严重错误
  }
}

const togglePlay = () => {
  if (!audioRef.value) return
  if (isPlaying.value) {
    audioRef.value.pause()
  } else {
    if (currentSong.value && currentSong.value.url) {
      audioRef.value.play().catch(() => {
        isPlaying.value = false
        ElMessage.error('播放异常')
      })
    }
  }
  isPlaying.value = !isPlaying.value
}

const nextSong = () => {
  if (playlist.value.length === 0) return
  currentIndex.value = (currentIndex.value + 1) % playlist.value.length
  nextTick(() => {
    if (audioRef.value) {
      audioRef.value.play()
      isPlaying.value = true
    }
  })
}

const prevSong = () => {
  if (playlist.value.length === 0) return
  currentIndex.value = (currentIndex.value - 1 + playlist.value.length) % playlist.value.length
  nextTick(() => {
    if (audioRef.value) {
      audioRef.value.play()
      isPlaying.value = true
    }
  })
}

// 播放中更新进度
const onTimeUpdate = () => {
  // 如果用户正在拖动滑块，不要更新进度，否则会闪烁
  if (isDragging.value) return 
  
  if (audioRef.value && audioRef.value.duration > 0) {
    progress.value = (audioRef.value.currentTime / audioRef.value.duration) * 100
  }
}

// 【新增】开始拖动
const onSliderInput = () => {
  isDragging.value = true
}

// 【新增】拖动结束，跳转进度
const onSliderChange = (val) => {
  isDragging.value = false
  if (audioRef.value && audioRef.value.duration) {
    audioRef.value.currentTime = (val / 100) * audioRef.value.duration
    // 如果是暂停状态，拖动后自动播放体验更好
    if (!isPlaying.value) {
      audioRef.value.play()
      isPlaying.value = true
    }
  }
}

// === 其他逻辑 ===
const handleRandomArticle = async () => {
  try {
    const res = await getFrontRandomBlog()
    if (res.code === 200 || res.code === 20000 || res.success) {
      const targetId = res.data?.id
      if (targetId) router.push(`/article/${targetId}`)
    }
  } catch (error) {}
}

const searchModalVisible = ref(false)
const keyword = ref('')
const blogList = ref([])
const loading = ref(false)
const hasSearched = ref(false)
const searchInputRef = ref(null)
let searchTimer = null

const openSearch = () => {
  searchModalVisible.value = true
  keyword.value = ''
  blogList.value = []
  hasSearched.value = false
  nextTick(() => {
    if (searchInputRef.value) searchInputRef.value.focus()
  })
}

const closeSearch = () => {
  searchModalVisible.value = false
  if (searchTimer) clearTimeout(searchTimer)
}

const executeSearch = async () => {
  if (!keyword.value.trim()) {
    blogList.value = []
    return
  }
  loading.value = true
  hasSearched.value = true
  try {
    const res = await getFrontBlogList(1, 20, null, null, keyword.value)
    if (res.code === 200 || res.success) {
       blogList.value = res.data.records
    }
  } finally {
    loading.value = false
  }
}

watch(keyword, (newVal) => {
  if (!newVal.trim()) {
    blogList.value = []
    hasSearched.value = false
    return
  }
  if (searchTimer) clearTimeout(searchTimer)
  searchTimer = setTimeout(() => {
    executeSearch()
  }, 500)
})

const goToDetail = (id) => {
  closeSearch()
  router.push(`/article/${id}`)
}

const formatDate = (time) => {
  if (!time) return ''
  return time.split(' ')[0]
}

const handleScroll = () => {
  const windowHeight = window.innerHeight
  const scrollTop = window.scrollY
  const threshold = windowHeight * 0.66
  showGlobal.value = scrollTop > threshold
  isScrolled.value = scrollTop > 50
}

const toggleMobileMenu = () => {
  mobileMenuOpen.value = !mobileMenuOpen.value
}
const closeMenu = () => {
  mobileMenuOpen.value = false
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
  handleScroll()
  initMusicPlayer()
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
/* ========== 导航栏容器 ========== */
.hy-navbar {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 72px; /* 调整为 8 的倍数 */
  z-index: 1000;
  background-color: #D2D2D2;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  transition: all 0.4s ease;
  display: flex;
}
.hy-navbar.scrolled {
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.hy-container { 
  width: 100%; 
  padding: 0 var(--spacing-xl); 
  display: flex; 
  justify-content: space-between; 
  align-items: center; 
  height: 100%; 
  position: relative; 
}
.nav-left { display: flex; align-items: center; flex: 1; }
.nav-center { display: flex; gap: var(--spacing-xl); position: absolute; left: 50%; transform: translateX(-50%); }
.nav-right { display: flex; align-items: center; gap: var(--spacing-md); flex: 1; justify-content: flex-end; }

/* ========== Logo 胶囊动画 ========== */
.brand-capsule {
  position: relative; display: flex; justify-content: center; align-items: center;
  height: 40px; width: 80px; text-decoration: none; border-radius: 8px; 
  background-color: transparent; transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  overflow: hidden; 
}
.brand-text {
  font-size: 1.5rem; color: #333; font-weight: bold; letter-spacing: var(--letter-spacing-wide);
  position: absolute; transition: all 0.3s ease; opacity: 1; transform: translateY(0);
}
.brand-icon {
  font-size: 1.4rem; color: #fff; position: absolute; transition: all 0.3s ease;
  opacity: 0; transform: translateY(20px); display: flex; align-items: center;
}
.brand-capsule:hover { width: 100px; background-color: #333; border-radius: 20px; }
.brand-capsule:hover .brand-text { opacity: 0; transform: translateY(-20px); }
.brand-capsule:hover .brand-icon { opacity: 1; transform: translateY(0); }

/* ========== 导航链接 ========== */
.hy-nav-item { 
  color: var(--bs-gray-700); 
  text-decoration: none; 
  font-size: 1rem; 
  position: relative; 
  transition: all 0.3s; 
  padding: var(--spacing-xs) 0; 
  font-weight: 500; 
  letter-spacing: var(--letter-spacing-base);
}
.hy-nav-item:hover, .hy-nav-item.active { color: var(--bs-black); font-weight: 700; }
.hy-nav-item::after { content: ''; position: absolute; width: 0; height: 2px; bottom: 0; left: 50%; background-color: var(--bs-black); transition: all 0.3s ease; transform: translateX(-50%); }
.hy-nav-item:hover::after, .hy-nav-item.active::after { width: 100%; }

/* ========== 图标按钮 ========== */
.icon-btn { 
  color: var(--bs-gray-800); 
  font-size: 1rem; 
  cursor: pointer; 
  transition: all 0.3s; 
  display: flex; 
  align-items: center; 
  justify-content: center; 
  text-decoration: none; 
  padding: var(--spacing-xs);
  border-radius: 50%;
}
.icon-btn:hover { background-color: rgba(0, 0, 0, 0.05); color: var(--bs-black); }


/* === 🎵 音乐播放器动画 === */
.music-playing {
  color: #409EFF !important;
  animation: musicRotate 3s linear infinite;
}
@keyframes musicRotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* 移动端菜单 */
.hy-mobile-menu { position: absolute; top: 85px; left: 0; width: 100%; background-color: rgba(255, 255, 255, 0.98); border-top: 1px solid #eee; box-shadow: 0 4px 6px rgba(0,0,0,0.05); display: flex; flex-direction: column; }
.mobile-link { padding: 15px; text-align: center; border-bottom: 1px solid #f5f5f5; color: #333; text-decoration: none; font-size: 1rem; }
.hy-menu-toggle { background: none; border: none; font-size: 1.5rem; cursor: pointer; padding: 0; margin-left: 10px; }
.slide-down-enter-active, .slide-down-leave-active { transition: all 0.3s ease; max-height: 500px; opacity: 1; }
.slide-down-enter-from, .slide-down-leave-to { max-height: 0; opacity: 0; }
.d-none { display: none !important; }
.d-flex { display: flex !important; }
@media (min-width: 768px) { .d-md-flex { display: flex !important; } .d-md-none { display: none !important; } }
@media (max-width: 767px) { .d-md-flex { display: none !important; } .d-md-none { display: block !important; } .hy-container { padding: 0 20px; } .nav-center { display: none; } }

/* 搜索弹窗 */
.search-overlay { position: fixed; top: 0; left: 0; width: 100vw; height: 100vh; background: rgba(0, 0, 0, 0.5); z-index: 2000; display: flex; justify-content: center; align-items: center; animation: fadeIn 0.3s ease; }
.search-modal { width: 500px; max-width: 90%; background: #fff; border-radius: 12px; padding: 20px; box-shadow: 0 10px 30px rgba(0,0,0,0.2); display: flex; flex-direction: column; gap: 20px; animation: slideUp 0.3s ease; }
.search-header { display: flex; justify-content: center; align-items: center; position: relative; height: 30px; }
.search-title { font-size: 1.2rem; font-weight: bold; color: #333; }
.close-icon { position: absolute; right: 0; top: 0; font-size: 1.5rem; cursor: pointer; color: #999; line-height: 1; }
.close-icon:hover { color: #333; }
.search-input-box { display: flex; align-items: stretch; height: 45px; gap: 10px; }
.search-icon-wrapper { width: 50px; background-color: #f5f5f5; border-radius: 8px; display: flex; justify-content: center; align-items: center; }
.search-input { flex: 1; border: 2px solid #f5f5f5; border-radius: 8px; padding: 0 15px; font-size: 1rem; outline: none; transition: border-color 0.3s; }
.search-input:focus { border-color: #333; }
.search-results { max-height: 300px; overflow-y: auto; border-top: 1px solid #eee; padding-top: 10px; }
.result-tip { text-align: center; color: #999; padding: 20px; }
.result-list { list-style: none; padding: 0; margin: 0; }
.result-list li { padding: 12px; border-bottom: 1px solid #f9f9f9; cursor: pointer; display: flex; justify-content: space-between; align-items: center; transition: background 0.2s; border-radius: 6px; }
.result-list li:hover { background-color: #f5f5f5; }
.result-title { font-weight: 500; color: #333; flex: 1; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; margin-right: 10px; }
.result-date { font-size: 0.85rem; color: #999; }
@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
@keyframes slideUp { from { transform: translateY(20px); opacity: 0; } to { transform: translateY(0); opacity: 1; } }
</style>

<style>
/* 音乐播放器小面板 */
.music-popover {
  padding: 15px !important;
  border-radius: 12px !important;
  background: rgba(255, 255, 255, 0.95) !important;
  backdrop-filter: blur(10px);
  border: 1px solid #eee;
  box-shadow: 0 5px 20px rgba(0,0,0,0.15) !important;
  user-select: none; /* 禁止文字选中，消除闪烁光标 */
  outline: none;     /* 移除聚焦边框 */
}
.mini-player {
  display: flex;
  flex-direction: column;
  gap: 12px;
  text-align: center;
}
.no-music-tip {
  color: #999;
  font-size: 13px;
  display: flex;
  flex-direction: column;
  gap: 5px;
}
.song-info {
  display: flex;
  flex-direction: column;
}
.song-name {
  font-weight: bold;
  color: #333;
  font-size: 14px;
}
.song-artist {
  font-size: 12px;
  color: #888;
}
.player-controls {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
}
.ctrl-btn {
  font-size: 20px;
  cursor: pointer;
  color: #555;
  transition: all 0.2s;
}
.ctrl-btn:hover {
  color: #409EFF;
  transform: scale(1.1);
}
.play-btn {
  font-size: 32px;
  color: #409EFF;
}

/* 覆盖 el-slider 样式，使其在弹窗中更精致 */
.progress-wrapper {
  padding: 0 5px;
}
.music-slider {
  --el-slider-main-bg-color: #409EFF;
  --el-slider-runway-bg-color: #eee;
  height: 20px; /* 调整占位高度 */
}
.music-slider .el-slider__bar {
  height: 4px;
  border-radius: 2px;
}
.music-slider .el-slider__runway {
  height: 4px;
  border-radius: 2px;
  margin: 8px 0;
}
.music-slider .el-slider__button {
  width: 10px;
  height: 10px;
  border: 2px solid #409EFF;
  background-color: #fff;
}
</style>