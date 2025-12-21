<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-logo">
        <h1>景安堂后台管理系统</h1>
      </div>
      <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" class="login-form">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="用户名" prefix-icon="el-icon-user" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" placeholder="密码" prefix-icon="el-icon-lock" type="password" />
        </el-form-item>
        <el-form-item>
          <el-button :loading="loading" type="primary" style="width: 100%" @click="handleLogin">登录</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const loginForm = reactive({
  username: '',
  password: ''
})

// Add this ref for the form instance
const loginFormRef = ref(null)

const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const loading = ref(false)

const handleLogin = () => {
  // Use loginFormRef.value to access the form instance
  if (!loginFormRef.value) return
  loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await userStore.login(loginForm.username, loginForm.password)
        
        // 获取重定向路径
        const redirect = router.currentRoute.value.query.redirect || '/'
        router.push({ path: redirect })
        ElMessage.success('登录成功')
      } catch (error) {
        ElMessage.error(error.message || '登录失败')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style lang="scss" scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f7fa;
  
  .login-box {
    width: 400px;
    padding: 30px;
    background-color: #fff;
    border-radius: 4px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    
    .login-logo {
      text-align: center;
      margin-bottom: 30px;
      
      h1 {
        font-size: 24px;
        color: #409EFF;
      }
    }
    
    .login-form {
      .el-form-item {
        margin-bottom: 20px;
      }
    }
  }
}

@media screen and (max-width: 768px) {
  .login-box {
    width: 90% !important;
    padding: 20px !important;
  }
}
</style>
