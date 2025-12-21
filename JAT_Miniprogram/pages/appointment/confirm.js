// appointment/confirm.js
const app = getApp()

Page({
  data: {
    appointmentInfo: {
      date: '',
      timeSlot: '',
      serviceName: '',
      duration: 0,
      therapistName: '',
      serviceId: '',
      timeSlotId: '',
      therapistId: ''
    },
    shopInfo: {
      address: '加载中...'
    },
    contactInfo: {
      name: '',
      phone: '',
      remark: ''
    },
    paymentMethods: [
      {
        id: 'wechat',
        name: '微信支付',
        icon: '/static/images/icons/wechat-pay.png'
      },
      {
        id: 'card',
        name: '会员卡支付',
        icon: '/static/images/icons/card-pay.png',
        desc: '使用会员卡次数抵扣'
      }
    ],
    selectedPayment: 'wechat',
    coupons: [],
    selectedCoupon: null,
    tempSelectedCoupon: null,
    showCouponPopup: false,
    cards: [],
    selectedCard: null,
    tempSelectedCard: null,
    showCardPopup: false,
    originalPrice: 0,
    finalPrice: 0,
    canSubmit: false
  },
  
  onLoad(options) {
    // 获取预约信息
    if (options.date && options.timeSlotId && options.serviceId && options.therapistId) {
      this.setData({
        'appointmentInfo.date': options.date,
        'appointmentInfo.timeSlotId': options.timeSlotId,
        'appointmentInfo.serviceId': options.serviceId,
        'appointmentInfo.therapistId': options.therapistId
      })
      
      // 加载预约详情
      this.loadAppointmentDetail()
      
      // 加载店铺信息
      this.loadShopInfo()
      
      // 加载用户信息
      this.loadUserInfo()
      
      // 加载优惠券
      this.loadCoupons()
      
      // 加载会员卡
      this.loadCards()
    } else {
      wx.showToast({
        title: '参数错误',
        icon: 'none'
      })
      
      setTimeout(() => {
        wx.navigateBack()
      }, 1500)
    }
  },
  
  // 加载预约详情
  loadAppointmentDetail() {
    const { date, timeSlotId, serviceId, therapistId } = this.data.appointmentInfo
    
    app.request({
      url: '/api/appointment/detail',
      method: 'GET',
      data: {
        date,
        timeSlotId,
        serviceId,
        therapistId
      },
      success: (res) => {
        if (res.data) {
          this.setData({
            appointmentInfo: {
              ...this.data.appointmentInfo,
              timeSlot: res.data.timeSlot,
              serviceName: res.data.serviceName,
              duration: res.data.duration,
              therapistName: res.data.therapistName
            },
            originalPrice: res.data.price,
            finalPrice: res.data.price
          })
          
          // 检查是否可以提交
          this.checkCanSubmit()
        }
      }
    })
  },
  
  // 加载店铺信息
  loadShopInfo() {
    app.request({
      url: '/api/shop/info',
      method: 'GET',
      success: (res) => {
        if (res.data) {
          this.setData({
            shopInfo: res.data
          })
        }
      }
    })
  },
  
  // 加载用户信息
  loadUserInfo() {
    const userInfo = wx.getStorageSync('userInfo')
    
    if (userInfo) {
      this.setData({
        'contactInfo.name': userInfo.name || '',
        'contactInfo.phone': userInfo.phone || ''
      })
      
      // 检查是否可以提交
      this.checkCanSubmit()
    }
  },
  
  // 加载优惠券
  loadCoupons() {
    const { serviceId } = this.data.appointmentInfo
    
    app.request({
      url: '/api/coupon/available',
      method: 'GET',
      data: {
        serviceId
      },
      success: (res) => {
        this.setData({
          coupons: res.data || []
        })
      }
    })
  },
  
  // 加载会员卡
  loadCards() {
    const { serviceId } = this.data.appointmentInfo
    
    app.request({
      url: '/api/card/available',
      method: 'GET',
      data: {
        serviceId
      },
      success: (res) => {
        this.setData({
          cards: res.data || []
        })
      }
    })
  },
  
  // 姓名输入
  onNameInput(e) {
    this.setData({
      'contactInfo.name': e.detail.value
    })
    
    // 检查是否可以提交
    this.checkCanSubmit()
  },
  
  // 手机号输入
  onPhoneInput(e) {
    this.setData({
      'contactInfo.phone': e.detail.value
    })
    
    // 检查是否可以提交
    this.checkCanSubmit()
  },
  
  // 备注输入
  onRemarkInput(e) {
    this.setData({
      'contactInfo.remark': e.detail.value
    })
  },
  
  // 支付方式变更
  onPaymentChange(e) {
    this.setData({
      selectedPayment: e.detail.value
    })
    
    // 如果选择会员卡支付，但没有选择会员卡，则弹出会员卡选择
    if (e.detail.value === 'card' && !this.data.selectedCard && this.data.cards.length > 0) {
      this.showCardList()
    }
    
    // 重新计算价格
    this.calculatePrice()
  },
  
  // 显示优惠券列表
  showCouponList() {
    this.setData({
      showCouponPopup: true,
      tempSelectedCoupon: this.data.selectedCoupon
    })
  },
  
  // 隐藏优惠券列表
  hideCouponList() {
    this.setData({
      showCouponPopup: false,
      tempSelectedCoupon: null
    })
  },
  
  // 选择优惠券
  selectCoupon(e) {
    const { id } = e.currentTarget.dataset
    
    if (!id) {
      // 不使用优惠券
      this.setData({
        tempSelectedCoupon: null
      })
      return
    }
    
    const coupon = this.data.coupons.find(item => item.id === id)
    
    if (coupon) {
      this.setData({
        tempSelectedCoupon: coupon
      })
    }
  },
  
  // 确认优惠券选择
  confirmCoupon() {
    this.setData({
      selectedCoupon: this.data.tempSelectedCoupon,
      showCouponPopup: false
    })
    
    // 重新计算价格
    this.calculatePrice()
  },
  
  // 显示会员卡列表
  showCardList() {
    this.setData({
      showCardPopup: true,
      tempSelectedCard: this.data.selectedCard
    })
  },
  
  // 隐藏会员卡列表
  hideCardList() {
    this.setData({
      showCardPopup: false,
      tempSelectedCard: null
    })
  },
  
  // 选择会员卡
  selectCard(e) {
    const { id } = e.currentTarget.dataset
    
    if (!id) {
      // 不使用会员卡
      this.setData({
        tempSelectedCard: null
      })
      return
    }
    
    const card = this.data.cards.find(item => item.id === id)
    
    if (card) {
      this.setData({
        tempSelectedCard: card
      })
    }
  },
  
  // 确认会员卡选择
  confirmCard() {
    this.setData({
      selectedCard: this.data.tempSelectedCard,
      showCardPopup: false
    })
    
    // 如果选择了会员卡，自动切换到会员卡支付
    if (this.data.tempSelectedCard) {
      this.setData({
        selectedPayment: 'card'
      })
    } else if (this.data.selectedPayment === 'card') {
      // 如果取消选择会员卡，但支付方式是会员卡，则切换到微信支付
      this.setData({
        selectedPayment: 'wechat'
      })
    }
    
    // 重新计算价格
    this.calculatePrice()
  },
  
  // 计算最终价格
  calculatePrice() {
    let finalPrice = this.data.originalPrice
    
    // 如果使用会员卡支付，价格为0
    if (this.data.selectedPayment === 'card' && this.data.selectedCard) {
      finalPrice = 0
    } else {
      // 如果使用优惠券
      if (this.data.selectedCoupon) {
        finalPrice = Math.max(0, finalPrice - this.data.selectedCoupon.value)
      }
    }
    
    this.setData({
      finalPrice
    })
  },
  
  // 检查是否可以提交
  checkCanSubmit() {
    const { name, phone } = this.data.contactInfo
    const { serviceName } = this.data.appointmentInfo
    
    // 验证姓名和手机号
    const isNameValid = !!name.trim()
    const isPhoneValid = /^1\d{10}$/.test(phone)
    
    // 验证服务信息是否加载完成
    const isServiceLoaded = !!serviceName
    
    this.setData({
      canSubmit: isNameValid && isPhoneValid && isServiceLoaded
    })
  },
  
  // 提交预约
  submitAppointment() {
    if (!this.data.canSubmit) {
      return
    }
    
    const { date, timeSlotId, serviceId, therapistId } = this.data.appointmentInfo
    const { name, phone, remark } = this.data.contactInfo
    const { selectedPayment, selectedCoupon, selectedCard } = this.data
    
    // 构建请求参数
    const params = {
      date,
      timeSlotId,
      serviceId,
      therapistId,
      contactName: name,
      contactPhone: phone,
      remark: remark || undefined,
      paymentMethod: selectedPayment,
      couponId: selectedCoupon ? selectedCoupon.id : undefined,
      cardId: selectedCard ? selectedCard.id : undefined
    }
    
    app.request({
      url: '/api/appointment/create',
      method: 'POST',
      data: params,
      success: (res) => {
        if (selectedPayment === 'wechat' && this.data.finalPrice > 0) {
          // 如果是微信支付且金额大于0，跳转到支付页面
          wx.navigateTo({
            url: `/pages/payment/index?orderId=${res.data.orderId}&orderType=appointment`
          })
        } else {
          // 如果是会员卡支付或金额为0，直接提示预约成功
          wx.showToast({
            title: '预约成功',
            icon: 'success'
          })
          
          // 跳转到预约详情页
          setTimeout(() => {
            wx.redirectTo({
              url: `/pages/appointment/detail?id=${res.data.appointmentId}`
            })
          }, 1500)
        }
      }
    })
  }
})
