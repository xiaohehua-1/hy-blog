<template>
  <div class="app-container">
    <el-card class="filter-container" shadow="never">
      <el-row justify="space-between" align="middle">
        <div class="filter-left">
          <span class="panel-title">📜 系统日志</span>
        </div>
        <div class="filter-right">
          <el-button type="danger" :icon="Delete" @click="handleClean">清理日志</el-button>
        </div>
      </el-row>
    </el-card>

    <el-card class="table-container" shadow="never">
      <div class="search-wrapper">
        <el-form :inline="true" :model="queryParams" class="search-form">
          <el-form-item label="IP地址">
            <el-input v-model="queryParams.ip" placeholder="请输入IP查询" clearable @keyup.enter="handleSearch" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table v-loading="loading" :data="list" border stripe style="width: 100%" :header-cell-style="{ background: '#f5f7fa', color: '#606266', fontWeight: 'bold' }">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="ip" label="IP地址" width="130" align="center" />
        <el-table-column prop="content" label="日志内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="method" label="请求方式" width="100" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.method === 'GET'" type="info">GET</el-tag>
            <el-tag v-else-if="scope.row.method === 'POST'" type="success">POST</el-tag>
            <el-tag v-else-if="scope.row.method === 'PUT'" type="warning">PUT</el-tag>
            <el-tag v-else-if="scope.row.method === 'DELETE'" type="danger">DELETE</el-tag>
            <el-tag v-else>{{ scope.row.method || '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="requestUrl" label="请求URL" min-width="200" show-overflow-tooltip />
        <el-table-column prop="params" label="请求参数" min-width="150" show-overflow-tooltip />
        <el-table-column prop="os" label="操作系统" width="120" align="center" />
        <el-table-column prop="browser" label="浏览器" width="120" align="center" />
        <el-table-column prop="spiderType" label="爬虫类型" width="120" align="center" />
        <el-table-column prop="createTime" label="创建时间" width="170" align="center" />
        
        <el-table-column label="操作" width="100" align="center" fixed="right">
          <template #default="scope">
            <el-button type="danger" link :icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50, 100]"
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
/**
 * 后台操作日志页
 * 按 IP 搜索，单条删除 + 按天数批量清理
 */
import { ref, reactive, onMounted } from 'vue'
import { Search, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const loading = ref(true)
const list = ref([])
const total = ref(0)
const queryParams = reactive({ pageNum: 1, pageSize: 10, ip: '' })

const getList = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/sys/log/list', { params: queryParams })
    list.value = res.data.page.records
    total.value = res.data.page.total
  } finally {
    loading.value = false
  }
}

const handleSearch = () => { queryParams.pageNum = 1; getList() }

/**
 * 清理 N 天前的日志，弹出输入框让管理员指定天数，默认 30 天
 */
const handleClean = () => {
  ElMessageBox.prompt('请输入要清理多少天之前的日志', '清理日志', {
    confirmButtonText: '确认清理',
    cancelButtonText: '取消',
    inputPattern: /^[1-9][0-9]*$/,
    inputErrorMessage: '请输入正确的天数（正整数）',
    inputValue: '30'
  }).then(async ({ value }) => {
    try {
      const res = await request.delete('/admin/sys/log/clean', { params: { days: value } })
      ElMessage.success(res.message || '清理成功')
      getList()
    } catch (e) {
      ElMessage.error('清理失败')
    }
  }).catch(() => {})
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该条日志?', '警告', { type: 'warning' }).then(async () => {
    await request.delete(`/admin/sys/log/${row.id}`)
    ElMessage.success('删除成功')
    getList()
  }).catch(() => {})
}

onMounted(() => { getList() })
</script>

<style scoped>
.app-container { padding: 20px; }
.filter-container { margin-bottom: 20px; }
.panel-title { font-size: 18px; font-weight: bold; color: #303133; }
.search-wrapper { margin-bottom: 15px; padding-bottom: 15px; border-bottom: 1px dashed #e4e7ed; }
.pagination-container { margin-top: 20px; }
</style>