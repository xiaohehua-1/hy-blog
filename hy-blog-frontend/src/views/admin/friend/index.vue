<template>
  <div class="app-container">
    <el-card class="filter-container" shadow="never">
      <el-row justify="space-between" align="middle">
        <div class="filter-left">
          <span class="panel-title">🔗 友链管理</span>
        </div>
        <div class="filter-right">
          <el-button type="primary" :icon="Plus" @click="handleCreate">添加友链</el-button>
        </div>
      </el-row>
    </el-card>

    <el-card class="table-container" shadow="never">
      
      <div class="tabs-wrapper">
        <el-radio-group v-model="queryParams.status" @change="handleFilter" size="large">
          <el-radio-button :label="1">
            <el-icon class="mr-1"><CircleCheck /></el-icon> 已审核通过
          </el-radio-button>
          <el-radio-button :label="0">
            <el-icon class="mr-1"><Bell /></el-icon> 待审核
          </el-radio-button>
          <el-radio-button :label="-1">
            <el-icon class="mr-1"><CircleClose /></el-icon> 未通过
          </el-radio-button>
        </el-radio-group>
      </div>

      <el-table
        v-loading="loading"
        :data="list"
        border
        stripe
        style="width: 100%"
        :header-cell-style="{ background: '#f5f7fa', color: '#606266', fontWeight: 'bold', textAlign: 'center' }"
        :cell-style="{ textAlign: 'center' }"
      >
        <el-table-column prop="id" label="ID" width="80" sortable />
        
        <el-table-column label="图标" width="100">
          <template #default="scope">
            <el-image 
              class="avatar-img"
              :src="scope.row.pictureAddress" 
              :preview-src-list="[scope.row.pictureAddress]"
              fit="cover"
              preview-teleported
            >
              <template #error>
                <div class="image-slot"><el-icon><Picture /></el-icon></div>
              </template>
            </el-image>
          </template>
        </el-table-column>

        <el-table-column prop="blogName" label="博客名称" min-width="150" show-overflow-tooltip>
          <template #default="scope">
            <span class="blog-name">{{ scope.row.blogName }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="blogAddress" label="博客地址" min-width="200" show-overflow-tooltip>
          <template #default="scope">
            <a :href="scope.row.blogAddress" target="_blank" class="link-type">{{ scope.row.blogAddress }}</a>
          </template>
        </el-table-column>

        <el-table-column v-if="queryParams.status === 1" prop="blogDescription" label="博客描述" min-width="200" show-overflow-tooltip />
        
        <el-table-column v-if="queryParams.status === -1" prop="reason" label="拒绝原因" min-width="180">
          <template #default="scope">
            <el-tag type="danger" effect="plain">{{ scope.row.reason }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="email" label="站长邮箱" width="180" show-overflow-tooltip />

        <el-table-column prop="updateTime" label="更新时间" width="170" sortable />

        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <div class="action-group">
              <template v-if="scope.row.status === 1">
                <el-button type="primary" link :icon="Edit" @click="handleEdit(scope.row)">编辑</el-button>
                <el-button type="danger" link :icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
              </template>

              <template v-else-if="scope.row.status === 0">
                <el-button type="success" link :icon="Check" @click="handlePass(scope.row)">通过</el-button>
                <el-button type="danger" link :icon="Close" @click="handleRejectModal(scope.row)">拒绝</el-button>
              </template>

              <template v-else>
                <el-button type="primary" link :icon="Edit" @click="handleEdit(scope.row)">修改</el-button>
                <el-button type="danger" link :icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
              </template>
            </div>
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

    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" @close="resetForm" center draggable>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" status-icon>
        <el-form-item label="ID" prop="id">
          <el-input 
            v-model.number="form.id" 
            placeholder="自动生成" 
            :disabled="dialog.type === 'edit'"
          >
             <template #append v-if="dialog.type === 'add'">可选</template>
          </el-input>
        </el-form-item>
        <el-form-item label="博客名" prop="blogName">
          <el-input v-model="form.blogName" placeholder="请输入博客名称" />
        </el-form-item>
        <el-form-item label="博客地址" prop="blogAddress">
          <el-input v-model="form.blogAddress" placeholder="请输入地址 (http://...)" />
        </el-form-item>
        <el-form-item label="图片地址" prop="pictureAddress">
          <el-input v-model="form.pictureAddress" placeholder="请输入头像URL" />
        </el-form-item>
        <el-form-item label="描述" prop="blogDescription">
          <el-input type="textarea" v-model="form.blogDescription" placeholder="一句话介绍..." />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="用于接收审核通知" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible = false">取 消</el-button>
        <el-button type="primary" @click="submitForm">保 存</el-button>
      </template>
    </el-dialog>

    <el-dialog title="拒绝申请" v-model="rejectDialog.visible" width="400px" center>
      <el-input
        v-model="rejectDialog.reason"
        type="textarea"
        :rows="4"
        placeholder="请输入拒绝原因..."
      />
      <template #footer>
        <el-button @click="rejectDialog.visible = false">取 消</el-button>
        <el-button type="danger" @click="submitReject">确认拒绝</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { Plus, Edit, Delete, Picture, Check, Close, CircleCheck, CircleClose, Bell } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

// 核心逻辑保持不变
const loading = ref(true)
const list = ref([])
const total = ref(0)
const queryParams = reactive({ pageNum: 1, pageSize: 10, status: 1 })
const dialog = reactive({ visible: false, title: '', type: 'add' })
const formRef = ref(null)
const form = reactive({ id: null, blogName: '', blogAddress: '', pictureAddress: '', blogDescription: '', email: '' })
const rejectDialog = reactive({ visible: false, id: null, reason: '' })

const rules = {
  id: [{ type: 'integer', message: 'ID需为整数', trigger: 'blur', transform: (v) => v === "" ? null : Number(v) }],
  blogName: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  blogAddress: [{ required: true, message: '请输入地址', trigger: 'blur' }, { type: 'url', message: '格式错误', trigger: 'blur' }],
  pictureAddress: [{ required: true, message: '请输入图片地址', trigger: 'blur' }]
}

const getList = async () => {
  loading.value = true
  try {
    const res = await request.post('/admin/friend/list', queryParams)
    list.value = res.data.page.records
    total.value = res.data.page.total
  } finally {
    loading.value = false
  }
}

const handleFilter = () => { queryParams.pageNum = 1; getList() }
const resetForm = () => { Object.keys(form).forEach(k => form[k] = ''); form.id = null; nextTick(() => formRef.value?.clearValidate()) }
const handleCreate = () => { resetForm(); dialog.type = 'add'; dialog.title = '添加友链'; dialog.visible = true }
const handleEdit = (row) => { resetForm(); dialog.type = 'edit'; dialog.title = '编辑友链'; dialog.visible = true; nextTick(() => Object.assign(form, row)) }

const submitForm = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    if (form.id === '') form.id = null
    try {
      if (dialog.type === 'edit') {
        await request.put('/admin/friend/update', form)
        ElMessage.success('更新成功')
      } else {
        await request.post('/admin/friend/save', form)
        ElMessage.success('添加成功')
      }
      dialog.visible = false
      getList()
    } catch (e) { console.error(e) }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该友链吗?', '警告', { type: 'warning' }).then(async () => {
    await request.delete(`/admin/friend/${row.id}`)
    ElMessage.success('删除成功')
    getList()
  }).catch(() => {})
}

const handlePass = (row) => {
  ElMessageBox.confirm(`通过 [${row.blogName}] ?`, '审核', { type: 'success' }).then(async () => {
    await request.put(`/admin/friend/pass/${row.id}`)
    ElMessage.success('已通过')
    getList()
  }).catch(() => {})
}

const handleRejectModal = (row) => { rejectDialog.id = row.id; rejectDialog.reason = ''; rejectDialog.visible = true }
const submitReject = async () => {
  if (!rejectDialog.reason) return ElMessage.warning('请填写原因')
  try {
    await request.put(`/admin/friend/reject/${rejectDialog.id}`, null, { params: { reason: rejectDialog.reason } })
    ElMessage.success('已拒绝')
    rejectDialog.visible = false
    getList()
  } catch (e) { console.error(e) }
}

onMounted(() => { getList() })
</script>

<style scoped>
.app-container { padding: 20px; }

/* 卡片 1：顶部操作栏 */
.filter-container { margin-bottom: 20px; }
.panel-title { font-size: 18px; font-weight: bold; color: #303133; }

/* 卡片 2：表格区域 */
.table-container { 
  /* 去掉固定高度，实现自适应 */
  padding-bottom: 20px; 
}

/* Tabs 样式优化 */
.tabs-wrapper {
  margin-bottom: 20px;
  /* 增加一点底部间距，让 Tabs 和表格不那么挤 */
}

/* 辅助样式 */
.mr-1 { margin-right: 4px; }
.avatar-img { width: 40px; height: 40px; border-radius: 50%; border: 1px solid #ebeef5; }
.image-slot { display: flex; justify-content: center; align-items: center; width: 100%; height: 100%; background: #f5f7fa; color: #909399; }
.link-type { color: #409EFF; text-decoration: none; }
.link-type:hover { text-decoration: underline; }
.blog-name { font-weight: 500; }

.pagination-container { margin-top: 20px; }
.action-group { display: flex; justify-content: center; align-items: center; gap: 8px; }
</style>