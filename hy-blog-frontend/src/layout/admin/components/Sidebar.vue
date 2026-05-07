<template>
  <el-aside width="220px" class="sidebar-container">
    <div class="logo-container">
      <img src="@/assets/images/avatar.png" alt="logo" class="logo-img" />
      <span class="logo-text">HyBlog Admin</span>
    </div>

    <el-scrollbar>
      <el-menu
        :default-active="activeMenu"
        class="el-menu-vertical"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        :unique-opened="false"
        router
      >
        <template v-for="route in menuList" :key="route.path">
          
          <template v-if="route.meta && route.meta.hidden"></template>

          <el-sub-menu 
            v-else-if="route.children && route.children.length > 0" 
            :index="resolvePath(route.path)"
          >
            <template #title>
              <el-icon v-if="route.meta && route.meta.icon">
                <component :is="route.meta.icon" />
              </el-icon>
              <span>{{ route.meta.title }}</span>
            </template>
            
            <template v-for="child in route.children" :key="child.path">
              <el-menu-item 
                v-if="!child.meta?.hidden"
                :index="resolvePath(route.path, child.path)"
              >
                 <span>{{ child.meta.title }}</span>
              </el-menu-item>
            </template>
          </el-sub-menu>

          <el-menu-item v-else :index="resolvePath(route.path)">
            <el-icon v-if="route.meta && route.meta.icon">
              <component :is="route.meta.icon" />
            </el-icon>
            <template #title>{{ route.meta.title }}</template>
          </el-menu-item>

        </template>
      </el-menu>
    </el-scrollbar>
  </el-aside>
</template>

<script setup>
/**
 * 后台侧边栏菜单组件
 * 根据 adminRoutes 动态渲染多级菜单，高亮当前路由，支持 hidden 控制显隐
 */
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import adminRoutes from '@/router/modules/adminRoutes'

const route = useRoute()

/** 从路由配置中提取 admin 下的子路由作为菜单项 */
const adminRouteEntry = adminRoutes.find(r => r.path === '/admin')
const menuList = computed(() => {
  return adminRouteEntry ? adminRouteEntry.children : []
})

/** 高亮当前菜单项，优先使用 meta.activeMenu 指定的路径 */
const activeMenu = computed(() => {
  const { meta, path } = route
  if (meta.activeMenu) {
    return meta.activeMenu
  }
  return path
})

/** 拼接父子路由路径为完整的 /admin/parent/child */
const resolvePath = (parentPath, childPath) => {
  if (!childPath) {
    return `/admin/${parentPath}`
  }
  return `/admin/${parentPath}/${childPath}`
}
</script>

<style scoped>
.sidebar-container {
  height: 100vh;
  background-color: #304156;
  box-shadow: 2px 0 6px rgba(0, 21, 41, 0.35);
  display: flex;
  flex-direction: column;
}

.logo-container {
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #2b2f3a;
  color: #fff;
  font-weight: 600;
  font-size: 18px;
  overflow: hidden;
}

.logo-img {
  width: 32px;
  height: 32px;
  margin-right: 10px;
  border-radius: 5px;
}

.el-scrollbar {
  flex: 1;
}

.el-menu-vertical {
  border-right: none;
}

/* 鼠标悬停时的背景色 (Element 默认也有，这里可以微调) */
:deep(.el-menu-item:hover), :deep(.el-sub-menu__title:hover) {
  background-color: #263445 !important;
}

/* 子菜单背景色略深，体现层级感 */
:deep(.el-sub-menu .el-menu-item) {
  background-color: #1f2d3d !important;
}

/* 子菜单悬停 */
:deep(.el-sub-menu .el-menu-item:hover) {
  background-color: #001528 !important;
}
</style>