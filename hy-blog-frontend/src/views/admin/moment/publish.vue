<template>
  <div class="app-container">
    <el-card class="header-card" shadow="never">
      <el-row justify="space-between" align="middle">
        <div class="header-left">
          <span class="panel-title">{{ isEdit ? '👋 编辑动态' : '👋 发布动态' }}</span>
        </div>
        <div class="header-right">
          <el-button :icon="Back" @click="$router.back()">返回列表</el-button>
        </div>
      </el-row>
    </el-card>

    <el-card class="main-card" shadow="never">
      <el-form :model="form" label-width="80px" class="moment-form">
        <el-row :gutter="30">
          
          <el-col :span="16" :xs="24" class="left-col">
            <el-form-item label="动态内容" label-width="80px" style="margin-bottom: 20px;">
              <el-input
                v-model="form.content"
                type="textarea"
                :rows="8"
                placeholder="记录当下的想法..."
                maxlength="1000"
                show-word-limit
                class="content-input"
              />
            </el-form-item>

            <el-form-item label="动态配图">
              <div class="custom-image-wall">
                <draggable 
                  v-model="fileList" 
                  item-key="uid" 
                  class="draggable-list"
                  animation="200"
                  @end="updateFormImages"
                >
                  <template #item="{ element, index }">
                    <div class="image-item">
                      <img :src="element.url" class="thumbnail" />
                      <div class="image-actions">
                        <span class="action-icon" @click="handlePreview(element)">
                          <el-icon><ZoomIn /></el-icon>
                        </span>
                        <span class="action-icon" @click="handleRemove(index)">
                          <el-icon><Delete /></el-icon>
                        </span>
                      </div>
                    </div>
                  </template>
                </draggable>

                <el-upload
                  class="upload-trigger"
                  action="/api/admin/upload/file"
                  :headers="uploadHeaders"
                  :show-file-list="false" 
                  :on-success="handleUploadSuccess"
                  multiple
                >
                  <div class="upload-box">
                    <el-icon class="upload-icon"><Plus /></el-icon>
                  </div>
                </el-upload>
              </div>
              
              <el-button link type="primary" :icon="Link" @click="addNetworkImage" style="margin-top: 8px;">
                添加网络图片链接
              </el-button>
            </el-form-item>
          </el-col>

          <el-col :span="8" :xs="24" class="right-col">
            <el-form-item label="发布位置">
              <el-input v-model="form.location" placeholder="所在位置">
                <template #append>
                  <el-button :icon="Position" @click="getLocation" :loading="locationLoading">
                    定位
                  </el-button>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item label="外部链接">
              <el-input v-model="form.extraUrl" placeholder="https://... (外部文章或视频链接)" :prefix-icon="Link" />
            </el-form-item>

            <el-form-item label="定时发布">
              <el-date-picker
                v-model="form.publishTime"
                type="datetime"
                placeholder="留空则立即发布"
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width: 100%"
              />
            </el-form-item>

            <el-form-item label="发布设置">
              <div class="setting-group">
                <el-checkbox v-model="form.isPrivate" label="私密动态" border />
                <el-checkbox v-model="form.isTop" label="置顶显示" border />
              </div>
            </el-form-item>

            <div class="tips mt-20">
              <p>💡 提示：</p>
              <p class="tip-text">现在支持拖拽排序啦！上传后按住图片即可调整顺序。</p>
            </div>

            <div class="footer-btn mt-20">
              <el-button 
                type="primary" 
                size="large" 
                @click="submit" 
                :loading="submitting" 
                style="width: 100%; font-weight: bold;"
              >
                {{ isEdit ? '保存修改' : '立即发布' }}
              </el-button>
            </div>
          </el-col>
        </el-row>
      </el-form>
      
      <el-dialog v-model="dialogVisible" title="图片预览" width="50%">
        <img :src="dialogImageUrl" alt="Preview Image" style="width: 100%; border-radius: 8px;" />
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
/**
 * 动态发布/编辑页
 * 支持图片拖拽排序（vuedraggable）、定位获取、定时发布、私密/置顶设置
 */
import { ref, reactive, computed, onMounted } from 'vue'
import { Plus, Position, ZoomIn, Delete, Back, Link } from '@element-plus/icons-vue' 
import { useRoute, useRouter } from 'vue-router'
import { saveMoment, getMomentDetail, updateMoment } from '@/api/moment'
import { ElMessage, ElMessageBox } from 'element-plus'
import draggable from 'vuedraggable' 

const route = useRoute()
const router = useRouter()
const isEdit = computed(() => !!route.params.id)
const submitting = ref(false)
const locationLoading = ref(false)

const dialogImageUrl = ref('')
const dialogVisible = ref(false)

const form = reactive({
  id: null,
  content: '',
  images: '',
  location: '',
  extraUrl: '',
  publishTime: '',
  isPrivate: false,
  isTop: false
})

const fileList = ref([]) // 结构: { uid: 1, url: '...' }
const uploadHeaders = computed(() => ({ satoken: localStorage.getItem('token') }))

// === 图片上传成功 ===
const handleUploadSuccess = (res) => {
  if (res.code === 20000 || res.code === 200 || res.success) {
    const realUrl = res.data.url || res.data; 
    // 手动推入 fileList
    fileList.value.push({
      uid: Date.now() + Math.random(), // 生成唯一key
      url: realUrl
    })
    updateFormImages();
    ElMessage.success('上传成功');
  } else {
    ElMessage.error(res.message || '上传失败');
  }
}

// === 移除图片 ===
const handleRemove = (index) => {
  fileList.value.splice(index, 1);
  updateFormImages();
}

const handlePreview = (element) => {
  dialogImageUrl.value = element.url;
  dialogVisible.value = true;
}

const addNetworkImage = () => {
  ElMessageBox.prompt('请输入图片URL', '网络图片', {
    inputPattern: /^https?:\/\/.+/,
    inputErrorMessage: '格式不正确'
  }).then(({ value }) => {
    fileList.value.push({ uid: Date.now(), url: value });
    updateFormImages();
  }).catch(() => {})
}

// 每次列表变动（包括拖拽结束），同步给 form.images
const updateFormImages = () => {
  const urls = fileList.value.map(f => f.url).filter(Boolean);
  form.images = urls.join(',');
}

// === 定位 ===
const getLocation = () => {
  locationLoading.value = true
  fetch('https://ipapi.co/json/')
    .then(res => res.json())
    .then(data => {
      form.location = data.city ? `${data.city}, ${data.country_name}` : '未知位置'
      ElMessage.success(`定位成功: ${form.location}`)
    })
    .catch(() => {
      form.location = '地球'
      ElMessage.warning('定位服务连接超时，请手动输入')
    })
    .finally(() => locationLoading.value = false)
}

const initData = async () => {
  if (isEdit.value) {
    const res = await getMomentDetail(route.params.id)
    const data = res.data.data || res.data 
    Object.assign(form, data)
    
    if (form.images) {
      // 初始化 fileList，赋予 uid 以便拖拽使用
      fileList.value = form.images.split(',').map((url, i) => ({ 
        uid: i, 
        url: url 
      }))
    }
  }
}

const submit = async () => {
  updateFormImages(); // 提交前再次确认
  if (!form.content && !form.images) {
    return ElMessage.warning('内容和图片不能同时为空');
  }
  
  submitting.value = true;
  try {
    if (isEdit.value) {
      await updateMoment(form);
      ElMessage.success('修改成功');
    } else {
      await saveMoment(form);
      ElMessage.success('发布成功');
    }
    router.push('/admin/content/moment');
  } finally {
    submitting.value = false;
  }
}

onMounted(() => initData())
</script>

<style scoped>
.app-container { padding: 20px; }
.header-card { margin-bottom: 20px; }
.panel-title { font-size: 18px; font-weight: bold; color: #303133; }
.moment-form { margin-top: 10px; }

/* === 核心：自定义图片墙样式 (仿 Element Upload) === */
.custom-image-wall {
  display: flex;
  flex-wrap: wrap;
  gap: 8px; /* 图片之间的间距 */
}

.draggable-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.image-item {
  width: 148px;
  height: 148px;
  border-radius: 6px;
  border: 1px solid #cdd0d6;
  position: relative;
  overflow: hidden;
  background-color: #fff;
  cursor: grab; /* 抓手光标 */
}
.image-item:active { cursor: grabbing; }

.thumbnail {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 悬停遮罩 */
.image-actions {
  position: absolute;
  width: 100%;
  height: 100%;
  left: 0;
  top: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
  opacity: 0;
  transition: opacity 0.3s;
}
.image-item:hover .image-actions {
  opacity: 1;
}
.action-icon {
  color: #fff;
  font-size: 20px;
  cursor: pointer;
}
.action-icon:hover { color: #409EFF; }

/* 上传按钮样式 */
.upload-box {
  width: 148px;
  height: 148px;
  background-color: #fbfdff;
  border: 1px dashed #c0ccda;
  border-radius: 6px;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
}
.upload-box:hover {
  border-color: #409EFF;
  color: #409EFF;
}
.upload-icon {
  font-size: 28px;
  color: #8c939d;
}

/* 左栏样式 */
.left-col {
  border-right: 1px solid #f0f0f0;
  padding-right: 30px !important;
}
.content-input :deep(.el-textarea__inner) {
  background-color: #f9f9f9;
  border-radius: 8px;
  padding: 15px;
  font-size: 15px;
  box-shadow: none;
  border: 1px solid #eee;
}
.content-input :deep(.el-textarea__inner):focus {
  background-color: #fff;
  border-color: #409EFF;
}

/* 右栏样式 */
.right-col { padding-left: 10px !important; }
.tips {
  color: #999;
  font-size: 13px;
  background: #fdfdfd;
  padding: 15px;
  border-radius: 8px;
  border: 1px dashed #eee;
}
.tip-text { margin-top: 5px; }
.setting-group { display: flex; flex-direction: column; gap: 10px; }
.mt-20 { margin-top: 20px; }
.footer-btn { text-align: right; }
</style>
