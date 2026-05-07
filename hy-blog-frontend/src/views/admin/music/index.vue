<template>
  <div class="app-container">
    <el-card class="filter-container" shadow="never">
      <el-row justify="space-between" align="middle">
        <div class="filter-left">
          <span class="panel-title">🎵 音乐管理</span>
        </div>
        <div class="filter-right">
          <el-button type="primary" :icon="Plus" @click="handleCreate">添加音乐</el-button>
        </div>
      </el-row>
    </el-card>

    <el-card class="table-container" shadow="never">
      <div class="search-wrapper">
        <el-form :inline="true" :model="queryParams" class="search-form">
          <el-form-item label="歌名">
            <el-input v-model="queryParams.title" placeholder="请输入歌名" clearable @keyup.enter="handleSearch" />
          </el-form-item>
          <el-form-item label="歌手">
            <el-input v-model="queryParams.artist" placeholder="请输入歌手" clearable @keyup.enter="handleSearch" />
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
        
        <el-table-column label="封面" width="100">
          <template #default="scope">
            <el-image 
              class="cover-img"
              :src="scope.row.coverPath" 
              :preview-src-list="[scope.row.coverPath]"
              fit="cover"
              preview-teleported
              loading="lazy"
            >
              <template #error>
                <div class="image-slot"><el-icon><Picture /></el-icon></div>
              </template>
            </el-image>
          </template>
        </el-table-column>

        <el-table-column prop="title" label="歌名" min-width="150" show-overflow-tooltip />
        <el-table-column prop="artist" label="歌手" min-width="150" show-overflow-tooltip />

        <el-table-column label="试听" width="320">
          <template #default="scope">
            <audio 
              :src="scope.row.filePath" 
              controls 
              preload="metadata"
              class="audio-player"
            ></audio>
          </template>
        </el-table-column>

        <el-table-column label="启用" align="center" width="100">
          <template #default="scope">
            <el-switch
              v-model="scope.row.enabled"
              :active-value="true"
              :inactive-value="false"
              active-color="#13ce66"
              inactive-color="#ff4949"
              @change="handleStatusChange(scope.row)"
            />
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="创建时间" width="170" sortable />
        
        <el-table-column label="操作" width="180" fixed="right">
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

    <el-dialog :title="dialog.title" v-model="dialog.visible" width="550px" @close="resetForm" center draggable>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" status-icon>
        
        <el-form-item label="封面" prop="coverPath">
          <el-upload
            class="cover-uploader"
            action="/api/admin/upload/file"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleCoverSuccess"
            :on-error="handleUploadError"
            accept=".jpg,.jpeg,.png"
          >
            <img v-if="form.coverPath" :src="form.coverPath" class="cover-preview" />
            <el-icon v-else class="cover-uploader-icon"><Plus /></el-icon>
            <template #tip>
              <div class="el-upload__tip">只能上传jpg/png文件，且不超过2MB</div>
            </template>
          </el-upload>
        </el-form-item>

        <el-form-item label="歌名" prop="title">
          <el-input v-model="form.title" placeholder="请输入歌名" />
        </el-form-item>

        <el-form-item label="歌手" prop="artist">
          <el-input v-model="form.artist" placeholder="请输入歌手名" />
        </el-form-item>

        <el-form-item label="状态" prop="enabled">
          <el-radio-group v-model="form.enabled">
            <el-radio :label="true">启用</el-radio>
            <el-radio :label="false">禁用</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="文件" prop="filePath">
          <el-upload
            class="upload-demo"
            action="/api/admin/upload/file" 
            :headers="uploadHeaders"
            :on-success="handleMusicSuccess" 
            :on-error="handleUploadError"
            :limit="1"
            :show-file-list="false"
            accept=".mp3,.flac,.wav"
          >
            <el-button type="primary" :icon="Upload">选取音乐文件</el-button>
            <div class="upload-tip" v-if="form.filePath">
              <el-icon style="vertical-align: middle; margin-right: 5px"><Headset /></el-icon>
              已上传: {{ form.fileName || '未知文件名' }}
            </div>
          </el-upload>
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
 * 后台音乐管理页
 * 歌名/歌手模糊搜索，封面+音乐文件上传，播放器试听，启用开关即时切换
 */
import { ref, reactive, onMounted, nextTick, computed } from 'vue'
import { Plus, Edit, Delete, Search, Refresh, Picture, Upload, Headset } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const loading = ref(true)
const list = ref([])
const total = ref(0)
const queryParams = reactive({ pageNum: 1, pageSize: 10, title: '', artist: '' })

const dialog = reactive({ visible: false, title: '', type: 'add' })
const formRef = ref(null)
const form = reactive({
  id: null,
  title: '',
  artist: '',
  fileName: '',
  filePath: '',
  coverPath: '',
  enabled: true
})

// 计算 Token (防空处理)
const uploadHeaders = computed(() => {
  const token = localStorage.getItem('token')
  return token ? { satoken: token } : {}
})

const rules = {
  title: [{ required: true, message: '请输入歌名', trigger: 'blur' }],
  artist: [{ required: true, message: '请输入歌手', trigger: 'blur' }],
  filePath: [{ required: true, message: '请上传音乐文件', trigger: 'change' }],
  coverPath: [{ required: true, message: '请上传封面图片', trigger: 'change' }]
}

const getList = async () => {
  loading.value = true
  try {
    const res = await request.post('/admin/music/list', queryParams)
    // 兼容不同的后端返回结构
    if (res.data && res.data.page) {
       list.value = res.data.page.records
       total.value = res.data.page.total
    } else {
       list.value = []
       total.value = 0
    }
  } catch(e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => { queryParams.pageNum = 1; getList() }
const resetQuery = () => { queryParams.title = ''; queryParams.artist = ''; handleSearch() }

const handleStatusChange = async (row) => {
  try {
    const data = { id: row.id, enabled: row.enabled }
    const res = await request.put('/admin/music/update', data)
    if (res.code === 20000 || res.success) {
      ElMessage.success('状态更新成功')
    } else {
      row.enabled = !row.enabled
      ElMessage.error(res.message || '更新失败')
    }
  } catch (error) {
    row.enabled = !row.enabled
    ElMessage.error('系统异常')
  }
}

const resetForm = () => {
  Object.keys(form).forEach(k => {
    if (k === 'enabled') form[k] = true
    else form[k] = ''
  })
  form.id = null
  nextTick(() => formRef.value?.clearValidate())
}

const handleCreate = () => { resetForm(); dialog.type = 'add'; dialog.title = '添加音乐'; dialog.visible = true }
const handleEdit = (row) => { 
  resetForm()
  dialog.type = 'edit'
  dialog.title = '编辑音乐'
  dialog.visible = true
  nextTick(() => {
    Object.assign(form, row)
    // 确保 enabled 字段存在
    if (form.enabled === undefined || form.enabled === null) form.enabled = true
  })
}

// === 音乐文件上传成功 ===
const handleMusicSuccess = (res) => {
  if (res.code === 20000) {
    // 假设后端返回 data: { url: '...', fileName: '...' }
    form.filePath = res.data.url
    form.fileName = res.data.fileName || res.data.originalFilename
    ElMessage.success('音乐上传成功')
    formRef.value.clearValidate('filePath')
  } else {
    ElMessage.error(res.message || '上传失败')
  }
}

// === 封面图片上传成功 ===
const handleCoverSuccess = (res) => {
  if (res.code === 20000) {
    form.coverPath = res.data.url
    ElMessage.success('封面上传成功')
    formRef.value.clearValidate('coverPath')
  } else {
    ElMessage.error(res.message || '上传失败')
  }
}

const handleUploadError = () => {
  ElMessage.error('上传出错，请检查网络或文件大小')
}

const submitForm = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      if (dialog.type === 'edit') {
        await request.put('/admin/music/update', form)
        ElMessage.success('更新成功')
      } else {
        await request.post('/admin/music/save', form)
        ElMessage.success('添加成功')
      }
      dialog.visible = false
      getList()
    } catch (e) { console.error(e) }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该音乐吗?', '警告', { type: 'warning' }).then(async () => {
    await request.delete(`/admin/music/${row.id}`)
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
.search-form .el-form-item { margin-bottom: 10px; margin-right: 15px; }

/* 列表图片 */
.cover-img { width: 50px; height: 50px; border-radius: 4px; border: 1px solid #ebeef5; }
.image-slot { display: flex; justify-content: center; align-items: center; width: 100%; height: 100%; background: #f5f7fa; color: #909399; }

.audio-player { height: 40px; width: 100%; display: block; }
.pagination-container { margin-top: 20px; }
.upload-tip { margin-top: 8px; font-size: 13px; color: #409EFF; display: flex; align-items: center; }

/* === 核心修复：封面上传组件样式 === */

/* 1. 使用 :deep 穿透到 Element Plus 内部的 el-upload 容器 */
:deep(.cover-uploader .el-upload) {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  /* 强制设置宽高 */
  width: 100px;
  height: 100px;
  /* 强制 Flex 居中 */
  display: flex;
  justify-content: center;
  align-items: center;
  transition: var(--el-transition-duration-fast);
}

/* 2. 鼠标悬停变色 */
:deep(.cover-uploader .el-upload:hover) {
  border-color: #409EFF;
}

/* 3. 图标样式调整 */
:deep(.cover-uploader-icon) {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  /* 确保图标本身的内容也居中 */
  line-height: 100px;
  text-align: center;
}

/* 4. 图片预览样式 */
.cover-preview {
  width: 100px;
  height: 100px;
  display: block;
  object-fit: cover;
}
</style>