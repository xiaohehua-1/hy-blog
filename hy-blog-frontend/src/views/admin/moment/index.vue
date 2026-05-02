<template>
  <div class="app-container">
    <el-card class="filter-container" shadow="never">
      <el-row justify="space-between" align="middle">
        <div class="filter-left">
          <span class="panel-title">👋 动态管理</span>
        </div>
        <div class="filter-right">
          <el-button type="primary" :icon="Plus" @click="handleCreate">发布动态</el-button>
          <el-button type="danger" :icon="Delete" :disabled="ids.length === 0" @click="handleBatchDelete">批量删除</el-button>
        </div>
      </el-row>
    </el-card>

    <el-card class="table-container" shadow="never">
      <div class="search-wrapper">
        <el-form :inline="true" :model="queryParams" class="search-form">
          <el-form-item label="内容">
            <el-input v-model="queryParams.content" placeholder="请输入内容关键字" clearable @keyup.enter="handleSearch" style="width: 200px" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
            <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table
        v-loading="loading"
        :data="list"
        border
        stripe
        style="width: 100%"
        @selection-change="handleSelectionChange"
        :header-cell-style="{ background: '#f5f7fa', color: '#606266', fontWeight: 'bold' }"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column prop="id" label="ID" width="70" align="center" sortable />
        
        <el-table-column label="内容" min-width="250" show-overflow-tooltip align="left">
          <template #default="{ row }">
            <span class="moment-content" @click="handleEdit(row)">{{ row.content || '(纯图片动态)' }}</span>
          </template>
        </el-table-column>

        <el-table-column label="图片" width="160" align="center">
          <template #default="{ row }">
            <div v-if="row.images" class="img-list">
              <el-image 
                v-for="(img, index) in row.images.split(',')"
                :key="index"
                :src="img" 
                :preview-src-list="row.images.split(',')"
                fit="cover"
                class="table-img"
                :initial-index="index"
                preview-teleported
              />
            </div>
            <span v-else style="color:#ddd;">无</span>
          </template>
        </el-table-column>

        <el-table-column label="链接" width="80" align="center">
          <template #default="{ row }">
            <a v-if="row.extraUrl" :href="row.extraUrl" target="_blank" class="link-icon">
              <el-icon><LinkIcon /></el-icon>
            </a>
            <span v-else>-</span>
          </template>
        </el-table-column>

        <el-table-column prop="location" label="位置" width="120" show-overflow-tooltip align="center" />
        
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
             <div class="status-tags">
               <el-tag v-if="row.isTop" type="danger" size="small" effect="dark">置顶</el-tag>
               <el-tag v-if="row.isPrivate" type="info" size="small">私密</el-tag>
               <el-tag v-else-if="isScheduled(row.publishTime)" type="warning" size="small">待发</el-tag>
               <el-tag v-else type="success" size="small">公开</el-tag>
             </div>
          </template>
        </el-table-column>

        <el-table-column prop="publishTime" label="发布时间" width="170" align="center" sortable />

        <el-table-column prop="updateTime" label="修改时间" width="170" align="center" sortable />
        
        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template #default="scope">
            <el-button type="primary" link :icon="Edit" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="danger" link :icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.current"
          v-model:page-size="queryParams.size"
          :page-sizes="[10, 20, 50]"
          layout="prev, pager, next, jumper, ->, total, sizes"
          :total="total"
          background
          @size-change="getList"
          @current-change="getList"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Plus, Edit, Delete, Link as LinkIcon, Search, Refresh } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { getMomentList, deleteMoment, deleteMomentBatch } from '@/api/moment'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const list = ref([])
const total = ref(0)
const ids = ref([])
const queryParams = reactive({ 
  current: 1, 
  size: 10,
  content: ''
})

// 判断是否是定时未来发布的
const isScheduled = (timeStr) => {
  if (!timeStr) return false
  const publishTime = new Date(timeStr).getTime()
  const now = new Date().getTime()
  return publishTime > now
}

const getList = async () => {
  loading.value = true
  try {
    const res = await getMomentList(queryParams)
    if (res.data && res.data.page) {
      list.value = res.data.page.records
      total.value = res.data.page.total
    }
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  queryParams.current = 1
  getList()
}

const resetQuery = () => {
  queryParams.content = ''
  handleSearch()
}

const handleCreate = () => router.push('/admin/content/moment/publish')
const handleEdit = (row) => router.push(`/admin/content/moment/edit/${row.id}`)

const handleSelectionChange = (selection) => {
  ids.value = selection.map(item => item.id)
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该动态吗? 删除后不可恢复', '警告', { 
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning' 
  }).then(async () => {
    await deleteMoment(row.id)
    ElMessage.success('删除成功')
    getList()
  }).catch(() => {})
}

const handleBatchDelete = () => {
  ElMessageBox.confirm(`确认永久删除选中的 ${ids.value.length} 条动态吗?`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deleteMomentBatch(ids.value)
    ElMessage.success('批量删除成功')
    getList()
  }).catch(() => {})
}

onMounted(() => getList())
</script>

<style scoped>
.app-container { padding: 20px; }
.filter-container { margin-bottom: 20px; }
.panel-title { font-size: 18px; font-weight: bold; color: #303133; }
.search-wrapper { margin-bottom: 15px; padding-bottom: 15px; border-bottom: 1px dashed #e4e7ed; }
.search-form .el-form-item { margin-bottom: 10px; margin-right: 10px; }

.img-list {
  display: flex;
  gap: 5px;
  justify-content: center;
  flex-wrap: wrap;
}
.table-img {
  width: 40px;
  height: 40px;
  border-radius: 4px;
  border: 1px solid #eee;
  cursor: pointer;
}
.link-icon {
  font-size: 18px;
  color: #409EFF;
  transition: color 0.3s;
}
.link-icon:hover { color: #66b1ff; }

.moment-content { color: #409EFF; cursor: pointer; font-weight: 500; }
.moment-content:hover { text-decoration: underline; }

.status-tags {
  display: flex;
  flex-direction: column;
  gap: 4px;
  align-items: center;
}
.pagination-container { margin-top: 20px; }
</style>
