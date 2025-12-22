<template>
  <div class="app-container">
    <el-card class="filter-container" shadow="never">
      <el-row justify="space-between" align="middle">
        <div class="filter-left">
          <span class="panel-title">📝 文章管理</span>
        </div>
        <div class="filter-right">
          <el-button type="primary" :icon="Plus" @click="$router.push('/admin/article/publish')">发布文章</el-button>
          <el-button type="danger" :icon="Delete" :disabled="ids.length === 0" @click="handleBatchDelete">批量删除</el-button>
        </div>
      </el-row>
    </el-card>

    <el-card class="table-container" shadow="never">
      <div class="search-wrapper">
        <el-form :inline="true" :model="queryParams" class="search-form">
          <el-form-item label="标题">
            <el-input v-model="queryParams.title" placeholder="请输入标题" clearable @keyup.enter="handleSearch" style="width: 180px" />
          </el-form-item>
          
          <el-form-item label="分类">
            <el-select v-model="queryParams.typeId" placeholder="请选择分类" clearable filterable style="width: 140px">
              <el-option v-for="item in typeList" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>

          <el-form-item label="标签">
            <el-select v-model="queryParams.tagId" placeholder="请选择标签" clearable filterable style="width: 140px">
              <el-option v-for="item in tagList" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>

          <el-form-item label="状态">
            <el-select v-model="queryParams.published" placeholder="请选择" clearable style="width: 100px">
              <el-option label="已发布" :value="true" />
              <el-option label="草稿" :value="false" />
            </el-select>
          </el-form-item>

          <el-form-item label="推荐">
            <el-select v-model="queryParams.recommend" placeholder="请选择" clearable style="width: 100px">
              <el-option label="是" :value="true" />
              <el-option label="否" :value="false" />
            </el-select>
          </el-form-item>
          
          <el-form-item label="版权">
            <el-select v-model="queryParams.copyright" placeholder="请选择" clearable style="width: 100px">
              <el-option label="原创" :value="1" />
              <el-option label="转载" :value="2" />
              <el-option label="翻译" :value="3" />
            </el-select>
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
        <el-table-column prop="id" label="ID" width="60" align="center" sortable />
        
        <el-table-column label="首图" width="100" align="center">
          <template #default="scope">
            <el-image 
              class="table-img"
              :src="scope.row.firstPicture" 
              :preview-src-list="[scope.row.firstPicture]"
              fit="cover"
              preview-teleported
            >
              <template #error>
                <div class="image-slot"><el-icon><Picture /></el-icon></div>
              </template>
            </el-image>
          </template>
        </el-table-column>

        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip align="left">
          <template #default="scope">
             <span class="blog-title" @click="handleEdit(scope.row)">{{ scope.row.title }}</span>
          </template>
        </el-table-column>

        <el-table-column label="分类" width="120" align="center">
          <template #default="scope">
            <el-tag effect="light">{{ scope.row.typeName || '未分类' }}</el-tag> 
          </template>
        </el-table-column>

        <el-table-column label="标签" width="180" align="center">
          <template #default="scope">
            <div class="tag-group">
              <el-tag 
                v-for="tag in scope.row.tagList" 
                :key="tag.id" 
                size="small" 
                effect="plain"
                class="mx-1"
              >
                {{ tag.name }}
              </el-tag>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.published" type="success" effect="dark">已发布</el-tag>
            <el-tag v-else type="info" effect="dark">草稿</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="推荐" width="80" align="center">
          <template #default="scope">
            <el-switch v-model="scope.row.recommend" disabled />
          </template>
        </el-table-column>
        
        <el-table-column label="版权" width="80" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.copyright === 1" type="danger" effect="plain">原创</el-tag>
            <el-tag v-else-if="scope.row.copyright === 2" type="warning" effect="plain">转载</el-tag>
            <el-tag v-else type="info" effect="plain">翻译</el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="createTime" label="发布时间" width="170" align="center" sortable />

        <el-table-column prop="updateTime" label="更新时间" width="170" align="center" sortable />
        
        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template #default="scope">
            <el-button type="primary" link :icon="Edit" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="danger" link :icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
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
import { Search, Refresh, Plus, Delete, Edit, Picture } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { useRouter } from 'vue-router'

const router = useRouter()
const loading = ref(true)
const list = ref([])
const total = ref(0)
const ids = ref([])
const typeList = ref([]) 
const tagList = ref([])  

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  title: '',
  typeId: null,
  tagId: null, 
  published: null,
  recommend: null,
  copyright: null
})

const initData = async () => {
  const [resType, resTag] = await Promise.all([
    request.get('/admin/type/listAll'),
    request.get('/admin/tag/listAll')
  ])
  typeList.value = resType.data.list
  tagList.value = resTag.data.list
}

const getList = async () => {
  loading.value = true
  try {
    const res = await request.post('/admin/blog/list', queryParams)
    list.value = res.data.page.records
    total.value = res.data.page.total
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  queryParams.pageNum = 1
  getList()
}

const resetQuery = () => {
  queryParams.title = ''
  queryParams.typeId = null
  queryParams.tagId = null
  queryParams.published = null
  queryParams.recommend = null
  queryParams.copyright = null
  handleSearch()
}

const handleEdit = (row) => {
  router.push(`/admin/article/edit/${row.id}`)
}

const handleSelectionChange = (selection) => {
  ids.value = selection.map(item => item.id)
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该文章吗? 删除后不可恢复', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await request.delete(`/admin/blog/${row.id}`)
    ElMessage.success('删除成功')
    getList()
  }).catch(() => {})
}

const handleBatchDelete = () => {
  ElMessageBox.confirm(`确认永久删除选中的 ${ids.value.length} 篇文章吗?`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await request.delete('/admin/blog/delete/batch', { data: ids.value })
    ElMessage.success('批量删除成功')
    getList()
  }).catch(() => {})
}

onMounted(() => {
  initData()
  getList()
})
</script>

<style scoped>
.app-container { padding: 20px; }
.filter-container { margin-bottom: 20px; }
.panel-title { font-size: 18px; font-weight: bold; color: #303133; }
.search-wrapper { margin-bottom: 15px; padding-bottom: 15px; border-bottom: 1px dashed #e4e7ed; }
.search-form .el-form-item { margin-bottom: 10px; margin-right: 10px; }
.table-img { width: 60px; height: 40px; border-radius: 4px; border: 1px solid #eee; }
.image-slot { display: flex; justify-content: center; align-items: center; width: 100%; height: 100%; background: #f5f7fa; color: #909399; }
.tag-group { display: flex; flex-wrap: wrap; gap: 4px; justify-content: center; }
.mx-1 { margin: 0; }
.blog-title { color: #409EFF; cursor: pointer; font-weight: 500; }
.blog-title:hover { text-decoration: underline; }
.pagination-container { margin-top: 20px; }
</style>