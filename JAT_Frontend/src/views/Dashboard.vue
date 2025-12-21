<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <!-- 统计卡片 -->
      <el-col :xs="24" :sm="12" :md="6" v-for="(item, index) in statCards" :key="index" class="card-panel-col">
        <div class="card-panel">
          <div class="card-panel-icon-wrapper">
            <i :class="item.icon" class="card-panel-icon"></i>
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">{{ item.title }}</div>
            <div class="card-panel-num">{{ item.value }}</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <!-- 营业额趋势图 -->
      <el-col :xs="24" :sm="24" :md="12">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>营业额趋势</span>
              <el-radio-group v-model="revenueChartType" size="small">
                <el-radio-button label="week">本周</el-radio-button>
                <el-radio-button label="month">本月</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div class="chart-container">
            <div ref="revenueChart" class="chart"></div>
          </div>
        </el-card>
      </el-col>

      <!-- 服务项目占比图 -->
      <el-col :xs="24" :sm="24" :md="12">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>服务项目占比</span>
            </div>
          </template>
          <div class="chart-container">
            <div ref="serviceChart" class="chart"></div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <!-- 今日预约列表 -->
      <el-col :xs="24" :sm="24" :md="12">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>今日预约</span>
              <el-button type="primary" size="small" @click="goToAppointment">查看更多</el-button>
            </div>
          </template>
          <div class="table-container">
            <el-table :data="todayAppointments" style="width: 100%" v-loading="appointmentLoading">
              <el-table-column prop="customerName" label="顾客" width="100"></el-table-column>
              <el-table-column prop="serviceName" label="服务项目" width="120"></el-table-column>
              <el-table-column prop="therapistName" label="技师" width="100"></el-table-column>
              <el-table-column prop="appointmentTime" label="预约时间"></el-table-column>
              <el-table-column prop="status" label="状态" width="100">
                <template #default="scope">
                  <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-card>
      </el-col>

      <!-- 今日订单列表 -->
      <el-col :xs="24" :sm="24" :md="12">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>今日订单</span>
              <el-button type="primary" size="small" @click="goToOrder">查看更多</el-button>
            </div>
          </template>
          <div class="table-container">
            <el-table :data="todayOrders" style="width: 100%" v-loading="orderLoading">
              <el-table-column prop="orderNo" label="订单号" width="120"></el-table-column>
              <el-table-column prop="customerName" label="顾客" width="100"></el-table-column>
              <el-table-column prop="amount" label="金额" width="100">
                <template #default="scope">
                  ¥{{ scope.row.amount.toFixed(2) }}
                </template>
              </el-table-column>
              <el-table-column prop="createTime" label="创建时间"></el-table-column>
              <el-table-column prop="status" label="状态" width="100">
                <template #default="scope">
                  <el-tag :type="getOrderStatusType(scope.row.status)">{{ getOrderStatusText(scope.row.status) }}</el-tag>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
// import { ref, onMounted, reactive } from 'vue'
import { ref, onMounted, watch, nextTick, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { getTodayAppointmentStats, getWeekAppointmentStats } from '@/api/appointment'
import { getTodayOrderStats, getMonthOrderStats } from '@/api/order'
import { getOrderStats } from '@/api/order'
import { getCardStats } from '@/api/card'

const router = useRouter()

// 统计卡片数据
const statCards = ref([
  { title: '今日营业额', value: '¥0', icon: 'el-icon-money' },
  { title: '今日订单数', value: '0', icon: 'el-icon-s-order' },
  { title: '今日预约数', value: '0', icon: 'el-icon-date' },
  { title: '会员总数', value: '0', icon: 'el-icon-user' }
])

// 图表相关
const revenueChart = ref(null)
const serviceChart = ref(null)
const revenueChartType = ref('week')
let revenueChartInstance = null
let serviceChartInstance = null

// 表格数据
const todayAppointments = ref([])
const todayOrders = ref([])
const appointmentLoading = ref(true)
const orderLoading = ref(true)

// 获取统计数据
const fetchStats = async () => {
  try {
    // 获取今日订单统计
    const todayOrderRes = await getTodayOrderStats()
    if (todayOrderRes.code === 200) {
      statCards.value[0].value = '¥' + todayOrderRes.data.totalAmount.toFixed(2)
      statCards.value[1].value = todayOrderRes.data.orderCount
    }

    // 获取今日预约统计
    const todayAppointmentRes = await getTodayAppointmentStats()
    if (todayAppointmentRes.code === 200) {
      statCards.value[2].value = todayAppointmentRes.data.appointmentCount
    }

    // 获取会员统计
    const cardStatsRes = await getCardStats()
    if (cardStatsRes.code === 200) {
      statCards.value[3].value = cardStatsRes.data.totalCount
    }
  } catch (error) {
    console.error('获取统计数据失败', error)
  }
}

// 获取今日预约列表
const fetchTodayAppointments = async () => {
  appointmentLoading.value = true
  try {
    const res = await getWeekAppointmentStats()
    if (res.code === 200) {
      todayAppointments.value = res.data.todayAppointments || []
    }
  } catch (error) {
    console.error('获取今日预约失败', error)
  } finally {
    appointmentLoading.value = false
  }
}

// 获取今日订单列表
const fetchTodayOrders = async () => {
  orderLoading.value = true
  try {
    const res = await getTodayOrderStats()
    if (res.code === 200) {
      todayOrders.value = res.data.todayOrders || []
    }
  } catch (error) {
    console.error('获取今日订单失败', error)
  } finally {
    orderLoading.value = false
  }
}

// 初始化营业额趋势图
const initRevenueChart = async () => {
  if (!revenueChart.value) return

  revenueChartInstance = echarts.init(revenueChart.value)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '营业额',
        type: 'bar',
        data: [0, 0, 0, 0, 0, 0, 0],
        itemStyle: {
          color: '#409EFF'
        }
      }
    ]
  }
  
  revenueChartInstance.setOption(option)
  
  // 加载实际数据
  updateRevenueChart()
}

// 更新营业额趋势图数据
const updateRevenueChart = async () => {
  try {
    let res
    if (revenueChartType.value === 'week') {
      // 获取本周数据
      res = await getMonthOrderStats()
      if (res.code === 200) {
        const weekData = res.data.weekData || []
        const xAxisData = weekData.map(item => item.date)
        const seriesData = weekData.map(item => item.amount)
        
        revenueChartInstance.setOption({
          xAxis: {
            data: xAxisData
          },
          series: [
            {
              data: seriesData
            }
          ]
        })
      }
    } else {
      // 获取本月数据
      res = await getMonthOrderStats()
      if (res.code === 200) {
        const monthData = res.data.monthData || []
        const xAxisData = monthData.map(item => item.date)
        const seriesData = monthData.map(item => item.amount)
        
        revenueChartInstance.setOption({
          xAxis: {
            data: xAxisData
          },
          series: [
            {
              data: seriesData
            }
          ]
        })
      }
    }
  } catch (error) {
    console.error('获取营业额数据失败', error)
  }
}

// 初始化服务项目占比图
const initServiceChart = async () => {
  if (!serviceChart.value) return

  serviceChartInstance = echarts.init(serviceChart.value)
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center',
      data: []
    },
    series: [
      {
        name: '服务项目',
        type: 'pie',
        radius: ['50%', '70%'],
        avoidLabelOverlap: false,
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: '16',
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: []
      }
    ]
  }
  
  serviceChartInstance.setOption(option)
  
  // 加载实际数据
  try {
    const res = await getOrderStats()
    if (res.code === 200) {
      const serviceData = res.data.serviceStats || []
      const legendData = serviceData.map(item => item.name)
      const seriesData = serviceData.map(item => ({
        name: item.name,
        value: item.count
      }))
      
      serviceChartInstance.setOption({
        legend: {
          data: legendData
        },
        series: [
          {
            data: seriesData
          }
        ]
      })
    }
  } catch (error) {
    console.error('获取服务项目数据失败', error)
  }
}

// 窗口大小变化时重新调整图表大小
const handleResize = () => {
  revenueChartInstance && revenueChartInstance.resize()
  serviceChartInstance && serviceChartInstance.resize()
}

// 预约状态处理
const getStatusType = (status) => {
  const statusMap = {
    0: 'info',    // 待确认
    1: 'success', // 已确认
    2: 'warning', // 进行中
    3: 'success', // 已完成
    4: 'danger'   // 已取消
  }
  return statusMap[status] || 'info'
}

const getStatusText = (status) => {
  const statusMap = {
    0: '待确认',
    1: '已确认',
    2: '进行中',
    3: '已完成',
    4: '已取消'
  }
  return statusMap[status] || '未知'
}

// 订单状态处理
const getOrderStatusType = (status) => {
  const statusMap = {
    0: 'info',    // 待支付
    1: 'success', // 已支付
    2: 'danger',  // 已取消
    3: 'warning'  // 已退款
  }
  return statusMap[status] || 'info'
}

const getOrderStatusText = (status) => {
  const statusMap = {
    0: '待支付',
    1: '已支付',
    2: '已取消',
    3: '已退款'
  }
  return statusMap[status] || '未知'
}

// 页面跳转
const goToAppointment = () => {
  router.push('/appointment')
}

const goToOrder = () => {
  router.push('/order')
}

// 监听图表类型变化
watch(revenueChartType, () => {
  updateRevenueChart()
})

onMounted(() => {
  fetchStats()
  fetchTodayAppointments()
  fetchTodayOrders()
  
  // 初始化图表
  nextTick(() => {
    initRevenueChart()
    initServiceChart()
    
    // 监听窗口大小变化
    window.addEventListener('resize', handleResize)
  })
})

onBeforeUnmount(() => {
  // 移除窗口大小变化监听
  window.removeEventListener('resize', handleResize)
  
  // 销毁图表实例
  revenueChartInstance && revenueChartInstance.dispose()
  serviceChartInstance && serviceChartInstance.dispose()
})
</script>

<style lang="scss" scoped>
.dashboard-container {
  padding: 20px;
  
  .card-panel-col {
    margin-bottom: 20px;
  }
  
  .card-panel {
    height: 108px;
    cursor: pointer;
    font-size: 12px;
    position: relative;
    overflow: hidden;
    color: #666;
    background: #fff;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    border-radius: 4px;
    
    &:hover {
      .card-panel-icon-wrapper {
        color: #fff;
      }
      
      .icon-people {
        background: #40c9c6;
      }
      
      .icon-message {
        background: #36a3f7;
      }
      
      .icon-money {
        background: #f4516c;
      }
      
      .icon-shopping {
        background: #34bfa3;
      }
    }
    
    .card-panel-icon-wrapper {
      float: left;
      margin: 14px 0 0 14px;
      padding: 16px;
      transition: all 0.38s ease-out;
      border-radius: 6px;
    }
    
    .card-panel-icon {
      float: left;
      font-size: 48px;
    }
    
    .card-panel-description {
      float: right;
      font-weight: bold;
      margin: 26px 26px 0 0;
      
      .card-panel-text {
        line-height: 18px;
        color: rgba(0, 0, 0, 0.45);
        font-size: 16px;
        margin-bottom: 12px;
      }
      
      .card-panel-num {
        font-size: 20px;
      }
    }
  }
  
  .chart-row {
    margin-bottom: 20px;
  }
  
  .chart-container {
    position: relative;
    width: 100%;
    height: 300px;
  }
  
  .chart {
    width: 100%;
    height: 100%;
  }
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .table-container {
    margin-top: 10px;
  }
}

@media (max-width: 768px) {
  .dashboard-container {
    padding: 10px;
    
    .card-panel {
      height: auto;
      padding: 10px;
      
      .card-panel-icon-wrapper {
        float: none;
        margin: 0 auto;
        padding: 10px;
        text-align: center;
      }
      
      .card-panel-icon {
        float: none;
        font-size: 36px;
      }
      
      .card-panel-description {
        float: none;
        margin: 10px 0 0 0;
        text-align: center;
      }
    }
    
    .chart-container {
      height: 250px;
    }
  }
}
</style>
