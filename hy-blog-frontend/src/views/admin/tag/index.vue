<template>
  <div class="app-container">
    <el-card class="filter-container" shadow="never">
      <el-row justify="space-between" align="middle">
        <div class="filter-left">
          <span class="panel-title">🏷️ 标签管理</span>
        </div>
        <div class="filter-right">
          <el-button type="primary" :icon="Plus" @click="handleCreate">添加标签</el-button>
        </div>
      </el-row>
    </el-card>

    <el-card class="table-container" shadow="never">
      <el-table
        v-loading="loading"
        :data="list"
        border
        stripe
        style="width: 100%"
        :header-cell-style="{ background: '#f5f7fa', color: '#606266', fontWeight: 'bold' }"
      >
        <el-table-column prop="id" label="ID" width="100" align="center" sortable />
        
        <el-table-column prop="name" label="标签名" min-width="150" align="center">
          <template #default="scope">
            <el-tag size="large" effect="plain">{{ scope.row.name }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="创建时间" width="200" align="center">
          <template #default="scope">
            <div class="time-cell">
              <el-icon><Clock /></el-icon>
              <span>{{ scope.row.createTime }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="updateTime" label="更新时间" width="200" align="center">
          <template #default="scope">
            <div class="time-cell">
              <el-icon><Clock /></el-icon>
              <span>{{ scope.row.updateTime }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="scope">
            <el-button type="primary" link :icon="Edit" @click="handleUpdate(scope.row)">
              编辑
            </el-button>
            <el-button type="danger" link :icon="Delete" @click="handleDelete(scope.row)">
              删除
            </el-button>
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

    <el-dialog
      :title="dialog.title"
      v-model="dialog.visible"
      width="500px"
      @close="resetForm"
      center
      draggable 
    >
      <el-form ref="dataFormRef" :model="form" :rules="rules" label-width="80px" status-icon>
        <el-form-item label="ID" prop="id">
          <el-input 
            v-model.number="form.id" 
            placeholder="如果不填，系统将自动生成" 
            :disabled="dialog.type === 'edit'"
          >
             <template #append v-if="dialog.type === 'add'">可选</template>
          </el-input>
        </el-form-item>
        <el-form-item label="标签名" prop="name">
          <el-input v-model="form.name" placeholder="请输入标签名称" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialog.visible = false">取 消</el-button>
          <el-button type="primary" @click="submitForm">保 存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * 后台标签管理页
 * 标签增删改查，ID 新增时可选（不填由后端自动生成）
 */
import { ref, reactive, onMounted, nextTick } from 'vue'
import { Plus, Edit, Delete, Clock } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

// --- 数据定义 ---
const loading = ref(true)
const list = ref([])
const total = ref(0)
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10
})

const dialog = reactive({
  visible: false,
  title: '',
  type: 'add'
})

const dataFormRef = ref(null)
const form = reactive({
  id: null,
  name: ''
})

// 表单校验规则
const rules = {
  id: [
    { type: 'integer', message: 'ID必须为整数', trigger: 'blur', transform: (value) => value === "" ? null : Number(value) }
  ],
  name: [
    { required: true, message: '请输入标签名称', trigger: 'blur' },
    { min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur' }
  ]
}

// --- 方法实现 ---

const getList = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/tag/list', { params: queryParams })
    list.value = res.data.page.records
    total.value = res.data.page.total
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  form.id = null
  form.name = ''
  nextTick(() => {
    dataFormRef.value?.clearValidate()
  })
}

const handleCreate = () => {
  resetForm()
  dialog.type = 'add'
  dialog.title = '添加标签'
  dialog.visible = true
}

const handleUpdate = (row) => {
  resetForm()
  dialog.type = 'edit'
  dialog.title = '编辑标签'
  dialog.visible = true
  nextTick(() => {
    form.id = row.id
    form.name = row.name
  })
}

const submitForm = () => {
  dataFormRef.value.validate(async (valid) => {
    if (!valid) return
    // 提交前处理：如果 id 是空字符串，设为 null 以便后端自动生成
    if (form.id === '') form.id = null

    try {
      if (dialog.type === 'add') {
        await request.post('/admin/tag/save', form)
        ElMessage.success('添加成功')
      } else {
        await request.put('/admin/tag/update', form)
        ElMessage.success('修改成功')
      }
      dialog.visible = false
      getList() 
    } catch (error) {
      console.error(error)
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
    '此操作将永久删除该标签, 是否继续?',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    await request.delete(`/admin/tag/${row.id}`)
    ElMessage.success('删除成功')
    getList()
  }).catch(() => {
    // 取消删除
  })
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.app-container { padding: 20px; }
.filter-container { margin-bottom: 20px; }
.panel-title { font-size: 18px; font-weight: bold; color: #303133; }
.table-container { padding: 10px; }
.pagination-container { margin-top: 20px; }
.time-cell { display: flex; align-items: center; justify-content: center; gap: 5px; color: #606266; }
</style>