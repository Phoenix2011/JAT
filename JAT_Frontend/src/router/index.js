import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/auth'
import { ElMessage } from 'element-plus'

// 路由懒加载
const Login = () => import('../views/Login.vue')
const Layout = () => import('../layout/index.vue')
const Dashboard = () => import('../views/Dashboard.vue')
const NotFound = () => import('../views/error/404.vue')

// 业务页面
const Appointment = () => import('../views/appointment/index.vue')
const Customer = () => import('../views/customer/index.vue')
const Order = () => import('../views/order/index.vue')
const Service = () => import('../views/service/index.vue')
const ServiceItem = () => import('../views/serviceItem/index.vue')
const Therapist = () => import('../views/therapist/index.vue')
const Card = () => import('../views/card/index.vue')
const Statistics = () => import('../views/statistics/index.vue')

// 公共路由
export const constantRoutes = [
  {
    path: '/login',
    component: Login,
    hidden: true
  },
  {
    path: '/404',
    component: NotFound,
    hidden: true
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        component: Dashboard,
        name: 'Dashboard',
        meta: { title: '首页', icon: 'dashboard' }
      },
      {
        path: 'appointment',
        component: Appointment,
        name: 'Appointment',
        meta: { title: '预约管理', icon: 'calendar' }
      },
      {
        path: 'customer',
        component: Customer,
        name: 'Customer',
        meta: { title: '客户管理', icon: 'user' }
      },
      {
        path: 'order',
        component: Order,
        name: 'Order',
        meta: { title: '订单管理', icon: 'wallet' }
      },
      {
        path: 'service',
        component: Service,
        name: 'Service',
        meta: { title: '服务管理', icon: 'shopping-cart' }
      },
      {
        path: 'serviceItem',
        component: ServiceItem,
        name: 'ServiceItem',
        meta: { title: '服务项目管理', icon: 'serviceItem' }
      },
      {
        path: 'therapist',
        component: Therapist,
        name: 'Therapist',
        meta: { title: '技师管理', icon: 'user-tie' }
      },
      {
        path: 'card',
        component: Card,
        name: 'Card',
        meta: { title: '会员卡管理', icon: 'credit-card' }
      },
      {
        path: 'statistics',
        component: Statistics,
        name: 'Statistics',
        meta: { title: '统计分析', icon: 'chart-bar' }
      }
    ]
  },
  // 404页面必须放在最后
  { path: '/:pathMatch(.*)*', redirect: '/404', hidden: true }
]

const router = createRouter({
  history: createWebHistory(),
  routes: constantRoutes,
  scrollBehavior: () => ({ top: 0 })
})

// 白名单路由，不需要认证
const whiteList = ['/login', '/404']

// 路由守卫
router.beforeEach((to, from, next) => {
  const hasToken = getToken()
  
  if (hasToken) {
    if (to.path === '/login') {
      // 已登录用户访问登录页，重定向到首页
      next({ path: '/' })
    } else {
      next()
    }
  } else {
    if (whiteList.indexOf(to.path) !== -1) {
      // 在白名单中，直接进入
      next()
    } else {
      // 其他没有访问权限的页面将重定向到登录页面
      ElMessage.error('请先登录')
      next(`/login?redirect=${to.path}`)
    }
  }
})

export default router
