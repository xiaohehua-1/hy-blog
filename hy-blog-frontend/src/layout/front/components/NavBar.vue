<template>
  <nav class="hy-navbar fixed" :class="{ 'scrolled': isScrolled }" v-show="!isHome || showGlobal">
    <div class="hy-container">
      
      <div class="nav-left">
        <router-link to="/" class="hy-brand">
          <strong>HeYi</strong>
        </router-link>
      </div>

      <div class="nav-center d-none d-md-flex">
        <router-link to="/" class="hy-nav-item" active-class="active">首页</router-link>
        <router-link to="/tags" class="hy-nav-item" active-class="active">标签</router-link>
        <router-link to="/archives" class="hy-nav-item" active-class="active">归档</router-link>
        <router-link to="/message" class="hy-nav-item" active-class="active">留言板</router-link>
        <router-link to="/friends" class="hy-nav-item" active-class="active">友链</router-link>
        <a href="https://www.travellings.cn/go.html" target="_blank" class="hy-nav-item">🚇开往</a>
        <router-link to="/about" class="hy-nav-item" active-class="active">关于我</router-link>
      </div>

      <div class="nav-right d-flex align-items-center">
        <a href="javascript:void(0)" class="hy-action-btn" @click="openSearch">
          <strong>搜索</strong>
        </a>
        <button class="hy-menu-toggle d-md-none" @click="toggleMobileMenu">
          <span v-if="!mobileMenuOpen">☰</span>
          <span v-else>✕</span>
        </button>
      </div>
    </div>

    <transition name="slide-down">
      <div v-show="mobileMenuOpen" class="hy-mobile-menu">
        <router-link to="/" class="mobile-link" @click="closeMenu">首页</router-link>
        <router-link to="/tags" class="mobile-link" @click="closeMenu">标签</router-link>
        <router-link to="/archives" class="mobile-link" @click="closeMenu">归档</router-link>
        <router-link to="/message" class="mobile-link" @click="closeMenu">留言板</router-link>
        <router-link to="/friends" class="mobile-link" @click="closeMenu">友链</router-link>
        <router-link to="/about" class="mobile-link" @click="closeMenu">关于我</router-link>
      </div>
    </transition>
  </nav>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const showGlobal = ref(false)   // 控制首页是否显示全局导航
const isScrolled = ref(false)   // 修复：控制是否添加 scrolled 类 (阴影等)
const mobileMenuOpen = ref(false)

const isHome = computed(() => route.path === '/')

const handleScroll = () => {
  const windowHeight = window.innerHeight
  const scrollTop = window.scrollY
  
  // 1. 控制首页全局导航的显示 (超过 2/3 屏)
  const threshold = windowHeight * 0.66
  showGlobal.value = scrollTop > threshold

  // 2. 修复：控制 scrolled 类的状态 (只要稍微滚动就生效，或者你可以设为 > 0)
  isScrolled.value = scrollTop > 50
}

const toggleMobileMenu = () => {
  mobileMenuOpen.value = !mobileMenuOpen.value
}

const closeMenu = () => {
  mobileMenuOpen.value = false
}

const openSearch = () => {
  alert('搜索功能开发中...')
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
  handleScroll()
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
/* ========== 全局导航栏样式 (Height: 60px) ========== */
.hy-navbar {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 60px; 
  z-index: 1000;
  background-color: #D2D2D2;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  transition: all 0.4s ease;
  display: flex;
}

/* 滚动时的样式增强（如果需要） */
.hy-navbar.scrolled {
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.hy-container {
  width: 100%;
  padding: 0 40px; 
  display: flex;
  justify-content: space-between; 
  align-items: center;
  height: 100%;
  position: relative; 
}

/* 左侧：Logo */
.nav-left {
  display: flex;
  align-items: center;
  flex: 1; 
}

/* 中间：菜单 */
.nav-center {
  display: flex;
  gap: 30px;
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
}

/* 右侧：功能区 */
.nav-right {
  display: flex;
  align-items: center;
  gap: 15px;
  flex: 1; 
  justify-content: flex-end; 
}

/* Brand */
.hy-brand {
  font-size: 1.5rem;
  color: #333;
  text-decoration: none;
  font-weight: bold;
  letter-spacing: 1px;
}

/* Menu Items */
.hy-nav-item {
  color: #555;
  text-decoration: none;
  font-size: 1rem;
  position: relative;
  transition: color 0.3s;
  padding: 5px 0;
  font-weight: 500;
}

.hy-nav-item:hover, .hy-nav-item.active {
  color: #000;
  font-weight: 700;
}

.hy-nav-item::after {
  content: '';
  position: absolute;
  width: 0;
  height: 2px;
  bottom: 0;
  left: 50%;
  background-color: #000;
  transition: all 0.3s ease;
  transform: translateX(-50%);
}

.hy-nav-item:hover::after, .hy-nav-item.active::after {
  width: 100%;
}

.hy-action-btn {
  color: #333;
  text-decoration: none;
  font-size: 1rem;
  cursor: pointer;
  transition: opacity 0.3s;
}

.hy-mobile-menu {
  position: absolute;
  top: 60px;
  left: 0;
  width: 100%;
  background-color: rgba(255, 255, 255, 0.98);
  border-top: 1px solid #eee;
  box-shadow: 0 4px 6px rgba(0,0,0,0.05);
  display: flex;
  flex-direction: column;
}

/* Utils */
.d-none { display: none !important; }
.d-flex { display: flex !important; }
@media (min-width: 768px) { .d-md-flex { display: flex !important; } .d-md-none { display: none !important; } }
@media (max-width: 767px) { 
  .d-md-flex { display: none !important; } 
  .d-md-none { display: block !important; } 
  .hy-container { padding: 0 20px; } 
  .nav-center { display: none; } 
}
.mobile-link { padding: 15px; text-align: center; border-bottom: 1px solid #f5f5f5; color: #333; text-decoration: none; font-size: 1rem; }
.hy-menu-toggle { background: none; border: none; font-size: 1.5rem; cursor: pointer; padding: 0; margin-left: 10px; }
.slide-down-enter-active, .slide-down-leave-active { transition: all 0.3s ease; max-height: 500px; opacity: 1; }
.slide-down-enter-from, .slide-down-leave-to { max-height: 0; opacity: 0; }
</style>