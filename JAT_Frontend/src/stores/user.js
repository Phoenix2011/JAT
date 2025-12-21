import { defineStore } from 'pinia'
import { login, logout, getInfo } from '@/api/user'
import { getToken, setToken, removeToken } from '@/utils/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken(),
    name: '',
    avatar: '',
    roles: [],
    permissions: []
  }),
  
  actions: {
    // 登录
    login(username, password) {
      return new Promise((resolve, reject) => {
        login({ username, password }).then(response => {
          const { data } = response
          this.token = data.token
          setToken(data.token)
          resolve()
        }).catch(error => {
          reject(error)
        })
        // // 模拟登录API调用
        // setTimeout(() => {
        //   if (username === 'admin' && password === 'admin') {
        //     const token = 'mock-token-' + Date.now()
        //     this.token = token
        //     setToken(token)
        //     this.name = '管理员'
        //     this.roles = ['admin']
        //     this.permissions = ['*:*:*']
        //     resolve()
        //   } else {
        //     reject(new Error('用户名或密码错误'))
        //   }
        // }, 1000)
      })
    },
    
    // 获取用户信息
    getInfo() {
      return new Promise((resolve, reject) => {
        getInfo().then(response => {
          const { data } = response
          
          if (!data) {
            reject('验证失败，请重新登录')
          }
          
          const { roles, name, avatar, permissions } = data
          
          // 角色必须是非空数组
          if (!roles || roles.length <= 0) {
            reject('用户没有角色权限!')
          }
          
          this.roles = roles
          this.name = name
          this.avatar = avatar
          this.permissions = permissions
          resolve(data)
        }).catch(error => {
          reject(error)
        })
        // if (this.token) {
        //   const data = {
        //     name: this.name || '管理员',
        //     avatar: '',
        //     roles: this.roles.length > 0 ? this.roles : ['admin'],
        //     permissions: this.permissions.length > 0 ? this.permissions : ['*:*:*']
        //   }
          
        //   this.name = data.name
        //   this.roles = data.roles
        //   this.permissions = data.permissions
        //   resolve(data)
        // } else {
        //   reject('验证失败，请重新登录')
        // }
      })
    },
    
    // 退出登录
    logout() {
      return new Promise((resolve, reject) => {
        logout().then(() => {
          this.token = ''
          this.roles = []
          this.permissions = []
          removeToken()
          resolve()
        }).catch(error => {
          reject(error)
        })
        // this.token = ''
        // this.name = ''
        // this.roles = []
        // this.permissions = []
        // removeToken()
        // resolve()
      })
    },
    
    // 重置令牌
    resetToken() {
      return new Promise(resolve => {
        this.token = ''
        this.name = ''
        this.roles = []
        this.permissions = []
        removeToken()
        resolve()
      })
    }
  }
})
