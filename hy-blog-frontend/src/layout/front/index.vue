<template>
  <div class="front-wrapper">
    <NavBar />

    <main class="main-content" :style="{ paddingTop: isTransparentPage ? '0' : '60px' }">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>

    <Footer />
  </div>
</template>

<script setup>
/**
 * 前台布局
 * 上：NavBar 导航栏，中：router-view 内容区（支持页面切换动画），下：Footer 页脚
 * 首页和动态页背景图顶天立地，NavBar 直接叠加在背景上（paddingTop=0）
 */
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import NavBar from './components/NavBar.vue'
import Footer from './components/Footer.vue'

const route = useRoute()

// 首页和动态页使用透明背景，NavBar 叠加在背景图之上
const transparentPagePaths = ['/', '/moments']

/** 当前路由是否为透明背景页，控制 main-content 的 paddingTop */
const isTransparentPage = computed(() => {
  return transparentPagePaths.includes(route.path)
})
</script>

<style scoped>
.front-wrapper {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.main-content {
  flex: 1; 
  width: 100%;
}

/* 页面切换动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>