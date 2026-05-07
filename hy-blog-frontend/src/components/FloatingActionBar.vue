<template>
  <div>
    <div class="action-bar">
      <div class="action-item" @click="showReward = true">
        <span class="action-label">赞赏</span>
        <div class="action-icon btn-reward">
          <el-icon><Present /></el-icon>
        </div>
      </div>

      <div class="action-item" @click="$emit('click-comment')">
        <span class="action-label">评论</span>
        <div class="action-icon btn-comment">
          <el-icon><ChatLineSquare /></el-icon>
        </div>
      </div>

      <div class="action-item" @click="scrollToTop">
        <span class="action-label">返回顶部</span>
        <div class="action-icon btn-top">
          <el-icon><Top /></el-icon>
        </div>
      </div>
    </div>

    <transition name="fade">
      <div class="reward-overlay" v-if="showReward" @click.self="showReward = false">
        <div class="reward-modal">
          <div class="reward-header">
            <span class="reward-title">赞赏码</span>
            <el-icon class="close-icon" @click="showReward = false"><Close /></el-icon>
          </div>
          <div class="reward-divider"></div>
          <div class="reward-tip">如果我的博客帮助到了您，您可以友情支持一下。</div>
          <div class="pay-container">
            <div class="pay-item">
              <div class="pay-label">
                <img src="@/assets/icons/wechat.svg" class="pay-icon" alt="wechat" /> 微信
              </div>
              <div class="qr-box">
                <img :src="wxImg" alt="微信收款码">
              </div>
            </div>
            <div class="pay-item">
              <div class="pay-label">
                 <span class="alipay-text">支付宝</span>
              </div>
              <div class="qr-box">
                <img :src="zfbImg" alt="支付宝收款码">
              </div>
            </div>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
/**
 * 悬浮操作栏组件
 * 固定右下角：赞赏弹窗、评论跳转、返回顶部
 */
import { ref, defineEmits } from 'vue'
import { Present, ChatLineSquare, Top, Close } from '@element-plus/icons-vue'
import wxQr from '@/assets/images/wx.jpg'
import zfbQr from '@/assets/images/zfb.jpg'

const emit = defineEmits(['click-comment'])

const showReward = ref(false)
const wxImg = wxQr
const zfbImg = zfbQr

/** 平滑滚动到页面顶部 */
const scrollToTop = () => {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}
</script>

<style scoped>
/* === 悬浮操作栏样式 === */
.action-bar {
  position: fixed; right: 40px; bottom: 50px;
  display: flex; flex-direction: column; gap: 15px; z-index: 998;
}
.action-item { position: relative; cursor: pointer; display: flex; align-items: center; justify-content: flex-end; }

.action-icon {
  width: 48px; height: 48px; border-radius: 50%; background-color: #fff;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  display: flex; justify-content: center; align-items: center;
  font-size: 20px; color: #555;
  transition: all 0.3s cubic-bezier(0.18, 0.89, 0.32, 1.28);
}

.btn-reward:hover { background-color: #f56c6c; color: #fff; transform: scale(1.1); }
.btn-comment:hover { background-color: #409eff; color: #fff; transform: scale(1.1); }
.btn-top:hover { background-color: #67c23a; color: #fff; transform: scale(1.1); }

.action-label {
  position: absolute; right: 60px;
  background-color: rgba(0, 0, 0, 0.7); color: #fff;
  padding: 5px 10px; border-radius: 4px; font-size: 0.85rem;
  white-space: nowrap; opacity: 0; transform: translateX(10px);
  transition: all 0.3s ease; pointer-events: none;
}
.action-label::after {
  content: ''; position: absolute; right: -5px; top: 50%;
  transform: translateY(-50%);
  border-left: 5px solid rgba(0, 0, 0, 0.7);
  border-top: 5px solid transparent; border-bottom: 5px solid transparent;
}
.action-item:hover .action-label { opacity: 1; transform: translateX(0); }

/* === 赞赏弹窗样式 === */
.reward-overlay {
  position: fixed; top: 0; left: 0; width: 100vw; height: 100vh;
  background-color: rgba(0, 0, 0, 0.5); z-index: 2000;
  display: flex; justify-content: center; align-items: center;
}
.reward-modal {
  background: #fff; width: 500px; max-width: 90%; border-radius: 12px;
  padding: 0; box-shadow: 0 10px 30px rgba(0,0,0,0.2);
  animation: slideUp 0.3s ease; overflow: hidden;
}
.reward-header { display: flex; justify-content: space-between; align-items: center; padding: 15px 20px; }
.reward-title { font-size: 1.1rem; font-weight: bold; color: #333; }
.close-icon { font-size: 1.2rem; cursor: pointer; color: #999; transition: color 0.3s; }
.close-icon:hover { color: #333; }
.reward-divider { height: 1px; background-color: #eee; width: 100%; }
.reward-tip { padding: 20px; text-align: center; color: #666; font-size: 0.95rem; background-color: #fcfcfc; }
.pay-container { display: flex; justify-content: space-around; padding: 20px 30px 30px; gap: 20px; }
.pay-item { display: flex; flex-direction: column; align-items: center; gap: 10px; }
.pay-label { font-weight: bold; color: #333; display: flex; align-items: center; gap: 5px; }
.pay-icon { width: 20px; height: 20px; }
.alipay-text { color: #027aff; }
.qr-box { width: 160px; height: 160px; padding: 5px; border: 1px solid #eee; border-radius: 8px; background: #fff; }
.qr-box img { width: 100%; height: 100%; object-fit: contain; }

@keyframes slideUp { from { transform: translateY(20px); opacity: 0; } to { transform: translateY(0); opacity: 1; } }
.fade-enter-active, .fade-leave-active { transition: opacity 0.3s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>