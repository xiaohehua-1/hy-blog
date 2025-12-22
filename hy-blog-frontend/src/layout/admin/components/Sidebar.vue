<template>
  <el-aside width="220px" class="sidebar-container">
    <div class="logo">
      <img src="https://wpimg.wallstcn.com/69a1c46c-eb1c-4b46-8bd4-e9e686ef5251.png" alt="logo">
      <span>HyBlog Admin</span>
    </div>
    
    <el-menu
      active-text-color="#409EFF"
      background-color="#304156"
      text-color="#bfcbd9"
      :default-active="$route.path"
      router
      class="el-menu-vertical"
    >
      <el-menu-item index="/admin/dashboard">
        <el-icon><Odometer /></el-icon>
        <span>首页</span>
      </el-menu-item>

      <el-menu-item index="/admin/article">
        <el-icon><Document /></el-icon>
        <span>文章管理</span>
      </el-menu-item>

      <el-menu-item index="/admin/type">
        <el-icon><Folder /></el-icon>
        <span>分类管理</span>
      </el-menu-item>

      <el-menu-item index="/admin/tag">
        <el-icon><PriceTag /></el-icon>
        <span>标签管理</span>
      </el-menu-item>

      <el-menu-item index="/admin/friend">
        <el-icon><Link /></el-icon>
        <span>友链管理</span>
      </el-menu-item>
      
      <el-menu-item index="/admin/music">
        <el-icon><Headset /></el-icon>
        <span>音乐管理</span>
      </el-menu-item>

      <el-menu-item index="/admin/user">
        <el-icon><User /></el-icon>
        <span>用户管理</span>
      </el-menu-item>

      <el-menu-item index="/admin/interaction">
        <el-icon><ChatDotRound /></el-icon>
        <span>互动管理</span>
      </el-menu-item>
      
      <el-menu-item index="/admin/sync">
        <el-icon><Refresh /></el-icon>
        <span>同步文章</span>
      </el-menu-item>

      <el-menu-item index="/admin/config">
        <el-icon><Setting /></el-icon>
        <span>系统配置管理</span>
      </el-menu-item>

      <el-menu-item index="/admin/log">
        <el-icon><Tickets /></el-icon>
        <span>系统日志管理</span>
      </el-menu-item>

      <el-menu-item index="/admin/blacklist">
        <el-icon><Lock /></el-icon>
        <span>黑名单IP管理</span>
      </el-menu-item>
    </el-menu>
  </el-aside>
</template>

<script setup>
// 补充了之前漏掉的 ChatDotRound 和 Lock 图标引入
import { 
  Odometer, 
  Document, 
  Folder, 
  PriceTag, 
  Link, 
  Headset, 
  Setting, 
  Tickets, 
  User, 
  Refresh,
  ChatDotRound, // 新增
  Lock          // 新增
} from '@element-plus/icons-vue'
</script>

<style scoped>
.sidebar-container {
  background-color: #304156;
  height: 100vh;
  overflow-y: auto;
}
.logo {
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #2b2f3a;
  color: #fff;
  font-weight: bold;
  font-size: 18px;
}
.logo img {
  width: 32px;
  height: 32px;
  margin-right: 10px;
}
.el-menu-vertical {
  border-right: none;
}
</style>