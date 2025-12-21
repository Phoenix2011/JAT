// appointment/index.js
const app = getApp()

Page({
  data: {
    year: 0,
    month: 0,
    weekdays: ['日', '一', '二', '三', '四', '五', '六'],
    days: [],
    today: '',
    selectedDate: '',
    timeSlots: [],
    selectedTimeSlot: '',
    categories: [],
    services: [],
    searchKeyword: '',
    currentTab: 'all',
    selectedService: '',
    therapists: [],
    selectedTherapist: '',
    canConfirm: false
  },
  
  onLoad(options) {
    // 如果有服务ID，则预先选择
    if (options.serviceId) {
      this.setData({
        selectedService: options.serviceId
      })
      
      // 加载技师列表
      this.loadTherapists()
    }
    
    // 初始化日历
    this.initCalendar()
    
    // 加载分类
    this.loadCategories()
  },
  
  // 初始化日历
  initCalendar() {
    const now = new Date()
    const year = now.getFullYear()
    const month = now.getMonth() + 1
    const day = now.getDate()
    const today = `${year}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}`
    
    this.setData({
      year,
      month,
      today
    })
    
    this.generateCalendar(year, month)
    
    // 加载可预约日期
    this.loadAvailableDates(year, month)
  },
  
  // 生成日历数据
  generateCalendar(year, month) {
    const days = []
    
    // 获取当月第一天是星期几
    const firstDay = new Date(year, month - 1, 1).getDay()
    
    // 获取当月天数
    const daysInMonth = new Date(year, month, 0).getDate()
    
    // 获取上个月天数
    const daysInPrevMonth = new Date(year, month - 1, 0).getDate()
    
    // 上个月的年月
    let prevYear = year
    let prevMonth = month - 1
    if (prevMonth === 0) {
      prevYear--
      prevMonth = 12
    }
    
    // 下个月的年月
    let nextYear = year
    let nextMonth = month + 1
    if (nextMonth === 13) {
      nextYear++
      nextMonth = 1
    }
    
    // 添加上个月的日期
    for (let i = 0; i < firstDay; i++) {
      const day = daysInPrevMonth - firstDay + i + 1
      days.push({
        year: prevYear,
        month: prevMonth,
        day,
        date: `${prevYear}-${prevMonth.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}`,
        available: false
      })
    }
    
    // 添加当月的日期
    for (let i = 1; i <= daysInMonth; i++) {
      days.push({
        year,
        month,
        day: i,
        date: `${year}-${month.toString().padStart(2, '0')}-${i.toString().padStart(2, '0')}`,
        available: false // 默认不可约，后续会更新
      })
    }
    
    // 添加下个月的日期，补齐6行7列
    const remainingDays = 42 - days.length
    for (let i = 1; i <= remainingDays; i++) {
      days.push({
        year: nextYear,
        month: nextMonth,
        day: i,
        date: `${nextYear}-${nextMonth.toString().padStart(2, '0')}-${i.toString().padStart(2, '0')}`,
        available: false
      })
    }
    
    this.setData({
      days
    })
  },
  
  // 加载可预约日期
  loadAvailableDates(year, month) {
    app.request({
      url: '/api/appointment/availableDates',
      method: 'GET',
      data: {
        year,
        month
      },
      success: (res) => {
        const availableDates = res.data || []
        
        // 更新日历数据
        const days = this.data.days.map(day => {
          if (day.month === month && availableDates.includes(day.day)) {
            return {
              ...day,
              available: true
            }
          }
          return day
        })
        
        this.setData({
          days
        })
      }
    })
  },
  
  // 上个月
  prevMonth() {
    let { year, month } = this.data
    
    month--
    if (month === 0) {
      year--
      month = 12
    }
    
    this.setData({
      year,
      month
    })
    
    this.generateCalendar(year, month)
    this.loadAvailableDates(year, month)
  },
  
  // 下个月
  nextMonth() {
    let { year, month } = this.data
    
    month++
    if (month === 13) {
      year++
      month = 1
    }
    
    this.setData({
      year,
      month
    })
    
    this.generateCalendar(year, month)
    this.loadAvailableDates(year, month)
  },
  
  // 选择日期
  selectDate(e) {
    const { date, available } = e.currentTarget.dataset
    
    if (!available) {
      return
    }
    
    this.setData({
      selectedDate: date,
      selectedTimeSlot: '',
      timeSlots: []
    })
    
    // 加载时间段
    this.loadTimeSlots(date)
  },
  
  // 加载时间段
  loadTimeSlots(date) {
    app.request({
      url: '/api/appointment/timeSlots',
      method: 'GET',
      data: {
        date
      },
      success: (res) => {
        this.setData({
          timeSlots: res.data || []
        })
      }
    })
  },
  
  // 选择时间段
  selectTimeSlot(e) {
    const { id, available } = e.currentTarget.dataset
    
    if (!available) {
      return
    }
    
    this.setData({
      selectedTimeSlot: id
    })
    
    // 如果已经选择了服务，则加载技师列表
    if (this.data.selectedService) {
      this.loadTherapists()
    } else {
      // 加载服务列表
      this.loadServices()
    }
    
    // 检查是否可以确认预约
    this.checkCanConfirm()
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
  
  // 切换分类
  switchTab(e) {
    const tab = e.currentTarget.dataset.tab
    
    if (tab === this.data.currentTab) {
      return
    }
    
    this.setData({
      currentTab: tab
    })
    
    // 加载服务列表
    this.loadServices()
  },
  
  // 搜索输入
  onSearchInput(e) {
    this.setData({
      searchKeyword: e.detail.value
    })
  },
  
  // 搜索确认
  onSearch() {
    this.loadServices()
  },
  
  // 清除搜索
  clearSearch() {
    this.setData({
      searchKeyword: ''
    })
    
    this.loadServices()
  },
  
  // 加载服务列表
  loadServices() {
    const { currentTab, searchKeyword, selectedTimeSlot } = this.data
    
    // 构建请求参数
    const params = {
      timeSlotId: selectedTimeSlot,
      keyword: searchKeyword || undefined
    }
    
    // 根据分类设置参数
    if (currentTab !== 'all') {
      params.categoryId = currentTab
    }
    
    app.request({
      url: '/api/service/listForAppointment',
      method: 'GET',
      data: params,
      success: (res) => {
        this.setData({
          services: res.data || []
        })
      }
    })
  },
  
  // 选择服务
  selectService(e) {
    const { id } = e.currentTarget.dataset
    
    this.setData({
      selectedService: id
    })
    
    // 加载技师列表
    this.loadTherapists()
    
    // 检查是否可以确认预约
    this.checkCanConfirm()
  },
  
  // 加载技师列表
  loadTherapists() {
    const { selectedDate, selectedTimeSlot, selectedService } = this.data
    
    if (!selectedDate || !selectedTimeSlot || !selectedService) {
      return
    }
    
    app.request({
      url: '/api/therapist/listForAppointment',
      method: 'GET',
      data: {
        date: selectedDate,
        timeSlotId: selectedTimeSlot,
        serviceId: selectedService
      },
      success: (res) => {
        this.setData({
          therapists: res.data || []
        })
      }
    })
  },
  
  // 选择技师
  selectTherapist(e) {
    const { id } = e.currentTarget.dataset
    
    this.setData({
      selectedTherapist: id
    })
    
    // 检查是否可以确认预约
    this.checkCanConfirm()
  },
  
  // 检查是否可以确认预约
  checkCanConfirm() {
    const { selectedDate, selectedTimeSlot, selectedService, selectedTherapist } = this.data
    
    this.setData({
      canConfirm: !!(selectedDate && selectedTimeSlot && selectedService && selectedTherapist)
    })
  },
  
  // 确认预约
  confirmAppointment() {
    // 检查登录状态
    if (!app.globalData.isLogin) {
      wx.navigateTo({
        url: '/pages/login/index?redirect=' + encodeURIComponent('/pages/appointment/index')
      })
      return
    }
    
    const { selectedDate, selectedTimeSlot, selectedService, selectedTherapist } = this.data
    
    // 跳转到预约确认页面
    wx.navigateTo({
      url: `/pages/appointment/confirm?date=${selectedDate}&timeSlotId=${selectedTimeSlot}&serviceId=${selectedService}&therapistId=${selectedTherapist}`
    })
  }
})
