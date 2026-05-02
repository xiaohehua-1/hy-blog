<template>
  <div class="moments-page-wrapper">
    <el-row justify="center">
      <el-col :xs="24" :sm="22" :md="18" :lg="18" :xl="18">
        
        <div class="moment-header">
          <div class="header-bg">
            <div class="bg-overlay"></div>
            <div class="header-title-box">
              <span class="small-text">分享生活中的小确幸</span>
              <h1 class="big-text">碎碎念念</h1>
            </div>
            <div class="user-profile-box">
              <div class="user-name">小荷花</div>
              <div class="avatar-sig-col">
                <div class="avatar-wrapper square-avatar">
                  <img src="@/assets/images/me.jpg" alt="avatar" />
                </div>
                <div class="signature-text">
                  雄关漫道真如铁，而今迈步从头越
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="moment-feed" v-loading="loading">
          <div class="waterfall-container">
            <div 
              class="moment-card glass-card" 
              v-for="item in list" 
              :key="item.id" 
              @click="openDetail(item)"
            >
              <div class="card-content" v-if="item.content">{{ item.content }}</div>

              <div 
                class="card-images" 
                v-if="item.images"
                :class="getGridClass(item.images.split(',').length)"
              >
                <el-image 
                  v-for="(img, idx) in item.images.split(',')" 
                  :key="idx"
                  :src="img"
                  :preview-src-list="item.images.split(',')"
                  class="moment-img"
                  fit="cover"
                  loading="lazy"
                  @click.stop
                >
                  <template #error>
                    <div class="image-slot"><el-icon><Picture /></el-icon></div>
                  </template>
                </el-image>
              </div>

              <div class="card-status">
                <span class="time-text">{{ formatTime(item.publishTime || item.createTime) }}</span>
                
                <div class="status-right">
                  <a v-if="item.extraUrl" :href="item.extraUrl" target="_blank" @click.stop class="link-capsule">
                    <el-icon class="link-icon-big"><Link /></el-icon> <span>链接</span>
                  </a>
                  
                  <span class="icon-action">
                    <img src="@/assets/icons/pinglun.svg" class="action-svg-icon" alt="评论" />
                    {{ item.comments ? item.comments.length : 0 }}
                  </span>
                  
                  <span 
                    class="icon-action like-btn" 
                    @click.stop="handleLike(item)"
                  >
                    <img 
                      src="@/assets/icons/dianzan.svg" 
                      class="action-svg-icon dianzan-anim" 
                      :class="{ 'is-active': isLiked(item.id) }"
                      alt="点赞"
                    />
                    <span :class="{ 'liked-num': isLiked(item.id) }">
                      {{ item.likeCount || 0 }}
                    </span>
                  </span>
                </div>
              </div>

              <div class="card-divider-pink"></div>

              <div class="card-comments-preview" v-if="item.comments && item.comments.length > 0">
                <div 
                  class="comment-line" 
                  v-for="(c, index) in item.comments.slice(0, 3)" 
                  :key="c.id || index"
                >
                  <span class="c-nick">{{ c.nickname }}：</span>
                  <span class="c-content">{{ c.content }}</span>
                </div>
                
                <div 
                  v-if="item.comments.length > 3" 
                  class="more-comments-btn"
                  @click.stop="openDetail(item)"
                >
                  <span class="expand-text">查看全部评论 &gt;</span>
                </div>
              </div>
            </div>
          </div>
          
          <div class="load-more" v-if="hasMore">
            <el-button link @click="loadNextPage" :loading="loading">加载更多</el-button>
          </div>
          <el-empty v-else-if="!loading && list.length === 0" description="暂无动态" />
        </div>

      </el-col>
    </el-row>

    <FloatingActionBar />

    <el-dialog
      v-model="dialogVisible"
      title="动态详情"
      width="600px"
      custom-class="moment-dialog glass-dialog"
      append-to-body
      destroy-on-close
    >
      <div v-if="currentItem" class="detail-wrapper">
        <div class="d-header-simple">
          <div class="d-avatar-box">
             <img src="@/assets/images/me.jpg" class="d-avatar-img">
          </div>
          <div class="d-name-row">
            <span class="d-name">小荷花</span>
            <el-tag v-if="currentItem.isPrivate === 1" size="small" type="info" effect="plain" class="ms-2">
              <el-icon><Lock /></el-icon> 仅自己可见
            </el-tag>
          </div>
        </div>
        
        <div class="d-body">{{ currentItem.content }}</div>
        
        <div 
           class="d-imgs card-images" 
           v-if="currentItem.images"
           :class="getGridClass(currentItem.images.split(',').length)"
        >
           <el-image 
             v-for="(img, i) in currentItem.images.split(',')" 
             :key="i" :src="img" fit="cover" class="moment-img"
             :preview-src-list="currentItem.images.split(',')"
           />
        </div>

        <div class="d-footer-info">
          <span class="d-time">{{ formatTime(currentItem.publishTime || currentItem.createTime) }}</span>
          <div class="d-stats">
            <span class="d-stat-item">
              <img src="@/assets/icons/pinglun.svg" class="action-svg-icon" />
              {{ currentItem.comments ? currentItem.comments.length : 0 }}
            </span>
            <span 
              class="d-stat-item like-btn" 
              @click="handleLike(currentItem)"
            >
              <img 
                src="@/assets/icons/dianzan.svg" 
                class="action-svg-icon dianzan-anim" 
                :class="{ 'is-active': isLiked(currentItem.id) }"
              />
              {{ currentItem.likeCount || 0 }}
            </span>
          </div>
        </div>

        <el-divider content-position="center" class="d-divider">评论区</el-divider>

        <MessageForm 
          ref="messageFormRef"
          module="moment" 
          :target-id="currentItem.id" 
          @success="handleCommentSuccess" 
        />
        <div style="height: 30px;"></div>
        <MessageList 
          :key="refreshKey"
          module="moment" 
          :target-id="currentItem.id" 
          @reply="onReplyComment"
        />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { Link, Picture, Lock } from '@element-plus/icons-vue' 
import { getFrontMomentList, likeMoment } from '@/api/moment'
import { ElMessage } from 'element-plus'
import MessageForm from '@/views/front/message/components/MessageForm.vue'
import MessageList from '@/views/front/message/components/MessageList.vue'
import FloatingActionBar from '@/components/FloatingActionBar.vue'
import SocketService from '@/utils/websocket'

const loading = ref(false)
const list = ref([])
const current = ref(1)
const hasMore = ref(true)

const dialogVisible = ref(false)
const currentItem = ref(null)
const refreshKey = ref(0)
const messageFormRef = ref(null)

const getGridClass = (len) => {
  if (len === 1) return 'grid-1'
  if (len === 2) return 'grid-2'
  if (len === 4) return 'grid-4'
  return 'grid-3'
}

const getList = async (page = 1) => {
  loading.value = true
  try {
    const res = await getFrontMomentList({ current: page, size: 9 })
    if (res.data && res.data.page) {
      const newRecords = res.data.page.records
      if (page === 1) list.value = newRecords
      else list.value = [...list.value, ...newRecords]
      hasMore.value = list.value.length < res.data.page.total
    }
  } finally {
    loading.value = false
  }
}

const loadNextPage = () => {
  current.value++
  getList(current.value)
}

const formatTime = (time) => {
  if (!time) return ''
  return time.replace('T', ' ')
}

const isLiked = (id) => {
  const likedList = JSON.parse(localStorage.getItem('liked_moments') || '[]')
  return likedList.includes(id)
}

const handleLike = async (item) => {
  const likedList = JSON.parse(localStorage.getItem('liked_moments') || '[]')
  const index = likedList.indexOf(item.id)

  if (index !== -1) {
    item.likeCount = Math.max(0, (item.likeCount || 1) - 1)
    likedList.splice(index, 1)
    localStorage.setItem('liked_moments', JSON.stringify(likedList))
    ElMessage.info('已取消点赞')
  } else {
    try {
      await likeMoment(item.id)
      item.likeCount = (item.likeCount || 0) + 1
      likedList.push(item.id)
      localStorage.setItem('liked_moments', JSON.stringify(likedList))
      ElMessage.success('点赞成功 +1')
    } catch (e) {
      console.error(e)
    }
  }
}

const openDetail = (item) => {
  currentItem.value = item
  refreshKey.value++ 
  dialogVisible.value = true
}

const onReplyComment = (comment) => {
  nextTick(() => {
    if (messageFormRef.value) {
      messageFormRef.value.setReply(comment)
      messageFormRef.value.$el.scrollIntoView({ behavior: 'smooth', block: 'center' })
    }
  })
}

const handleCommentSuccess = () => {
  refreshKey.value++ 
}

onMounted(() => {
  getList()
  
  // 注册 WebSocket 实时刷新回调
  SocketService.getInstance.registerCallBack('moment_comment', (data) => {
    if (data === 'refresh_moment_comment') {
      // 1. 刷新卡片列表（预览评论）
      getList(1)
      // 2. 如果详情弹窗开启，也刷新弹窗里的列表
      if (dialogVisible.value) {
        refreshKey.value++
      }
    }
  })
})

onUnmounted(() => {
  // 取消注册回调
  SocketService.getInstance.unRegisterCallBack('moment_comment')
})
</script>

<style scoped>
.moments-page-wrapper {
  min-height: 100vh;
  padding-bottom: var(--spacing-3xl);
  background-image: url('@/assets/images/bg_01.png');
  background-repeat: repeat;
  background-attachment: fixed;
  overflow-x: hidden; 
}

.waterfall-container {
  column-count: 3 !important; 
  column-gap: var(--spacing-lg); 
  padding: 0 var(--spacing-sm); 
  width: 100%; 
  box-sizing: border-box;
}

.moment-header {
  position: relative; width: 100%; margin-bottom: 120px; padding: 0 var(--spacing-sm); box-sizing: border-box;
}
.header-bg {
  position: relative; width: 100%; height: 400px; 
  border-radius: 24px; margin-top: 80px; 
  background-image: url('@/assets/images/DT.jpg');
  background-size: cover; background-position: center 0%; 
  box-shadow: 0 8px 32px rgba(0,0,0,0.15);
}
.bg-overlay {
  position: absolute; top: 0; left: 0; width: 100%; height: 100%;
  background: rgba(0, 0, 0, 0.2); border-radius: 24px;
}
.header-title-box {
  position: absolute; top: var(--spacing-lg); left: var(--spacing-xl); color: #fff; text-shadow: 0 2px 4px rgba(0,0,0,0.6);
}
.big-text { font-size: 3rem; font-weight: bold; margin: 0; letter-spacing: var(--letter-spacing-wide); }
.small-text { font-size: 1.1rem; opacity: 0.95; letter-spacing: var(--letter-spacing-base); display: block; margin-top: var(--spacing-xs); }

.user-profile-box {
  position: absolute; display: flex; align-items: center; z-index: 10;
  right: -40px; bottom: -80px; 
}
.user-name {
  font-size: 1.6rem; font-weight: bold; color: #fff;
  text-shadow: 0 2px 5px rgba(0,0,0,0.8);
  margin-right: -60px; margin-bottom: 56px; 
  letter-spacing: var(--letter-spacing-base);
}
.avatar-sig-col { display: flex; flex-direction: column; align-items: center; }
.square-avatar {
  width: 120px; height: 120px; 
  border-radius: 12px; border: 4px solid #fff;
  background: #fff; box-shadow: 0 4px 16px rgba(0,0,0,0.2); overflow: hidden;
  transition: transform 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}
.square-avatar:hover { transform: rotate(5deg) scale(1.05); }
.square-avatar img { width: 100%; height: 100%; object-fit: cover; }
.signature-text {
  font-size: 1.1rem; font-weight: bold; color: var(--bs-gray-900);
  background: rgba(255, 255, 255, 0.9); padding: var(--spacing-sm) var(--spacing-lg);
  border-radius: 24px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  white-space: nowrap; margin-top: var(--spacing-md);
  position: relative; 
  left: -88px; 
  letter-spacing: var(--letter-spacing-base);
}

/* === 卡片样式优化 === */
.glass-card {
  background: rgba(255, 255, 255, 0.05) !important; 
  backdrop-filter: blur(12px) !important; 
  -webkit-backdrop-filter: blur(12px) !important;
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.1);
}
.moment-card {
  break-inside: avoid; border-radius: 16px; padding: var(--spacing-lg); margin-bottom: var(--spacing-lg);
  background: transparent; 
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer; display: inline-block; width: 100%; box-sizing: border-box;
}
.moment-card:hover {
  transform: translateY(-4px); 
  box-shadow: 0 16px 40px rgba(0,0,0,0.12); 
  border-color: #ff69b4; 
}

.card-content {
  font-size: 1.1rem; 
  color: var(--bs-gray-900); line-height: var(--line-height-base); margin-bottom: var(--spacing-md); 
  white-space: pre-wrap; font-weight: 500;
  letter-spacing: var(--letter-spacing-base);
}

.card-images { display: flex; flex-wrap: wrap; gap: var(--spacing-xs); margin-bottom: var(--spacing-md); }
.moment-img { width: 32%; aspect-ratio: 1/1; border-radius: 8px; display: block; }
.grid-1 .moment-img { width: 60%; aspect-ratio: auto; max-height: 400px; }
.grid-2 .moment-img { width: 49%; }
.grid-4 { width: 70%; }
.grid-4 .moment-img { width: 48%; }

.card-status { display: flex; justify-content: space-between; align-items: center; padding-bottom: var(--spacing-xs); margin-top: var(--spacing-sm); }
.time-text { font-size: 0.95rem; color: var(--bs-gray-600); }
.status-right { display: flex; align-items: center; gap: var(--spacing-md); }

.link-capsule {
  background: rgba(255, 240, 245, 0.8); 
  color: #ff69b4; padding: var(--spacing-xs) var(--spacing-md); 
  border-radius: 20px; text-decoration: none; 
  display: flex; align-items: center; gap: var(--spacing-xs); 
  font-size: 0.8rem; font-weight: bold; transition: all 0.2s;
  border: 1px solid rgba(255, 105, 180, 0.2);
}
.link-capsule:hover { 
  background: #ff69b4; color: #fff; box-shadow: 0 4px 12px rgba(255, 105, 180, 0.4);
}
.link-icon-big { font-size: 1rem; font-weight: bold; }

.icon-action { font-size: 0.9rem; display: flex; align-items: center; gap: var(--spacing-xs); cursor: pointer; transition: color 0.2s; }
.icon-action:hover { color: var(--bs-primary); }

.action-svg-icon { width: 22px; height: 22px; transition: transform 0.3s ease; }
.dianzan-anim { transition: transform 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275); }
.dianzan-anim.is-active { transform: scale(1.3); filter: drop-shadow(0 0 4px #ff69b4); }
.liked-num { color: #ff69b4; font-weight: bold; }

.card-divider-pink {
  height: 2px; background: #ffc0cb; margin: var(--spacing-md) 1px; opacity: 0.8; border-radius: 2px;
}

/* 评论预览区：也要透明 */
.card-comments-preview {
  background: rgba(255, 255, 255, 0.1); 
  padding: var(--spacing-sm); border-radius: 8px; margin-top: var(--spacing-xs);
  border-top: 1px solid rgba(255,255,255,0.2);
}
.comment-line { font-size: 0.9rem; margin-bottom: var(--spacing-xs); color: var(--bs-gray-700); line-height: 1.4; }
.c-nick { color: var(--bs-gray-900); font-weight: 700; } 

.more-comments-btn {
  margin-top: var(--spacing-sm); text-align: center;
}
.expand-text {
  font-size: 0.8rem; color: var(--bs-primary); cursor: pointer; 
  padding: 2px var(--spacing-sm); transition: all 0.2s;
}
.expand-text:hover { background: rgba(255, 255, 255, 0.3); border-radius: 4px; }

.detail-wrapper { padding: 0 var(--spacing-xs); }
.d-header-simple { display: flex; align-items: center; gap: var(--spacing-md); margin-bottom: var(--spacing-lg); }
.d-avatar-box { width: 52px; height: 52px; border-radius: 8px; overflow: hidden; flex-shrink: 0; border: 2px solid #fff; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
.d-avatar-img { width: 100%; height: 100%; object-fit: cover; }
.d-name-row { display: flex; flex-direction: column; gap: 4px; }
.d-name { font-size: 1.1rem; font-weight: bold; color: var(--bs-gray-900); }
.d-body { font-size: 1.1rem; line-height: var(--line-height-article); color: var(--bs-gray-900); margin-bottom: var(--spacing-lg); white-space: pre-wrap; }
.d-footer-info { display: flex; justify-content: space-between; align-items: center; margin-top: var(--spacing-lg); color: var(--bs-gray-500); font-size: 0.9rem; }
.d-stats { display: flex; gap: var(--spacing-lg); }
.d-stat-item { display: flex; align-items: center; gap: var(--spacing-xs); cursor: pointer; font-size: 0.95rem; }
.d-divider { margin: var(--spacing-xl) 0; border-top-color: #ffc0cb; opacity: 0.6; }


@media (max-width: 768px) {
  .waterfall-container { column-count: 1 !important; }
  .header-bg { height: 260px; margin-top: 65px; }
  .header-title-box { top: 40px; left: 20px; }
  .big-text { font-size: 32px; }
  .user-profile-box { right: 20px; bottom: -40px; }
  .square-avatar { width: 90px; height: 90px; }
  .user-name { display: none; }
  .signature-text { font-size: 14px; margin-top: 10px; }
}
</style>