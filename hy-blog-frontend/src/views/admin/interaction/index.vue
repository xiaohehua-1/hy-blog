<template>
  <div class="app-container">
    <el-card class="filter-container" shadow="never">
      <el-row justify="space-between" align="middle">
        <div class="filter-left">
          <span class="panel-title">💬 互动管理</span>
        </div>
        <div class="filter-right">
          <el-button type="danger" :icon="Delete" :disabled="selectedIds.length === 0" @click="handleBatchDelete">批量删除</el-button>
        </div>
      </el-row>
    </el-card>

    <el-card class="table-container" shadow="never">
      <div class="search-wrapper">
        <el-form :inline="true" class="search-form" @submit.prevent>
          <el-form-item label="关键字">
            <el-input 
              v-model="currentQuery.keyword" 
              placeholder="请输入评论内容或昵称" 
              clearable 
              @keyup.enter="handleSearch" 
              style="width: 240px" 
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
            <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <div class="tabs-wrapper">
        <el-tabs v-model="activeTab" @tab-change="handleTabChange" class="demo-tabs">
          <el-tab-pane label="文章评论" name="comment">
            <template #label><span class="custom-tabs-label"><el-icon><ChatDotRound /></el-icon> 文章评论</span></template>
          </el-tab-pane>
          <el-tab-pane label="网站留言" name="message">
            <template #label><span class="custom-tabs-label"><el-icon><Message /></el-icon> 网站留言</span></template>
          </el-tab-pane>
          <el-tab-pane label="动态评论" name="momentComment">
            <template #label><span class="custom-tabs-label"><el-icon><ChatLineSquare /></el-icon> 动态评论</span></template>
          </el-tab-pane>
        </el-tabs>
      </div>

      <div v-show="activeTab === 'comment'">
        <el-table v-loading="loading" :data="commentList" row-key="id" default-expand-all border stripe style="width: 100%" @selection-change="handleSelectionChange" :header-cell-style="{ background: '#f5f7fa', color: '#606266', fontWeight: 'bold', textAlign: 'center' }" :cell-style="{ textAlign: 'center' }">
          <el-table-column type="selection" width="55" align="center" />
          <el-table-column prop="id" label="ID" width="100" align="center" />
          <el-table-column label="用户" width="220" align="left" header-align="center">
            <template #default="scope">
              <div class="user-info">
                <el-avatar :size="36" :src="scope.row.adminComment ? adminAvatar : scope.row.avatar" class="mr-10">{{ scope.row.nickname?.charAt(0) }}</el-avatar>
                <div class="text-info">
                  <div class="name">
                    {{ scope.row.nickname }}
                    <el-tag v-if="scope.row.adminComment" size="small" type="danger" effect="dark" style="margin-left: 5px;">站长</el-tag>
                  </div>
                  <div class="email">{{ scope.row.email }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="互动内容" min-width="320" align="left" header-align="center">
            <template #default="scope">
              <div class="content-wrapper">
                <div v-if="scope.row.parentCommentId && scope.row.parentCommentId !== 0" class="reply-reference"><el-icon><Connection /></el-icon> 子评论 (回复)</div>
                <div v-else class="root-reference"><el-icon><ChatDotSquare /></el-icon> 根评论 (首发)</div>
                <div :class="['actual-content', scope.row.adminComment ? 'admin-reply-content' : '']">{{ scope.row.content }}</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="所属文章" min-width="180" show-overflow-tooltip>
            <template #default="scope"><el-tag effect="plain" type="info" class="blog-tag">📄 {{ scope.row.blogTitle }}</el-tag></template>
          </el-table-column>
          <el-table-column prop="createTime" label="时间" width="170" sortable />
          <el-table-column label="操作" width="160" fixed="right">
            <template #default="scope">
              <el-button type="primary" link :icon="ChatLineRound" @click="handleOpenReply(scope.row, 'comment')">回复</el-button>
              <el-button type="danger" link :icon="Delete" @click="handleDeleteSingle(scope.row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination-container">
          <el-pagination v-model:current-page="commentQuery.pageNum" v-model:page-size="commentQuery.pageSize" :total="commentTotal" :page-sizes="[10, 20, 50]" layout="prev, pager, next, jumper, ->, total, sizes" background @size-change="getCommentList" @current-change="getCommentList" />
        </div>
      </div>

      <div v-show="activeTab === 'message'">
        <el-table v-loading="loading" :data="messageList" row-key="id" default-expand-all border stripe style="width: 100%" @selection-change="handleSelectionChange" :header-cell-style="{ background: '#f5f7fa', color: '#606266', fontWeight: 'bold', textAlign: 'center' }" :cell-style="{ textAlign: 'center' }">
          <el-table-column type="selection" width="55" align="center" />
          <el-table-column prop="id" label="ID" width="100" align="center" />
          <el-table-column label="用户" width="220" align="left" header-align="center">
            <template #default="scope">
              <div class="user-info">
                <el-avatar :size="36" :src="scope.row.adminMessage ? adminAvatar : scope.row.avatar" style="background-color: #f56c6c" class="mr-10">{{ scope.row.nickname?.charAt(0) }}</el-avatar>
                <div class="text-info">
                  <div class="name">
                    {{ scope.row.nickname }}
                    <el-tag v-if="scope.row.adminMessage" size="small" type="danger" effect="dark" style="margin-left: 5px;">站长</el-tag>
                  </div>
                  <div class="email">{{ scope.row.email }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="互动内容" min-width="320" align="left" header-align="center">
            <template #default="scope">
              <div class="content-wrapper">
                <div v-if="scope.row.parentMessageId && scope.row.parentMessageId !== 0" class="reply-reference"><el-icon><Connection /></el-icon> 子留言 (回复)</div>
                <div v-else class="root-reference"><el-icon><ChatDotSquare /></el-icon> 根留言 (首发)</div>
                <div :class="['actual-content', scope.row.adminMessage ? 'admin-reply-content' : '']">{{ scope.row.content }}</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="时间" width="170" sortable />
          <el-table-column label="操作" width="160" fixed="right">
            <template #default="scope">
              <el-button type="primary" link :icon="ChatLineRound" @click="handleOpenReply(scope.row, 'message')">回复</el-button>
              <el-button type="danger" link :icon="Delete" @click="handleDeleteSingle(scope.row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination-container">
          <el-pagination v-model:current-page="messageQuery.pageNum" v-model:page-size="messageQuery.pageSize" :total="messageTotal" :page-sizes="[10, 20, 50]" layout="prev, pager, next, jumper, ->, total, sizes" background @size-change="getMessageList" @current-change="getMessageList" />
        </div>
      </div>

      <div v-show="activeTab === 'momentComment'">
        <el-table v-loading="loading" :data="momentCommentList" row-key="id" default-expand-all border stripe style="width: 100%" @selection-change="handleSelectionChange" :header-cell-style="{ background: '#f5f7fa', color: '#606266', fontWeight: 'bold', textAlign: 'center' }" :cell-style="{ textAlign: 'center' }">
          <el-table-column type="selection" width="55" align="center" />
          <el-table-column prop="id" label="ID" width="100" align="center" />
          <el-table-column label="用户" width="220" align="left" header-align="center">
            <template #default="scope">
              <div class="user-info">
                <el-avatar :size="36" :src="scope.row.adminComment ? adminAvatar : scope.row.avatar" style="background-color: #e6a23c" class="mr-10">{{ scope.row.nickname?.charAt(0) }}</el-avatar>
                <div class="text-info">
                  <div class="name">
                    {{ scope.row.nickname }}
                    <el-tag v-if="scope.row.adminComment" size="small" type="danger" effect="dark" style="margin-left: 5px;">站长</el-tag>
                  </div>
                  <div class="email">{{ scope.row.email }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="互动内容" min-width="320" align="left" header-align="center">
            <template #default="scope">
              <div class="content-wrapper">
                <div v-if="scope.row.parentCommentId && scope.row.parentCommentId !== 0" class="reply-reference"><el-icon><Connection /></el-icon> 子评论 (回复)</div>
                <div v-else class="root-reference"><el-icon><ChatDotSquare /></el-icon> 根评论 (首发)</div>
                <div :class="['actual-content', scope.row.adminComment ? 'admin-reply-content' : '']">{{ scope.row.content }}</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="所属动态 ID" width="150" align="center">
            <template #default="scope"><el-tag effect="plain" type="warning" class="blog-tag">💡 动态: {{ scope.row.momentId }}</el-tag></template>
          </el-table-column>
          <el-table-column prop="createTime" label="时间" width="170" sortable />
          <el-table-column label="操作" width="160" fixed="right">
            <template #default="scope">
              <el-button type="primary" link :icon="ChatLineRound" @click="handleOpenReply(scope.row, 'momentComment')">回复</el-button>
              <el-button type="danger" link :icon="Delete" @click="handleDeleteSingle(scope.row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination-container">
          <el-pagination v-model:current-page="momentCommentQuery.pageNum" v-model:page-size="momentCommentQuery.pageSize" :total="momentCommentTotal" :page-sizes="[10, 20, 50]" layout="prev, pager, next, jumper, ->, total, sizes" background @size-change="getMomentCommentList" @current-change="getMomentCommentList" />
        </div>
      </div>
    </el-card>

    <el-dialog v-model="replyDialogVisible" title="回复评论" width="500px">
      <div style="margin-bottom: 15px; color: #666; background: #f4f4f5; padding: 10px; border-radius: 4px;">
        正在回复 <strong>@{{ replyTarget?.nickname }}</strong> : <br/>
        <span style="font-size: 13px; margin-top: 5px; display: inline-block;">{{ replyTarget?.content }}</span>
      </div>
      <el-input v-model="replyContent" type="textarea" :rows="4" placeholder="请输入作为站长的回复内容..." />
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="replyDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="replySubmitting" @click="submitReply">发送回复</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * 后台互动管理页（Tab 切换）
 * 文章评论 / 网站留言 / 动态评论三个 Tab，支持树形列表展示、关键字搜索、批量删除、站长回复
 */
import { ref, reactive, computed, onMounted } from 'vue'
import { Search, Refresh, ChatDotRound, Message, ChatLineSquare, Delete, ChatLineRound, Connection, ChatDotSquare } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import adminAvatarImg from '@/assets/images/me.jpg'

const adminAvatar = adminAvatarImg
const activeTab = ref('comment')
const loading = ref(false)
const selectedIds = ref([]) // 用于存放当前勾选的 ID 数组

// ===== 数据状态 =====
const commentList = ref([])
const commentTotal = ref(0)
const commentQuery = reactive({ pageNum: 1, pageSize: 10, keyword: '' })

const messageList = ref([])
const messageTotal = ref(0)
const messageQuery = reactive({ pageNum: 1, pageSize: 10, keyword: '' })

const momentCommentList = ref([])
const momentCommentTotal = ref(0)
const momentCommentQuery = reactive({ pageNum: 1, pageSize: 10, keyword: '' })

// ===== 树形转换算法 =====
const listToTree = (list, parentIdKey) => {
  if (!list || list.length === 0) return []
  const map = {}
  const roots = []
  const data = JSON.parse(JSON.stringify(list))

  data.forEach(item => { item.children = []; map[item.id] = item })
  data.forEach(item => {
    const parentId = item[parentIdKey]
    if (parentId && parentId !== 0 && map[parentId] && parentId !== item.id) {
      map[parentId].children.push(item)
    } else { roots.push(item) }
  })
  const cleanEmptyChildren = (arr) => {
    arr.forEach(item => {
      if (item.children && item.children.length === 0) delete item.children
      else if (item.children) cleanEmptyChildren(item.children)
    })
  }
  cleanEmptyChildren(roots)
  return roots
}

// ===== 列表获取 =====
const getCommentList = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/comment/list', { params: commentQuery })
    commentList.value = listToTree(res.data.page.records, 'parentCommentId')
    commentTotal.value = res.data.page.total
  } finally { loading.value = false }
}

const getMessageList = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/message/list', { params: messageQuery })
    messageList.value = listToTree(res.data.page.records, 'parentMessageId')
    messageTotal.value = res.data.page.total
  } finally { loading.value = false }
}

const getMomentCommentList = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/momentComment/list', { params: momentCommentQuery })
    momentCommentList.value = listToTree(res.data.page.records, 'parentCommentId')
    momentCommentTotal.value = res.data.page.total
  } finally { loading.value = false }
}

// ===== 搜索与切换逻辑 =====
const currentQuery = computed(() => {
  if (activeTab.value === 'comment') return commentQuery
  if (activeTab.value === 'message') return messageQuery
  return momentCommentQuery
})

const handleSearch = () => {
  currentQuery.value.pageNum = 1
  if (activeTab.value === 'comment') getCommentList()
  else if (activeTab.value === 'message') getMessageList()
  else getMomentCommentList()
}

const resetQuery = () => {
  currentQuery.value.keyword = ''
  handleSearch()
}

const handleTabChange = (tabName) => {
  selectedIds.value = [] // 切换 Tab 时清空勾选
  if (tabName === 'comment') getCommentList()
  else if (tabName === 'message') getMessageList()
  else if (tabName === 'momentComment') getMomentCommentList()
}

// 记录复选框变化
const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

// ===== 删除逻辑 =====
// 统一的删除请求前缀获取
const getUrlPrefix = () => {
  if (activeTab.value === 'comment') return '/admin/comment'
  if (activeTab.value === 'message') return '/admin/message'
  return '/admin/momentComment'
}

// 单个删除
const handleDeleteSingle = (id) => {
  ElMessageBox.confirm('确认删除该记录吗?', '警告', { type: 'warning' }).then(async () => {
    await request.delete(`${getUrlPrefix()}/${id}`)
    ElMessage.success('删除成功')
    handleSearch()
  }).catch(() => {})
}

// 批量删除：改为串行执行，避免并发触发后端 WebSocket 写入冲突（TEXT_FULL_WRITING）
const handleBatchDelete = () => {
  ElMessageBox.confirm(`确认永久删除选中的 ${selectedIds.value.length} 条记录吗?`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    loading.value = true
    const prefix = getUrlPrefix()
    try {
      for (const id of selectedIds.value) {
        await request.delete(`${prefix}/${id}`)
      }
      ElMessage.success('批量删除成功')
      selectedIds.value = []
      handleSearch()
    } catch (e) {
      ElMessage.error('部分或全部删除失败')
    } finally {
      loading.value = false
    }
  }).catch(() => {})
}

// ===== 回复逻辑 =====
const replyDialogVisible = ref(false)
const replyTarget = ref(null)
const replyType = ref('')
const replyContent = ref('')
const replySubmitting = ref(false)

const handleOpenReply = (row, type) => {
  replyTarget.value = row; replyType.value = type; replyContent.value = ''; replyDialogVisible.value = true
}

const submitReply = async () => {
  if (!replyContent.value.trim()) return ElMessage.warning('回复内容不能为空')
  replySubmitting.value = true
  try {
    let url = ''
    let data = {
      content: replyContent.value, nickname: '小荷花', avatar: 'https://cravatar.cn/avatar/1?d=mp', 
      email: 'admin@admin.com', adminComment: true, adminMessage: true
    }
    if (replyType.value === 'comment') {
      url = '/admin/comment/reply'; data.blogId = replyTarget.value.blogId; data.parentCommentId = replyTarget.value.id; data.rootCommentId = replyTarget.value.rootCommentId || replyTarget.value.id
    } else if (replyType.value === 'message') {
      url = '/admin/message/reply'; data.parentMessageId = replyTarget.value.id; data.rootMessageId = replyTarget.value.rootMessageId || replyTarget.value.id
    } else if (replyType.value === 'momentComment') {
      url = '/admin/momentComment/reply'; data.momentId = replyTarget.value.momentId; data.parentCommentId = replyTarget.value.id; data.rootCommentId = replyTarget.value.rootCommentId || replyTarget.value.id
    }
    await request.post(url, data)
    ElMessage.success('回复成功')
    replyDialogVisible.value = false
    handleSearch() 
  } finally { replySubmitting.value = false }
}

onMounted(() => getCommentList())
</script>

<style scoped>
.app-container { padding: 20px; }
.filter-container { margin-bottom: 20px; }
.panel-title { font-size: 18px; font-weight: bold; color: #303133; }
.table-container { padding-bottom: 20px; }

/* 搜索区域样式 (参考博客管理) */
.search-wrapper { margin-bottom: 15px; padding-bottom: 15px; border-bottom: 1px dashed #e4e7ed; }
.search-form .el-form-item { margin-bottom: 10px; margin-right: 10px; }

.tabs-wrapper { margin-bottom: 15px; }
.custom-tabs-label .el-icon { vertical-align: middle; margin-right: 4px; }
.custom-tabs-label { font-size: 15px; }
.user-info { display: flex; align-items: center; }
.mr-10 { margin-right: 10px; }
.text-info { display: flex; flex-direction: column; line-height: 1.4; text-align: left; }
.text-info .name { font-weight: 600; font-size: 14px; color: #303133; display: flex; align-items: center; }
.text-info .email { font-size: 12px; color: #909399; }
.blog-tag { max-width: 100%; overflow: hidden; text-overflow: ellipsis; }
.pagination-container { margin-top: 20px; }

/* 互动内容区专属样式 */
.content-wrapper { display: flex; flex-direction: column; gap: 8px; padding: 6px 0; }
.reply-reference { display: inline-flex; align-items: center; gap: 4px; font-size: 12px; color: #e6a23c; background: #fdf6ec; padding: 2px 8px; border-radius: 12px; width: fit-content; border: 1px solid #faecd8; }
.root-reference { display: inline-flex; align-items: center; gap: 4px; font-size: 12px; color: #67c23a; background: #f0f9eb; padding: 2px 8px; border-radius: 12px; width: fit-content; border: 1px solid #e1f3d8; }
.actual-content { font-size: 13.5px; color: #444; line-height: 1.6; background: #f4f4f5; padding: 10px 14px; border-radius: 6px; border-left: 4px solid #909399; white-space: pre-wrap; word-break: break-all; box-shadow: 0 1px 3px rgba(0,0,0,0.05); }
.admin-reply-content { background: #fef0f0; border-left-color: #f56c6c; color: #333; }
</style>