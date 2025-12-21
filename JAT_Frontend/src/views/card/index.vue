<template>
  <div class="card-container">
    <div class="search-bar">
      <el-form :inline="true" :model="queryParams" class="demo-form-inline">
        <el-form-item label="顾客姓名">
          <el-input v-model="queryParams.customerName" placeholder="请输入顾客姓名" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="卡号">
          <el-input v-model="queryParams.cardNo" placeholder="请输入卡号" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="卡类型">
          <el-select v-model="queryParams.packageId" placeholder="请选择卡类型" clearable>
            <el-option
              v-for="item in packageOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
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
          <span>会员卡列表</span>
          <el-button type="primary" @click="handleAdd">新增会员卡</el-button>
        </div>
      </template>
      
      <el-table v-loading="loading" :data="cardList" style="width: 100%">
        <el-table-column type="index" width="50" />
        <el-table-column prop="id" label="卡号" width="150" />
        <el-table-column prop="customer_name" label="顾客姓名" width="120" />
        <el-table-column prop="service_name" label="卡类型" width="150" />
        <el-table-column prop="totalTimes" label="总次数" width="100" />
        <el-table-column prop="remainingTimes" label="剩余次数" width="100" />
        <el-table-column prop="amount" label="充值金额" width="100" />
        <el-table-column prop="start_date" label="购买日期" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleView(scope.row)">查看</el-button>
            <el-button type="success" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="warning" size="small" @click="handleFreeze(scope.row)" v-if="scope.row.status === 1">冻结</el-button>
            <el-button type="info" size="small" @click="handleUnfreeze(scope.row)" v-if="scope.row.status === 2">解冻</el-button>
            <el-button type="danger" size="small" @click="handleConsume(scope.row)" v-if="scope.row.status === 1 && scope.row.remainingTimes > 0">消费</el-button>
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

    <!-- 新增/编辑会员卡对话框 -->
    <el-dialog :title="cardTitle" v-model="cardOpen" width="800px" append-to-body>
      <el-form ref="cardFormRef" :model="cardForm" :rules="cardRules" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="顾客" prop="customerId">
              <el-select
                v-model="cardForm.customerId"
                placeholder="请选择顾客"
                filterable
                remote
                :remote-method="searchCustomers"
                :loading="customerLoading"
                style="width: 100%"
                @change="handleCustomerChange"
              >
                <el-option
                  v-for="item in customerOptions"
                  :key="item.id"
                  :label="`${item.name} (${item.phone})`"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="卡号" prop="cardNo">
              <el-input v-model="cardForm.cardNo" placeholder="系统自动生成" :disabled="true" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="套餐类型" prop="packageId">
              <el-select v-model="cardForm.packageId" placeholder="请选择套餐类型" style="width: 100%" @change="handlePackageChange">
                <el-option
                  v-for="item in packageOptions"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="总次数" prop="totalTimes">
              <el-input-number v-model="cardForm.totalTimes" :min="1" :max="999" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="购买价格" prop="purchasePrice">
              <el-input-number v-model="cardForm.purchasePrice" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="有效期(天)" prop="validityDays">
              <el-input-number v-model="cardForm.validityDays" :min="1" :max="3650" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="购买日期" prop="purchaseDate">
              <el-date-picker
                v-model="cardForm.purchaseDate"
                type="date"
                placeholder="选择购买日期"
                style="width: 100%"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="到期日期" prop="expiryDate">
              <el-date-picker
                v-model="cardForm.expiryDate"
                type="date"
                placeholder="选择到期日期"
                style="width: 100%"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="cardForm.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancelCard">取 消</el-button>
          <el-button type="primary" @click="submitCardForm">确 定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 查看会员卡详情对话框 -->
    <el-dialog title="会员卡详情" v-model="detailOpen" width="700px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="卡号">{{ cardDetail.cardNo }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(cardDetail.status)">{{ getStatusText(cardDetail.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="顾客姓名">{{ cardDetail.customerName }}</el-descriptions-item>
        <el-descriptions-item label="顾客电话">{{ cardDetail.customerPhone }}</el-descriptions-item>
        <el-descriptions-item label="套餐类型">{{ cardDetail.packageName }}</el-descriptions-item>
        <el-descriptions-item label="总次数">{{ cardDetail.totalTimes }}</el-descriptions-item>
        <el-descriptions-item label="剩余次数">{{ cardDetail.remainingTimes }}</el-descriptions-item>
        <el-descriptions-item label="已消费次数">{{ cardDetail.totalTimes - cardDetail.remainingTimes }}</el-descriptions-item>
        <el-descriptions-item label="购买价格">¥{{ cardDetail.purchasePrice }}</el-descriptions-item>
        <el-descriptions-item label="有效期">{{ cardDetail.validityDays }}天</el-descriptions-item>
        <el-descriptions-item label="购买日期">{{ cardDetail.purchaseDate }}</el-descriptions-item>
        <el-descriptions-item label="到期日期">{{ cardDetail.expiryDate }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ cardDetail.remark || '无' }}</el-descriptions-item>
      </el-descriptions>
      
      <!-- 消费记录 -->
      <div style="margin-top: 20px;">
        <h4>消费记录</h4>
        <el-table :data="consumeRecords" style="width: 100%" max-height="300">
          <el-table-column prop="consumeDate" label="消费日期" width="120" />
          <el-table-column prop="serviceName" label="服务项目" width="150" />
          <el-table-column prop="therapistName" label="技师" width="100" />
          <el-table-column prop="consumeTimes" label="消费次数" width="100" />
          <el-table-column prop="remark" label="备注" />
        </el-table>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailOpen = false">关 闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 消费对话框 -->
    <el-dialog title="会员卡消费" v-model="consumeOpen" width="500px" append-to-body>
      <el-form ref="consumeFormRef" :model="consumeForm" :rules="consumeRules" label-width="100px">
        <el-form-item label="卡号">
          <el-input v-model="consumeForm.cardNo" :disabled="true" />
        </el-form-item>
        <el-form-item label="顾客姓名">
          <el-input v-model="consumeForm.customerName" :disabled="true" />
        </el-form-item>
        <el-form-item label="剩余次数">
          <el-input v-model="consumeForm.remainingTimes" :disabled="true" />
        </el-form-item>
        <el-form-item label="服务项目" prop="serviceName">
          <el-input v-model="consumeForm.serviceName" placeholder="请输入服务项目" />
        </el-form-item>
        <el-form-item label="技师" prop="therapistName">
          <el-input v-model="consumeForm.therapistName" placeholder="请输入技师姓名" />
        </el-form-item>
        <el-form-item label="消费次数" prop="consumeTimes">
          <el-input-number v-model="consumeForm.consumeTimes" :min="1" :max="consumeForm.remainingTimes" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="consumeForm.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="consumeOpen = false">取 消</el-button>
          <el-button type="primary" @click="submitConsumeForm">确 定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {getCustomerCardList} from "@/api/card";

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  customerName: '',
  cardNo: '',
  packageId: undefined,
  status: undefined
})

const cardList = ref([])
const packageOptions = ref([])
const customerOptions = ref([])
const total = ref(0)
const loading = ref(true)

// 状态选项
const statusOptions = ref([
  { value: 1, label: '正常' },
  { value: 2, label: '冻结' },
  { value: 3, label: '过期' },
  { value: 4, label: '用完' }
])

// 对话框相关
const cardOpen = ref(false)
const detailOpen = ref(false)
const consumeOpen = ref(false)
const cardTitle = ref('')
const cardFormRef = ref(null)
const consumeFormRef = ref(null)
const customerLoading = ref(false)

// 会员卡表单
const cardForm = reactive({
  id: undefined,
  customerId: undefined,
  cardNo: '',
  packageId: undefined,
  totalTimes: 1,
  purchasePrice: 0,
  validityDays: 365,
  purchaseDate: '',
  expiryDate: '',
  remark: ''
})

// 会员卡详情
const cardDetail = reactive({
  cardNo: '',
  customerName: '',
  customerPhone: '',
  packageName: '',
  totalTimes: 0,
  remainingTimes: 0,
  purchasePrice: 0,
  validityDays: 0,
  purchaseDate: '',
  expiryDate: '',
  status: 1,
  remark: ''
})

// 消费表单
const consumeForm = reactive({
  cardId: undefined,
  cardNo: '',
  customerName: '',
  remainingTimes: 0,
  serviceName: '',
  therapistName: '',
  consumeTimes: 1,
  remark: ''
})

// 消费记录
const consumeRecords = ref([])

// 表单验证规则
const cardRules = {
  customerId: [{ required: true, message: '请选择顾客', trigger: 'change' }],
  packageId: [{ required: true, message: '请选择套餐类型', trigger: 'change' }],
  totalTimes: [{ required: true, message: '请输入总次数', trigger: 'blur' }],
  purchasePrice: [{ required: true, message: '请输入购买价格', trigger: 'blur' }],
  validityDays: [{ required: true, message: '请输入有效期', trigger: 'blur' }],
  purchaseDate: [{ required: true, message: '请选择购买日期', trigger: 'change' }],
  expiryDate: [{ required: true, message: '请选择到期日期', trigger: 'change' }]
}

const consumeRules = {
  serviceName: [{ required: true, message: '请输入服务项目', trigger: 'blur' }],
  therapistName: [{ required: true, message: '请输入技师姓名', trigger: 'blur' }],
  consumeTimes: [{ required: true, message: '请输入消费次数', trigger: 'blur' }]
}

// 获取会员卡列表
const getCardData = async () => {
  loading.value = true
  getCustomerCardList(queryParams).then(response => {
    cardList.value = response.data.records
    total.value = response.data.total
    loading.value = false
  })
}

// 获取套餐选项
const getPackageOptions = async () => {
  // 模拟数据
  packageOptions.value = [
    { id: 1, name: '全身按摩套餐', price: 128.00, times: 10 },
    { id: 2, name: '足疗套餐', price: 88.00, times: 20 },
    { id: 3, name: '艾灸套餐', price: 98.00, times: 15 },
    { id: 4, name: '推拿套餐', price: 108.00, times: 12 }
  ]
}

// 搜索顾客
const searchCustomers = (query) => {
  if (query !== '') {
    customerLoading.value = true
    setTimeout(() => {
      customerOptions.value = [
        { id: 1, name: '张三', phone: '13800138000' },
        { id: 2, name: '李四', phone: '13900139000' },
        { id: 3, name: '王五', phone: '13700137000' },
        { id: 4, name: '赵六', phone: '13600136000' }
      ].filter(item => item.name.includes(query) || item.phone.includes(query))
      customerLoading.value = false
    }, 200)
  } else {
    customerOptions.value = []
  }
}

// 状态处理
const getStatusType = (status) => {
  const statusMap = {
    1: 'success', // 正常
    2: 'warning', // 冻结
    3: 'danger',  // 过期
    4: 'info'     // 用完
  }
  return statusMap[status] || 'info'
}

const getStatusText = (status) => {
  const statusMap = {
    1: '正常',
    2: '冻结',
    3: '过期',
    4: '用完'
  }
  return statusMap[status] || '未知'
}

// 生成卡号
const generateCardNo = () => {
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  const random = String(Math.floor(Math.random() * 10000)).padStart(4, '0')
  return `CARD${year}${month}${day}${random}`
}

// 计算到期日期
const calculateExpiryDate = (purchaseDate, validityDays) => {
  if (!purchaseDate || !validityDays) return ''
  const date = new Date(purchaseDate)
  date.setDate(date.getDate() + validityDays)
  return date.toISOString().split('T')[0]
}

// 查询
const handleQuery = () => {
  queryParams.pageNum = 1
  getCardData()
}

// 重置
const resetQuery = () => {
  Object.assign(queryParams, {
    pageNum: 1,
    pageSize: 10,
    customerName: '',
    cardNo: '',
    packageId: undefined,
    status: undefined
  })
  getCardData()
}

// 分页处理
const handleSizeChange = (val) => {
  queryParams.pageSize = val
  getCardData()
}

const handleCurrentChange = (val) => {
  queryParams.pageNum = val
  getCardData()
}

// 顾客变化
const handleCustomerChange = (customerId) => {
  const customer = customerOptions.value.find(c => c.id === customerId)
  if (customer) {
    // 可以在这里设置一些默认值
  }
}

// 套餐变化
const handlePackageChange = (packageId) => {
  const pkg = packageOptions.value.find(p => p.id === packageId)
  if (pkg) {
    cardForm.totalTimes = pkg.times
    cardForm.purchasePrice = pkg.price * pkg.times
  }
}

// 重置表单
const resetCardForm = () => {
  Object.assign(cardForm, {
    id: undefined,
    customerId: undefined,
    cardNo: '',
    packageId: undefined,
    totalTimes: 1,
    purchasePrice: 0,
    validityDays: 365,
    purchaseDate: '',
    expiryDate: '',
    remark: ''
  })
  customerOptions.value = []
  if (cardFormRef.value) {
    cardFormRef.value.resetFields()
  }
}

// 操作处理
const handleAdd = () => {
  resetCardForm()
  cardForm.cardNo = generateCardNo()
  cardForm.purchaseDate = new Date().toISOString().split('T')[0]
  cardTitle.value = '新增会员卡'
  cardOpen.value = true
}

const handleView = (row) => {
  Object.assign(cardDetail, {
    cardNo: row.cardNo,
    customerName: row.customerName,
    customerPhone: row.customerPhone,
    packageName: row.packageName,
    totalTimes: row.totalTimes,
    remainingTimes: row.remainingTimes,
    purchasePrice: row.purchasePrice,
    validityDays: row.validityDays,
    purchaseDate: row.purchaseDate,
    expiryDate: row.expiryDate,
    status: row.status,
    remark: row.remark
  })
  
  // 模拟消费记录
  consumeRecords.value = [
    {
      consumeDate: '2025-01-05',
      serviceName: '全身按摩',
      therapistName: '张师傅',
      consumeTimes: 1,
      remark: '客户满意'
    },
    {
      consumeDate: '2025-01-03',
      serviceName: '全身按摩',
      therapistName: '李师傅',
      consumeTimes: 1,
      remark: ''
    }
  ]
  
  detailOpen.value = true
}

const handleEdit = (row) => {
  resetCardForm()
  
  // 先设置顾客选项
  customerOptions.value = [
    { id: row.id, name: row.customerName, phone: row.customerPhone }
  ]
  
  Object.assign(cardForm, {
    id: row.id,
    customerId: row.id,
    cardNo: row.cardNo,
    packageId: 1, // 模拟数据，实际应该从row中获取
    totalTimes: row.totalTimes,
    purchasePrice: row.purchasePrice,
    validityDays: row.validityDays,
    purchaseDate: row.purchaseDate,
    expiryDate: row.expiryDate,
    remark: row.remark
  })
  
  cardTitle.value = '编辑会员卡'
  cardOpen.value = true
}

const handleFreeze = (row) => {
  ElMessageBox.confirm(
    `确定要冻结会员卡"${row.cardNo}"吗？`,
    '系统提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    const index = cardList.value.findIndex(item => item.id === row.id)
    if (index !== -1) {
      cardList.value[index].status = 2
    }
    ElMessage.success('会员卡冻结成功')
  }).catch(() => {
    ElMessage.info('已取消操作')
  })
}

const handleUnfreeze = (row) => {
  ElMessageBox.confirm(
    `确定要解冻会员卡"${row.cardNo}"吗？`,
    '系统提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    const index = cardList.value.findIndex(item => item.id === row.id)
    if (index !== -1) {
      cardList.value[index].status = 1
    }
    ElMessage.success('会员卡解冻成功')
  }).catch(() => {
    ElMessage.info('已取消操作')
  })
}

const handleConsume = (row) => {
  Object.assign(consumeForm, {
    cardId: row.id,
    cardNo: row.cardNo,
    customerName: row.customerName,
    remainingTimes: row.remainingTimes,
    serviceName: '',
    therapistName: '',
    consumeTimes: 1,
    remark: ''
  })
  consumeOpen.value = true
}

// 提交表单
const submitCardForm = () => {
  if (!cardFormRef.value) return
  cardFormRef.value.validate((valid) => {
    if (valid) {
      // 计算到期日期
      cardForm.expiryDate = calculateExpiryDate(cardForm.purchaseDate, cardForm.validityDays)
      
      if (cardForm.id) {
        // 编辑会员卡
        const index = cardList.value.findIndex(item => item.id === cardForm.id)
        if (index !== -1) {
          const customer = customerOptions.value.find(c => c.id === cardForm.customerId)
          const pkg = packageOptions.value.find(p => p.id === cardForm.packageId)
          
          Object.assign(cardList.value[index], {
            customerName: customer ? customer.name : '',
            customerPhone: customer ? customer.phone : '',
            packageName: pkg ? pkg.name : '',
            totalTimes: cardForm.totalTimes,
            purchasePrice: cardForm.purchasePrice,
            validityDays: cardForm.validityDays,
            purchaseDate: cardForm.purchaseDate,
            expiryDate: cardForm.expiryDate,
            remark: cardForm.remark
          })
        }
        ElMessage.success('会员卡修改成功')
      } else {
        // 新增会员卡
        const customer = customerOptions.value.find(c => c.id === cardForm.customerId)
        const pkg = packageOptions.value.find(p => p.id === cardForm.packageId)
        
        const newCard = {
          id: Date.now(),
          cardNo: cardForm.cardNo,
          customerName: customer ? customer.name : '',
          customerPhone: customer ? customer.phone : '',
          packageName: pkg ? pkg.name : '',
          totalTimes: cardForm.totalTimes,
          remainingTimes: cardForm.totalTimes,
          purchasePrice: cardForm.purchasePrice,
          validityDays: cardForm.validityDays,
          purchaseDate: cardForm.purchaseDate,
          expiryDate: cardForm.expiryDate,
          status: 1,
          remark: cardForm.remark
        }
        cardList.value.unshift(newCard)
        total.value++
        ElMessage.success('会员卡创建成功')
      }
      cardOpen.value = false
    }
  })
}

// 提交消费表单
const submitConsumeForm = () => {
  if (!consumeFormRef.value) return
  consumeFormRef.value.validate((valid) => {
    if (valid) {
      const index = cardList.value.findIndex(item => item.id === consumeForm.cardId)
      if (index !== -1) {
        cardList.value[index].remainingTimes -= consumeForm.consumeTimes
        if (cardList.value[index].remainingTimes <= 0) {
          cardList.value[index].status = 4 // 用完
        }
      }
      ElMessage.success('消费记录添加成功')
      consumeOpen.value = false
    }
  })
}

// 取消表单
const cancelCard = () => {
  cardOpen.value = false
  resetCardForm()
}

onMounted(() => {
  getPackageOptions()
  getCardData()
})
</script>

<style lang="scss" scoped>
.card-container {
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
  .card-container {
    padding: 10px;
    
    .el-form-item {
      margin-bottom: 10px;
    }
  }
}
</style>

