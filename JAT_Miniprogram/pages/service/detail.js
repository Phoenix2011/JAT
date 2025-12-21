// service/detail.js
const app = getApp()

Page({
  data: {
    serviceId: null,
    serviceType: 'service', // service 或 package
    serviceDetail: {},
    recommendTherapists: [],
    reviews: [],
    reviewPage: 1,
    reviewPageSize: 5,
    hasMoreReviews: true,
    loadingReviews: false,
    shopInfo: {
      phone: '13800138000'
    }
  },
  
  onLoad(options) {
    // 获取服务ID和类型
    if (options.id) {
      this.setData({
        serviceId: options.id,
        serviceType: options.type || 'service'
      })
      
      // 加载服务详情
      this.loadServiceDetail()
      
      // 加载推荐技师
      this.loadRecommendTherapists()
      
      // 加载评价
      this.loadReviews(true)
      
      // 加载店铺信息
      this.loadShopInfo()
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
  
  onShareAppMessage() {
    const { serviceDetail } = this.data
    
    return {
      title: serviceDetail.name,
      path: `/pages/service/detail?id=${this.data.serviceId}&type=${this.data.serviceType}`,
      imageUrl: serviceDetail.images && serviceDetail.images.length > 0 ? serviceDetail.images[0] : ''
    }
  },
  
  // 加载服务详情
  loadServiceDetail() {
    const { serviceId, serviceType } = this.data
    
    app.request({
      url: `/api/service/detail`,
      method: 'GET',
      data: {
        id: serviceId,
        type: serviceType
      },
      success: (res) => {
        this.setData({
          serviceDetail: res.data || {}
        })
      }
    })
  },
  
  // 加载推荐技师
  loadRecommendTherapists() {
    const { serviceId, serviceType } = this.data
    
    app.request({
      url: '/api/therapist/recommendForService',
      method: 'GET',
      data: {
        serviceId,
        serviceType
      },
      success: (res) => {
        this.setData({
          recommendTherapists: res.data || []
        })
      }
    })
  },
  
  // 加载评价
  loadReviews(refresh = false) {
    const { serviceId, serviceType, reviewPage, reviewPageSize } = this.data
    
    // 如果是刷新，则重置页码
    if (refresh) {
      this.setData({
        reviewPage: 1,
        reviews: [],
        hasMoreReviews: true
      })
    }
    
    // 如果没有更多数据，则不加载
    if (!this.data.hasMoreReviews && !refresh) {
      return
    }
    
    // 设置加载状态
    this.setData({
      loadingReviews: true
    })
    
    app.request({
      url: '/api/review/list',
      method: 'GET',
      data: {
        serviceId,
        serviceType,
        pageNum: refresh ? 1 : reviewPage,
        pageSize: reviewPageSize
      },
      success: (res) => {
        const newReviews = res.data.list || []
        const total = res.data.total || 0
        
        // 更新评价列表
        this.setData({
          reviews: [...this.data.reviews, ...newReviews],
          reviewPage: reviewPage + 1,
          hasMoreReviews: this.data.reviews.length + newReviews.length < total
        })
      },
      complete: () => {
        this.setData({
          loadingReviews: false
        })
      }
    })
  },
  
  // 加载更多评价
  loadMoreReviews() {
    if (!this.data.loadingReviews) {
      this.loadReviews()
    }
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
  
  // 预览服务图片
  previewImage(e) {
    const { url } = e.currentTarget.dataset
    const { images } = this.data.serviceDetail
    
    wx.previewImage({
      current: url,
      urls: images
    })
  },
  
  // 预览评价图片
  previewReviewImage(e) {
    const { urls, current } = e.currentTarget.dataset
    
    wx.previewImage({
      current,
      urls
    })
  },
  
  // 跳转到技师详情
  navigateToTherapistDetail(e) {
    const { id } = e.currentTarget.dataset
    
    wx.navigateTo({
      url: `/pages/therapist/detail?id=${id}`
    })
  },
  
  // 拨打电话
  makePhoneCall() {
    wx.makePhoneCall({
      phoneNumber: this.data.shopInfo.phone
    })
  },
  
  // 跳转到店铺主页
  navigateToShop() {
    wx.switchTab({
      url: '/pages/index/index'
    })
  },
  
  // 加入购物车
  addToCart() {
    // 检查登录状态
    if (!app.globalData.isLogin) {
      wx.navigateTo({
        url: '/pages/login/index'
      })
      return
    }
    
    const { serviceId, serviceDetail } = this.data
    
    app.request({
      url: '/api/cart/add',
      method: 'POST',
      data: {
        serviceId,
        count: 1
      },
      success: (res) => {
        wx.showToast({
          title: '已加入购物车',
          icon: 'success'
        })
      }
    })
  },
  
  // 立即购买/预约
  buyNow() {
    // 检查登录状态
    if (!app.globalData.isLogin) {
      wx.navigateTo({
        url: '/pages/login/index'
      })
      return
    }
    
    const { serviceId, serviceType, serviceDetail } = this.data
    
    if (serviceType === 'package') {
      // 购买套餐
      wx.navigateTo({
        url: `/pages/order/confirm?serviceId=${serviceId}&type=package`
      })
    } else {
      // 立即预约
      wx.navigateTo({
        url: `/pages/appointment/confirm?serviceId=${serviceId}`
      })
    }
  }
})
