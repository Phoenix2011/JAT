<template>
  <div class="app-wrapper">
    <!-- 侧边栏 -->
    <div class="sidebar-container" :class="{ 'sidebar-collapsed': isCollapse }">
      <div class="sidebar-logo">
        <h2 v-if="!isCollapse">景安堂</h2>
        <h2 v-else>JAT</h2>
      </div>
      
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :unique-opened="true"
        :collapse-transition="false"
        mode="vertical"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
      >
        <template v-for="route in routes" :key="route.path">
          <template v-for="child in route.children" :key="child.path">
            <el-menu-item :index="child.path" @click="handleMenuClick(child)">
              <el-icon>
                <component :is="getIcon(child.meta.icon)" />
              </el-icon>
              <template #title>{{ child.meta.title }}</template>
            </el-menu-item>
          </template>
        </template>
      </el-menu>
    </div>

    <!-- 主内容区域 -->
    <div class="main-container" :class="{ 'main-expanded': isCollapse }">
      <!-- 顶部导航栏 -->
      <div class="navbar">
        <div class="navbar-left">
          <el-button
            type="text"
            @click="toggleSidebar"
            class="hamburger"
          >
            <el-icon>
              <Fold v-if="!isCollapse" />
              <Expand v-else />
            </el-icon>
          </el-button>
          
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="currentRoute.meta.title !== '首页'">
              {{ currentRoute.meta.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div class="navbar-right">
          <el-dropdown @command="handleCommand">
            <span class="el-dropdown-link">
              <el-icon><User /></el-icon>
              管理员
              <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>

      <!-- 页面内容 -->
      <div class="app-main">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { constantRoutes } from '@/router'
import { ElMessage } from 'element-plus'
import {
  House as Dashboard,
  Calendar,
  User,
  ShoppingCartFull,
  Setting as Service,
  UserFilled as UserTie,
  CreditCard,
  Wallet,
  DataAnalysis as ChartBar,
  Fold,
  Expand,
  ArrowDown
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 侧边栏折叠状态
const isCollapse = ref(false)

// 当前激活的菜单
const activeMenu = computed(() => {
  const { path } = route
  return path.split('/')[1] || 'dashboard'
})

// 当前路由信息
const currentRoute = computed(() => route)

// 过滤出需要显示的路由
const routes = computed(() => {
  return constantRoutes.filter(route => !route.hidden && route.children && route.children.length > 0)
})

// 图标映射
const iconMap = {
  dashboard: Dashboard,
  calendar: Calendar,
  user: User,
  wallet: Wallet,
  'shopping-cart': ShoppingCartFull,
  serviceItem: Service,
  'user-tie': UserTie,
  'credit-card': CreditCard,
  'chart-bar': ChartBar
}

// 获取图标组件
const getIcon = (iconName) => {
  return iconMap[iconName] || Dashboard
}

// 切换侧边栏
const toggleSidebar = () => {
  isCollapse.value = !isCollapse.value
}

// 菜单点击处理
const handleMenuClick = (menuItem) => {
  if (route.path !== `/${menuItem.path}`) {
    router.push(`/${menuItem.path}`)
  }
}

// 下拉菜单命令处理
const handleCommand = async (command) => {
  switch (command) {
    case 'profile':
      // 跳转到个人中心
      ElMessage.info('个人中心功能待实现')
      break
    case 'logout':
      // 退出登录
      try {
        await userStore.logout()
        router.push('/login')
        ElMessage.success('退出登录成功')
      } catch (error) {
        console.error('退出登录失败', error)
        // 即使API失败也要清除本地状态
        userStore.resetToken()
        router.push('/login')
      }
      break
  }
}
</script>

<style lang="scss" scoped>
.app-wrapper {
  display: flex;
  height: 100vh;
  width: 100%;
}

.sidebar-container {
  width: 210px;
  height: 100%;
  background-color: #304156;
  transition: width 0.28s;
  overflow: hidden;
  
  &.sidebar-collapsed {
    width: 64px;
  }
  
  .sidebar-logo {
    height: 50px;
    line-height: 50px;
    text-align: center;
    background-color: #2b2f3a;
    color: #fff;
    font-weight: bold;
    
    h2 {
      margin: 0;
      font-size: 16px;
    }
  }
  
  .el-menu {
    border-right: none;
    height: calc(100% - 50px);
    overflow-y: auto;
    
    &:not(.el-menu--collapse) {
      width: 210px;
    }
  }
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  transition: margin-left 0.28s;
  
  &.main-expanded {
    margin-left: 0;
  }
}

.navbar {
  height: 50px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  
  .navbar-left {
    display: flex;
    align-items: center;
    
    .hamburger {
      margin-right: 20px;
      font-size: 18px;
      color: #5a5e66;
      
      &:hover {
        background-color: rgba(0, 0, 0, 0.025);
      }
    }
  }
  
  .navbar-right {
    .el-dropdown-link {
      cursor: pointer;
      display: flex;
      align-items: center;
      color: #5a5e66;
      
      &:hover {
        color: #409EFF;
      }
    }
  }
}

.app-main {
  flex: 1;
  padding: 20px;
  background-color: #f0f2f5;
  overflow-y: auto;
}

// 响应式设计
@media (max-width: 768px) {
  .sidebar-container {
    position: fixed;
    top: 0;
    left: 0;
    z-index: 1001;
    
    &.sidebar-collapsed {
      left: -210px;
    }
  }
  
  .main-container {
    margin-left: 0;
    
    &.main-expanded {
      margin-left: 0;
    }
  }
}
</style>

