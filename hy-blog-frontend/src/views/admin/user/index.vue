<template>
  <div class="app-container">
    <el-card class="filter-container" shadow="never">
      <el-row justify="space-between" align="middle">
        <div class="filter-left">
          <span class="panel-title">👤 用户管理</span>
        </div>
        <div class="filter-right">
          <el-button type="primary" :icon="Plus" @click="handleCreate">添加用户</el-button>
        </div>
      </el-row>
    </el-card>

    <el-card class="table-container" shadow="never">
       <div class="search-wrapper">
        <el-form :inline="true" :model="queryParams" class="search-form">
          <el-form-item label="账号">
            <el-input v-model="queryParams.username" placeholder="请输入账号" clearable @keyup.enter="handleSearch" />
          </el-form-item>
          <el-form-item label="昵称">
            <el-input v-model="queryParams.nickname" placeholder="请输入昵称" clearable @keyup.enter="handleSearch" />
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
        :header-cell-style="{ background: '#f5f7fa', color: '#606266', fontWeight: 'bold', textAlign: 'center' }"
        :cell-style="{ textAlign: 'center' }"
      >
        <el-table-column prop="id" label="ID" width="80" sortable />
        
        <el-table-column label="头像" width="100">
          <template #default="scope">
            <el-avatar :size="40" :src="scope.row.avatar" />
          </template>
        </el-table-column>

        <el-table-column prop="username" label="账号" min-width="120" />
        <el-table-column prop="nickname" label="昵称" min-width="120" />
        <el-table-column prop="email" label="邮箱" min-width="180" show-overflow-tooltip />
        
        <el-table-column prop="createTime" label="创建时间" width="170" sortable />
        <el-table-column prop="updateTime" label="更新时间" width="170" sortable />

        <el-table-column label="操作" width="250" fixed="right">
          <template #default="scope">
            <el-button type="primary" link :icon="Edit" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="warning" link :icon="Key" @click="handleResetPwd(scope.row)">修改密码</el-button>
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

    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" @close="resetForm" center draggable>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" status-icon>
        <el-form-item label="账号" prop="username">
          <el-input v-model="form.username" placeholder="登录账号" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="dialog.type === 'add'">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="前台显示的名称" />
        </el-form-item>
        <el-form-item label="头像" prop="avatar">
          <el-upload
            class="avatar-uploader"
            action="/api/admin/upload/file"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :on-error="handleAvatarError"
          >
            <img v-if="form.avatar" :src="form.avatar" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="Email" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible = false">取 消</el-button>
        <el-button type="primary" @click="submitForm">保 存</el-button>
      </template>
    </el-dialog>

    <el-dialog title="修改密码" v-model="pwdDialog.visible" width="400px" center>
      <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="80px">
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input v-model="pwdForm.oldPassword" type="password" show-password placeholder="请输入旧密码" />
        </el-form-item>
        
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="pwdForm.newPassword" type="password" show-password placeholder="请输入新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pwdDialog.visible = false">取 消</el-button>
        <el-button type="primary" @click="submitResetPwd">确 定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, computed } from 'vue'
import { Plus, Edit, Delete, Search, Refresh, Key } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

// ... 省略基础数据定义 (list, queryParams等，保持不变) ...
const loading = ref(true)
const list = ref([])
const total = ref(0)
const queryParams = reactive({ pageNum: 1, pageSize: 10, username: '', nickname: '' })
const dialog = reactive({ visible: false, title: '', type: 'add' })
const pwdDialog = reactive({ visible: false, id: null })
const formRef = ref(null)
const pwdFormRef = ref(null)
const form = reactive({ id: null, username: '', password: '', nickname: '', avatar: '', email: '' })

// 核心修改：密码表单数据
const pwdForm = reactive({ 
  oldPassword: '', 
  newPassword: '' 
})

const uploadHeaders = computed(() => ({ satoken: localStorage.getItem('token') }))

const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, message: '最少6位', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }]
}

// 核心修改：密码校验规则
const pwdRules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }, { min: 6, message: '最少6位', trigger: 'blur' }]
}

// ... 省略 getList, handleSearch, resetQuery, resetForm, handleCreate, handleEdit, submitForm, handleAvatarSuccess 等逻辑 (保持不变) ...
const getList = async () => {
  loading.value = true
  try {
    const res = await request.post('/admin/user/list', queryParams)
    list.value = res.data.page.records
    total.value = res.data.page.total
  } finally {
    loading.value = false
  }
}
const handleSearch = () => { queryParams.pageNum = 1; getList() }
const resetQuery = () => { queryParams.username = ''; queryParams.nickname = ''; handleSearch() }
const resetForm = () => { Object.keys(form).forEach(k => form[k] = ''); form.id = null; nextTick(() => formRef.value?.clearValidate()) }
const handleCreate = () => { resetForm(); dialog.title = '添加用户'; dialog.type = 'add'; dialog.visible = true }
const handleEdit = (row) => { resetForm(); dialog.title = '编辑资料'; dialog.type = 'edit'; dialog.visible = true; nextTick(() => { Object.assign(form, row); form.password = '' }) }
const submitForm = () => { formRef.value.validate(async (valid) => { if (!valid) return; try { if (dialog.type === 'edit') { await request.put('/admin/user/update', form); ElMessage.success('更新成功') } else { await request.post('/admin/user/save', form); ElMessage.success('创建成功') } dialog.visible = false; getList() } catch (e) { console.error(e) } }) }
const handleAvatarSuccess = (res) => { if (res.code === 20000) { form.avatar = res.data.url; ElMessage.success('上传成功') } else { ElMessage.error(res.message) } }
const handleAvatarError = () => ElMessage.error('上传失败')
const handleDelete = (row) => { ElMessageBox.confirm('确认删除该用户吗?', '警告', { type: 'warning' }).then(async () => { await request.delete(`/admin/user/${row.id}`); ElMessage.success('删除成功'); getList() }).catch(() => {}) }

// 打开修改密码
const handleResetPwd = (row) => {
  pwdDialog.id = row.id
  pwdForm.oldPassword = ''
  pwdForm.newPassword = ''
  pwdDialog.visible = true
  nextTick(() => pwdFormRef.value?.clearValidate())
}

// 提交修改密码 (核心修改)
const submitResetPwd = () => {
  pwdFormRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      await request.put('/admin/user/password', {
        id: pwdDialog.id,
        oldPassword: pwdForm.oldPassword,
        newPassword: pwdForm.newPassword
      })
      ElMessage.success('密码修改成功')
      pwdDialog.visible = false
    } catch (e) { console.error(e) }
  })
}

onMounted(() => { getList() })
</script>

<style scoped>
.app-container { padding: 20px; }
.filter-container { margin-bottom: 20px; }
.search-wrapper { margin-bottom: 15px; border-bottom: 1px dashed #e4e7ed; padding-bottom: 15px; }
.panel-title { font-size: 18px; font-weight: bold; color: #303133; }
.pagination-container { margin-top: 20px; }
/* 头像上传样式 (保持不变) */
.avatar-uploader .el-upload { border: 1px dashed var(--el-border-color); border-radius: 6px; cursor: pointer; position: relative; overflow: hidden; transition: var(--el-transition-duration-fast); }
.avatar-uploader .el-upload:hover { border-color: var(--el-color-primary); }
.avatar-uploader-icon { font-size: 28px; color: #8c939d; width: 100px; height: 100px; text-align: center; line-height: 100px; }
.avatar { width: 100px; height: 100px; display: block; object-fit: cover; }
</style>