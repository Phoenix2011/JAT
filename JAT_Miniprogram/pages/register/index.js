// register.js
const app = getApp()

Page({
  data: {
    phone: '',
    code: '',
    name: '',
    gender: '1', // 默认男性
    inviteCode: '',
    agreed: false,
    codeSent: false,
    countDown: 60,
    showInviteCode: true // 是否显示邀请码输入框
  },
  
  onLoad(options) {
    // 如果URL中包含邀请码，则自动填充
    if (options.inviteCode) {
      this.setData({
        inviteCode: options.inviteCode
      })
    }
    
    // 检查是否需要显示邀请码输入框
    this.checkInviteCodeSetting()
  },
  
  // 检查是否需要显示邀请码输入框
  checkInviteCodeSetting() {
    app.request({
      url: '/api/config/inviteCodeRequired',
      method: 'GET',
      showLoading: false,
      success: (res) => {
        this.setData({
          showInviteCode: res.data.show
        })
      }
    })
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
  
  // 姓名输入
  onNameInput(e) {
    this.setData({
      name: e.detail.value
    })
  },
  
  // 性别选择
  onGenderChange(e) {
    this.setData({
      gender: e.detail.value
    })
  },
  
  // 邀请码输入
  onInviteCodeInput(e) {
    this.setData({
      inviteCode: e.detail.value
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
        phone,
        type: 'register' // 注册类型
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
  
  // 注册
  register() {
    const { phone, code, name, gender, inviteCode, agreed } = this.data
    
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
    
    // 验证姓名
    if (!name) {
      wx.showToast({
        title: '请输入姓名',
        icon: 'none'
      })
      return
    }
    
    // 验证性别
    if (!gender) {
      wx.showToast({
        title: '请选择性别',
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
    
    // 注册
    app.request({
      url: '/api/auth/register',
      method: 'POST',
      data: {
        phone,
        code,
        name,
        gender,
        inviteCode: inviteCode || undefined
      },
      success: (res) => {
        // 保存token
        wx.setStorageSync('token', res.data.token)
        
        // 保存用户信息
        wx.setStorageSync('userInfo', res.data.userInfo)
        
        // 更新全局数据
        app.globalData.isLogin = true
        app.globalData.userInfo = res.data.userInfo
        
        // 注册成功提示
        wx.showToast({
          title: '注册成功',
          icon: 'success'
        })
        
        // 跳转到首页
        setTimeout(() => {
          wx.switchTab({
            url: '/pages/index/index'
          })
        }, 1500)
      }
    })
  },
  
  // 跳转到登录页面
  navigateToLogin() {
    wx.navigateBack()
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
