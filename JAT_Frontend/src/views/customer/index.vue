<template>
  <div class="customer-container">
    <div class="search-bar">
      <el-form :inline="true" :model="queryParams" class="demo-form-inline">
        <el-form-item label="顾客ID">
          <el-input v-model="queryParams.id" type = "number" placeholder="请输入顾客ID" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="queryParams.name" placeholder="请输入顾客姓名" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="queryParams.phone" placeholder="请输入顾客电话" clearable @keyup.enter="handleQuery" />
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
          <span>顾客列表</span>
          <el-button type="primary" @click="handleAdd" v-hasPermi="['customer:add']">新增顾客</el-button>
        </div>
      </template>
      
      <el-table v-loading="loading" :data="customerList" @row-dblclick="handleRowDblClick" style="width: 100%">
        <!-- <el-table-column type="index" width="50" /> -->
        <el-table-column prop="id" label="顾客ID" width="70" />
        <el-table-column prop="name" label="顾客姓名" width="160" />
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="scope">
            {{ scope.row.gender === 1 ? '男' : '女' }}
          </template>
        </el-table-column>
        <el-table-column prop="age" label="年龄" width="80" />
        <el-table-column prop="phone" label="电话" width="120" />
        <el-table-column prop="createTime" label="注册日期" width="110">
          <template #default="scope">
            {{ scope.row.createTime ? dayjs(scope.row.createTime).format('YYYY-MM-DD') : '' }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="110">
          <template #default="scope">
            {{ scope.row.createTime ? dayjs(scope.row.createTime).format('HH:MM:ss') : '' }}
          </template>
        </el-table-column>
        <el-table-column prop="lastVisitTime" label="最近到店时间" width="180" >
          <template #default="scope">
            {{ scope.row.lastVisitTime ? dayjs(scope.row.lastVisitTime).format('YYYY-MM-DD HH:mm:ss') : '' }}
          </template>
        </el-table-column>
<!--        <el-table-column prop="visitCount" label="到店次数" width="100" />-->
<!--        <el-table-column prop="totalSpent" label="累计消费" width="120">-->
<!--          <template #default="scope">-->
<!--            ¥{{ scope.row.totalSpent ? scope.row.totalSpent.toFixed(2) : '0.00' }}-->
<!--          </template>-->
<!--        </el-table-column>-->
        <el-table-column prop="remark" label="备注" width="280" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleView(scope.row)" v-hasPermi="['customer:query']">查看</el-button>
            <el-button type="success" size="small" @click="handleEdit(scope.row)" v-hasPermi="['customer:edit']">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row)" v-hasPermi="['customer:delete']">删除</el-button>
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

    <!-- 添加或修改顾客对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="customerForm" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="ID" prop="id" v-if="form.id !== null">
          <el-input v-model="form.id" readonly />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入顾客姓名" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="form.gender">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="0">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="年龄" prop="age">
          <el-input-number v-model="form.age" :min="1" :max="120" />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入顾客电话" />
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

    <!-- 顾客详情对话框 -->
    <el-dialog title="顾客详情" v-model="viewOpen" width="800px" append-to-body>
      <el-tabs v-model="activeTab"  @tab-click="handleTabClick" >
        <el-tab-pane label="基本信息" name="basic">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="顾客ID">{{ customerDetail.id }}</el-descriptions-item>
            <el-descriptions-item label="姓名">{{ customerDetail.name }}</el-descriptions-item>
            <el-descriptions-item label="性别">{{ customerDetail.gender === 1 ? '男' : '女' }}</el-descriptions-item>
            <el-descriptions-item label="年龄">{{ customerDetail.age }}</el-descriptions-item>
            <el-descriptions-item label="电话">{{ customerDetail.phone }}</el-descriptions-item>
            <el-descriptions-item label="最近到店时间">{{ customerDetail.lastVisitTime }}</el-descriptions-item>
            <el-descriptions-item label="注册时间">{{ customerDetail.createTime }}</el-descriptions-item>
            <el-descriptions-item label="更新时间">{{ customerDetail.updateTime }}</el-descriptions-item>
            <el-descriptions-item label="到店次数">{{ customerDetail.visitCount }}</el-descriptions-item>
            <el-descriptions-item label="累计消费">¥{{ customerDetail.totalSpent ? customerDetail.totalSpent.toFixed(2) : '0.00' }}</el-descriptions-item>
<!--            <el-descriptions-item />-->
            <el-descriptions-item label="备注" :span="2">{{ customerDetail.remark }}</el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>
        
        <el-tab-pane label="卡项信息" name="cards">
          <el-table :data="customerCards" style="width: 100%" v-loading="cardsLoading">
            <el-table-column prop="service_name" label="卡项名称" />
            <el-table-column prop="card_type" label="卡类型" >
              <template #default="scope">
                  {{ scope.row.status === 1 ? '次卡' : '储值卡' }}
              </template>
            </el-table-column>
            <el-table-column prop="total_count" label="总次数" />
            <el-table-column prop="used_count" label="已用次数" />
            <el-table-column prop="remaining_count" label="剩余次数" />
            <el-table-column prop="amount" label="金额" />
            <el-table-column prop="remaining_amount" label="剩余金额" />
            <el-table-column prop="status" label="状态">
              <template #default="scope">
                <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
                  {{ scope.row.status === 1 ? '有效' : '已过期' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template #default="scope">
                <el-button type="primary" size="small" @click="viewCardDetail(scope.row)">查看详情</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        
        <el-tab-pane label="消费记录" name="orders" >
          <el-table :data="customerOrders" style="width: 100%" v-loading="ordersLoading">
            <el-table-column prop="order_no" label="订单号" width="70" />
            <el-table-column prop="create_time" label="消费时间" width="180" />
            <el-table-column prop="payment_amount" label="消费金额">
              <template #default="scope">
                ¥{{ scope.row.payment_amount.toFixed(2) }}
              </template>
            </el-table-column>
            <el-table-column prop="paymentMethod" label="支付方式">
              <template #default="scope">
                {{ getPaymentMethodText(scope.row.paymentMethod) }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态">
              <template #default="scope">
                <el-tag :type="getOrderStatusType(scope.row.status)">
                  {{ getOrderStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template #default="scope">
                <el-button type="primary" size="small" @click="viewOrderDetail(scope.row)">查看详情</el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination-container">
            <el-pagination
                v-model:current-page="defaultPageSetting.pageNum"
                v-model:page-size="defaultPageSetting.pageSize"
                :page-sizes="[10, 20, 50, 100]"
                layout="total, sizes, prev, pager, next, jumper"
                :total="customerOrders.total"
                @size-change="handleOrdersSizeChange"
                @current-change="handleOrdersCurrentChange"
            />
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="预约记录" name="appointments">
          <el-table :data="customerAppointments" style="width: 100%" v-loading="appointmentsLoading">
            <el-table-column prop="service_name" label="服务项目" />
            <el-table-column prop="therapist_name" label="技师" />
            <el-table-column prop="appointment_time" label="预约时间" width="170">
              <template #default="scope">
                {{ scope.row.appointment_date }} {{ scope.row.start_time }}
              </template>
            </el-table-column>
            <el-table-column prop="duration" label="预约时长" />
            <el-table-column prop="status" label="状态">
              <template #default="scope">
                <el-tag :type="getAppointmentStatusType(scope.row.status)">
                  {{ getAppointmentStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
<!--            <el-table-column label="操作" width="150">-->
<!--              <template #default="scope">-->
<!--                <el-button type="primary" size="small" @click="viewAppointmentDetail(scope.row)">查看详情</el-button>-->
<!--              </template>-->
<!--            </el-table-column>-->
          </el-table>
        </el-tab-pane>
        
        <!-- <el-tab-pane label="邀请记录" name="invitations" v-if="hasInvitations">
          <div class="invitation-stats">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="邀请码">{{ invitationStats.invitationCode }}</el-descriptions-item>
              <el-descriptions-item label="邀请人数">{{ invitationStats.inviteeCount }}</el-descriptions-item>
              <el-descriptions-item label="总返利">¥{{ invitationStats.totalCommission ? invitationStats.totalCommission.toFixed(2) : '0.00' }}</el-descriptions-item>
              <el-descriptions-item label="可用佣金">¥{{ invitationStats.availableCommission ? invitationStats.availableCommission.toFixed(2) : '0.00' }}</el-descriptions-item>
              <el-descriptions-item label="冻结佣金">¥{{ invitationStats.frozenCommission ? invitationStats.frozenCommission.toFixed(2) : '0.00' }}</el-descriptions-item>
              <el-descriptions-item label="已提现佣金">¥{{ invitationStats.withdrawnCommission ? invitationStats.withdrawnCommission.toFixed(2) : '0.00' }}</el-descriptions-item>
            </el-descriptions>
          </div>
          
          <div class="invitation-list">
            <h3>邀请列表</h3>
            <el-table :data="invitationList" style="width: 100%" v-loading="invitationsLoading">
              <el-table-column prop="inviteeName" label="被邀请人" />
              <el-table-column prop="inviteePhone" label="联系电话" />
              <el-table-column prop="createTime" label="邀请时间" />
            </el-table>
          </div>
        </el-tab-pane> -->
      </el-tabs>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCustomerList, getCustomer, addCustomer, updateCustomer, deleteCustomer, getCustomerCards, getCustomerOrders, getCustomerAppointments, getCustomerInvitationStats, getCustomerInvitationList } from '@/api/customer'
import dayjs from "dayjs";

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSizes: 10,
  name: '',
  phone: undefined
})

const defaultPageSetting = reactive({
  pageNum: 1,
  pageSize: 10
})

// 表格数据
const customerList = ref([])
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
  remark: ''
})

// 表单校验规则
const rules = {
  name: [
    { required: true, message: '顾客姓名不能为空', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '顾客性别不能为空', trigger: 'change' }
  ]
}

// 顾客详情数据
const customerDetail = ref({})
const activeTab = ref('basic')
const customerCards = ref([])
const customerOrders = ref([])
const customerAppointments = ref([])
// const invitationStats = ref({})
// const invitationList = ref([])
const cardsLoading = ref(false)
const ordersLoading = ref(false)
const appointmentsLoading = ref(false)
// const invitationsLoading = ref(false)
// const hasInvitations = ref(false)

// 表单引用
const customerForm = ref(null)

// 获取顾客列表
const getList = () => {
  loading.value = true
  getCustomerList(queryParams).then(response => {
    customerList.value = response.data.records
    total.value = response.data.total
    loading.value = false
  })
}

// 查询按钮操作
const handleQuery = () => {
  queryParams.current = 1
  getList()
}

// 重置按钮操作
const resetQuery = () => {
  queryParams.id = ''
  queryParams.name = ''
  queryParams.phone = ''
  handleQuery()
}

// 表单重置
const reset = () => {
  form.id = null
  form.name = ''
  form.gender = 1
  form.age = undefined
  form.phone = ''
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
  title.value = '添加顾客'
}

// 修改按钮操作
const handleEdit = (row) => {
  reset()
  const id = row.id || row.customerId
  getCustomer(id).then(response => {
    Object.assign(form, response.data)
    open.value = true
    title.value = '修改顾客'
  })
}

const handleRowDblClick = (row) => {
  handleView(row)
}

// 查看按钮操作
const handleView = (row) => {
  const id = row.id || row.customerId
  getCustomer(id).then(response => {
    customerDetail.value = response.data
    viewOpen.value = true
    activeTab.value = 'basic'
    
    // 加载卡项信息
    // loadCustomerCards(id)
    
    // 加载消费记录
    // loadCustomerOrders(id)
    
    // 加载预约记录
    // loadCustomerAppointments(id)
    
    // 加载邀请记录
    // loadCustomerInvitations(id)
  })
}

const handleTabClick = (tab) => {
  const customerId = customerDetail.value.id
  if (tab.paneName === 'cards') {
    loadCustomerCards(customerId)
  } else if (tab.paneName === 'orders') {
    loadCustomerOrders(customerId, defaultPageSetting)
  } else if (tab.paneName === 'appointments') {
    loadCustomerAppointments(customerId)
  }
  // else if (tab.paneName === 'invitations' && invitationList.value.length === 0) {
  //   loadCustomerInvitations(customerId)
  // }
}

// 加载顾客卡项
const loadCustomerCards = (customerId) => {
  cardsLoading.value = true
  getCustomerCards(customerId).then(response => {
    customerCards.value = response.data
    cardsLoading.value = false
  })
}

// 加载顾客消费记录
const loadCustomerOrders = (customerId) => {
  ordersLoading.value = true
  getCustomerOrders(customerId, defaultPageSetting).then(response => {
    customerOrders.value = response.data.records
    // defaultPageSetting.pageNum = response.data.pageNum
    // defaultPageSetting.pageSize = response.data.pageSize
    customerOrders.value.total = response.data.total
    ordersLoading.value = false
  })
}

// 加载顾客预约记录
const loadCustomerAppointments = (customerId) => {
  appointmentsLoading.value = true
  getCustomerAppointments(customerId).then(response => {
    customerAppointments.value = response.data
    appointmentsLoading.value = false
  })
}

// 加载顾客邀请记录
// const loadCustomerInvitations = (customerId) => {
//   invitationsLoading.value = true
//
//   // 获取邀请统计
//   getCustomerInvitationStats(customerId).then(response => {
//     invitationStats.value = response.data
//     hasInvitations.value = invitationStats.value.inviteeCount > 0
//     invitationsLoading.value = false
//   })
//
//   // 获取邀请列表
//   getCustomerInvitationList(customerId).then(response => {
//     invitationList.value = response.data
//   })
// }

// 提交表单
const submitForm = () => {
  customerForm.value.validate(valid => {
    if (valid) {
      if (form.id != null) {
        updateCustomer(form).then(response => {
          ElMessage.success('修改成功')
          open.value = false
          getList()
        })
      } else {
        addCustomer(form).then(response => {
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
  const id = row.id || row.customerId
  ElMessageBox.confirm('确认删除该顾客吗?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    deleteCustomer(id).then(() => {
      ElMessage.success('删除成功')
      getList()
    })
  }).catch(() => {})
}

// 查看卡项详情
const viewCardDetail = (row) => {
  // 实现卡项详情查看逻辑
}

// 查看订单详情
const viewOrderDetail = (row) => {
  // 实现订单详情查看逻辑
}

// 查看预约详情
const viewAppointmentDetail = (row) => {
  // 实现预约详情查看逻辑
}

// 支付方式文本
const getPaymentMethodText = (method) => {
  const methodMap = {
    1: '次卡',
    2: '储值卡',
    3: '微信',
    4: '支付宝',
    5: '现金'
  }
  return methodMap[method] || '未知'
}

// 订单状态类型
const getOrderStatusType = (status) => {
  const statusMap = {
    0: 'info',    // 待支付
    1: 'success', // 已支付
    2: 'danger',  // 已取消
    3: 'warning'  // 已退款
  }
  return statusMap[status] || 'info'
}

// 订单状态文本
const getOrderStatusText = (status) => {
  const statusMap = {
    0: '待支付',
    1: '已支付',
    2: '已取消',
    3: '已退款'
  }
  return statusMap[status] || '未知'
}

// 预约状态类型
const getAppointmentStatusType = (status) => {
  const statusMap = {
    0: 'info',    // 待确认
    1: 'success', // 已确认
    2: 'warning', // 进行中
    3: 'success', // 已完成
    4: 'danger'   // 已取消
  }
  return statusMap[status] || 'info'
}

// 预约状态文本
const getAppointmentStatusText = (status) => {
  const statusMap = {
    0: '待确认',
    1: '已确认',
    2: '进行中',
    3: '已完成',
    4: '已取消'
  }
  return statusMap[status] || '未知'
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

// 消费记录分页大小改变
const handleOrdersSizeChange = (val) => {
  defaultPageSetting.pageSize = val
  loadCustomerOrders(customerDetail.value.id)
}

// 消费记录分页页码改变
const handleOrdersCurrentChange = (val) => {
  defaultPageSetting.pageNum = val
  loadCustomerOrders(customerDetail.value.id)
}

onMounted(() => {
  getList()
})
</script>

<style lang="scss" scoped>
.customer-container {
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
  
  .invitation-stats {
    margin-bottom: 20px;
  }
  
  .invitation-list {
    margin-top: 20px;
    
    h3 {
      margin-bottom: 10px;
    }
  }
}

@media screen and (max-width: 768px) {
  .customer-container {
    padding: 10px;
    
    .el-form-item {
      margin-bottom: 10px;
    }
  }
}
</style>
