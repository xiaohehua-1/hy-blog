<template>
  <div id="app">
    <PageProgress v-if="!isAdminRoute" />
    
    <router-view />
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import PageProgress from '@/components/PageProgress.vue'
import SocketService from '@/utils/websocket'

const route = useRoute()

// 初始化 WebSocket 连接
onMounted(() => {
  SocketService.getInstance.connect()
})

// 判断是否为后台管理页
const isAdminRoute = computed(() => {
  // 如果你的后台路径包含 /admin 或 /backend，这里会返回 true
  return route.path.includes('/admin') || route.path.includes('/login')
})
</script>