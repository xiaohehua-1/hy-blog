<template>
  <div class="app-container">
    <el-card class="filter-container" shadow="never">
      <el-row justify="space-between" align="middle">
        <div class="filter-left">
          <span class="panel-title">💬 互动管理</span>
        </div>
        <div class="filter-right">
          </div>
      </el-row>
    </el-card>

    <el-card class="table-container" shadow="never">
      
      <div class="tabs-wrapper">
        <el-tabs v-model="activeTab" @tab-change="handleTabChange" class="demo-tabs">
          <el-tab-pane label="文章评论" name="comment">
            <template #label>
              <span class="custom-tabs-label">
                <el-icon><ChatDotRound /></el-icon> 文章评论
              </span>
            </template>
          </el-tab-pane>
          <el-tab-pane label="网站留言" name="message">
            <template #label>
              <span class="custom-tabs-label">
                <el-icon><Message /></el-icon> 网站留言
              </span>
            </template>
          </el-tab-pane>
        </el-tabs>
      </div>

      <div v-show="activeTab === 'comment'">
        <el-table 
          v-loading="loading" 
          :data="commentList" 
          border 
          stripe 
          style="width: 100%"
          :header-cell-style="{ background: '#f5f7fa', color: '#606266', fontWeight: 'bold', textAlign: 'center' }"
          :cell-style="{ textAlign: 'center' }"
        >
          <el-table-column prop="id" label="ID" width="80" align="center" />
          
          <el-table-column label="用户" width="220" align="left" header-align="center">
            <template #default="scope">
              <div class="user-info">
                <el-avatar :size="36" :src="scope.row.avatar" class="mr-10">{{ scope.row.nickname?.charAt(0) }}</el-avatar>
                <div class="text-info">
                  <div class="name">{{ scope.row.nickname }}</div>
                  <div class="email">{{ scope.row.email }}</div>
                </div>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="content" label="评论内容" min-width="300" show-overflow-tooltip align="left" header-align="center" />
          
          <el-table-column label="所属文章" min-width="180" show-overflow-tooltip>
            <template #default="scope">
              <el-tag effect="plain" type="info" class="blog-tag">📄 {{ scope.row.blogTitle }}</el-tag>
            </template>
          </el-table-column>

          <el-table-column prop="createTime" label="时间" width="170" sortable />

          <el-table-column label="操作" width="120" fixed="right">
            <template #default="scope">
              <el-button type="danger" link :icon="Delete" @click="handleDeleteComment(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-container">
          <el-pagination
            v-model:current-page="commentQuery.pageNum"
            v-model:page-size="commentQuery.pageSize"
            :total="commentTotal"
            :page-sizes="[10, 20, 50]"
            layout="prev, pager, next, jumper, ->, total, sizes"
            background
            @size-change="getCommentList"
            @current-change="getCommentList"
          />
        </div>
      </div>

      <div v-show="activeTab === 'message'">
        <el-table 
          v-loading="loading" 
          :data="messageList" 
          border 
          stripe 
          style="width: 100%"
          :header-cell-style="{ background: '#f5f7fa', color: '#606266', fontWeight: 'bold', textAlign: 'center' }"
          :cell-style="{ textAlign: 'center' }"
        >
          <el-table-column prop="id" label="ID" width="80" align="center" />
          
          <el-table-column label="用户" width="220" align="left" header-align="center">
            <template #default="scope">
              <div class="user-info">
                <el-avatar :size="36" :src="scope.row.avatar" style="background-color: #f56c6c" class="mr-10">
                  {{ scope.row.nickname?.charAt(0) }}
                </el-avatar>
                <div class="text-info">
                  <div class="name">{{ scope.row.nickname }}</div>
                  <div class="email">{{ scope.row.email }}</div>
                </div>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="content" label="留言内容" min-width="300" show-overflow-tooltip align="left" header-align="center" />
          
          <el-table-column prop="createTime" label="时间" width="170" sortable />

          <el-table-column label="操作" width="120" fixed="right">
            <template #default="scope">
              <el-button type="danger" link :icon="Delete" @click="handleDeleteMessage(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-container">
          <el-pagination
            v-model:current-page="messageQuery.pageNum"
            v-model:page-size="messageQuery.pageSize"
            :total="messageTotal"
            :page-sizes="[10, 20, 50]"
            layout="prev, pager, next, jumper, ->, total, sizes"
            background
            @size-change="getMessageList"
            @current-change="getMessageList"
          />
        </div>
      </div>

    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ChatDotRound, Message, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

// 状态控制
const activeTab = ref('comment')
const loading = ref(false)

// === 评论数据 ===
const commentList = ref([])
const commentTotal = ref(0)
const commentQuery = reactive({ pageNum: 1, pageSize: 10 })

// === 留言数据 ===
const messageList = ref([])
const messageTotal = ref(0)
const messageQuery = reactive({ pageNum: 1, pageSize: 10 })

// 获取评论列表
const getCommentList = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/comment/list', { params: commentQuery })
    commentList.value = res.data.page.records
    commentTotal.value = res.data.page.total
  } finally {
    loading.value = false
  }
}

// 获取留言列表
const getMessageList = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/message/list', { params: messageQuery })
    messageList.value = res.data.page.records
    messageTotal.value = res.data.page.total
  } finally {
    loading.value = false
  }
}

// Tab 切换事件
const handleTabChange = (tabName) => {
  if (tabName === 'comment') {
    getCommentList()
  } else {
    getMessageList()
  }
}

// 删除评论
const handleDeleteComment = (row) => {
  ElMessageBox.confirm('确认删除这条评论吗?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
      await request.delete(`/admin/comment/${row.id}`)
      ElMessage.success('删除成功')
      getCommentList()
    }).catch(() => {})
}

// 删除留言
const handleDeleteMessage = (row) => {
  ElMessageBox.confirm('确认删除这条留言吗?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
      await request.delete(`/admin/message/${row.id}`)
      ElMessage.success('删除成功')
      getMessageList()
    }).catch(() => {})
}

// 初始化
onMounted(() => {
  getCommentList() // 默认加载评论
})
</script>

<style scoped>
.app-container { padding: 20px; }

/* 顶部卡片 */
.filter-container { margin-bottom: 20px; }
.panel-title { font-size: 18px; font-weight: bold; color: #303133; }

/* 底部卡片 */
.table-container { 
  /* 自适应高度，不设 min-height */
  padding-bottom: 20px; 
}

/* Tabs 样式优化 */
.tabs-wrapper {
  margin-bottom: 15px; 
}
.custom-tabs-label .el-icon { vertical-align: middle; margin-right: 4px; }
.custom-tabs-label { font-size: 15px; }

/* 用户信息列样式 */
.user-info { display: flex; align-items: center; }
.mr-10 { margin-right: 10px; }
.text-info { display: flex; flex-direction: column; line-height: 1.4; }
.text-info .name { font-weight: 600; font-size: 14px; color: #303133; }
.text-info .email { font-size: 12px; color: #909399; }

.blog-tag { max-width: 100%; overflow: hidden; text-overflow: ellipsis; }

.pagination-container { margin-top: 20px; }
</style>