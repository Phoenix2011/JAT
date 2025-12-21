<template>
  <div class="order-container">
    <div class="search-bar">
      <el-form :inline="true" :model="queryParams" class="demo-form-inline">
        <el-form-item label="订单号">
          <el-input v-model="queryParams.orderNo" placeholder="请输入订单号" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="顾客姓名">
          <el-input v-model="queryParams.customerName" placeholder="请输入顾客姓名" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="订单日期">
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
        <el-form-item label="状态">
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

    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>订单列表</span>
          <el-button type="primary" @click="handleAdd">新增订单</el-button>
        </div>
      </template>
      
      <el-table v-loading="loading" :data="orderList" style="width: 100%">
         <el-table-column type="index" width="50" />
        <el-table-column prop="order_no" label="订单号" width="150" />
        <el-table-column prop="customer_id" label="顾客ID" width="120" />
        <el-table-column prop="customer_name" label="顾客姓名" width="120" />
        <el-table-column prop="total_amount" label="订单金额" width="120">
          <template #default="scope">
            ¥{{ scope.row.payment_amount ? scope.row.payment_amount.toFixed(2) : '0.00' }}
          </template>
        </el-table-column>
        <el-table-column prop="create_time" label="创建时间" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleView(scope.row)">查看</el-button>
            <el-button type="success" size="small" @click="handleEdit(scope.row)" v-if="scope.row.status === 0">编辑</el-button>
            <el-button type="danger" size="small" @click="handleCancel(scope.row)" v-if="scope.row.status === 0">取消</el-button>
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

    <!-- 新增/编辑订单对话框 -->
    <el-dialog :title="orderTitle" v-model="orderOpen" width="800px" append-to-body>
      <el-form ref="orderFormRef" :model="orderForm" :rules="orderRules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="顾客姓名" prop="customerName">
              <el-input v-model="orderForm.customerName" placeholder="请输入顾客姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="顾客电话" prop="customerPhone">
              <el-input v-model="orderForm.customerPhone" placeholder="请输入顾客电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="服务项目" prop="serviceId">
              <el-select v-model="orderForm.serviceId" placeholder="请选择服务项目" style="width: 100%">
                <el-option
                  v-for="item in serviceOptions"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                  @click="handleServiceChange(item)"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="技师" prop="therapistId">
              <el-select v-model="orderForm.therapistId" placeholder="请选择技师" style="width: 100%">
                <el-option
                  v-for="item in therapistOptions"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="订单金额" prop="amount">
              <el-input-number v-model="orderForm.amount" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="实付金额" prop="payAmount">
              <el-input-number v-model="orderForm.payAmount" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="orderForm.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancelOrder">取 消</el-button>
          <el-button type="primary" @click="submitOrderForm">确 定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 查看订单详情对话框 -->
    <el-dialog title="订单详情" v-model="detailOpen" width="600px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="订单号">{{ orderDetail.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(orderDetail.status)">{{ getStatusText(orderDetail.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="顾客姓名">{{ orderDetail.customerName }}</el-descriptions-item>
        <el-descriptions-item label="顾客电话">{{ orderDetail.customerPhone }}</el-descriptions-item>
        <el-descriptions-item label="服务项目">{{ orderDetail.serviceName }}</el-descriptions-item>
        <el-descriptions-item label="技师">{{ orderDetail.therapistName }}</el-descriptions-item>
        <el-descriptions-item label="订单金额">¥{{ orderDetail.amount }}</el-descriptions-item>
        <el-descriptions-item label="实付金额">¥{{ orderDetail.payAmount }}</el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">{{ orderDetail.createTime }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ orderDetail.remark || '无' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailOpen = false">关 闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderList } from '@/api/order'

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  orderNo: '',
  customerName: '',
  status: undefined,
  startDate: '',
  endDate: ''
})

// 数据列表
const orderList = ref([])
const total = ref(0)
const dateRange = ref([])
const loading = ref(true)

// 状态选项
const statusOptions = ref([
  { value: 0, label: '待支付' },
  { value: 1, label: '已支付' },
  { value: 2, label: '已取消' },
  { value: 3, label: '已退款' }
])

// 服务项目选项
const serviceOptions = ref([
  { id: 1, name: '全身按摩', price: 128.00 },
  { id: 2, name: '足疗', price: 88.00 },
  { id: 3, name: '艾灸', price: 98.00 }
])

// 技师选项
const therapistOptions = ref([
  { id: 1, name: '张师傅' },
  { id: 2, name: '李师傅' },
  { id: 3, name: '王师傅' }
])

// 对话框相关
const orderOpen = ref(false)
const detailOpen = ref(false)
const orderTitle = ref('')
const orderFormRef = ref(null)

// 订单表单
const orderForm = reactive({
  id: undefined,
  customerName: '',
  customerPhone: '',
  serviceId: undefined,
  therapistId: undefined,
  amount: 0,
  payAmount: 0,
  remark: ''
})

// 订单详情
const orderDetail = reactive({
  orderNo: '',
  customerName: '',
  customerPhone: '',
  serviceName: '',
  therapistName: '',
  amount: 0,
  payAmount: 0,
  status: 0,
  createTime: '',
  remark: ''
})

// 表单验证规则
const orderRules = {
  customerName: [{ required: true, message: '请输入顾客姓名', trigger: 'blur' }],
  customerPhone: [{ required: true, message: '请输入顾客电话', trigger: 'blur' }],
  serviceId: [{ required: true, message: '请选择服务项目', trigger: 'change' }],
  therapistId: [{ required: true, message: '请选择技师', trigger: 'change' }],
  amount: [{ required: true, message: '请输入订单金额', trigger: 'blur' }],
  payAmount: [{ required: true, message: '请输入实付金额', trigger: 'blur' }]
}

// 获取订单列表
const getOrderData = async () => {
  loading.value = true
  getOrderList(queryParams).then(response => {
    orderList.value = response.data.records
    total.value = response.data.total
    loading.value = false
  })
}

// 状态处理
const getStatusType = (status) => {
  const statusMap = {
    0: 'info',    // 待支付
    1: 'success', // 已支付
    2: 'danger',  // 已取消
    3: 'warning'  // 已退款
  }
  return statusMap[status] || 'info'
}

const getStatusText = (status) => {
  const statusMap = {
    0: '待支付',
    1: '已支付',
    2: '已取消',
    3: '已退款'
  }
  return statusMap[status] || '未知'
}

// 日期范围变化
const handleDateRangeChange = (dates) => {
  if (dates && dates.length === 2) {
    queryParams.startDate = dates[0]
    queryParams.endDate = dates[1]
  } else {
    queryParams.startDate = ''
    queryParams.endDate = ''
  }
}

// 查询
const handleQuery = () => {
  queryParams.pageNum = 1
  getOrderData()
}

// 重置
const resetQuery = () => {
  Object.assign(queryParams, {
    pageNum: 1,
    pageSize: 10,
    orderNo: '',
    customerName: '',
    status: undefined,
    startDate: '',
    endDate: ''
  })
  dateRange.value = []
  getOrderData()
}

// 分页处理
const handleSizeChange = (val) => {
  queryParams.pageSize = val
  getOrderData()
}

const handleCurrentChange = (val) => {
  queryParams.pageNum = val
  getOrderData()
}

// 服务项目变化
const handleServiceChange = (service) => {
  orderForm.amount = service.price
  orderForm.payAmount = service.price
}

// 重置表单
const resetOrderForm = () => {
  Object.assign(orderForm, {
    id: undefined,
    customerName: '',
    customerPhone: '',
    serviceId: undefined,
    therapistId: undefined,
    amount: 0,
    payAmount: 0,
    remark: ''
  })
  if (orderFormRef.value) {
    orderFormRef.value.resetFields()
  }
}

// 操作处理
const handleAdd = () => {
  resetOrderForm()
  orderTitle.value = '新增订单'
  orderOpen.value = true
}

const handleView = (row) => {
  Object.assign(orderDetail, {
    orderNo: row.orderNo,
    customerName: row.customerName,
    customerPhone: row.customerPhone,
    serviceName: row.serviceName,
    therapistName: row.therapistName,
    amount: row.amount.toFixed(2),
    payAmount: row.payAmount.toFixed(2),
    status: row.status,
    createTime: row.createTime,
    remark: row.remark
  })
  detailOpen.value = true
}

const handleEdit = (row) => {
  resetOrderForm()
  Object.assign(orderForm, {
    id: row.id,
    customerName: row.customerName,
    customerPhone: row.customerPhone,
    serviceId: 1, // 模拟数据，实际应该从row中获取
    therapistId: 1, // 模拟数据，实际应该从row中获取
    amount: row.amount,
    payAmount: row.payAmount,
    remark: row.remark
  })
  orderTitle.value = '编辑订单'
  orderOpen.value = true
}

const handleCancel = (row) => {
  ElMessageBox.confirm(
    `确定要取消订单"${row.orderNo}"吗？`,
    '系统提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // 模拟取消订单
    const index = orderList.value.findIndex(item => item.id === row.id)
    if (index !== -1) {
      orderList.value[index].status = 2
    }
    ElMessage.success('订单取消成功')
  }).catch(() => {
    ElMessage.info('已取消操作')
  })
}

// 提交表单
const submitOrderForm = () => {
  if (!orderFormRef.value) return
  orderFormRef.value.validate((valid) => {
    if (valid) {
      if (orderForm.id) {
        // 编辑订单
        const index = orderList.value.findIndex(item => item.id === orderForm.id)
        if (index !== -1) {
              const serviceName = serviceOptions.value.find(s => s.id === orderForm.serviceId)
              const therapistName = therapistOptions.value.find(t => t.id === orderForm.therapistId)
              
              Object.assign(orderList.value[index], {
                customerName: orderForm.customerName,
                customerPhone: orderForm.customerPhone,
                serviceName: serviceName ? serviceName.name : '',
                therapistName: therapistName ? therapistName.name : '',
                amount: orderForm.amount,
                payAmount: orderForm.payAmount,
                remark: orderForm.remark
              })
        }
        ElMessage.success('订单修改成功')
      } else {
        // 新增订单
        const serviceName = serviceOptions.value.find(s => s.id === orderForm.serviceId)
        const therapistName = therapistOptions.value.find(t => t.id === orderForm.therapistId)
        
        const newOrder = {
          id: Date.now(),
          orderNo: 'ORD' + new Date().toISOString().slice(0, 10).replace(/-/g, '') + String(Date.now()).slice(-4),
          customerName: orderForm.customerName,
          customerPhone: orderForm.customerPhone,
          serviceName: serviceName ? serviceName.name : '',
          therapistName: therapistName ? therapistName.name : '',
          amount: orderForm.amount,
          payAmount: orderForm.payAmount,
          createTime: new Date().toLocaleString(),
          status: 0,
          remark: orderForm.remark
        }
        orderList.value.unshift(newOrder)
        total.value++
        ElMessage.success('订单创建成功')
      }
      orderOpen.value = false
    }
  })
}

// 取消表单
const cancelOrder = () => {
  orderOpen.value = false
  resetOrderForm()
}

onMounted(() => {
  getOrderData()
})
</script>

<style lang="scss" scoped>
.order-container {
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
  
  .dialog-footer {
    text-align: right;
  }
}

@media screen and (max-width: 768px) {
  .order-container {
    padding: 10px;
    
    .el-form-item {
      margin-bottom: 10px;
    }
  }
}
</style>

