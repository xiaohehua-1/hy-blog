<template>
  <div class="message-container">
    
    <div class="content-bg"></div>
    
    <div class="message-hero">
      <img src="@/assets/images/message.JPG" alt="msg-bg" class="hero-img">
      <div class="hero-text">
        <div class="hero-title">📪 留言板</div>
        <div class="hero-subtitle">有什么对我想说的话，来吧！</div>
      </div>
    </div>

    <div class="main-content">
      <el-row justify="center">
        <el-col :span="14" :xs="22">
          
          <MessageForm 
            ref="formRef"
            module="message"
            @success="handleSuccess" 
          />

          <MessageList 
            ref="listRef"
            class="mt-5"
            module="message"
            @reply="handleReplyAction"
          />

        </el-col>
      </el-row>
    </div>

  </div>
</template>

<script setup>
/**
 * 留言板页面
 * MessageForm 提交 + MessageList 树形展示 + WebSocket 实时刷新
 */
import { ref, onMounted, onUnmounted } from 'vue'
import MessageForm from './components/MessageForm.vue'
import MessageList from './components/MessageList.vue'
import SocketService from '@/utils/websocket'

const formRef = ref(null)
const listRef = ref(null)

// 1. 成功后的回调
const handleSuccess = () => {
  if (listRef.value) {
    listRef.value.refresh()
  }
}

// 2. 点击回复
const handleReplyAction = (item) => {
  const formEl = document.querySelector('.message-form-card')
  if (formEl) formEl.scrollIntoView({ behavior: 'smooth', block: 'center' })
  formRef.value.setReply(item)
}

onMounted(() => {
  // 注册 WebSocket 实时刷新回调
  SocketService.getInstance.registerCallBack('message_board', (data) => {
    if (data === 'refresh_message') {
      if (listRef.value) {
        listRef.value.refresh()
      }
    }
  })
})

onUnmounted(() => {
  // 取消注册回调
  SocketService.getInstance.unRegisterCallBack('message_board')
})
</script>

<style scoped>
.message-container { position: relative; min-height: 100vh; }
.content-bg {
  position: fixed; top: 0; left: 0; width: 100%; height: 100%;
  background-image: url('@/assets/images/bg_01.png');
  background-repeat: repeat; z-index: -2;
}

/* Hero 修改：去掉 margin-top，增加高度，确保置顶 */
.message-hero {
  position: relative;
  width: 100%;
  height: 400px; /* 增加高度，多显示点图片 */
  overflow: hidden;
  display: flex; align-items: center; justify-content: center;
  margin-top: 0; /* 关键：置顶 */
}
.hero-img {
  position: absolute; width: 100%; height: 100%; object-fit: cover;
  z-index: -1; filter: brightness(0.8);
}
.hero-text { text-align: center; color: #fff; z-index: 1; text-shadow: 0 4px 12px rgba(0,0,0,0.5); }
.hero-title { font-size: 3rem; font-weight: bold; margin-bottom: var(--spacing-sm); letter-spacing: var(--letter-spacing-wide); }
.hero-subtitle { font-size: 1.25rem; opacity: 0.95; letter-spacing: var(--letter-spacing-base); }

/* 内容区 */
.main-content {
  padding-bottom: var(--spacing-3xl);
  position: relative;
  margin-top: -48px; /* 8px 规范 */
  z-index: 2;
}

</style>