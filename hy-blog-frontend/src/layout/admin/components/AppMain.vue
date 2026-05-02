<template>
  <section class="app-main">
    <router-view v-slot="{ Component }">
      <transition name="fade-transform" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>
  </section>
</template>

<script setup>
</script>

<style scoped>
.app-main {
  /* 减去顶部导航栏的高度(50px)和padding(20px*2)，保证内容区撑满且不溢出 */
  min-height: calc(100vh - 90px);
  width: 100%;
  position: relative;
  overflow: hidden;
}

/* 页面切换动画：淡入淡出 + 轻微位移 */
.fade-transform-leave-active,
.fade-transform-enter-active {
  transition: all 0.5s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
</style>