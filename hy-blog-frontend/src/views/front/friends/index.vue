<template>
  <div class="friend-container">
    
    <div class="content-bg"></div>
    <div class="nav-placeholder"></div>

    <div class="main-content">
      <el-row justify="center">
        <el-col :span="14" :xs="22">
          
          <div class="friend-header mb-5">
            <div class="sub-title">
              <span class="highlight">不妨</span> , 留下你的痕迹🎞
            </div>
            <h1 class="main-title">让我们，交个<span class="highlight">朋友</span></h1>
          </div>

          <el-row :gutter="40" class="apply-section row-stretch">
            
            <el-col :md="10" :xs="24" class="col-stretch">
              <div class="info-block h-100">
                <h4 class="info-title mt-0">申请友链网站要求</h4>
                <div class="requirements">
                  <p>🚫 无色情内容，无政治敏感内容，网站要能长期正常访问</p>
                  <p>📅 一个月内有新文章更新</p>
                  <p>🤝 原创博客、技术博客、游记博客优先</p>
                  <p>💖 先友后链，需要交换友链，先把本站添加到你的网站中，然后根据下面的格式，给我发email或在留言板给我留言~</p>
                </div>

                <h4 class="info-title mt-4">👋申请格式</h4>
                
                <div class="copy-list">
                  <div class="copy-item">
                    <span>博客标题：<span id="blogTitle">HeYi的客栈</span></span>
                    <el-icon class="copy-btn" @click="copyText('HeYi的客栈')"><CopyDocument /></el-icon>
                  </div>
                  
                  <div class="copy-item">
                    <span>博客地址：<span id="blogAddress">https://www.heyi.space/</span></span>
                    <el-icon class="copy-btn" @click="copyText('https://www.heyi.space/')"><CopyDocument /></el-icon>
                  </div>

                  <div class="copy-item">
                    <span>图片地址：<span id="blogPic">https://www.heyi.space/logo.jpg</span></span>
                    <el-icon class="copy-btn" @click="copyText('https://www.heyi.space/logo.jpg')"><CopyDocument /></el-icon>
                  </div>

                  <div class="copy-item">
                    <span>博客描述：<span>个人博客，分享技术与生活</span></span>
                    <el-icon class="copy-btn" @click="copyText('个人博客，分享技术与生活')"><CopyDocument /></el-icon>
                  </div>
                </div>

                <div class="warning-box mt-auto">
                  <h4 class="info-title text-danger">❌无法访问或单方面取消</h4>
                  <p class="mb-0">不定时排查，长期不更新文章、无法访问或单方面取消，将会移除链接，恕不告知😵</p>
                </div>
              </div>
            </el-col>

            <el-col :md="14" :xs="24" class="col-stretch">
              <div class="form-box h-100">
                <div class="form-item mt-0">
                  <label><el-icon><Edit /></el-icon> 你的博客名称 <span class="required">*</span></label>
                  <input v-model="form.blogName" class="custom-input" type="text">
                </div>
                
                <div class="form-item">
                  <label><el-icon><Link /></el-icon> 你的博客地址 <span class="required">*</span></label>
                  <input v-model="form.blogAddress" class="custom-input" type="text">
                </div>

                <div class="form-item">
                  <label><el-icon><Picture /></el-icon> 你的博客头像 <span class="required">*</span></label>
                  <input v-model="form.pictureAddress" class="custom-input" type="text">
                </div>

                <div class="form-item">
                  <label><el-icon><Document /></el-icon> 你的博客描述 <span class="required">*</span></label>
                  <input v-model="form.blogDescription" class="custom-input" type="text">
                </div>

                <div class="form-item">
                  <label><el-icon><Message /></el-icon> 你的邮箱地址 <span class="note">(审核通过之后会提醒哦)</span></label>
                  <input v-model="form.email" class="custom-input" type="text">
                </div>

                <div class="btn-container">
                  <button class="apply-btn" @click="handleSubmit">
                    <span v-if="!submitting">申请友链</span>
                    <span v-else>提交中...</span>
                  </button>
                </div>
              </div>
            </el-col>

          </el-row>

          <hr class="divider" />

          <div class="friend-list-section">
            <el-row :gutter="20">
              <el-col 
                :md="6" :sm="12" :xs="24"
                v-for="item in friendList" 
                :key="item.id"
                class="mb-4"
              >
                <a :href="item.blogAddress" target="_blank" class="friend-card">
                  <div class="card-status-dot"></div>
                  
                  <div class="card-body">
                    <div class="card-top">
                      <div class="friend-avatar">
                        <img :src="item.pictureAddress" @error="handleImgError" alt="logo">
                      </div>
                      <div class="friend-name text-truncate">{{ item.blogName }}</div>
                    </div>
                    <div class="friend-desc text-truncate-2">
                      {{ item.blogDescription }}
                    </div>
                  </div>
                  
                  <div class="card-dots"></div>
                </a>
              </el-col>
            </el-row>
          </div>

        </el-col>
      </el-row>
    </div>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getFriendList, applyFriendLink } from '@/api/friend'
import { ElMessage } from 'element-plus'
import { CopyDocument, Edit, Link, Picture, Document, Message, StarFilled } from '@element-plus/icons-vue'
import defaultAvatar from '@/assets/images/avatar.png'

const friendList = ref([])
const submitting = ref(false)

const form = reactive({
  blogName: '',
  blogAddress: '',
  pictureAddress: '',
  blogDescription: '',
  email: ''
})

const fetchFriends = async () => {
  try {
    const res = await getFriendList()
    if (res.success) {
      friendList.value = res.data.list
    }
  } catch (e) { console.error(e) }
}

const handleSubmit = async () => {
  if (!form.blogName || !form.blogAddress || !form.email) {
    ElMessage.warning('请填写完整必填项')
    return
  }
  
  submitting.value = true
  try {
    const res = await applyFriendLink(form)
    if (res.success) {
      ElMessage.success('申请提交成功，请等待审核~')
      Object.keys(form).forEach(key => form[key] = '')
    } else {
      ElMessage.error(res.message || '提交失败')
    }
  } catch (e) {
    ElMessage.error('网络异常')
  } finally {
    submitting.value = false
  }
}

const copyText = (text) => {
  if (navigator.clipboard) {
    navigator.clipboard.writeText(text).then(() => { ElMessage.success('复制成功') })
  } else {
    const textarea = document.createElement('textarea')
    textarea.value = text
    document.body.appendChild(textarea)
    textarea.select()
    document.execCommand('Copy')
    document.body.removeChild(textarea)
    ElMessage.success('复制成功')
  }
}

const handleImgError = (e) => { e.target.src = defaultAvatar }

onMounted(() => { fetchFriends() })
</script>

<style scoped>
.friend-container { position: relative; min-height: 100vh; }
.content-bg {
  position: fixed; top: 0; left: 0; width: 100%; height: 100%;
  background-image: url('@/assets/images/bg_01.png');
  background-repeat: repeat; z-index: -2;
}
.nav-placeholder { height: 85px; }

/* 头部标语 (上移) */
.friend-header { margin-top: 0; }
.sub-title { font-size: 1.2rem; margin-bottom: 5px; color: #555; }
.main-title { font-size: 3rem; font-weight: bold; color: #333; text-shadow: 2px 2px 4px rgba(0,0,0,0.1); }
.highlight { color: #03A87C; }

/* 布局辅助 */
.row-stretch { align-items: stretch; display: flex; }
.col-stretch { display: flex; flex-direction: column; }

/* 左栏：信息 */
.info-block {
  padding: 30px; /* 统一 Padding */
  /* background: rgba(255, 255, 255, 0.5); 可选：如果你想两边都有底色 */
  border-radius: 8px;
  display: flex; flex-direction: column;
}
.info-title { 
  font-weight: bold; margin-bottom: 12px; font-size: 1.1rem; 
  border-left: 4px solid #03A87C; padding-left: 10px;
  line-height: 1.5; /* 确保行高 */
}
/* 强制左侧第一个标题无上边距，对齐右侧 */
.info-block .info-title:first-child { margin-top: 0 !important; }

.requirements p { color: #666; line-height: 1.8; margin-bottom: 5px; font-size: 0.95rem; }
.copy-item { display: flex; align-items: center; gap: 8px; margin-bottom: 8px; color: #555; font-size: 0.9rem; }
.copy-btn { cursor: pointer; color: #999; transition: color 0.3s; }
.copy-btn:hover { color: #03A87C; }
.warning-box { margin-top: auto; padding-top: 20px; }
.text-danger { color: #dc3545; }

/* 右栏：表单 */
.form-box {
  padding: 30px; /* 统一 Padding，与左侧 info-block 保持一致 */
  padding-left: 10px; /* 视觉上右栏稍微靠左一点点 */
  display: flex; flex-direction: column; justify-content: space-between;
}
.form-item { margin-bottom: 20px; width: 100%; }
/* 强制右侧第一个输入框无上边距 */
.form-item:first-child { margin-top: 0 !important; }

.form-item label { 
  display: flex; align-items: center; gap: 8px; 
  font-weight: bold; margin-bottom: 8px; color: #333; 
  font-size: 1rem; line-height: 1.5;
}
.required { color: red; margin-left: 4px; }
.note { font-weight: normal; font-size: 0.8rem; color: #888; }

/* 输入框 */
.custom-input {
  width: 100%; /* 填满容器 */
  box-sizing: border-box; /* 含 padding */
  padding: 12px 15px;
  border: 1px solid transparent; 
  border-radius: 6px;
  outline: none;
  background-color: #F3F3F3; /* 修改背景色 */
  color: #333;
  transition: all 0.3s;
  font-size: 0.95rem;
}
.custom-input:focus { 
  background-color: #fff;
  border-color: #03A87C; 
  box-shadow: 0 0 0 3px rgba(3, 168, 124, 0.1); 
}

/* 按钮容器 */
.btn-container { width: 100%; margin-top: 10px; }
.apply-btn {
  width: 100%; /* 填满 */
  box-sizing: border-box;
  background-color: #03A87C; /* 绿色 */
  color: #fff;
  border: none;
  padding: 12px 0;
  border-radius: 6px;
  font-weight: bold;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 4px 10px rgba(3, 168, 124, 0.3);
}
.apply-btn:hover { 
  background-color: #028f69; 
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(3, 168, 124, 0.4);
}

.divider { margin: 50px 0; border: none; border-top: 1px dashed #ccc; }

/* === 友链卡片 (默认显示悬停样式，只是淡一点) === */
.friend-card {
  display: block;
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  position: relative;
  /* 默认：带有颜色的边框和阴影，但比较淡 */
  border: 1px solid rgba(3, 168, 124, 0.3); 
  box-shadow: 5px 5px 0px rgba(3, 168, 124, 0.1); 
  transition: all 0.3s ease;
  height: 140px;
  text-decoration: none;
  overflow: hidden;
  z-index: 1;
}

/* 悬停交互：颜色变深，位移 */
.friend-card:hover {
  transform: translateY(-5px);
  border-color: #03A87C; /* 深绿 */
  box-shadow: 6px 6px 0px rgba(3, 168, 124, 0.3); /* 深绿阴影 */
}

.card-status-dot {
  position: absolute;
  top: 15px; right: 15px;
  width: 10px; height: 10px;
  background-color: #03A87C;
  border-radius: 2px;
}

.card-body { position: relative; z-index: 5; }

.card-top { display: flex; align-items: center; gap: 15px; margin-bottom: 12px; }
.friend-avatar { width: 45px; height: 45px; flex-shrink: 0; }
.friend-avatar img {
  width: 100%; height: 100%; object-fit: cover;
  border-radius: 50%; border: 2px solid #f0f0f0;
}

.friend-name { font-weight: bold; font-size: 1.1rem; color: #333; }
.friend-card:hover .friend-name { color: #03A87C; }

.friend-desc { font-size: 0.85rem; color: #666; line-height: 1.5; }

/* 点阵装饰 (加大、加深) */
.card-dots {
  position: absolute;
  bottom: -10px; right: -10px; 
  width: 100px; height: 100px; /* 大尺寸 */
  /* 颜色加深 (#999) */
  background-image: radial-gradient(#999 20%, transparent 20%);
  background-size: 6px 6px;
  opacity: 0.4; /* 透明度提高，更明显 */
  z-index: 1;
  transition: transform 0.5s;
}
.friend-card:hover .card-dots {
  transform: scale(1.1) rotate(10deg);
  /* 悬停变绿 */
  background-image: radial-gradient(#03A87C 20%, transparent 20%);
  opacity: 0.3;
}

@media (max-width: 768px) {
  .friend-header { text-align: center; margin-top: 20px; }
  .form-box { padding: 0; margin-top: 30px; }
  .info-block { text-align: center; padding: 0; }
  .copy-item { justify-content: center; }
  .row-stretch { flex-direction: column; }
}
</style>