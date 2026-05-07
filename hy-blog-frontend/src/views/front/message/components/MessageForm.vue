<template>
  <div class="message-form-card">
    <div class="form-header">
      <el-icon><EditPen /></el-icon> {{ title }}
      <span v-if="replyObj" class="reply-tag ms-3">
        正在回复 @{{ replyObj.nickname }}
        <el-icon class="close-icon" @click="cancelReply"><CircleClose /></el-icon>
      </span>
    </div>

    <div class="user-info-row">
      <div class="avatar-preview">
        <img :src="finalAvatar" alt="avatar">
      </div>
      <div class="input-group">
        <el-input v-model="form.email" placeholder="邮箱 (必填，Gravatar/QQ头像)" @blur="handleEmailBlur">
          <template #prefix><el-icon><Message /></el-icon></template>
        </el-input>
        <el-input v-model="form.nickname" placeholder="昵称 (必填)">
          <template #prefix><el-icon><User /></el-icon></template>
        </el-input>
        <el-input v-model="form.address" placeholder="站点 (选填)">
          <template #prefix><el-icon><Link /></el-icon></template>
        </el-input>
      </div>
    </div>

    <div class="content-box mt-3">
      <el-input
        v-model="form.content"
        type="textarea"
        :rows="3"
        :placeholder="placeholderText"
        resize="none"
        maxlength="500"
        show-word-limit
        class="custom-textarea"
        @focus="handleFocus"
        @blur="handleBlur"
      />
    </div>

    <div class="action-row mt-3">
      <el-popover placement="bottom-start" :width="320" trigger="click">
        <template #reference>
          <el-button class="emoji-btn" circle>😊</el-button>
        </template>
        <EmojiPicker :native="true" @select="onSelectEmoji" />
      </el-popover>

      <el-button type="primary" class="send-btn" :loading="submitting" @click="handleSubmit">
        <el-icon class="me-1"><Position /></el-icon> 发 送
      </el-button>
    </div>
  </div>
</template>

<script setup>
/**
 * 通用留言/评论表单组件
 * 支持 message / blog / moment 三种模块，含邮箱头像自动解析、表情包、回复引用
 */
import { ref, reactive, computed, defineProps, defineEmits, defineExpose } from 'vue'
import { EditPen, Message, User, Link, Position, CircleClose } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import md5 from 'js-md5'
import request from '@/utils/request'
import EmojiPicker from 'vue3-emoji-picker'
import 'vue3-emoji-picker/css'
import defaultAvatarImg from '@/assets/images/avatar.png'

const props = defineProps({
  module: { type: String, default: 'message' },
  targetId: { type: [String, Number], default: 0 },
  title: { type: String, default: '留言' }
})

const emit = defineEmits(['success'])
const submitting = ref(false)

const form = reactive({
  email: '', nickname: '', address: '', content: '', avatar: ''
})
const replyObj = ref(null)
const placeholderText = ref('注意文明发言哦~')

const finalAvatar = computed(() => form.avatar || defaultAvatarImg)

/**
 * 邮箱失焦时自动解析头像：
 * QQ 邮箱 → QQ 头像 API，其他邮箱 → Gravatar (cravatar.cn 国内镜像)
 */
const handleEmailBlur = () => {
  const email = form.email.trim()
  if (!email) { form.avatar = ''; return }
  const qqRegex = /^[1-9][0-9]{4,10}@qq\.com$/
  if (qqRegex.test(email)) {
    const qq = email.replace('@qq.com', '')
    form.avatar = `http://q1.qlogo.cn/g?b=qq&nk=${qq}&s=100`
  } else {
    const emailMd5 = md5(email)
    form.avatar = `https://cravatar.cn/avatar/${emailMd5}?d=mp`
  }
}

const handleFocus = () => { if(!replyObj.value) placeholderText.value = '写下你想说的话...' }
const handleBlur = () => { if(!form.content && !replyObj.value) placeholderText.value = '注意文明发言哦~' }
const onSelectEmoji = (emoji) => { form.content += emoji.i }

/** 设置回复目标，更新 placeholder 提示 */
const setReply = (item) => {
  replyObj.value = item
  placeholderText.value = `回复 @${item.nickname}: `
}
/** 取消回复状态 */
const cancelReply = () => {
  replyObj.value = null
  placeholderText.value = '注意文明发言哦~'
}

/** 提交表单：校验必填 → 根据 module 选择 API → 发布成功通知父组件刷新 */
const handleSubmit = async () => {
  if (!form.email || !form.nickname || !form.content) {
    return ElMessage.warning('请填写完整的 邮箱、昵称 和 内容')
  }

  submitting.value = true
  try {
    let url = ''
    let data = { ...form }

    if (props.module === 'moment') {
      url = '/front/moment/comment'
      data.momentId = props.targetId
    } else if (props.module === 'blog') {
      url = '/front/comment/save' 
      data.blogId = props.targetId 
    } else {
      url = '/front/message/save'
    }

    // 回复状态下补全父子关系字段：comment 模块用 parentCommentId，message 模块用 parentMessageId
    if (replyObj.value) {
      if (props.module === 'moment' || props.module === 'blog') {
        data.parentCommentId = replyObj.value.id
        data.rootCommentId = replyObj.value.rootCommentId || replyObj.value.id
      } else {
        data.parentMessageId = replyObj.value.id
        data.rootMessageId = replyObj.value.rootMessageId || replyObj.value.id
      }
    }

    const res = await request.post(url, data)
    
    if (res.code === 200 || res.success) {
      ElMessage.success('发布成功')
      form.content = ''
      cancelReply()
      emit('success')
    }
  } finally {
    submitting.value = false
  }
}

const reset = () => {
  form.content = ''
  cancelReply()
}
// 暴露方法给父组件调用
defineExpose({ reset, setReply })
</script>

<style scoped>
.message-form-card { background: #fff; border-radius: 12px; padding: var(--spacing-lg); border: 1px solid var(--bs-gray-200); box-shadow: 0 4px 12px rgba(0,0,0,0.03); }
.form-header { font-weight: bold; margin-bottom: var(--spacing-lg); display: flex; align-items: center; font-size: 1.1rem; color: var(--bs-gray-900); }
.reply-tag { font-size: 0.9rem; color: var(--bs-primary); background: #ecf5ff; padding: 4px 12px; border-radius: 6px; display: flex; align-items: center; gap: var(--spacing-xs); margin-left: var(--spacing-md); }
.close-icon { cursor: pointer; transition: color 0.2s; } .close-icon:hover { color: #f56c6c; }
.user-info-row { display: flex; gap: var(--spacing-md); margin-bottom: var(--spacing-md); }
.avatar-preview { width: 48px; height: 48px; border-radius: 8px; border: 1px solid var(--bs-gray-200); overflow: hidden; flex-shrink: 0; }
.avatar-preview img { width: 100%; height: 100%; object-fit: cover; }
.input-group { flex: 1; display: flex; gap: var(--spacing-sm); }
.action-row { display: flex; justify-content: space-between; align-items: center; }
.emoji-btn { font-size: 1.25rem; border: 1px solid var(--bs-gray-200); }
.send-btn { width: 110px; letter-spacing: var(--letter-spacing-wide); font-weight: 600; border-radius: 8px; }
@media (max-width: 768px) { .user-info-row { flex-direction: column; } .input-group { flex-direction: column; } }

</style>