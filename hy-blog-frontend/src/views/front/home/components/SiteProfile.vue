<template>
  <div class="site-profile sidebar-card" v-if="config">
    
    <div class="profile-bg">
      <img src="@/assets/images/overview.jpg" alt="background">
    </div>

    <div class="profile-content">
      
      <div class="profile-avatar">
        <router-link to="/about">
          <img src="@/assets/images/me.jpg" alt="avatar">
        </router-link>
      </div>

      <h2 class="profile-name">{{ config.author || 'HeYi' }}</h2>

      <div class="profile-desc" v-html="config.siteProfile || '暂无简介'"></div>

      <div class="profile-info-list">
        
        <div class="info-item" v-if="config.siteLocation">
          <el-icon class="info-icon"><LocationInformation /></el-icon>
          <span>{{ config.siteLocation }}</span>
        </div>

        <div class="info-item" v-if="config.siteEmail">
          <el-icon class="info-icon"><Message /></el-icon>
          <span>{{ config.siteEmail }}</span>
        </div>

        <div class="info-item" v-if="config.siteQq">
          <el-icon class="info-icon">
            <img src="@/assets/icons/qq.svg" class="svg-icon" alt="QQ" />
          </el-icon>
          <span>{{ config.siteQq }}</span>
        </div>

        <div class="info-item" v-if="config.siteWechat">
          <el-icon class="info-icon">
            <img src="@/assets/icons/wechat.svg" class="svg-icon" alt="Wechat" />
          </el-icon>
          <span>{{ config.siteWechat }}</span>
        </div>

      </div>

      <div class="social-grid">
        
        <a :href="config.siteBilibili || '#'" target="_blank" class="social-btn">
          <img src="@/assets/icons/bilibili.svg" class="btn-svg-icon" alt="bilibili" />
          <span>Bilibili</span>
        </a>

        <a :href="config.siteGithub || '#'" target="_blank" class="social-btn">
          <img src="@/assets/icons/github.svg" class="btn-svg-icon" alt="github" />
          <span>GitHub</span>
        </a>

        <a :href="config.siteCsdn || '#'" target="_blank" class="social-btn">
          <img src="@/assets/icons/csdn.svg" class="btn-svg-icon" alt="csdn" />
          <span>CSDN</span>
        </a>

        <router-link to="/about" class="social-btn">
          <el-icon class="btn-icon"><InfoFilled /></el-icon>
          <span>More Info</span>
        </router-link>

      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getSiteConfig } from '@/api/config'
import { LocationInformation, InfoFilled, Message } from '@element-plus/icons-vue'

const config = ref({})

const fetchConfig = async () => {
  try {
    const res = await getSiteConfig()
    if (res.success) {
      config.value = res.data.data
    }
  } catch (err) {
    console.error("获取站点配置失败", err)
  }
}

onMounted(() => {
  fetchConfig()
})
</script>

<style scoped>
/* 修改点1：不透明白色卡片设计 
   回归经典：白底 + 阴影，区分度更好
*/
.sidebar-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05); /* 柔和阴影 */
  overflow: hidden;
  margin-bottom: 20px;
  transition: all 0.3s ease;
}
.sidebar-card:hover {
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

/* 背景图 */
.profile-bg { height: 120px; width: 100%; overflow: hidden; }
.profile-bg img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.5s; }
.site-profile:hover .profile-bg img { transform: scale(1.05); }

/* 内容区域 */
.profile-content { padding: 0 20px 20px 20px; position: relative; text-align: center; }

/* 修改点2：头像样式 
   去掉了 :hover 的 rotate 旋转，只保留边框和阴影
*/
.profile-avatar {
  width: 80px; height: 80px; margin: -40px auto 10px; position: relative; z-index: 2;
  border-radius: 50%; overflow: hidden;
  border: 4px solid #fff; /* 纯白边框，与卡片背景融合 */
  box-shadow: 0 2px 10px rgba(0,0,0,0.15);
  transition: transform 0.3s;
  background: #fff; /* 防止图片未加载透出背景 */
}
.profile-avatar img { width: 100%; height: 100%; object-fit: cover; }
/* 鼠标放上去稍微放大一点点，表示可点击，但不旋转 */
.profile-avatar:hover { transform: scale(1.05); }

/* 文本信息 */
.profile-name { font-size: 1.4rem; font-weight: bold; color: #333; margin-bottom: 8px; }

/* 个人简介样式优化 
   v-html 渲染的内容可能包含 p 标签，这里统一调整
*/
.profile-desc { 
  font-size: 0.9rem; color: #555; line-height: 1.6; margin-bottom: 20px; 
  word-wrap: break-word; 
  /* 允许HTML内容正常换行 */
  white-space: pre-wrap; 
}
/* 如果 v-html 里面自带了 p 标签，去除多余边距 */
.profile-desc :deep(p) { margin: 0 0 5px 0; }

/* 信息列表 */
.profile-info-list { display: flex; flex-direction: column; gap: 10px; margin-bottom: 20px; padding: 0 10px; }
.info-item { display: flex; align-items: center; font-size: 0.9rem; color: #444; justify-content: flex-start; }
.info-icon { margin-right: 10px; font-size: 1.1rem; color: #666; width: 20px; text-align: center; display: flex; justify-content: center; align-items: center; }

/* SVG 图标样式 */
.svg-icon { width: 1em; height: 1em; vertical-align: middle; }
.btn-svg-icon { width: 1.1rem; height: 1.1rem; margin-right: 5px; vertical-align: middle; }

/* 社交按钮 Grid */
.social-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; }

/* 修改点3：统一链接悬停颜色
   不再区分 bilibili pink / github black / csdn orange
   全部统一使用主题色 var(--bs-primary)
*/
.social-btn {
  display: flex; align-items: center; justify-content: center; padding: 8px 5px;
  background: #f8f9fa; /* 淡淡的灰色背景 */
  border: 1px solid #eee;
  border-radius: 6px;
  text-decoration: none; color: #555; font-size: 0.85rem; font-weight: 500;
  transition: all 0.3s; cursor: pointer;
}
.btn-icon { margin-right: 5px; font-size: 1rem; }

/* 统一 Hover 效果：字体变主题色，边框变主题色 */
.social-btn:hover { 
  color: var(--bs-primary); /* 统一变蓝（或其他主题色） */
  border-color: var(--bs-primary);
  background: #fff;
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}
</style>