<template>
  <footer class="hy-footer">
    <div class="hy-container footer-flex">
      
      <div class="footer-left">
        <span class="brand-text">HeYi</span>
      </div>

      <div class="footer-center">
        <span class="poem-text">“ {{ currentPoem }} ”</span>
        <span class="refresh-icon" @click="refreshPoem" title="换一句">
          ↻
        </span>
      </div>

      <div class="footer-right">
        <a href="https://beian.miit.gov.cn/" target="_blank" class="beian-link">
          ICP备案号：待备案
        </a>
      </div>

    </div>
  </footer>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const poemList = [
  "雄关漫道真如铁，而今迈步从头越",
  "长风破浪会有时，直挂云帆济沧海",
  "大鹏一日同风起，扶摇直上九万里",
  "众里寻他千百度，蓦然回首，那人却在，灯火阑珊处",
  "人生得意须尽欢，莫使金樽空对月",
  "天生我材必有用，千金散尽还复来",
  "海内存知己，天涯若比邻",
  "莫愁前路无知己，天下谁人不识君",
  "落霞与孤鹜齐飞，秋水共长天一色",
  "山重水复疑无路，柳暗花明又一村"
]

const currentPoem = ref('')

const refreshPoem = () => {
  const randomIndex = Math.floor(Math.random() * poemList.length)
  const newPoem = poemList[randomIndex]
  if (newPoem === currentPoem.value && poemList.length > 1) {
    refreshPoem()
  } else {
    currentPoem.value = newPoem
  }
}

onMounted(() => {
  refreshPoem()
})
</script>

<style scoped>
.hy-footer {
  background-color: #ffffff; 
  /* 顶部黑色分割线 */
  border-top: 1px solid #333; 
  
  margin-top: auto;
  color: #555;
  width: 100%;
}

/* 核心容器 */
.hy-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  
  /* 严格锁定高度为 105px */
  height: 105px; 
  min-height: 105px;
}

.footer-flex {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
}

/* 左侧 */
.footer-left {
  display: flex;
  justify-content: center;
  align-items: center;
}

.brand-text {
  font-weight: 700;
  font-size: 1.2rem;
  color: #333;
}

/* 中间 */
.footer-center {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 12px;
  /* 确保引用了朱雀仿宋字体 */
  font-family: 'ZhuqueFangsong', serif; 
  font-size: 1.15rem;
  color: #444;
  text-align: center;
  padding: 0 20px;
}

.refresh-icon {
  cursor: pointer;
  font-size: 1.2rem;
  color: #888;
  transition: all 0.3s ease;
  user-select: none;
}

.refresh-icon:hover {
  transform: rotate(180deg);
  color: #000;
}

/* 右侧 */
.footer-right .beian-link {
  color: #999;
  text-decoration: none;
  font-size: 0.85rem;
  transition: color 0.3s;
}

.footer-right .beian-link:hover {
  color: #333;
  text-decoration: underline;
}

/* 响应式 */
@media (max-width: 768px) {
  .hy-container {
    height: auto; /* 手机端取消固定高度，避免内容重叠 */
    min-height: auto;
    padding: 20px;
  }
  
  .footer-flex {
    flex-direction: column;
    gap: 15px;
  }
  
  .footer-center {
    order: -1; 
    font-size: 1rem;
    margin-bottom: 5px;
  }
}
</style>