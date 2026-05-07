<template>
  <div class="app-container">
    <el-card class="filter-container" shadow="never">
      <el-row justify="space-between" align="middle">
        <div class="filter-left">
          <span class="panel-title">🚫 黑名单IP管理</span>
        </div>
        <div class="filter-right">
          <el-button type="primary" :icon="Plus" @click="handleCreate">添加IP</el-button>
        </div>
      </el-row>
    </el-card>

    <el-card class="table-container" shadow="never">
      <el-table v-loading="loading" :data="list" border stripe style="width: 100%" :header-cell-style="{ background: '#f5f7fa', color: '#606266', fontWeight: 'bold' }">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="ip" label="IP地址" min-width="200" align="center">
          <template #default="scope">
            <el-tag type="danger" effect="plain">{{ scope.row.ip }}</el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="status" label="状态" width="120" align="center">
          <template #default="scope">
            <el-switch v-model="scope.row.status" @change="handleStatusChange(scope.row)" />
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="创建时间" width="180" align="center" />
        <el-table-column prop="updateTime" label="更新时间" width="180" align="center" />

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

    <el-dialog :title="dialog.title" v-model="dialog.visible" width="400px" @close="resetForm" center>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="IP地址" prop="ip">
          <el-input v-model="form.ip" placeholder="请输入要封禁的IP" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" active-text="启用" inactive-text="禁用" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible = false">取 消</el-button>
        <el-button type="primary" @click="submitForm">保 存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * 后台黑名单 IP 管理页
 * IP 封禁/解封（开关切换），支持增删改查
 */
import { ref, reactive, onMounted, nextTick } from 'vue'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const loading = ref(true)
const list = ref([])
const total = ref(0)
const queryParams = reactive({ pageNum: 1, pageSize: 10 })

const dialog = reactive({ visible: false, title: '', type: 'add' })
const formRef = ref(null)
const form = reactive({ id: null, ip: '', status: true })

const rules = {
  ip: [{ required: true, message: '请输入IP', trigger: 'blur' }]
}

const getList = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/blacklist/list', { params: queryParams })
    list.value = res.data.page.records
    total.value = res.data.page.total
  } finally {
    loading.value = false
  }
}

const resetForm = () => { form.id = null; form.ip = ''; form.status = true; nextTick(() => formRef.value?.clearValidate()) }
const handleCreate = () => { resetForm(); dialog.title = '添加黑名单'; dialog.type = 'add'; dialog.visible = true }
const handleEdit = (row) => { resetForm(); dialog.title = '编辑黑名单'; dialog.type = 'edit'; dialog.visible = true; nextTick(() => Object.assign(form, row)) }

const submitForm = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      if (dialog.type === 'edit') await request.put('/admin/blacklist/update', form)
      else await request.post('/admin/blacklist/save', form)
      ElMessage.success('操作成功')
      dialog.visible = false
      getList()
    } catch (e) { console.error(e) }
  })
}

const handleStatusChange = async (row) => {
  try {
    await request.put('/admin/blacklist/update', row)
    ElMessage.success('状态已更新')
  } catch (e) {
    row.status = !row.status // 失败回滚
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认移除该IP吗?', '警告', { type: 'warning' }).then(async () => {
    await request.delete(`/admin/blacklist/${row.id}`)
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
.pagination-container { margin-top: 20px; }
</style>