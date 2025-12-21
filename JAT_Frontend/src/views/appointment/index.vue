<template>
  <div class="appointment-container">
    <div class="search-bar">
      <el-form :inline="true" :model="queryParams" class="demo-form-inline">
        <el-form-item label="顾客ID" style="width:10%">
          <el-input v-model="queryParams.customerId" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="顾客姓名" style="width:12%">
          <el-input v-model="queryParams.customerName" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="技师姓名" style="width:10%">
          <el-input v-model="queryParams.therapistName" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="预约日期" style="width:20%">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            @change="handleDateRangeChange"
          />
        </el-form-item>
        <el-form-item label="状态" style="width:10%">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option
              v-for="dict in statusOptions"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-tabs v-model="activeTab" @tab-click="handleTabClick">
      <el-tab-pane label="预约列表" name="list">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>预约列表</span>
              <el-button type="primary" @click="handleAdd" v-hasPermi="['appointment:add']">新增预约</el-button>
            </div>
          </template>

          <el-table v-loading="loading" :data="appointmentList" @row-dblclick="handleRowDblClick"
                    @sort-change="handleSortChange" style="width: 100%">
<!--            <el-table-column type="index" width="50" />-->
            <el-table-column prop="customer_id" label="顾客ID" width="80" />
            <el-table-column prop="customer_name" label="顾客姓名" width="180" />
            <el-table-column prop="service_name" label="服务项目" width="100" />
            <el-table-column prop="therapist_name" label="技师" width="80" />
            <el-table-column prop="appointment_date" label="预约日期" width="120" />
            <el-table-column prop="start_time" label="预约时间" width="110" sortable="custom" />
            <el-table-column prop="end_time" label="结束时间" width="100" />
            <el-table-column prop="duration" label="时长(分钟)" width="90" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">
                  {{ getStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="update_time" label="更新时间" width="160" sortable="custom" >
              <template #default="scope">
                {{ scope.row.update_time ? dayjs(scope.row.update_time).format('MM-DD HH:mm') : '' }}
              </template>
            </el-table-column>
            <el-table-column prop="remark" label="备注" />
            <el-table-column label="操作" width="260" fixed="right">
              <template #default="scope">
                <el-button type="primary" size="small" @click="handleView(scope.row)" v-hasPermi="['appointment:query']">查看</el-button>
                <el-button type="success" size="small" @click="handleEdit(scope.row)" v-hasPermi="['appointment:edit']" v-if="scope.row.status !== 3 && scope.row.status !== 4">编辑</el-button>
                <el-button type="warning" size="small" @click="handleUpdateStatus(scope.row, 3)" v-hasPermi="['appointment:edit']" v-if="scope.row.status === 2">完成</el-button>
                <el-button type="danger" size="small" @click="handleUpdateStatus(scope.row, 4)" v-hasPermi="['appointment:edit']" v-if="scope.row.status !== 3 && scope.row.status !== 4">取消</el-button>
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
      </el-tab-pane>

      <el-tab-pane label="预约日历" name="calendar">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>预约日历</span>
              <div class="calendar-controls">
                <el-select v-model="calendarFilter.therapist_id" placeholder="选择技师" clearable @change="handleCalendarFilterChange">
                  <el-option
                    v-for="item in therapistOptions"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id"
                  />
                </el-select>
                <el-button-group>
                  <el-button @click="prevMonth">上个月</el-button>
                  <el-button @click="today">本月</el-button>
                  <el-button @click="nextMonth">下个月</el-button>
                </el-button-group>
              </div>
            </div>
          </template>

          <div class="calendar-container">
            <div class="calendar-header">
              <div class="calendar-title">{{ calendarTitle }}</div>
            </div>

            <div class="calendar-body">
              <div class="calendar-weekdays">
                <div class="weekday" v-for="day in weekdays" :key="day">{{ day }}</div>
              </div>

              <div class="calendar-days">
                <div
                  v-for="(day, index) in calendarDays"
                  :key="index"
                  :class="['calendar-day', {
                    'other-month': day.otherMonth, 
                    'today': day.isToday,
                    'has-appointments': day.appointments && day.appointments.length > 0
                  }]"
                  @click="handleDayClick(day)"
                >
                  <div class="day-number">{{ day.day }}</div>
                  <div class="day-appointments" v-if="day.appointments && day.appointments.length > 0">
                    <div class="appointment-count">{{ day.appointments.length }}个预约</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- 添加或修改预约对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="appointmentFormRef" :model="appointmentForm" :rules="appointmentRules" label-width="100px">
        <el-form-item label="顾客" prop="customerId">
          <el-select
              v-model="queryCustomerParam.param"
              filterable
              remote
              reserve-keyword
              placeholder="请输入顾客姓名或ID"
              :loading="customerLoading"
              :remote-method="remoteSearchCustomer"
              style="width: 100%"
              @change="handleCustomerSelect"
          >
            <el-option
              v-for="item in customerOptions"
              :key="item.id"
              :label="'[' + item.id + '] ' + item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="服务项目" prop="service_id">
          <el-select v-model="appointmentForm.serviceId" placeholder="请选择服务项目"
                     style="width: 100%" @change="handleServiceChange">
            <el-option
              v-for="item in serviceOptions"
              :key="item.id"
              :label="item.name + ' (' + item.id + ')'"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="技师" prop="therapist_id">
          <el-select v-model="appointmentForm.therapistId" placeholder="请选择技师" style="width: 100%">
            <el-option
              v-for="item in therapistOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="预约日期" prop="appointment_date">
          <el-date-picker v-model="appointmentForm.appointmentDate" type="date" placeholder="选择日期" style="width: 100%" @change="handleDateChange" />
        </el-form-item>
        <el-form-item label="预约时间" prop="start_time">
          <el-select v-model="appointmentForm.startTime" placeholder="请选择时间段" style="width: 100%">
            <el-option
              v-for="item in timeSlotOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
              :disabled="item.disabled"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="时长(分钟)" prop="duration">
          <el-input-number v-model="appointmentForm.duration" :min="15" :max="480" :step="15" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="appointmentForm.status" placeholder="请选择状态" style="width: 100%">
            <el-option
              v-for="dict in statusOptions"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="appointmentForm.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancel">取 消</el-button>
          <el-button type="primary" @click="submitForm">确 定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 查看预约详情对话框 -->
    <el-dialog title="预约详情" v-model="viewOpen" width="600px" append-to-body>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="顾客ID">{{ appointmentDetail.customer_id }}</el-descriptions-item>
        <el-descriptions-item label="顾客姓名">{{ appointmentDetail.customer_name }}</el-descriptions-item>
        <el-descriptions-item label="顾客电话">{{ appointmentDetail.customer_phone }}</el-descriptions-item>
        <el-descriptions-item label="服务项目">{{ appointmentDetail.service_name }}</el-descriptions-item>
        <el-descriptions-item label="技师">{{ appointmentDetail.therapist_name }}</el-descriptions-item>
        <el-descriptions-item label="预约时间">{{ appointmentDetail.start_time }}</el-descriptions-item>
        <el-descriptions-item label="预计结束时间">{{ appointmentDetail.end_time }}</el-descriptions-item>
        <el-descriptions-item label="时长">{{ appointmentDetail.duration }}分钟</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(appointmentDetail.status)">
            {{ getStatusText(appointmentDetail.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ appointmentDetail.create_time }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ appointmentDetail.remark }}</el-descriptions-item>
      </el-descriptions>

      <div class="appointment-actions" v-if="appointmentDetail.status !== 3 && appointmentDetail.status !== 4">
        <el-button type="primary" @click="handleEditFromView">编辑</el-button>
        <el-button type="warning" @click="handleUpdateStatusFromView(3)" v-if="appointmentDetail.status === 2">完成</el-button>
        <el-button type="danger" @click="handleUpdateStatusFromView(4)">取消</el-button>
      </div>
    </el-dialog>

    <!-- 日历日期预约详情对话框 -->
    <el-dialog :title="selectedDate ? formatDate(selectedDate, 'YYYY年MM月DD日') + '预约列表' : '预约列表'" v-model="dayDetailOpen" width="60%" append-to-body>
      <el-table :data="dayAppointments" style="width: 100%">
        <el-table-column prop="customer_id" label="顾客ID" width="70" />
        <el-table-column prop="customer_name" label="顾客姓名" width="160" />
        <el-table-column prop="customer_phone" label="顾客电话" width="120" />
        <el-table-column prop="service_name" label="服务项目" width="100" />
        <el-table-column prop="therapist_name" label="技师" width="100" />
        <el-table-column prop="start_time" label="预约时间" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleViewFromDay(scope.row)">查看</el-button>
            <el-button type="success" size="small" @click="handleEditFromDay(scope.row)" v-if="scope.row.status !== 3 && scope.row.status !== 4">编辑</el-button>
            <el-button type="warning" size="small" @click="handleUpdateStatus(scope.row, 3)" v-hasPermi="['appointment:edit']" v-if="scope.row.status === 2">完成</el-button>
            <el-button type="danger" size="small" @click="handleUpdateStatus(scope.row, 4)" v-hasPermi="['appointment:edit']" v-if="scope.row.status !== 3 && scope.row.status !== 4">取消</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="dialog-footer" style="margin-top: 20px; text-align: right;">
        <el-button type="primary" @click="handleAddFromDay">新增预约</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAppointment, getAppointmentList, addAppointment, updateAppointment, getAppointmentsByDate, updateAppointmentStatus } from '@/api/appointment'
import { searchCustomers } from '@/api/customer'
import { getServiceList } from '@/api/service'
import { getTherapistList, getTherapistTimeSlots } from '@/api/therapist'
import dayjs from 'dayjs'

// 当前激活的标签页
const activeTab = ref('list')

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  customerId: '',
  customerName: '',
  therapistName: '',
  startDate: '',
  toDate: '',
  status: undefined
})

const queryCustomerParam = reactive({
  param: ''
})

// 日期范围
const dateRange = ref([])

// 表格数据
const appointmentList = ref([])
const total = ref(0)
const loading = ref(true)

// 弹出层标题
const title = ref('')
const open = ref(false)
const viewOpen = ref(false)

// 状态选项
const statusOptions = [
  { value: 0, label: '待确认' },
  { value: 1, label: '已确认' },
  { value: 2, label: '进行中' },
  { value: 3, label: '已完成' },
  { value: 4, label: '已取消' }
]

const appointmentFormRef = ref(null)
const appointmentForm = reactive({
  id: null,
  customerId: undefined,
  serviceId: undefined,
  therapistId: undefined,
  appointmentDate: '',
  startTime: '',
  endTime: '',
  duration: 60,
  status: 2,
  remark: ''
})

// 表单校验规则
const appointmentRules = {
  customerId: [
    { required: true, message: '顾客不能为空', trigger: 'blur' }
  ],
  serviceId: [
    { required: true, message: '服务项目不能为空', trigger: 'blur' }
  ],
  therapistId: [
    { required: true, message: '技师不能为空', trigger: 'blur' }
  ],
  appointmentDate: [
    { required: true, message: '预约日期不能为空', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '状态不能为空', trigger: 'blur' }
  ]
}

// 预约详情数据
const appointmentDetail = ref({})

// 下拉选项数据
const customerOptions = ref([])
const serviceOptions = ref([])
const therapistOptions = ref([])
const timeSlotOptions = ref([])
const customerLoading = ref(false)

// 日历相关
const calendarFilter = reactive({
  therapist_id: undefined,
  year: new Date().getFullYear(),
  month: new Date().getMonth() + 1
})
const calendarDays = ref([])
const weekdays = ['日', '一', '二', '三', '四', '五', '六']
const calendarTitle = computed(() => `${calendarFilter.year}年${calendarFilter.month}月`)

// 日历日期详情
const selectedDate = ref(null)
const dayDetailOpen = ref(false)
const dayAppointments = ref([])

// 表单引用
// const appointmentForm = ref(null)

// 获取预约列表
const getList = () => {
  loading.value = true
  
  const params = {
    ...queryParams
  }
  
  getAppointmentList(params).then(response => {
    appointmentList.value = response.data.records
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
  dateRange.value = []
  queryParams.customerId = ''
  queryParams.customerName = ''
  queryParams.therapistName = ''
  queryParams.fromDate = ''
  queryParams.toDate = ''
  queryParams.status = undefined
  handleQuery()
}

// 日期范围变化
const handleDateRangeChange = () => {
  if (dateRange.value && dateRange.value.length === 2) {
    queryParams.fromDate = dateRange.value[0]
    queryParams.toDate = dateRange.value[1]
  } else {
    queryParams.fromDate = ''
    queryParams.toDate = ''
  }
}

const handleSortChange = ({ prop, order }) => {
  if (prop === 'start_time') {
    queryParams.orderField = 'appointment_date,start_time'
  } else if (prop === 'update_time') {
    queryParams.orderField = 'update_time'
  } else {
    queryParams.orderField = ''
  }
  //queryParams.isAsc = order === 'ascending' ? 'True' : (order === 'descending' ? 'False' : '')
  if (order === 'ascending') {
    queryParams.isAsc = 'True'
  } else if (order === 'descending') {
    queryParams.isAsc = 'False'
  } else {
    queryParams.orderField = ''
    queryParams.isAsc = ''
  }
  getList()
}

// 标签页切换
const handleTabClick = (tab) => {
  if (tab.props.name === 'calendar') {
    generateCalendar()
  }
}

// 表单重置
const reset = () => {
  appointmentForm.id = null
  appointmentForm.customerId = undefined
  appointmentForm.serviceId = undefined
  appointmentForm.therapistId = undefined
  appointmentForm.appointmentDate = ''
  appointmentForm.startTime = ''
  appointmentForm.duration = 60
  appointmentForm.status = 0
  appointmentForm.remark = ''
  // customerOptions.value = [] // 清空顾客下拉选项
  queryCustomerParam.param = '' // 清空搜索参数
}

// 取消按钮
const cancel = () => {
  open.value = false
  reset()
}

// 搜索顾客
const remoteSearchCustomer = (param) => {
  queryCustomerParam.param = param
  if (queryCustomerParam) {
    customerLoading.value = true
    searchCustomers(queryCustomerParam).then(response => {
      customerOptions.value = response.data
      customerLoading.value = false
    })
  } else {
    customerOptions.value = []
  }
}
const handleCustomerSelect = (customer_id) => {
  appointmentForm.customerId = customer_id
}

// 服务项目
const loadServiceOptions = () => {
  getServiceList({ pageSize: 1000, status: 1 }).then(response => {
    serviceOptions.value = response.data.records
  })
}
const handleServiceChange = () => {
  const selectedService = serviceOptions.value.find(item => item.id === appointmentForm.serviceId)
  if (selectedService) {
    appointmentForm.duration = selectedService.duration
  }
}

// 加载技师选项
const loadTherapistOptions = () => {
  getTherapistList({ pageSize: 1000, status: 1 }).then(response => {
    therapistOptions.value = response.data.records
  })
}

// 预约日期变化
const handleDateChange = () => {
  if (appointmentForm.appointmentDate && appointmentForm.therapistId) {
    loadTimeSlots()
  }
}

// 加载时间段选项
const loadTimeSlots = () => {
  if (!appointmentForm.appointmentDate || !appointmentForm.therapistId) return

  const date = formatDate(appointmentForm.appointmentDate, 'YYYY-MM-DD')

  getTherapistTimeSlots(appointmentForm.therapistId, date).then(response => {
    const slots = response.data

    // 生成时间段选项
    const options = []
    const startHour = 9
    const endHour = 21
    const interval = 30 // 分钟

    for (let hour = startHour; hour < endHour; hour++) {
      for (let minute = 0; minute < 60; minute += interval) {
        const timeValue = `${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}`
        const timeLabel = `${timeValue}`

        // 检查该时间段是否已被预约
        const isBooked = slots.some(slot => {
          return slot.timeSlot === timeValue && slot.status === 1 && (!appointmentForm.id || slot.appointmentId !== appointmentForm.id)
        })

        options.push({
          value: timeValue,
          label: timeLabel,
          disabled: isBooked
        })
      }
    }

    timeSlotOptions.value = options
  })
}

// 新增按钮操作
const handleAdd = () => {
  reset()
  open.value = true
  title.value = '添加预约'
}

// 修改按钮操作
const handleEdit = (row) => {
  reset()
  const id = row.id
  getAppointment(id).then(response => {
    const data = response.data
    appointmentForm.id = data.id
    appointmentForm.customerId = data.customer_id
    appointmentForm.serviceId = data.service_id
    appointmentForm.therapistId = data.therapist_id
    appointmentForm.appointmentDate = dayjs(data.appointment_date).format('YYYY-MM-DD')
    appointmentForm.startTime = data.start_time
    appointmentForm.duration = data.duration ? data.duration : 60
    appointmentForm.status = data.status
    appointmentForm.remark = data.remark

    // 加载顾客信息
    customerOptions.value = [{
      id: data.customer_id,
      name: data.customer_name,
    }]
    // 设置搜索参数，确保 select 能显示
    queryCustomerParam.param = data.customer_id

    // 加载时间段选项
    loadTimeSlots()

    open.value = true
    title.value = '修改预约'
  })
}

const handleRowDblClick = (row) => {
  handleView(row)
}

// 查看按钮操作
const handleView = (row) => {
  const id = row.id
  getAppointment(id).then(response => {
    appointmentDetail.value = response.data
    viewOpen.value = true
  })
}

// 从详情页编辑
const handleEditFromView = () => {
  viewOpen.value = false
  handleEdit(appointmentDetail.value)
}

// 更新状态
const handleUpdateStatus = (row, status) => {
  const id = row.id
  const statusText = getStatusText(status)

  ElMessageBox.confirm(`确认将该预约状态修改为"${statusText}"吗?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    updateAppointmentStatus(id, status).then(() => {
      ElMessage.success('状态修改成功')
      getList()

      // 如果是日历视图，也需要刷新日历数据
      if (activeTab.value === 'calendar') {
        generateCalendar()
      }
    })
  }).catch(() => {})
}

// 从详情页更新状态
const handleUpdateStatusFromView = (status) => {
  handleUpdateStatus(appointmentDetail.value, status)
  viewOpen.value = false
}

// 提交表单
const submitForm = () => {
  appointmentFormRef.value.validate(valid => {
    if (valid) {
      // 组合日期和时间
      if (appointmentForm.startTime)
        appointmentForm.endTime = dayjs(appointmentForm.startTime, 'HH:mm').add(appointmentForm.duration, 'minute').format('HH:mm')

      const data = {
        ...appointmentForm
      }

      if (appointmentForm.id != null) {
        updateAppointment(data).then(() => {
          ElMessage.success('修改成功')
          open.value = false
          getList()

          // 如果是日历视图，也需要刷新日历数据
          if (activeTab.value === 'calendar') {
            generateCalendar()
          }
        })
      } else {
        addAppointment(data).then(() => {
          ElMessage.success('新增成功')
          open.value = false
          getList()

          // 如果是日历视图，也需要刷新日历数据
          if (activeTab.value === 'calendar') {
            generateCalendar()
          }
        })
      }
    }
  })
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

// 获取状态类型
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

// 获取状态文本
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

// 格式化日期
const formatDate = (date, format = 'YYYY-MM-DD') => {
  if (!date) return ''
  return dayjs(date).format(format)
}

// 生成日历数据
const generateCalendar = () => {
  const year = calendarFilter.year
  const month = calendarFilter.month

  // 获取当月第一天是星期几
  const firstDay = new Date(year, month - 1, 1).getDay()

  // 获取当月天数
  const daysInMonth = new Date(year, month, 0).getDate()

  // 获取上个月天数
  const daysInPrevMonth = new Date(year, month - 1, 0).getDate()

  // 获取当前日期
  const today = new Date()
  const currentYear = today.getFullYear()
  const currentMonth = today.getMonth() + 1
  const currentDate = today.getDate()

  const days = []

  // 上个月的日期
  for (let i = 0; i < firstDay; i++) {
    const day = daysInPrevMonth - firstDay + i + 1
    const prevMonth = month === 1 ? 12 : month - 1
    const prevYear = month === 1 ? year - 1 : year

    days.push({
      day,
      month: prevMonth,
      year: prevYear,
      otherMonth: true,
      isToday: false,
      date: new Date(prevYear, prevMonth - 1, day),
      appointments: []
    })
  }

  // 当月的日期
  for (let i = 1; i <= daysInMonth; i++) {
    days.push({
      day: i,
      month,
      year,
      otherMonth: false,
      isToday: i === currentDate && month === currentMonth && year === currentYear,
      date: new Date(year, month - 1, i),
      appointments: []
    })
  }

  // 下个月的日期
  const remainingDays = 42 - days.length // 6行7列 = 42
  for (let i = 1; i <= remainingDays; i++) {
    const nextMonth = month === 12 ? 1 : month + 1
    const nextYear = month === 12 ? year + 1 : year

    days.push({
      day: i,
      month: nextMonth,
      year: nextYear,
      otherMonth: true,
      isToday: false,
      date: new Date(nextYear, nextMonth - 1, i),
      appointments: []
    })
  }

  calendarDays.value = days

  // 获取当月预约数据
  const fromDate = formatDate(new Date(year, month - 1, 1), 'YYYY-MM-DD')
  const toDate = formatDate(new Date(year, month, 0), 'YYYY-MM-DD')

  const params = {
    fromDate,
    toDate,
    therapistId: calendarFilter.therapist_id
  }

  getAppointmentsByDate(params).then(response => {
    const appointments = response.data

    // 将预约数据添加到日历中
    appointments.forEach(appointment => {
      const appointment_date = dayjs(appointment.appointment_date).format('YYYY-MM-DD')
      const day = days.find(d => formatDate(d.date, 'YYYY-MM-DD') === appointment_date)

      if (day) {
        if (!day.appointments) {
          day.appointments = []
        }
        day.appointments.push(appointment)
      }
    })

    calendarDays.value = [...days]
  })
}

// 日历控制 - 上个月
const prevMonth = () => {
  if (calendarFilter.month === 1) {
    calendarFilter.year--
    calendarFilter.month = 12
  } else {
    calendarFilter.month--
  }
  generateCalendar()
}

// 日历控制 - 下个月
const nextMonth = () => {
  if (calendarFilter.month === 12) {
    calendarFilter.year++
    calendarFilter.month = 1
  } else {
    calendarFilter.month++
  }
  generateCalendar()
}

// 日历控制 - 今天
const today = () => {
  const now = new Date()
  calendarFilter.year = now.getFullYear()
  calendarFilter.month = now.getMonth() + 1
  generateCalendar()
}

// 日历筛选变化
const handleCalendarFilterChange = () => {
  generateCalendar()
}

// 点击日历日期
const handleDayClick = (day) => {
  selectedDate.value = day.date
  dayAppointments.value = day.appointments || []
  dayDetailOpen.value = true
}

// 从日历详情查看预约
const handleViewFromDay = (row) => {
  dayDetailOpen.value = false
  handleView(row)
}

// 从日历详情编辑预约
const handleEditFromDay = (row) => {
  dayDetailOpen.value = false
  handleEdit(row)
}

// 从日历详情新增预约
const handleAddFromDay = () => {
  reset()
  if (selectedDate.value) {
    appointmentForm.appointmentDate = formatDate(selectedDate.value, 'YYYY-MM-DD')
  }
  dayDetailOpen.value = false
  open.value = true
  title.value = '添加预约'
}

// 监听技师变化
watch(() => appointmentForm.therapistId, (newVal) => {
  if (newVal && appointmentForm.appointmentDate) {
    loadTimeSlots()
  }
})

onMounted(() => {
  // 获取预约列表
  getList()

  // 加载服务项目选项
  loadServiceOptions()

  // 加载技师选项
  loadTherapistOptions()
})
</script>

<style lang="scss" scoped>
.appointment-container {
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

  .appointment-actions {
    margin-top: 20px;
    text-align: right;
  }

  .calendar-container {
    margin-top: 20px;

    .calendar-header {
      display: flex;
      justify-content: center;
      margin-bottom: 20px;

      .calendar-title {
        font-size: 20px;
        font-weight: bold;
      }
    }

    .calendar-controls {
      display: flex;
      align-items: center;

      .el-select {
        margin-right: 10px;
        width: 150px;
      }
    }

    .calendar-weekdays {
      display: grid;
      grid-template-columns: repeat(7, 1fr);
      text-align: center;
      font-weight: bold;
      margin-bottom: 10px;

      .weekday {
        padding: 10px;
        border-bottom: 1px solid #ebeef5;
      }
    }

    .calendar-days {
      display: grid;
      grid-template-columns: repeat(7, 1fr);
      grid-template-rows: repeat(6, 1fr);
      gap: 5px;

      .calendar-day {
        min-height: 100px;
        border: 1px solid #ebeef5;
        border-radius: 4px;
        padding: 5px;
        cursor: pointer;

        &:hover {
          background-color: #f5f7fa;
        }

        &.other-month {
          color: #c0c4cc;
          background-color: #f9f9f9;
        }

        &.today {
          border-color: #409eff;

          .day-number {
            background-color: #409eff;
            color: #fff;
            border-radius: 50%;
            width: 24px;
            height: 24px;
            display: flex;
            align-items: center;
            justify-content: center;
          }
        }

        &.has-appointments {
          background-color: #f0f9eb;
        }

        .day-number {
          font-weight: bold;
          margin-bottom: 5px;
        }

        .day-appointments {
          .appointment-count {
            font-size: 12px;
            color: #67c23a;
            background-color: #f0f9eb;
            padding: 2px 5px;
            border-radius: 3px;
            display: inline-block;
          }
        }
      }
    }
  }
}

@media screen and (max-width: 768px) {
  .appointment-container {
    padding: 10px;

    .el-form-item {
      margin-bottom: 10px;
    }

    .calendar-container {
      .calendar-days {
        .calendar-day {
          min-height: 60px;
        }
      }
    }
  }
}
</style>
