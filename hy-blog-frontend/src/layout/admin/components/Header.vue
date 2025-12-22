<template>
  <el-header class="header-container">
    <div class="left-panel">
      </div>
    <div class="right-panel">
      <el-dropdown trigger="click">
        <div class="avatar-wrapper">
          <el-avatar :size="35" :src="userInfo.avatar || defaultAvatar"></el-avatar>
          <span class="username">{{ userInfo.nickname || userInfo.username }}</span>
          <el-icon><CaretBottom /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="$router.push('/')">回到首页</el-dropdown-item>
            <el-dropdown-item divided @click="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </el-header>
</template>

<script setup>
import { reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import { CaretBottom } from '@element-plus/icons-vue'

const router = useRouter()
const defaultAvatar = 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
const userInfo = reactive({})

// 核心修改：从后端获取最新用户信息
const getUserInfo = async () => {
  try {
    const res = await request.get('/admin/info') // 对应 AdminLoginController 的 info 接口
    Object.assign(userInfo, res.data.user)
  } catch (e) {
    console.error(e)
  }
}

const logout = async () => {
  try {
    await request.get('/admin/logout')
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    ElMessage.success('退出成功')
    router.push('/login')
  } catch (e) {
    console.error(e)
  }
}

onMounted(() => {
  getUserInfo()
})
</script>

<style scoped>
.header-container {
  background-color: #242f42;
  color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 50px;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);
}
.right-panel { display: flex; align-items: center; cursor: pointer; }
.avatar-wrapper { display: flex; align-items: center; color: #fff; }
.username { margin: 0 8px; font-size: 14px; }
</style>