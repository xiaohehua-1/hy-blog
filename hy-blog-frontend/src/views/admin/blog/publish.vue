<template>
  <div class="app-container">
    
    <el-card class="header-card" shadow="never">
      <el-row justify="space-between" align="middle">
        <div class="header-left">
          <span class="panel-title">{{ form.id ? '✍️ 编辑文章' : '✍️ 发布文章' }}</span>
        </div>
        <div class="header-right">
          <el-button :icon="Back" @click="$router.back()">返回</el-button>
        </div>
      </el-row>
    </el-card>

    <el-card class="main-card" shadow="never">
      <el-form :model="form" label-width="80px">
        
        <el-row :gutter="20" class="mb-20">
          <el-col :span="4">
            <el-select v-model="form.copyright" placeholder="版权类型" style="width: 100%">
              <el-option label="原创" :value="1" />
              <el-option label="转载" :value="2" />
              <el-option label="翻译" :value="3" />
            </el-select>
          </el-col>
          <el-col :span="20">
            <el-input 
              v-model="form.title" 
              placeholder="请输入文章标题" 
              maxlength="100" 
              show-word-limit 
            />
          </el-col>
        </el-row>

        <div class="mb-20 editor-container">
          <v-md-editor v-model="form.content" height="600px" placeholder="开始编写你的博客..." />
        </div>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="分类">
              <el-select v-model="form.typeId" placeholder="请选择分类" style="width: 100%">
                <el-option v-for="item in typeList" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="标签">
              <el-select v-model="form.tagIds" multiple placeholder="请选择标签" style="width: 100%">
                <el-option v-for="item in tagList" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>

            <el-form-item label="首图地址">
              <el-input v-model="form.firstPicture" placeholder="请输入图片URL (建议 800x450)" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="文章描述">
              <el-input type="textarea" v-model="form.description" :rows="5" placeholder="请输入文章摘要..." maxlength="200" show-word-limit />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row class="mb-20 checkbox-group">
          <el-col :span="24" style="padding-left: 80px;"> <el-checkbox v-model="form.recommend" label="推荐" border />
            <el-checkbox v-model="form.shareStatement" label="转载声明" border />
            <el-checkbox v-model="form.commentabled" label="允许评论" border />
            <el-checkbox v-model="form.appreciation" label="开启赞赏" border />
          </el-col>
        </el-row>

        <div class="footer-btn">
          <el-button type="warning" @click="handleSubmit(false)">存为草稿</el-button>
          <el-button type="primary" @click="handleSubmit(true)">发布文章</el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { Back } from '@element-plus/icons-vue' // 引入返回图标

const route = useRoute()
const router = useRouter()

const typeList = ref([])
const tagList = ref([])

const form = reactive({
  id: null,
  title: '',
  content: '',
  firstPicture: '',
  description: '',
  typeId: null,
  tagIds: [],
  copyright: 1, 
  recommend: false,
  shareStatement: true,
  commentabled: true,
  appreciation: false,
  published: false
})

const initData = async () => {
  const resType = await request.get('/admin/type/listAll')
  typeList.value = resType.data.list
  
  const resTag = await request.get('/admin/tag/listAll')
  tagList.value = resTag.data.list
}

const handleSubmit = async (isPublish) => {
  if (!form.title) return ElMessage.warning('标题不能为空')
  if (!form.content) return ElMessage.warning('内容不能为空')
  if (!form.typeId) return ElMessage.warning('请选择分类')

  form.published = isPublish

  try {
    if (form.id) {
      await request.put('/admin/blog/update', form)
      ElMessage.success(isPublish ? '发布成功' : '草稿保存成功')
    } else {
      await request.post('/admin/blog/save', form)
      ElMessage.success(isPublish ? '发布成功' : '草稿保存成功')
    }
    router.push('/admin/content/article') 
  } catch (e) {
    console.error(e)
  }
}

const getDetail = async (id) => {
  const res = await request.get(`/admin/blog/${id}`)
  const data = res.data.blog
  const tags = res.data.tagIds || []
  Object.assign(form, data)
  form.tagIds = tags
}

onMounted(() => {
  initData()
  if (route.params.id) {
    getDetail(route.params.id)
  }
})
</script>

<style scoped>
.app-container { padding: 20px; }

/* 🟢 关键样式：两个卡片之间的间距 */
.header-card { margin-bottom: 20px; }

/* 保持和列表页一致的标题字体 */
.panel-title { font-size: 18px; font-weight: bold; color: #303133; }

.mb-20 { margin-bottom: 20px; }
.editor-container { border: 1px solid #dcdfe6; border-radius: 4px; }
.checkbox-group { margin-top: 10px; }
.footer-btn { text-align: right; margin-top: 20px; }
</style>