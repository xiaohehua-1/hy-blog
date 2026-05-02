<template>
  <div class="message-list-container" v-loading="loading">
    <div v-if="treeList.length > 0">
      <div v-for="item in treeList" :key="item.id" class="message-item">
        
        <div class="root-msg-wrapper">
          <div class="msg-left">
            <component :is="item.address ? 'a' : 'div'" :href="item.address" target="_blank" class="avatar-link-box">
              <img 
                :src="(item.adminComment || item.adminMessage || item.isAdmin) ? adminAvatar : (item.avatar || defaultAvatar)" 
                :alt="(item.adminComment || item.adminMessage || item.isAdmin) ? '站长回复头像' : '用户头像'"
                class="msg-avatar"
              >
            </component>
          </div>
          <div class="msg-right">
            <div class="msg-header">
              <span class="msg-nickname">
                {{ item.nickname }}
                <span class="admin-badge" v-if="item.adminComment || item.adminMessage || item.isAdmin">站长</span>
              </span>
            </div>
            <div class="msg-content">{{ item.content }}</div>
            <div class="msg-footer">
              <span class="msg-time">{{ formatTime(item.createTime) }}</span>
              <span class="reply-btn" @click="handleReply(item)">
                <el-icon><ChatLineRound /></el-icon> 回复
              </span>
            </div>
          </div>
        </div>

        <div class="sub-msg-list" v-if="item.children && item.children.length > 0">
          <div v-for="child in item.children" :key="child.id" class="sub-msg-item">
             <div class="msg-left small">
               <img 
                :src="(child.adminComment || child.adminMessage || child.isAdmin) ? adminAvatar : (child.avatar || defaultAvatar)" 
                :alt="(child.adminComment || child.adminMessage || child.isAdmin) ? '站长回复头像' : '用户头像'"
                class="msg-avatar"
              >
            </div>
            <div class="msg-right">
              <div class="msg-header">
                <span class="msg-nickname">
                  {{ child.nickname }}
                  <span class="admin-badge" v-if="child.adminComment || child.adminMessage || child.isAdmin">站长</span>
                  <span class="reply-text" v-if="child.replyNickname">回复 @{{ child.replyNickname }}</span>
                </span>
              </div>
              <div class="msg-content">{{ child.content }}</div>
              <div class="msg-footer">
                <span class="msg-time">{{ formatTime(child.createTime) }}</span>
                <span class="reply-btn" @click="handleReply(child)">
                  <el-icon><ChatLineRound /></el-icon> 回复
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <el-empty v-else description="还没有人评论，快来抢沙发吧~" />

    <div class="pagination-box" v-if="total > 0">
      <el-pagination
        background
        layout="prev, pager, next"
        :total="total"
        :page-size="pageSize"
        :current-page="currentPage"
        @current-change="onPageChange"
        class="merged-pagination"
        prev-text="上一页"
        next-text="下一页"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, defineProps, defineEmits, watch, defineExpose } from 'vue'
import { Link, ChatLineRound } from '@element-plus/icons-vue'
import defaultAvatarImg from '@/assets/images/avatar.png'
import adminAvatarImg from '@/assets/images/me.jpg'
import request from '@/utils/request'

const props = defineProps({
  module: { type: String, default: 'message' },
  targetId: { type: [String, Number], default: 0 }
})

const emit = defineEmits(['reply'])
const defaultAvatar = defaultAvatarImg
const adminAvatar = adminAvatarImg
const loading = ref(false)
const treeList = ref([]) 
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// === 万能列表转树结构算法 ===
const listToTree = (list) => {
  if (!list || list.length === 0) return []
  
  const data = JSON.parse(JSON.stringify(list))
  const roots = []
  const map = {}

  // 1. 建立全量索引
  const buildMap = (nodes) => {
    nodes.forEach(node => {
      map[node.id] = node
      if (node.children && node.children.length > 0) {
        buildMap(node.children)
      }
    })
  }
  buildMap(data)

  // 2. 检查是否已经是树形结构
  const isAlreadyTree = data.some(item => item.children && item.children.length > 0)
  
  if (isAlreadyTree) {
     data.forEach(root => {
       if (root.children && root.children.length > 0) {
         root.children.forEach(child => {
           const pid = child.parentCommentId || child.parentMessageId
           const rid = child.rootCommentId || child.rootMessageId
           if (!child.replyNickname && pid && pid !== rid && map[pid]) {
             child.replyNickname = map[pid].nickname
           }
         })
       }
     })
     return data
  }

  // 3. 扁平列表转树形
  data.forEach(item => { item.children = [] })

  data.forEach(item => {
    const rootId = item.rootCommentId || item.rootMessageId
    const parentId = item.parentCommentId || item.parentMessageId
    
    if (rootId && rootId !== 0 && rootId !== -1 && map[rootId] && rootId !== item.id) {
      if (!item.replyNickname && parentId && parentId !== rootId && map[parentId]) {
         item.replyNickname = map[parentId].nickname
      }
      map[rootId].children.push(item)
    } else {
      roots.push(item)
    }
  })
  
  return roots
}

const getList = async (page = 1) => {
  loading.value = true
  try {
    let url = ''
    let params = { current: page, size: pageSize.value }

    if (props.module === 'moment') {
      url = `/front/moment/comment/list/${props.targetId}`
    } else if (props.module === 'blog') {
      url = '/front/comment/list'
      params.blogId = props.targetId 
    } else {
      url = '/front/message/list'
    }

    const res = await request.get(url, { params })
    
    if (res.data && res.data.page) {
      const records = res.data.page.records
      total.value = res.data.page.total
      currentPage.value = page
      treeList.value = listToTree(records)
    } else {
      treeList.value = []
      total.value = 0
    }
  } finally {
    loading.value = false
  }
}

// === 优化：中文时间格式解析 ===
const formatTime = (timeStr) => {
  if (!timeStr) return ''
  try {
    // 兼容 iOS 和一些浏览器的 Date 解析，将 "-" 替换为 "/"
    const dateObj = new Date(timeStr.replace('T', ' ').replace(/-/g, '/'))
    if (isNaN(dateObj.getTime())) return timeStr // 解析失败则原样返回
    
    const y = dateObj.getFullYear()
    const m = dateObj.getMonth() + 1
    const d = dateObj.getDate()
    const h = String(dateObj.getHours()).padStart(2, '0')
    const min = String(dateObj.getMinutes()).padStart(2, '0')
    const s = String(dateObj.getSeconds()).padStart(2, '0')
    
    return `${y}年${m}月${d}日 ${h}:${min}:${s}`
  } catch (e) {
    return timeStr
  }
}

const onPageChange = (page) => { getList(page) }
const handleReply = (item) => { emit('reply', item) }

watch(() => props.targetId, (newVal) => {
  if (newVal) getList(1)
})

onMounted(() => getList())

defineExpose({ refresh: () => getList(1) })
</script>

<style scoped>
.message-list-container { background: #fff; padding: 20px; border-radius: 8px; }
.message-item { padding: 15px 0; border-bottom: 1px solid #f0f0f0; }
.message-item:last-child { border-bottom: none; }

.root-msg-wrapper { display: flex; gap: 15px; }

/* 优化：YouTube极简风格子评论 */
.sub-msg-list {
  /* 移除原有的灰色背景、内边距、圆角 */
  margin-left: 55px; /* 加大缩进，体现层级 */
  margin-top: 15px;
}
.sub-msg-item { 
  display: flex; 
  gap: 15px; /* 对齐父级间距 */
  margin-bottom: 20px; 
}
.sub-msg-item:last-child { margin-bottom: 0; }

.msg-left { flex-shrink: 0; width: 40px; height: 40px; }
.msg-left.small { width: 32px; height: 32px; }

/* 优化：所有头像改为正圆形，显得更具现代感 */
.msg-avatar { 
  width: 40px; 
  height: 40px; 
  border-radius: 50%; /* 正圆 */
  object-fit: cover; 
}

.msg-right { flex: 1; }
.msg-nickname { font-weight: 600; font-size: 0.95rem; color: #111; }
.admin-badge { background: #f56c6c; color: #fff; font-size: 12px; padding: 1px 6px; border-radius: 10px; margin-left: 5px; font-weight: normal; }
.msg-content { font-size: 0.95rem; color: #333; margin: 6px 0; line-height: 1.6; word-break: break-word; }
.msg-footer { font-size: 0.8rem; color: #888; display: flex; gap: 15px; align-items: center; margin-top: 5px; }

.reply-btn { cursor: pointer; display: flex; align-items: center; gap: 3px; font-weight: 500; }
.reply-btn:hover { color: #065fd4; /* YouTube蓝色 */ }
.reply-text { color: #065fd4; margin-left: 5px; font-weight: 500; font-size: 0.9rem; }

/* 分页样式 */
.pagination-box { display: flex; justify-content: center; margin-top: 30px; }
:deep(.merged-pagination .el-pager li), 
:deep(.merged-pagination .btn-prev), 
:deep(.merged-pagination .btn-next) {
  margin: 0 !important; border-radius: 0 !important; border: 1px solid #ddd; border-left: none;
  background-color: #fff !important; color: #666; height: 36px; line-height: 34px;
}
:deep(.merged-pagination .el-pager li:first-child) { border-left: 1px solid #ddd; }
:deep(.merged-pagination .btn-prev) { border-radius: 4px 0 0 4px !important; border-right: none; padding: 0 15px !important; width: auto !important; }
:deep(.merged-pagination .btn-next) { border-radius: 0 4px 4px 0 !important; border-left: none; padding: 0 15px !important; width: auto !important; }
:deep(.merged-pagination .el-pager li:not(.is-disabled).is-active) { 
  background-color: var(--bs-primary, #409EFF) !important; color: #fff; border-color: var(--bs-primary, #409EFF); position: relative; z-index: 2; 
}
:deep(.merged-pagination .el-pager li:hover) { z-index: 1; color: var(--bs-primary, #409EFF); background-color: #f5f7fa !important; }
</style>