<template>
  <div class="therapist-container">
    <div class="search-bar">
      <el-form :inline="true" :model="queryParams" class="demo-form-inline">
        <el-form-item label="姓名">
          <el-input v-model="queryParams.name" placeholder="请输入技师姓名" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="queryParams.phone" placeholder="请输入技师电话" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>技师列表</span>
          <el-button type="primary" @click="handleAdd" v-hasPermi="['therapist:add']">新增技师</el-button>
        </div>
      </template>
      
      <el-table v-loading="loading" :data="therapistList" style="width: 100%">
        <el-table-column type="index" width="50" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="scope">
            {{ scope.row.gender === 1 ? '男' : '女' }}
          </template>
        </el-table-column>
        <el-table-column prop="age" label="年龄" width="80" />
        <el-table-column prop="phone" label="电话" width="120" />
        <el-table-column prop="specialties" label="专长" width="180" />
        <el-table-column prop="entryDate" label="入职日期" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '在职' : '离职' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleView(scope.row)" v-hasPermi="['therapist:query']">查看</el-button>
            <el-button type="success" size="small" @click="handleEdit(scope.row)" v-hasPermi="['therapist:edit']">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row)" v-hasPermi="['therapist:delete']">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 添加或修改技师对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="therapistForm" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入技师姓名" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="form.gender">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="0">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="年龄" prop="age">
          <el-input-number v-model="form.age" :min="18" :max="60" />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入技师电话" />
        </el-form-item>
        <el-form-item label="专长" prop="specialties">
          <el-input v-model="form.specialties" placeholder="请输入技师专长" />
        </el-form-item>
        <el-form-item label="入职日期" prop="entryDate">
          <el-date-picker v-model="form.entryDate" type="date" placeholder="选择入职日期" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">在职</el-radio>
            <el-radio :label="0">离职</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="提成比例" prop="commissionRate">
          <el-input-number v-model="form.commissionRate" :min="0" :max="1" :step="0.01" :precision="2" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancel">取 消</el-button>
          <el-button type="primary" @click="submitForm">确 定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 技师详情对话框 -->
    <el-dialog title="技师详情" v-model="viewOpen" width="800px" append-to-body>
      <el-tabs v-model="activeTab">
        <el-tab-pane label="基本信息" name="basic">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="姓名">{{ therapistDetail.name }}</el-descriptions-item>
            <el-descriptions-item label="性别">{{ therapistDetail.gender === 1 ? '男' : '女' }}</el-descriptions-item>
            <el-descriptions-item label="年龄">{{ therapistDetail.age }}</el-descriptions-item>
            <el-descriptions-item label="电话">{{ therapistDetail.phone }}</el-descriptions-item>
            <el-descriptions-item label="专长">{{ therapistDetail.specialties }}</el-descriptions-item>
            <el-descriptions-item label="入职日期">{{ therapistDetail.entryDate }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="therapistDetail.status === 1 ? 'success' : 'danger'">
                {{ therapistDetail.status === 1 ? '在职' : '离职' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="提成比例">{{ therapistDetail.commissionRate * 100 }}%</el-descriptions-item>
            <el-descriptions-item label="备注" :span="2">{{ therapistDetail.remark }}</el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>
        
        <el-tab-pane label="服务记录" name="serviceRecords">
          <div class="filter-container">
            <el-date-picker
              v-model="serviceRecordDateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              @change="handleServiceRecordDateChange"
            />
          </div>
          <el-table :data="serviceRecords" style="width: 100%" v-loading="serviceRecordsLoading">
            <el-table-column prop="serviceName" label="服务项目" />
            <el-table-column prop="customerName" label="顾客" />
            <el-table-column prop="serviceTime" label="服务时间" />
            <el-table-column prop="duration" label="时长(分钟)" />
            <el-table-column prop="price" label="价格">
              <template #default="scope">
                ¥{{ scope.row.price.toFixed(2) }}
              </template>
            </el-table-column>
            <el-table-column prop="commission" label="提成">
              <template #default="scope">
                ¥{{ scope.row.commission.toFixed(2) }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态">
              <template #default="scope">
                <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
                  {{ scope.row.status === 1 ? '已完成' : '进行中' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
          <div class="pagination-container">
            <el-pagination
              v-model:current-page="serviceRecordQuery.pageNum"
              v-model:page-size="serviceRecordQuery.pageSize"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next, jumper"
              :total="serviceRecordTotal"
              @size-change="handleServiceRecordSizeChange"
              @current-change="handleServiceRecordCurrentChange"
            />
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="业绩统计" name="performance">
          <div class="filter-container">
            <el-radio-group v-model="performanceTimeRange" @change="handlePerformanceTimeRangeChange">
              <el-radio-button label="week">本周</el-radio-button>
              <el-radio-button label="month">本月</el-radio-button>
              <el-radio-button label="year">本年</el-radio-button>
            </el-radio-group>
          </div>
          
          <el-card class="performance-card">
            <template #header>
              <div class="card-header">
                <span>业绩概览</span>
              </div>
            </template>
            <el-row :gutter="20">
              <el-col :span="8">
                <div class="stat-item">
                  <div class="stat-title">服务人次</div>
                  <div class="stat-value">{{ performanceStats.serviceCount || 0 }}</div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="stat-item">
                  <div class="stat-title">服务金额</div>
                  <div class="stat-value">¥{{ performanceStats.serviceAmount ? performanceStats.serviceAmount.toFixed(2) : '0.00' }}</div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="stat-item">
                  <div class="stat-title">提成金额</div>
                  <div class="stat-value">¥{{ performanceStats.commissionAmount ? performanceStats.commissionAmount.toFixed(2) : '0.00' }}</div>
                </div>
              </el-col>
            </el-row>
          </el-card>
          
          <div class="chart-container" ref="performanceChart"></div>
        </el-tab-pane>
        
        <el-tab-pane label="预约时间" name="schedule">
          <div class="filter-container">
            <el-date-picker
              v-model="scheduleDate"
              type="date"
              placeholder="选择日期"
              @change="handleScheduleDateChange"
            />
          </div>
          
          <el-table :data="timeSlots" style="width: 100%" v-loading="timeSlotsLoading">
            <el-table-column prop="timeSlot" label="时间段" />
            <el-table-column prop="status" label="状态">
              <template #default="scope">
                <el-tag :type="scope.row.status === 1 ? 'danger' : 'success'">
                  {{ scope.row.status === 1 ? '已预约' : '可预约' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="customerName" label="顾客" />
            <el-table-column prop="serviceName" label="服务项目" />
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as echarts from 'echarts'
import { getTherapistList, getTherapist, addTherapist, updateTherapist, deleteTherapist, getTherapistServiceRecords, getTherapistTimeSlots, getTherapistPerformance } from '@/api/therapist'

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  name: '',
  phone: ''
})

const therapistList = ref([])
const total = ref(0)
const loading = ref(true)

// 弹出层标题
const title = ref('')
const open = ref(false)
const viewOpen = ref(false)

// 表单参数
const form = reactive({
  id: null,
  name: '',
  gender: 1,
  age: 30,
  phone: '',
  specialties: '',
  entryDate: '',
  status: 1,
  commissionRate: 0.3,
  remark: ''
})

// 表单校验规则
const rules = {
  name: [
    { required: true, message: '技师姓名不能为空', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '技师性别不能为空', trigger: 'change' }
  ],
  phone: [
    { required: true, message: '技师电话不能为空', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  entryDate: [
    { required: true, message: '入职日期不能为空', trigger: 'change' }
  ],
  status: [
    { required: true, message: '状态不能为空', trigger: 'change' }
  ]
}

// 技师详情数据
const therapistDetail = ref({})
const activeTab = ref('basic')

// 服务记录相关
const serviceRecords = ref([])
const serviceRecordsLoading = ref(false)
const serviceRecordTotal = ref(0)
const serviceRecordQuery = reactive({
  pageNum: 1,
  pageSize: 10,
  startDate: '',
  endDate: ''
})
const serviceRecordDateRange = ref([])

// 业绩统计相关
const performanceTimeRange = ref('month')
const performanceStats = ref({})
const performanceChart = ref(null)
let chartInstance = null

// 预约时间相关
const scheduleDate = ref(new Date())
const timeSlots = ref([])
const timeSlotsLoading = ref(false)

// 表单引用
const therapistForm = ref(null)

// 获取技师列表
const getList = () => {
  loading.value = true
  getTherapistList(queryParams).then(response => {
    therapistList.value = response.data.records
    total.value = response.data.total
    loading.value = false
  })
}

// 查询按钮操作
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置按钮操作
const resetQuery = () => {
  queryParams.name = ''
  queryParams.phone = ''
  handleQuery()
}

// 表单重置
const reset = () => {
  form.id = null
  form.name = ''
  form.gender = 1
  form.age = 30
  form.phone = ''
  form.specialties = ''
  form.entryDate = ''
  form.status = 1
  form.commissionRate = 0.3
  form.remark = ''
}

// 取消按钮
const cancel = () => {
  open.value = false
  reset()
}

// 新增按钮操作
const handleAdd = () => {
  reset()
  open.value = true
  title.value = '添加技师'
}

// 修改按钮操作
const handleEdit = (row) => {
  reset()
  const id = row.id || row.therapistId
  getTherapist(id).then(response => {
    Object.assign(form, response.data)
    open.value = true
    title.value = '修改技师'
  })
}

// 查看按钮操作
const handleView = (row) => {
  const id = row.id || row.therapistId
  getTherapist(id).then(response => {
    therapistDetail.value = response.data
    viewOpen.value = true
    activeTab.value = 'basic'
    
    // 初始化日期范围为当月
    const now = new Date()
    const startDate = new Date(now.getFullYear(), now.getMonth(), 1)
    const endDate = new Date(now.getFullYear(), now.getMonth() + 1, 0)
    serviceRecordDateRange.value = [startDate, endDate]
    
    // 加载服务记录
    loadServiceRecords(id)
    
    // 加载业绩统计
    loadPerformanceStats(id)
    
    // 加载预约时间
    loadTimeSlots(id)
  })
}

// 加载技师服务记录
const loadServiceRecords = (therapistId) => {
  serviceRecordsLoading.value = true
  
  const params = {
    ...serviceRecordQuery
  }
  
  if (serviceRecordDateRange.value && serviceRecordDateRange.value.length === 2) {
    params.startDate = formatDate(serviceRecordDateRange.value[0])
    params.endDate = formatDate(serviceRecordDateRange.value[1])
  }
  
  getTherapistServiceRecords(therapistId, params).then(response => {
    serviceRecords.value = response.data.records
    serviceRecordTotal.value = response.data.total
    serviceRecordsLoading.value = false
  })
}

// 加载技师业绩统计
const loadPerformanceStats = (therapistId) => {
  const params = {
    timeRange: performanceTimeRange.value
  }
  
  getTherapistPerformance(therapistId, params).then(response => {
    performanceStats.value = response.data.stats
    
    // 初始化图表
    nextTick(() => {
      initPerformanceChart(response.data.chartData)
    })
  })
}

// 初始化业绩图表
const initPerformanceChart = (chartData) => {
  if (!performanceChart.value) return
  
  if (chartInstance) {
    chartInstance.dispose()
  }
  
  chartInstance = echarts.init(performanceChart.value)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    legend: {
      data: ['服务金额', '提成金额']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: chartData.xAxis
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '服务金额',
        type: 'bar',
        data: chartData.serviceAmount,
        itemStyle: {
          color: '#409EFF'
        }
      },
      {
        name: '提成金额',
        type: 'bar',
        data: chartData.commissionAmount,
        itemStyle: {
          color: '#67C23A'
        }
      }
    ]
  }
  
  chartInstance.setOption(option)
}

// 加载技师预约时间
const loadTimeSlots = (therapistId) => {
  timeSlotsLoading.value = true
  
  getTherapistTimeSlots(therapistId, formatDate(scheduleDate.value)).then(response => {
    timeSlots.value = response.data
    timeSlotsLoading.value = false
  })
}

// 服务记录日期范围变化
const handleServiceRecordDateChange = () => {
  serviceRecordQuery.pageNum = 1
  loadServiceRecords(therapistDetail.value.id)
}

// 业绩统计时间范围变化
const handlePerformanceTimeRangeChange = () => {
  loadPerformanceStats(therapistDetail.value.id)
}

// 预约时间日期变化
const handleScheduleDateChange = () => {
  loadTimeSlots(therapistDetail.value.id)
}

// 提交表单
const submitForm = () => {
  therapistForm.value.validate(valid => {
    if (valid) {
      if (form.id != null) {
        updateTherapist(form).then(response => {
          ElMessage.success('修改成功')
          open.value = false
          getList()
        })
      } else {
        addTherapist(form).then(response => {
          ElMessage.success('新增成功')
          open.value = false
          getList()
        })
      }
    }
  })
}

// 删除按钮操作
const handleDelete = (row) => {
  const id = row.id || row.therapistId
  ElMessageBox.confirm('确认删除该技师吗?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    deleteTherapist(id).then(() => {
      ElMessage.success('删除成功')
      getList()
    })
  }).catch(() => {})
}

// 服务记录分页大小改变
const handleServiceRecordSizeChange = (val) => {
  serviceRecordQuery.pageSize = val
  loadServiceRecords(therapistDetail.value.id)
}

// 服务记录分页页码改变
const handleServiceRecordCurrentChange = (val) => {
  serviceRecordQuery.pageNum = val
  loadServiceRecords(therapistDetail.value.id)
}

// 分页大小改变
const handleSizeChange = (val) => {
  queryParams.pageSize = val
  getList()
}

// 分页页码改变
const handleCurrentChange = (val) => {
  queryParams.pageNum = val
  getList()
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  
  return `${year}-${month}-${day}`
}

// 窗口大小变化时重新调整图表大小
const handleResize = () => {
  chartInstance && chartInstance.resize()
}

onMounted(() => {
  getList()
  
  // 监听窗口大小变化
  window.addEventListener('resize', handleResize)
})

// onBeforeUnmount(() => {
//   // 移除窗口大小变化监听
//   window.removeEventListener('resize', handleResize)
//
//   // 销毁图表实例
//   chartInstance && chartInstance.dispose()
// })
</script>

<style lang="scss" scoped>
.therapist-container {
  padding: 20px;
  
  .search-bar {
    margin-bottom: 20px;
  }
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .pagination-container {
    margin-top: 20px;
    text-align: right;
  }
  
  .filter-container {
    margin-bottom: 20px;
  }
  
  .performance-card {
    margin-bottom: 20px;
    
    .stat-item {
      text-align: center;
      padding: 20px 0;
      
      .stat-title {
        font-size: 14px;
        color: #606266;
        margin-bottom: 10px;
      }
      
      .stat-value {
        font-size: 24px;
        font-weight: bold;
        color: #303133;
      }
    }
  }
  
  .chart-container {
    width: 100%;
    height: 400px;
  }
}

@media screen and (max-width: 768px) {
  .therapist-container {
    padding: 10px;
    
    .el-form-item {
      margin-bottom: 10px;
    }
    
    .chart-container {
      height: 300px;
    }
  }
}
</style>
