// login.js
const app = getApp()

Page({
  data: {
    phone: '',
    code: '',
    agreed: false,
    codeSent: false,
    countDown: 60
  },
  
  onLoad(options) {
    // 如果有回调页面，保存起来
    if (options.redirect) {
      this.setData({
        redirect: decodeURIComponent(options.redirect)
      })
    }
  },
  
  // 手机号输入
  onPhoneInput(e) {
    this.setData({
      phone: e.detail.value
    })
  },
  
  // 验证码输入
  onCodeInput(e) {
    this.setData({
      code: e.detail.value
    })
  },
  
  // 协议勾选变化
  onAgreementChange(e) {
    this.setData({
      agreed: e.detail.value.length > 0
    })
  },
  
  // 发送验证码
  sendVerificationCode() {
    const { phone, codeSent } = this.data
    
    if (codeSent) return
    
    // 验证手机号
    if (!phone) {
      wx.showToast({
        title: '请输入手机号码',
        icon: 'none'
      })
      return
    }
    
    if (!/^1\d{10}$/.test(phone)) {
      wx.showToast({
        title: '手机号格式不正确',
        icon: 'none'
      })
      return
    }
    
    // 发送验证码
    app.request({
      url: '/api/auth/sendCode',
      method: 'POST',
      data: {
        phone
      },
      success: (res) => {
        wx.showToast({
          title: '验证码已发送',
          icon: 'success'
        })
        
        // 开始倒计时
        this.setData({
          codeSent: true
        })
        
        this.startCountDown()
      }
    })
  },
  
  // 开始倒计时
  startCountDown() {
    let countDown = this.data.countDown
    
    const timer = setInterval(() => {
      countDown--
      
      if (countDown <= 0) {
        clearInterval(timer)
        this.setData({
          codeSent: false,
          countDown: 60
        })
      } else {
        this.setData({
          countDown
        })
      }
    }, 1000)
    
    this.countDownTimer = timer
  },
  
  // 登录
  login() {
    const { phone, code, agreed } = this.data
    
    // 验证手机号
    if (!phone) {
      wx.showToast({
        title: '请输入手机号码',
        icon: 'none'
      })
      return
    }
    
    if (!/^1\d{10}$/.test(phone)) {
      wx.showToast({
        title: '手机号格式不正确',
        icon: 'none'
      })
      return
    }
    
    // 验证验证码
    if (!code) {
      wx.showToast({
        title: '请输入验证码',
        icon: 'none'
      })
      return
    }
    
    // 验证协议
    if (!agreed) {
      wx.showToast({
        title: '请阅读并同意用户协议和隐私政策',
        icon: 'none'
      })
      return
    }
    
    // 登录
    app.request({
      url: '/api/auth/login',
      method: 'POST',
      data: {
        phone,
        code
      },
      success: (res) => {
        // 保存token
        wx.setStorageSync('token', res.data.token)
        
        // 保存用户信息
        wx.setStorageSync('userInfo', res.data.userInfo)
        
        // 更新全局数据
        app.globalData.isLogin = true
        app.globalData.userInfo = res.data.userInfo
        
        // 登录成功提示
        wx.showToast({
          title: '登录成功',
          icon: 'success'
        })
        
        // 跳转到回调页面或首页
        setTimeout(() => {
          if (this.data.redirect) {
            wx.redirectTo({
              url: this.data.redirect
            })
          } else {
            wx.switchTab({
              url: '/pages/index/index'
            })
          }
        }, 1500)
      }
    })
  },
  
  // 微信登录
  wechatLogin(e) {
    if (!this.data.agreed) {
      wx.showToast({
        title: '请阅读并同意用户协议和隐私政策',
        icon: 'none'
      })
      return
    }
    
    if (e.detail.userInfo) {
      // 用户允许授权
      wx.login({
        success: (res) => {
          if (res.code) {
            // 获取用户信息
            const userInfo = e.detail.userInfo
            
            // 发送登录请求
            app.request({
              url: '/api/auth/wechatLogin',
              method: 'POST',
              data: {
                code: res.code,
                userInfo
              },
              success: (res) => {
                // 保存token
                wx.setStorageSync('token', res.data.token)
                
                // 保存用户信息
                wx.setStorageSync('userInfo', res.data.userInfo)
                
                // 更新全局数据
                app.globalData.isLogin = true
                app.globalData.userInfo = res.data.userInfo
                
                // 登录成功提示
                wx.showToast({
                  title: '登录成功',
                  icon: 'success'
                })
                
                // 跳转到回调页面或首页
                setTimeout(() => {
                  if (this.data.redirect) {
                    wx.redirectTo({
                      url: this.data.redirect
                    })
                  } else {
                    wx.switchTab({
                      url: '/pages/index/index'
                    })
                  }
                }, 1500)
              }
            })
          } else {
            wx.showToast({
              title: '登录失败，请重试',
              icon: 'none'
            })
          }
        }
      })
    } else {
      // 用户拒绝授权
      wx.showToast({
        title: '您拒绝了授权，无法使用微信登录',
        icon: 'none'
      })
    }
  },
  
  // 跳转到注册页面
  navigateToRegister() {
    wx.navigateTo({
      url: '/pages/register/index'
    })
  },
  
  // 显示用户协议
  showUserAgreement() {
    wx.navigateTo({
      url: '/pages/webview/index?title=用户协议&url=' + encodeURIComponent('https://example.com/agreement')
    })
  },
  
  // 显示隐私政策
  showPrivacyPolicy() {
    wx.navigateTo({
      url: '/pages/webview/index?title=隐私政策&url=' + encodeURIComponent('https://example.com/privacy')
    })
  },
  
  onUnload() {
    // 清除定时器
    if (this.countDownTimer) {
      clearInterval(this.countDownTimer)
    }
  }
})
