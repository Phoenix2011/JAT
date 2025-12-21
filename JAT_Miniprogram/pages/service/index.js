// service/index.js
const app = getApp()

Page({
  data: {
    searchKeyword: '',
    currentTab: 'all',
    sortType: 'default', // default, sales, price
    sortOrder: 'desc', // asc, desc
    categories: [],
    services: [],
    pageNum: 1,
    pageSize: 10,
    hasMore: true,
    loading: false
  },
  
  onLoad(options) {
    // 如果有分类ID，则切换到对应分类
    if (options.categoryId) {
      this.setData({
        currentTab: options.categoryId
      })
    }
    
    // 如果是套餐类型，则切换到套餐分类
    if (options.type === 'package') {
      this.setData({
        currentTab: 'package'
      })
    }
    
    // 加载分类
    this.loadCategories()
    
    // 加载服务列表
    this.loadServices(true)
  },
  
  onPullDownRefresh() {
    // 下拉刷新
    this.setData({
      pageNum: 1,
      hasMore: true,
      services: []
    })
    
    this.loadServices(true)
    
    wx.stopPullDownRefresh()
  },
  
  onReachBottom() {
    // 上拉加载更多
    if (this.data.hasMore && !this.data.loading) {
      this.loadServices()
    }
  },
  
  // 加载分类
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
  
  // 加载服务列表
  loadServices(refresh = false) {
    const { currentTab, sortType, sortOrder, searchKeyword, pageNum, pageSize } = this.data
    
    // 如果是刷新，则重置页码
    if (refresh) {
      this.setData({
        pageNum: 1,
        services: []
      })
    }
    
    // 如果没有更多数据，则不加载
    if (!this.data.hasMore && !refresh) {
      return
    }
    
    // 设置加载状态
    this.setData({
      loading: true
    })
    
    // 构建请求参数
    const params = {
      pageNum,
      pageSize,
      keyword: searchKeyword || undefined
    }
    
    // 根据分类设置参数
    if (currentTab !== 'all') {
      if (currentTab === 'package') {
        params.type = 'package'
      } else {
        params.categoryId = currentTab
      }
    }
    
    // 根据排序设置参数
    if (sortType !== 'default') {
      params.sortField = sortType
      params.sortOrder = sortOrder
    }
    
    // 请求服务列表
    app.request({
      url: '/api/service/list',
      method: 'GET',
      data: params,
      success: (res) => {
        const newServices = res.data.list || []
        const total = res.data.total || 0
        
        // 更新服务列表
        this.setData({
          services: [...this.data.services, ...newServices],
          pageNum: pageNum + 1,
          hasMore: this.data.services.length + newServices.length < total
        })
      },
      complete: () => {
        this.setData({
          loading: false
        })
      }
    })
  },
  
  // 切换分类
  switchTab(e) {
    const tab = e.currentTarget.dataset.tab
    
    if (tab === this.data.currentTab) {
      return
    }
    
    this.setData({
      currentTab: tab,
      pageNum: 1,
      hasMore: true,
      services: []
    })
    
    this.loadServices(true)
  },
  
  // 切换排序
  changeSort(e) {
    const type = e.currentTarget.dataset.type
    let order = this.data.sortOrder
    
    if (type === this.data.sortType) {
      // 如果点击的是当前排序类型，则切换排序顺序
      order = order === 'asc' ? 'desc' : 'asc'
    } else {
      // 如果点击的是新的排序类型
      // 销量默认降序，价格默认升序
      order = type === 'sales' ? 'desc' : 'asc'
    }
    
    this.setData({
      sortType: type,
      sortOrder: order,
      pageNum: 1,
      hasMore: true,
      services: []
    })
    
    this.loadServices(true)
  },
  
  // 搜索输入
  onSearchInput(e) {
    this.setData({
      searchKeyword: e.detail.value
    })
  },
  
  // 搜索确认
  onSearch() {
    this.setData({
      pageNum: 1,
      hasMore: true,
      services: []
    })
    
    this.loadServices(true)
  },
  
  // 清除搜索
  clearSearch() {
    this.setData({
      searchKeyword: '',
      pageNum: 1,
      hasMore: true,
      services: []
    })
    
    this.loadServices(true)
  },
  
  // 跳转到详情页
  navigateToDetail(e) {
    const { id, type } = e.currentTarget.dataset
    
    wx.navigateTo({
      url: `/pages/service/detail?id=${id}&type=${type || 'service'}`
    })
  }
})
