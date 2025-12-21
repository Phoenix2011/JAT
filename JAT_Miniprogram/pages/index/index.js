// index.js
const app = getApp()

Page({
  data: {
    banners: [],
    categories: [],
    recommendServices: [],
    hotPackages: [],
    recommendTherapists: [],
    shopInfo: {
      phone: '13800138000',
      address: '北京市朝阳区建国路88号',
      businessHours: '09:00-21:00'
    }
  },
  
  onLoad() {
    this.loadBanners()
    this.loadCategories()
    this.loadRecommendServices()
    this.loadHotPackages()
    this.loadRecommendTherapists()
    this.loadShopInfo()
  },
  
  onShow() {
    // 检查登录状态
    if (!app.globalData.isLogin) {
      // 尝试自动登录
      app.checkLogin()
    }
  },
  
  onPullDownRefresh() {
    // 下拉刷新
    this.loadBanners()
    this.loadCategories()
    this.loadRecommendServices()
    this.loadHotPackages()
    this.loadRecommendTherapists()
    this.loadShopInfo()
    
    wx.stopPullDownRefresh()
  },
  
  // 加载轮播图
  loadBanners() {
    app.request({
      url: '/api/banner/list',
      method: 'GET',
      success: (res) => {
        this.setData({
          banners: res.data || []
        })
      }
    })
  },
  
  // 加载服务分类
  loadCategories() {
    app.request({
      url: '/api/category/list',
      method: 'GET',
      success: (res) => {
        this.setData({
          categories: res.data || []
        })
      }
    })
  },
  
  // 加载推荐服务
  loadRecommendServices() {
    app.request({
      url: '/api/service/recommend',
      method: 'GET',
      success: (res) => {
        this.setData({
          recommendServices: res.data || []
        })
      }
    })
  },
  
  // 加载热门套餐
  loadHotPackages() {
    app.request({
      url: '/api/package/hot',
      method: 'GET',
      success: (res) => {
        this.setData({
          hotPackages: res.data || []
        })
      }
    })
  },
  
  // 加载推荐技师
  loadRecommendTherapists() {
    app.request({
      url: '/api/therapist/recommend',
      method: 'GET',
      success: (res) => {
        this.setData({
          recommendTherapists: res.data || []
        })
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
  
  // 点击轮播图
  onBannerTap(e) {
    const { id, type, url } = e.currentTarget.dataset
    
    if (type === 'service') {
      // 跳转到服务详情
      wx.navigateTo({
        url: `/pages/service/detail?id=${id}`
      })
    } else if (type === 'package') {
      // 跳转到套餐详情
      wx.navigateTo({
        url: `/pages/service/detail?id=${id}&type=package`
      })
    } else if (type === 'web') {
      // 打开网页
      wx.navigateTo({
        url: `/pages/webview/index?url=${encodeURIComponent(url)}`
      })
    }
  },
  
  // 点击分类
  onCategoryTap(e) {
    const { id } = e.currentTarget.dataset
    
    wx.navigateTo({
      url: `/pages/service/index?categoryId=${id}`
    })
  },
  
  // 跳转到服务列表
  navigateToService() {
    wx.switchTab({
      url: '/pages/service/index'
    })
  },
  
  // 跳转到套餐列表
  navigateToPackage() {
    wx.navigateTo({
      url: '/pages/service/index?type=package'
    })
  },
  
  // 跳转到服务详情
  navigateToServiceDetail(e) {
    const { id } = e.currentTarget.dataset
    
    wx.navigateTo({
      url: `/pages/service/detail?id=${id}`
    })
  },
  
  // 跳转到技师列表
  navigateToTherapist() {
    wx.navigateTo({
      url: '/pages/therapist/index'
    })
  },
  
  // 跳转到技师详情
  navigateToTherapistDetail(e) {
    const { id } = e.currentTarget.dataset
    
    wx.navigateTo({
      url: `/pages/therapist/detail?id=${id}`
    })
  },
  
  // 跳转到预约页面
  navigateToAppointment() {
    // 检查登录状态
    if (!app.globalData.isLogin) {
      wx.navigateTo({
        url: '/pages/login/index'
      })
      return
    }
    
    wx.switchTab({
      url: '/pages/appointment/index'
    })
  },
  
  // 拨打电话
  makePhoneCall() {
    wx.makePhoneCall({
      phoneNumber: this.data.shopInfo.phone
    })
  },
  
  // 打开地图
  openLocation() {
    const { latitude, longitude, address } = this.data.shopInfo
    
    if (latitude && longitude) {
      wx.openLocation({
        latitude,
        longitude,
        name: '推拿艾灸馆',
        address
      })
    } else {
      wx.showToast({
        title: '位置信息不完整',
        icon: 'none'
      })
    }
  }
})
