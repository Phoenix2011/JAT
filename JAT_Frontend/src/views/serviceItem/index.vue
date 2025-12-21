<template>
  <div class="service-container">
    <div class="search-bar">
      <el-form :inline="true" :model="categoryQueryParams" class="demo-form-inline">
        <el-form-item label="类别名称">
          <el-input v-model="categoryQueryParams.name" placeholder="请输入类别名称" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="分类" v-if="activeTab !== 'category'">
          <el-select v-model="categoryQueryParams.categoryId" placeholder="请选择分类" clearable>
            <el-option
              v-for="item in categoryOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
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
      <!-- 服务分类标签页 -->
      <el-tab-pane label="服务分类" name="category">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>服务分类列表</span>
              <el-button type="primary" @click="handleAddCategory">新增分类</el-button>
            </div>
          </template>
          
          <el-table :data="categoryList" style="width: 100%">
            <el-table-column type="index" width="50" />
            <el-table-column prop="name" label="分类名称" width="150" />
            <el-table-column prop="description" label="描述" />
            <el-table-column prop="sort" label="排序" width="100" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
                  {{ scope.row.status === 1 ? '启用' : '禁用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="scope">
                <el-button type="success" size="small" @click="handleEditCategory(scope.row)">编辑</el-button>
                <el-button type="danger" size="small" @click="handleDeleteCategory(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>

      <!-- 单次服务标签页 -->
      <el-tab-pane label="单次服务" name="service">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>单次服务列表</span>
              <el-button type="primary" @click="handleAddService">新增服务</el-button>
            </div>
          </template>
          
          <el-table :data="serviceList" style="width: 100%">
            <el-table-column type="index" width="50" />
            <el-table-column prop="name" label="服务名称" width="150" />
            <el-table-column prop="categoryName" label="分类" width="120" />
            <el-table-column prop="price" label="价格" width="100">
              <template #default="scope">
                ¥{{ scope.row.price.toFixed(2) }}
              </template>
            </el-table-column>
            <el-table-column prop="duration" label="时长(分钟)" width="120" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
                  {{ scope.row.status === 1 ? '上架' : '下架' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="description" label="描述" />
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="scope">
                <el-button type="success" size="small" @click="handleEditService(scope.row)">编辑</el-button>
                <el-button type="danger" size="small" @click="handleDeleteService(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          
          <div class="pagination-container">
            <el-pagination
              v-model:current-page="categoryQueryParams.pageNum"
              v-model:page-size="categoryQueryParams.pageSize"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next, jumper"
              :total="serviceTotal"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </div>
        </el-card>
      </el-tab-pane>

      <!-- 套餐标签页 -->
      <el-tab-pane label="套餐管理" name="package">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>套餐列表</span>
              <el-button type="primary" @click="handleAddPackage">新增套餐</el-button>
            </div>
          </template>
          
          <el-table :data="packageList" style="width: 100%">
            <el-table-column type="index" width="50" />
            <el-table-column prop="name" label="套餐名称" width="150" />
            <el-table-column prop="categoryName" label="分类" width="120" />
            <el-table-column prop="originalPrice" label="原价" width="100">
              <template #default="scope">
                ¥{{ scope.row.originalPrice.toFixed(2) }}
              </template>
            </el-table-column>
            <el-table-column prop="packagePrice" label="套餐价" width="100">
              <template #default="scope">
                ¥{{ scope.row.packagePrice.toFixed(2) }}
              </template>
            </el-table-column>
            <el-table-column prop="times" label="次数" width="80" />
            <el-table-column prop="validityDays" label="有效期(天)" width="120" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
                  {{ scope.row.status === 1 ? '上架' : '下架' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="description" label="描述" />
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="scope">
                <el-button type="success" size="small" @click="handleEditPackage(scope.row)">编辑</el-button>
                <el-button type="danger" size="small" @click="handleDeletePackage(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          
          <div class="pagination-container">
            <el-pagination
              v-model:current-page="packageQueryParams.pageNum"
              v-model:page-size="packageQueryParams.pageSize"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next, jumper"
              :total="packageTotal"
              @size-change="handlePackageSizeChange"
              @current-change="handlePackageCurrentChange"
            />
          </div>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- 服务分类对话框 -->
    <el-dialog :title="categoryTitle" v-model="categoryOpen" width="600px" append-to-body>
      <el-form ref="categoryFormRef" :model="categoryForm" :rules="categoryRules" label-width="100px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="categoryForm.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="categoryForm.description" type="textarea" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="categoryForm.sort" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="categoryForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="categoryOpen = false">取 消</el-button>
          <el-button type="primary" @click="submitCategoryForm">确 定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 单次服务对话框 -->
    <el-dialog :title="serviceTitle" v-model="serviceOpen" width="700px" append-to-body>
      <el-form ref="serviceFormRef" :model="serviceForm" :rules="serviceRules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="服务名称" prop="name">
              <el-input v-model="serviceForm.name" placeholder="请输入服务名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="服务分类" prop="categoryId">
              <el-select v-model="serviceForm.categoryId" placeholder="请选择分类" style="width: 100%">
                <el-option
                  v-for="item in categoryOptions"
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
            <el-form-item label="价格" prop="price">
              <el-input-number v-model="serviceForm.price" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="时长(分钟)" prop="duration">
              <el-input-number v-model="serviceForm.duration" :min="1" :max="480" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="serviceForm.status">
            <el-radio :label="1">上架</el-radio>
            <el-radio :label="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="服务描述" prop="description">
          <el-input v-model="serviceForm.description" type="textarea" placeholder="请输入服务描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="serviceOpen = false">取 消</el-button>
          <el-button type="primary" @click="submitServiceForm">确 定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 套餐对话框 -->
    <el-dialog :title="packageTitle" v-model="packageOpen" width="800px" append-to-body>
      <el-form ref="packageFormRef" :model="packageForm" :rules="packageRules" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="套餐名称" prop="name">
              <el-input v-model="packageForm.name" placeholder="请输入套餐名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="服务分类" prop="categoryId">
              <el-select v-model="packageForm.categoryId" placeholder="请选择分类" style="width: 100%">
                <el-option
                  v-for="item in categoryOptions"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="原价" prop="originalPrice">
              <el-input-number v-model="packageForm.originalPrice" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="套餐价" prop="packagePrice">
              <el-input-number v-model="packageForm.packagePrice" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="次数" prop="times">
              <el-input-number v-model="packageForm.times" :min="1" :max="999" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="有效期(天)" prop="validityDays">
              <el-input-number v-model="packageForm.validityDays" :min="1" :max="3650" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="packageForm.status">
                <el-radio :label="1">上架</el-radio>
                <el-radio :label="0">下架</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="包含服务" prop="serviceIds">
          <el-select v-model="packageForm.serviceIds" multiple placeholder="请选择包含的服务" style="width: 100%">
            <el-option
              v-for="item in availableServices"
              :key="item.id"
              :label="`${item.name} (¥${item.price})`"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="套餐描述" prop="description">
          <el-input v-model="packageForm.description" type="textarea" placeholder="请输入套餐描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="packageOpen = false">取 消</el-button>
          <el-button type="primary" @click="submitPackageForm">确 定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 当前激活的标签页
const activeTab = ref('service')

// 查询参数 - 服务分类
const categoryQueryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  name: '',
  categoryId: undefined
})

// 查询参数 - 单次服务
const serviceQueryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  name: '',
  categoryId: undefined
})

// 查询参数 - 套餐
const packageQueryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  name: '',
  categoryId: undefined
})

// 数据列表
const serviceList = ref([])
const categoryList = ref([])
const packageList = ref([])
const categoryOptions = ref([])
const availableServices = ref([])
const serviceTotal = ref(0)
const packageTotal = ref(0)

// 对话框相关
const categoryOpen = ref(false)
const serviceOpen = ref(false)
const packageOpen = ref(false)
const categoryTitle = ref('')
const serviceTitle = ref('')
const packageTitle = ref('')
const categoryFormRef = ref(null)
const serviceFormRef = ref(null)
const packageFormRef = ref(null)

// 表单数据
const categoryForm = reactive({
  id: undefined,
  name: '',
  description: '',
  sort: 0,
  status: 1
})

const serviceForm = reactive({
  id: undefined,
  name: '',
  categoryId: undefined,
  price: 0,
  duration: 60,
  status: 1,
  description: ''
})

const packageForm = reactive({
  id: undefined,
  name: '',
  categoryId: undefined,
  originalPrice: 0,
  packagePrice: 0,
  times: 1,
  validityDays: 365,
  serviceIds: [],
  status: 1,
  description: ''
})

// 表单验证规则
const categoryRules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
  sort: [{ required: true, message: '请输入排序', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const serviceRules = {
  name: [{ required: true, message: '请输入服务名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择服务分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  duration: [{ required: true, message: '请输入时长', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const packageRules = {
  name: [{ required: true, message: '请输入套餐名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择服务分类', trigger: 'change' }],
  originalPrice: [{ required: true, message: '请输入原价', trigger: 'blur' }],
  packagePrice: [{ required: true, message: '请输入套餐价', trigger: 'blur' }],
  times: [{ required: true, message: '请输入次数', trigger: 'blur' }],
  validityDays: [{ required: true, message: '请输入有效期', trigger: 'blur' }],
  serviceIds: [{ required: true, message: '请选择包含的服务', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

// 获取服务列表
const getServiceData = async () => {
  // 模拟数据
  serviceList.value = [
    {
      id: 1,
      name: '全身按摩',
      categoryId: 1,
      categoryName: '按摩服务',
      price: 128.00,
      duration: 60,
      status: 1,
      description: '全身放松按摩服务'
    },
    {
      id: 2,
      name: '足疗',
      categoryId: 2,
      categoryName: '足疗服务',
      price: 88.00,
      duration: 45,
      status: 1,
      description: '专业足疗服务'
    },
    {
      id: 3,
      name: '艾灸',
      categoryId: 3,
      categoryName: '中医理疗',
      price: 98.00,
      duration: 30,
      status: 1,
      description: '传统艾灸理疗'
    }
  ]
  availableServices.value = serviceList.value.filter(s => s.status === 1)
  serviceTotal.value = serviceList.value.length
}

// 获取分类列表
const getCategoryData = async () => {
  // 模拟数据
  categoryList.value = [
    {
      id: 1,
      name: '按摩服务',
      description: '各类按摩服务',
      sort: 1,
      status: 1
    },
    {
      id: 2,
      name: '足疗服务',
      description: '专业足疗服务',
      sort: 2,
      status: 1
    },
    {
      id: 3,
      name: '中医理疗',
      description: '传统中医理疗服务',
      sort: 3,
      status: 1
    }
  ]
  categoryOptions.value = categoryList.value.filter(c => c.status === 1)
}

// 获取套餐列表
const getPackageData = async () => {
  // 模拟数据
  packageList.value = [
    {
      id: 1,
      name: '全身按摩套餐',
      categoryId: 1,
      categoryName: '按摩服务',
      originalPrice: 1280.00,
      packagePrice: 1080.00,
      times: 10,
      validityDays: 365,
      serviceIds: [1],
      status: 1,
      description: '10次全身按摩套餐，有效期一年'
    },
    {
      id: 2,
      name: '足疗养生套餐',
      categoryId: 2,
      categoryName: '足疗服务',
      originalPrice: 1760.00,
      packagePrice: 1500.00,
      times: 20,
      validityDays: 365,
      serviceIds: [2],
      status: 1,
      description: '20次足疗套餐，养生保健'
    }
  ]
  packageTotal.value = packageList.value.length
}

// 查询
const handleQuery = () => {
  if (activeTab.value === 'category') {
    categoryQueryParams.pageNum = 1
    getCategoryData()
  } else if(activeTab.value === 'service') {
    serviceQueryParams.pageNum = 1
    getServiceData()
  } else if (activeTab.value === 'package') {
    packageQueryParams.pageNum = 1
    getPackageData()
  }
}

// 重置
const resetQuery = () => {
  if (activeTab.value === 'category') {
    Object.assign(categoryQueryParams, {
      pageNum: 1,
      pageSize: 10,
      name: '',
      categoryId: undefined
    })
    getCategoryData()
  } else if(activeTab.value === 'service') {
    Object.assign(serviceQueryParams, {
      pageNum: 1,
      pageSize: 10,
      name: '',
      categoryId: undefined
    })
    getServiceData()
  } else if (activeTab.value === 'package') {
    Object.assign(packageQueryParams, {
      pageNum: 1,
      pageSize: 10,
      name: '',
      categoryId: undefined
    })
    getPackageData()
  }
}

// 标签页切换
const handleTabClick = (tab) => {
  if (tab.name === 'category') {
    getCategoryData()
  } else if (tab.name === 'service') {
    getServiceData()
  } else if (tab.name === 'package') {
    getPackageData()
  }
}

// 分页处理 - 服务分类
const handleCategorySizeChange = (val) => {
  categoryQueryParams.pageSize = val
  getCategoryData()
}
const handleCategoryCurrentChange = (val) => {
  categoryQueryParams.pageNum = val
  getCategoryData()
}

// 分页处理 - 单次服务
const handleSizeChange = (val) => {
  serviceQueryParams.pageSize = val
  getServiceData()
}

const handleCurrentChange = (val) => {
  serviceQueryParams.pageNum = val
  getServiceData()
}

// 分页处理 - 套餐
const handlePackageSizeChange = (val) => {
  packageQueryParams.pageSize = val
  getPackageData()
}

const handlePackageCurrentChange = (val) => {
  packageQueryParams.pageNum = val
  getPackageData()
}

// 重置表单
const resetCategoryForm = () => {
  Object.assign(categoryForm, {
    id: undefined,
    name: '',
    description: '',
    sort: 0,
    status: 1
  })
  if (categoryFormRef.value) {
    categoryFormRef.value.resetFields()
  }
}

const resetServiceForm = () => {
  Object.assign(serviceForm, {
    id: undefined,
    name: '',
    categoryId: undefined,
    price: 0,
    duration: 60,
    status: 1,
    description: ''
  })
  if (serviceFormRef.value) {
    serviceFormRef.value.resetFields()
  }
}

const resetPackageForm = () => {
  Object.assign(packageForm, {
    id: undefined,
    name: '',
    categoryId: undefined,
    originalPrice: 0,
    packagePrice: 0,
    times: 1,
    validityDays: 365,
    serviceIds: [],
    status: 1,
    description: ''
  })
  if (packageFormRef.value) {
    packageFormRef.value.resetFields()
  }
}

// 服务分类操作
const handleAddCategory = () => {
  resetCategoryForm()
  categoryTitle.value = '新增分类'
  categoryOpen.value = true
}

const handleEditCategory = (row) => {
  resetCategoryForm()
  Object.assign(categoryForm, {
    id: row.id,
    name: row.name,
    description: row.description,
    sort: row.sort,
    status: row.status
  })
  categoryTitle.value = '编辑分类'
  categoryOpen.value = true
}

const handleDeleteCategory = (row) => {
  ElMessageBox.confirm(
    `确定要删除分类"${row.name}"吗？`,
    '系统提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    const index = categoryList.value.findIndex(item => item.id === row.id)
    if (index !== -1) {
      categoryList.value.splice(index, 1)
    }
    ElMessage.success('删除成功')
    getCategoryData()
  }).catch(() => {
    ElMessage.info('已取消删除')
  })
}

const submitCategoryForm = () => {
  if (!categoryFormRef.value) return
  categoryFormRef.value.validate((valid) => {
    if (valid) {
      if (categoryForm.id) {
        // 编辑分类
        const index = categoryList.value.findIndex(item => item.id === categoryForm.id)
        if (index !== -1) {
          Object.assign(categoryList.value[index], {
            name: categoryForm.name,
            description: categoryForm.description,
            sort: categoryForm.sort,
            status: categoryForm.status
          })
        }
        ElMessage.success('分类修改成功')
      } else {
        // 新增分类
        const newCategory = {
          id: Date.now(),
          name: categoryForm.name,
          description: categoryForm.description,
          sort: categoryForm.sort,
          status: categoryForm.status
        }
        categoryList.value.push(newCategory)
        ElMessage.success('分类创建成功')
      }
      categoryOpen.value = false
      getCategoryData()
    }
  })
}

// 单次服务操作
const handleAddService = () => {
  resetServiceForm()
  serviceTitle.value = '新增服务'
  serviceOpen.value = true
}

const handleEditService = (row) => {
  resetServiceForm()
  Object.assign(serviceForm, {
    id: row.id,
    name: row.name,
    categoryId: row.categoryId,
    price: row.price,
    duration: row.duration,
    status: row.status,
    description: row.description
  })
  serviceTitle.value = '编辑服务'
  serviceOpen.value = true
}

const handleDeleteService = (row) => {
  ElMessageBox.confirm(
    `确定要删除服务"${row.name}"吗？`,
    '系统提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    const index = serviceList.value.findIndex(item => item.id === row.id)
    if (index !== -1) {
      serviceList.value.splice(index, 1)
      serviceTotal.value--
    }
    ElMessage.success('删除成功')
    getServiceData()
  }).catch(() => {
    ElMessage.info('已取消删除')
  })
}

const submitServiceForm = () => {
  if (!serviceFormRef.value) return
  serviceFormRef.value.validate((valid) => {
    if (valid) {
      const category = categoryOptions.value.find(c => c.id === serviceForm.categoryId)
      
      if (serviceForm.id) {
        // 编辑服务
        const index = serviceList.value.findIndex(item => item.id === serviceForm.id)
        if (index !== -1) {
          Object.assign(serviceList.value[index], {
            name: serviceForm.name,
            categoryId: serviceForm.categoryId,
            categoryName: category ? category.name : '',
            price: serviceForm.price,
            duration: serviceForm.duration,
            status: serviceForm.status,
            description: serviceForm.description
          })
        }
        ElMessage.success('服务修改成功')
      } else {
        // 新增服务
        const newService = {
          id: Date.now(),
          name: serviceForm.name,
          categoryId: serviceForm.categoryId,
          categoryName: category ? category.name : '',
          price: serviceForm.price,
          duration: serviceForm.duration,
          status: serviceForm.status,
          description: serviceForm.description
        }
        serviceList.value.push(newService)
        serviceTotal.value++
        ElMessage.success('服务创建成功')
      }
      serviceOpen.value = false
      getServiceData()
    }
  })
}

// 套餐操作
const handleAddPackage = () => {
  resetPackageForm()
  packageTitle.value = '新增套餐'
  packageOpen.value = true
}

const handleEditPackage = (row) => {
  resetPackageForm()
  Object.assign(packageForm, {
    id: row.id,
    name: row.name,
    categoryId: row.categoryId,
    originalPrice: row.originalPrice,
    packagePrice: row.packagePrice,
    times: row.times,
    validityDays: row.validityDays,
    serviceIds: row.serviceIds || [],
    status: row.status,
    description: row.description
  })
  packageTitle.value = '编辑套餐'
  packageOpen.value = true
}

const handleDeletePackage = (row) => {
  ElMessageBox.confirm(
    `确定要删除套餐"${row.name}"吗？`,
    '系统提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    const index = packageList.value.findIndex(item => item.id === row.id)
    if (index !== -1) {
      packageList.value.splice(index, 1)
      packageTotal.value--
    }
    ElMessage.success('删除成功')
    getPackageData()
  }).catch(() => {
    ElMessage.info('已取消删除')
  })
}

const submitPackageForm = () => {
  if (!packageFormRef.value) return
  packageFormRef.value.validate((valid) => {
    if (valid) {
      const category = categoryOptions.value.find(c => c.id === packageForm.categoryId)
      
      if (packageForm.id) {
        // 编辑套餐
        const index = packageList.value.findIndex(item => item.id === packageForm.id)
        if (index !== -1) {
          Object.assign(packageList.value[index], {
            name: packageForm.name,
            categoryId: packageForm.categoryId,
            categoryName: category ? category.name : '',
            originalPrice: packageForm.originalPrice,
            packagePrice: packageForm.packagePrice,
            times: packageForm.times,
            validityDays: packageForm.validityDays,
            serviceIds: packageForm.serviceIds,
            status: packageForm.status,
            description: packageForm.description
          })
        }
        ElMessage.success('套餐修改成功')
      } else {
        // 新增套餐
        const newPackage = {
          id: Date.now(),
          name: packageForm.name,
          categoryId: packageForm.categoryId,
          categoryName: category ? category.name : '',
          originalPrice: packageForm.originalPrice,
          packagePrice: packageForm.packagePrice,
          times: packageForm.times,
          validityDays: packageForm.validityDays,
          serviceIds: packageForm.serviceIds,
          status: packageForm.status,
          description: packageForm.description
        }
        packageList.value.push(newPackage)
        packageTotal.value++
        ElMessage.success('套餐创建成功')
      }
      packageOpen.value = false
      getPackageData()
    }
  })
}

onMounted(() => {
  getCategoryData()
  getServiceData()
  getPackageData()
})
</script>

<style lang="scss" scoped>
.service-container {
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
  .service-container {
    padding: 10px;
    
    .el-form-item {
      margin-bottom: 10px;
    }
  }
}
</style>
