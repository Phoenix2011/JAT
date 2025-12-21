<template>
  <div class="statistics-container">
    <el-row :gutter="20">
      <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>数据概览</span>
              <div class="date-picker">
                <el-date-picker
                  v-model="dateRange"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  value-format="YYYY-MM-DD"
                  @change="handleDateRangeChange"
                />
              </div>
            </div>
          </template>
          
          <el-row :gutter="20">
            <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
              <div class="data-card">
                <div class="data-icon">
                  <el-icon><Money /></el-icon>
                </div>
                <div class="data-info">
                  <div class="data-title">总营业额</div>
                  <div class="data-value">¥{{ statistics.totalRevenue.toFixed(2) }}</div>
                </div>
              </div>
            </el-col>
            
            <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
              <div class="data-card">
                <div class="data-icon">
                  <el-icon><ShoppingCart /></el-icon>
                </div>
                <div class="data-info">
                  <div class="data-title">订单总数</div>
                  <div class="data-value">{{ statistics.totalOrders }}</div>
                </div>
              </div>
            </el-col>
            
            <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
              <div class="data-card">
                <div class="data-icon">
                  <el-icon><User /></el-icon>
                </div>
                <div class="data-info">
                  <div class="data-title">客户总数</div>
                  <div class="data-value">{{ statistics.totalCustomers }}</div>
                </div>
              </div>
            </el-col>
            
            <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
              <div class="data-card">
                <div class="data-icon">
                  <el-icon><Calendar /></el-icon>
                </div>
                <div class="data-info">
                  <div class="data-title">预约总数</div>
                  <div class="data-value">{{ statistics.totalAppointments }}</div>
                </div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <!-- 营业额趋势图 -->
      <el-col :xs="24" :sm="24" :md="12">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>营业额趋势</span>
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
      <!-- 客户增长趋势 -->
      <el-col :xs="24" :sm="24" :md="12">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>客户增长趋势</span>
            </div>
          </template>
          <div class="chart-container">
            <div ref="customerChart" class="chart"></div>
          </div>
        </el-card>
      </el-col>

      <!-- 预约完成率 -->
      <el-col :xs="24" :sm="24" :md="12">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>预约完成率</span>
            </div>
          </template>
          <div class="chart-container">
            <div ref="appointmentChart" class="chart"></div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, onBeforeUnmount } from 'vue'
import { Money, ShoppingCart, User, Calendar } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

// 日期范围
const dateRange = ref([])

// 统计数据
const statistics = reactive({
  totalRevenue: 0,
  totalOrders: 0,
  totalCustomers: 0,
  totalAppointments: 0
})

// 图表引用
const revenueChart = ref(null)
const serviceChart = ref(null)
const customerChart = ref(null)
const appointmentChart = ref(null)

// 图表实例
let revenueChartInstance = null
let serviceChartInstance = null
let customerChartInstance = null
let appointmentChartInstance = null

// 获取统计数据
const getStatisticsData = async () => {
  // 模拟数据
  Object.assign(statistics, {
    totalRevenue: 125680.50,
    totalOrders: 1256,
    totalCustomers: 568,
    totalAppointments: 2345
  })
}

// 初始化营业额趋势图
const initRevenueChart = () => {
  if (!revenueChart.value) return

  revenueChartInstance = echarts.init(revenueChart.value)
  
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '营业额',
        type: 'line',
        data: [8200, 9320, 9010, 9340, 12900, 13300, 13200, 13200, 12300, 13400, 13400, 13200],
        smooth: true,
        itemStyle: {
          color: '#409EFF'
        }
      }
    ]
  }
  
  revenueChartInstance.setOption(option)
}

// 初始化服务项目占比图
const initServiceChart = () => {
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
      top: 'center'
    },
    series: [
      {
        name: '服务项目',
        type: 'pie',
        radius: ['50%', '70%'],
        data: [
          { value: 335, name: '全身按摩' },
          { value: 310, name: '足疗' },
          { value: 234, name: '艾灸' },
          { value: 135, name: '拔罐' },
          { value: 148, name: '刮痧' }
        ]
      }
    ]
  }
  
  serviceChartInstance.setOption(option)
}

// 初始化客户增长趋势图
const initCustomerChart = () => {
  if (!customerChart.value) return

  customerChartInstance = echarts.init(customerChart.value)
  
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '新增客户',
        type: 'bar',
        data: [45, 52, 48, 61, 55, 67, 73, 69, 72, 78, 81, 85],
        itemStyle: {
          color: '#67C23A'
        }
      }
    ]
  }
  
  customerChartInstance.setOption(option)
}

// 初始化预约完成率图
const initAppointmentChart = () => {
  if (!appointmentChart.value) return

  appointmentChartInstance = echarts.init(appointmentChart.value)
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c}%'
    },
    series: [
      {
        name: '预约状态',
        type: 'pie',
        radius: '70%',
        data: [
          { value: 85, name: '已完成' },
          { value: 10, name: '已取消' },
          { value: 5, name: '未到店' }
        ],
        itemStyle: {
          emphasis: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }
  
  appointmentChartInstance.setOption(option)
}

// 窗口大小变化处理
const handleResize = () => {
  revenueChartInstance && revenueChartInstance.resize()
  serviceChartInstance && serviceChartInstance.resize()
  customerChartInstance && customerChartInstance.resize()
  appointmentChartInstance && appointmentChartInstance.resize()
}

// 日期范围变化
const handleDateRangeChange = (dates) => {
  if (dates && dates.length === 2) {
    // 根据日期范围重新获取数据
    getStatisticsData()
  }
}

onMounted(() => {
  getStatisticsData()
  
  nextTick(() => {
    initRevenueChart()
    initServiceChart()
    initCustomerChart()
    initAppointmentChart()
    
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
  customerChartInstance && customerChartInstance.dispose()
  appointmentChartInstance && appointmentChartInstance.dispose()
})
</script>

<style lang="scss" scoped>
.statistics-container {
  padding: 20px;
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .chart-row {
    margin-top: 20px;
  }
  
  .data-card {
    display: flex;
    align-items: center;
    padding: 20px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 8px;
    color: white;
    margin-bottom: 20px;
    
    .data-icon {
      font-size: 48px;
      margin-right: 20px;
      opacity: 0.8;
    }
    
    .data-info {
      flex: 1;
      
      .data-title {
        font-size: 14px;
        opacity: 0.9;
        margin-bottom: 8px;
      }
      
      .data-value {
        font-size: 24px;
        font-weight: bold;
      }
    }
    
    &:nth-child(2) .data-card {
      background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
    }
    
    &:nth-child(3) .data-card {
      background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    }
    
    &:nth-child(4) .data-card {
      background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
    }
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
}

@media (max-width: 768px) {
  .statistics-container {
    padding: 10px;
    
    .data-card {
      padding: 15px;
      
      .data-icon {
        font-size: 36px;
        margin-right: 15px;
      }
      
      .data-info .data-value {
        font-size: 20px;
      }
    }
    
    .chart-container {
      height: 250px;
    }
  }
}
</style>

