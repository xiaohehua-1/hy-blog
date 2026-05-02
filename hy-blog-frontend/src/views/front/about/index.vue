<template>
  <div class="about-container">
    
    <div class="content-bg"></div>
    <div class="nav-placeholder"></div>

    <div class="main-content">
      <el-row justify="center">
        <el-col :span="14" :xs="22">
          
          <div class="about-card">
            
            <div class="profile-section">
              <div class="avatar-box">
                <img src="@/assets/images/me.jpg" alt="me" class="my-avatar">
              </div>

              <div class="hello-text">
                <span class="highlight">HELLO</span> , MY NAME IS 👋
              </div>

              <h1 class="my-name">{{ config.author || 'HeYi' }}</h1>

              <div class="intro-text">
                {{ config.aboutMeIntroduction || '暂无简介' }}
              </div>

              <div 
                class="detail-content markdown-body" 
                v-html="config.aboutMeContent"
              ></div>
            </div>

            <div class="skill-section mt-5">
              <h2 class="skill-title">我的技能</h2>
              
              <p class="skill-desc">
                我的技能涵盖Java主流技术栈包含Web框架、微服务、数据库、缓存、消息队列，一些前端技术，以及Docker、Linux等运维相关技术
              </p>

              <div class="skill-grid">
                <el-row :gutter="40">
                  <el-col 
                    :span="6" :xs="12" 
                    v-for="(skill, index) in skills" 
                    :key="index"
                    class="skill-col"
                  >
                    <div class="skill-item">
                      <div class="skill-name">{{ skill }}</div>
                      <div class="skill-lines">
                        <div class="line red-line"></div>
                        <div class="line black-line"></div>
                      </div>
                    </div>
                  </el-col>
                </el-row>
              </div>
            </div>

          </div>

        </el-col>
      </el-row>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAboutMe } from '@/api/config' // 现在这个方法存在了

const config = ref({})
const skills = ref([])

const fetchConfig = async () => {
  try {
    const res = await getAboutMe()
    if (res.success) {
      // 修改点：后端返回的是 data("data", config)，所以这里要取 res.data.data
      config.value = res.data.data || {}
      
      // 解析技能 JSON 字符串
      if (config.value.aboutMeSkill) {
        try {
          skills.value = JSON.parse(config.value.aboutMeSkill)
        } catch (e) {
          console.error("技能解析失败", e)
          skills.value = []
        }
      }
    }
  } catch (e) { console.error(e) }
}

onMounted(() => {
  fetchConfig()
})
</script>

<style scoped>
.about-container { position: relative; min-height: 100vh; }
.content-bg {
  position: fixed; top: 0; left: 0; width: 100%; height: 100%;
  background-image: url('@/assets/images/bg_01.png');
  background-repeat: repeat; z-index: -2;
}
.nav-placeholder { height: 100px; } 

.about-card {
  text-align: center;
  padding: 20px 0;
}

/* === 第一板块 === */
.avatar-box { margin-bottom: 30px; }
.my-avatar {
  width: 120px; height: 120px;
  border-radius: 10px; 
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
  transition: transform 0.3s;
}
.my-avatar:hover { transform: rotate(5deg) scale(1.05); }

.hello-text {
  font-size: 1.1rem; font-weight: bold; color: #333; margin-bottom: 10px;
  letter-spacing: 1px;
}
.highlight { color: #03A87C; }

.my-name {
  font-size: 3rem; font-weight: bold; color: #333; margin-bottom: 20px;
  text-shadow: 2px 2px 4px rgba(0,0,0,0.1);
}

.intro-text {
  font-size: 1.1rem; color: #555; margin-bottom: 30px;
  line-height: 1.6;
}

.detail-content {
  font-size: 1rem; color: #666; line-height: 1.8; text-align: left;
  /* 增加半透明背景让文字更清晰 */
  background: rgba(255, 255, 255, 0.6);
  padding: 30px;
  border-radius: 8px;
  margin-bottom: 40px;
}
/* v-html 内容样式 */
:deep(.detail-content p) { text-indent: 2em; margin-bottom: 15px; }
:deep(.detail-content img) { max-width: 100%; border-radius: 6px; margin: 10px 0; }

/* === 第二板块：技能 === */
.skill-section { margin-top: 60px; }
.skill-title {
  font-size: 2.5rem; font-weight: bold; color: #333; margin-bottom: 20px;
  text-shadow: 2px 2px 4px rgba(0,0,0,0.1);
}
.skill-desc {
  font-size: 0.9rem; color: #888; margin-bottom: 40px; max-width: 800px; margin-left: auto; margin-right: auto;
}

.skill-grid { margin-top: 30px; }
.skill-col { margin-bottom: 40px; }

.skill-item {
  display: flex; flex-direction: column; align-items: center;
  transition: transform 0.3s;
}
.skill-item:hover { transform: translateY(-5px); }

.skill-name {
  font-size: 1.1rem; font-weight: bold; color: #444; margin-bottom: 10px;
}

/* 红黑线条装饰 */
.skill-lines {
  width: 100%;
  display: flex; flex-direction: column; gap: 4px;
}
.line { width: 100%; height: 3px; border-radius: 2px; }
.red-line { background-color: #dc3545; width: 80%; margin: 0 auto; }
.black-line { background-color: #333; width: 100%; }

@media (max-width: 768px) {
  .my-name { font-size: 2.2rem; }
  .detail-content { padding: 15px; }
  .skill-title { font-size: 2rem; }
}
</style>