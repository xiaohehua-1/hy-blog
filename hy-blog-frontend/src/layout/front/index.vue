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
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import NavBar from './components/NavBar.vue'
import Footer from './components/Footer.vue'

const route = useRoute()

// 定义需要“背景图顶天立地”的页面路径
const transparentPagePaths = ['/', '/moments']

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