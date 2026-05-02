<template>
  <footer class="hy-footer">
    <div class="hy-container">
      <div class="footer-flex">
        
        <div class="footer-left">
          <span class="brand-text">HeYi</span>
          <span class="runtime-divider">|</span>
          <span class="runtime-text">此网站已运营：{{ runTime }}</span>
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
    </div>
  </footer>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

// === 1. 诗句逻辑 ===
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

// === 2. 运行时间逻辑 ===
const runTime = ref('')
let timer = null
const START_DATE = '2026-01-27 00:00:00' 

const calcRunTime = () => {
  const start = new Date(START_DATE).getTime()
  const now = new Date().getTime()
  const diff = now - start

  if (diff < 0) {
    runTime.value = '即将诞生'
    return
  }

  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
  const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
  const seconds = Math.floor((diff % (1000 * 60)) / 1000)

  const d = days
  const h = hours < 10 ? '0' + hours : hours
  const m = minutes < 10 ? '0' + minutes : minutes
  const s = seconds < 10 ? '0' + seconds : seconds

  runTime.value = `${d}天 ${h}时 ${m}分 ${s}秒`
}

onMounted(() => {
  refreshPoem()
  calcRunTime()
  timer = setInterval(calcRunTime, 1000)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
.hy-footer {
  background-color: #ffffff; 
  border-top: 1px solid var(--bs-gray-200); 
  margin-top: auto;
  color: var(--bs-gray-700);
  width: 100%;
}

.hy-container {
  max-width: 1440px; /* 调整为 8 的倍数 */
  margin: 0 auto;
  padding: 0 var(--spacing-xl);
  height: 64px; /* 调整为 8 的倍数 */
}

/* === 核心布局修改 === */
.footer-flex {
  display: flex;
  justify-content: space-between;
  align-items: center; 
  height: 100%;
  width: 100%;
}

/* 1. 左侧容器 */
.footer-left {
  flex: 1; 
  display: flex;
  justify-content: flex-start; 
  align-items: center;
  gap: var(--spacing-sm);
}

/* 2. 中间容器 */
.footer-center {
  flex: 2; 
  display: flex;
  justify-content: center; 
  align-items: center;
  gap: var(--spacing-md);
  
  font-size: 1.1rem;
  color: var(--bs-gray-800);
  text-align: center;
}

/* 3. 右侧容器 */
.footer-right {
  flex: 1; 
  display: flex;
  justify-content: flex-end; 
  align-items: center;
}

/* === 细节样式 === */

.brand-text {
  font-weight: 700;
  font-size: 1.2rem;
  color: var(--bs-gray-900);
  letter-spacing: var(--letter-spacing-wide);
}

.runtime-divider {
  color: var(--bs-gray-300);
  font-size: 0.9rem;
}

.runtime-text {
  font-size: 0.9rem;
  color: var(--bs-gray-600);
  letter-spacing: var(--letter-spacing-base);
}

.refresh-icon {
  cursor: pointer;
  font-size: 1.2rem;
  color: #888;
  transition: all 0.3s ease;
  user-select: none;
  /* 微调图标位置，使其在视觉上与文字垂直居中 */
  position: relative;
  top: 1px; 
}

.refresh-icon:hover {
  transform: rotate(180deg);
  color: #000;
}

.beian-link {
  color: #999;
  text-decoration: none;
  font-size: 0.85rem;
  transition: color 0.3s;
}

.beian-link:hover {
  color: #333;
  text-decoration: underline;
}

/* === 响应式适配 === */
@media (max-width: 768px) {
  .hy-container {
    height: auto;
    padding: 20px 20px 30px 20px;
  }
  
  .footer-flex {
    flex-direction: column;
    gap: 15px;
    justify-content: center;
  }
  
  /* 手机端取消 flex: 1 限制，改为自适应 */
  .footer-left, .footer-center, .footer-right {
    flex: auto;
    width: 100%;
    justify-content: center; /* 手机端全部居中 */
  }

  .footer-left {
    flex-direction: column;
    gap: 5px;
  }
  
  .runtime-divider {
    display: none;
  }
  
  /* 手机端调整顺序：诗词在最上面 */
  .footer-center {
    order: -1;
    margin-bottom: 5px;
  }
}
</style>