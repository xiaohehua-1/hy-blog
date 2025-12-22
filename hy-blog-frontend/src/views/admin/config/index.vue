<template>
  <div class="app-container">
    <el-card class="filter-container" shadow="never">
      <el-row justify="space-between" align="middle">
        <div class="filter-left">
          <span class="panel-title">⚙️ 系统配置管理</span>
        </div>
        <div class="filter-right">
          </div>
      </el-row>
    </el-card>

    <el-card class="content-container" shadow="never">
      <el-tabs v-model="activeTab" class="demo-tabs">
        
        <el-tab-pane label="系统配置" name="first">
          <el-form :model="form" label-width="120px" class="config-form">
            
            <el-divider content-position="left">基础信息</el-divider>
            <el-form-item label="站长名称">
              <el-input v-model="form.author" placeholder="显示在前台左上角" style="width: 300px" />
            </el-form-item>

            <el-divider content-position="left">站点概览 (Sidebar)</el-divider>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="个人简介">
                  <el-input v-model="form.siteProfile" type="textarea" :rows="3" placeholder="支持HTML标签，例如: <br>换行" />
                </el-form-item>
                <el-form-item label="位置">
                  <el-input v-model="form.siteLocation" placeholder="例如: 中国·上海" />
                </el-form-item>
                <el-form-item label="邮箱">
                  <el-input v-model="form.siteEmail" placeholder="Email地址" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="QQ">
                  <el-input v-model="form.siteQq" placeholder="QQ号码" />
                </el-form-item>
                <el-form-item label="微信">
                  <el-input v-model="form.siteWechat" placeholder="微信号" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-divider content-position="left">社交链接 (新)</el-divider>
            <el-row :gutter="20">
              <el-col :span="8">
                <el-form-item label="GitHub">
                  <el-input v-model="form.siteGithub" placeholder="https://github.com/..." />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="Bilibili">
                  <el-input v-model="form.siteBilibili" placeholder="B站主页链接" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="CSDN">
                  <el-input v-model="form.siteCsdn" placeholder="CSDN主页链接" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-divider content-position="left">关于我 (About Me)</el-divider>
            <el-form-item label="自我介绍">
              <el-input v-model="form.aboutMeIntroduction" type="textarea" :rows="3" placeholder="简单的自我介绍..." />
            </el-form-item>
            
            <el-form-item label="详细内容">
              <el-input v-model="form.aboutMeContent" type="textarea" :rows="5" placeholder="详细介绍，支持 HTML 标签" />
            </el-form-item>

            <el-form-item label="我的技能">
              <div class="skill-tags">
                <el-tag
                  v-for="(tag, index) in dynamicSkills"
                  :key="index"
                  closable
                  :disable-transitions="false"
                  @close="handleCloseSkill(tag)"
                  size="large"
                  class="mx-1"
                >
                  {{ tag }}
                </el-tag>
                <el-input
                  v-if="inputVisible"
                  ref="InputRef"
                  v-model="inputValue"
                  class="input-new-tag"
                  size="default"
                  @keyup.enter="handleInputConfirm"
                  @blur="handleInputConfirm"
                />
                <el-button v-else class="button-new-tag" size="default" @click="showInput">
                  + 新增技能
                </el-button>
                <div class="tips">输入技能名称后按回车确认</div>
              </div>
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="onSubmit" :loading="loading" style="width: 150px; margin-top: 20px;">保 存 配 置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="CSDN配置" name="second">
          <el-form :model="form" label-width="120px" class="config-form" style="max-width: 600px;">
            <el-alert title="说明：此Session用于同步CSDN文章，请定期更新防止过期。" type="info" show-icon :closable="false" style="margin-bottom: 20px;" />
            
            <el-form-item label="CSDN Session">
              <el-input v-model="form.csdnSession" placeholder="请输入 CSDN 的 Cookie Session" type="textarea" :rows="4" />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="onSubmit" :loading="loading">保 存 CSDN 配 置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const activeTab = ref('first')
const loading = ref(false)

// 动态技能标签逻辑
const inputValue = ref('')
const dynamicSkills = ref([]) // 前端展示用的数组
const inputVisible = ref(false)
const InputRef = ref(null)

const form = reactive({
  id: null,
  author: '',
  siteProfile: '',
  siteLocation: '',
  siteEmail: '',
  siteQq: '',
  siteWechat: '',
  siteBilibili: '',
  siteGithub: '',
  siteCsdn: '',
  aboutMeIntroduction: '',
  aboutMeContent: '',
  aboutMeSkill: '', // 后端存储的是 JSON 字符串
  csdnSession: ''
})

// 获取配置
const getConfig = async () => {
  try {
    const res = await request.get('/admin/sys/config')
    Object.assign(form, res.data.config)
    
    // 解析技能 JSON -> Array
    if (form.aboutMeSkill) {
      try {
        dynamicSkills.value = JSON.parse(form.aboutMeSkill)
      } catch (e) {
        // 兼容旧数据或解析失败的情况
        dynamicSkills.value = form.aboutMeSkill.split(',')
      }
    } else {
      dynamicSkills.value = []
    }
  } catch (e) {
    console.error(e)
  }
}

// 提交保存
const onSubmit = async () => {
  loading.value = true
  
  // 序列化技能 Array -> JSON String
  form.aboutMeSkill = JSON.stringify(dynamicSkills.value)

  try {
    await request.put('/admin/sys/config', form)
    ElMessage.success('配置已更新')
    // 重新获取一下以确保同步
    getConfig()
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

// --- 标签输入处理 ---
const handleCloseSkill = (tag) => {
  dynamicSkills.value.splice(dynamicSkills.value.indexOf(tag), 1)
}

const showInput = () => {
  inputVisible.value = true
  nextTick(() => {
    InputRef.value.input.focus()
  })
}

const handleInputConfirm = () => {
  if (inputValue.value) {
    if (!dynamicSkills.value.includes(inputValue.value)) {
      dynamicSkills.value.push(inputValue.value)
    } else {
      ElMessage.warning('该技能已存在')
    }
  }
  inputVisible.value = false
  inputValue.value = ''
}

onMounted(() => {
  getConfig()
})
</script>

<style scoped>
.app-container { padding: 20px; }
.filter-container { margin-bottom: 20px; }
.panel-title { font-size: 18px; font-weight: bold; color: #303133; }

.config-form {
  padding: 20px 0;
  max-width: 1000px; /* 限制最大宽度，防止在大屏上太散 */
}

/* 标签输入样式 */
.mx-1 { margin-right: 10px; margin-bottom: 5px; }
.input-new-tag { width: 120px; margin-right: 10px; vertical-align: bottom; }
.button-new-tag { margin-right: 10px; vertical-align: bottom; }
.tips { font-size: 12px; color: #999; margin-top: 5px; }
</style>