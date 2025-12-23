<template>
  <div class="front-wrapper">
    <NavBar />

    <main class="main-content" :style="{ paddingTop: isHome ? '0' : '85px' }">
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

// 计算属性：判断当前是否是首页
const isHome = computed(() => route.path === '/')
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
  /* 注意：这里删除了固定的 padding-top，改用上面的内联样式控制 */
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