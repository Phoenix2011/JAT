// pages/profile/index.js 更新版 - 使用mock数据进行联调
const app = getApp();
const mockRequest = require('../../utils/mock-api');

Page({
  data: {
    userInfo: {},
    hasUserInfo: false
  },

  onLoad: function (options) {
    this.getUserInfo();
  },

  onShow: function () {
    // 每次页面显示时刷新用户信息，确保数据最新
    this.getUserInfo();
  },

  // 获取用户信息
  getUserInfo: function () {
    const token = wx.getStorageSync('token');
    if (!token) {
      wx.redirectTo({
        url: '/pages/login/index'
      });
      return;
    }

    wx.showLoading({
      title: '加载中...',
    });

    if (app.globalData.isMockData) {
      // 使用模拟数据
      mockRequest.getUserInfo().then(res => {
        wx.hideLoading();
        if (res.code === 200) {
          this.setData({
            userInfo: res.data,
            hasUserInfo: true
          });
        } else {
          wx.showToast({
            title: '获取用户信息失败',
            icon: 'none'
          });
        }
      });
    } else {
      // 使用真实API
      wx.request({
        url: app.globalData.baseUrl + '/api/customer/info',
        method: 'GET',
        header: {
          'Authorization': 'Bearer ' + token
        },
        success: (res) => {
          wx.hideLoading();
          if (res.data.code === 200) {
            this.setData({
              userInfo: res.data.data,
              hasUserInfo: true
            });
          } else {
            wx.showToast({
              title: '获取用户信息失败',
              icon: 'none'
            });
          }
        },
        fail: () => {
          wx.hideLoading();
          wx.showToast({
            title: '网络异常，请重试',
            icon: 'none'
          });
        }
      });
    }
  },

  // 页面导航
  navigateTo: function (e) {
    const url = e.currentTarget.dataset.url;
    wx.navigateTo({
      url: url
    });
  },

  // 联系客服
  contactService: function () {
    // 获取客服微信号
    wx.showModal({
      title: '联系客服',
      content: `店长微信号：spa_manager，点击确定复制微信号`,
      success: (res) => {
        if (res.confirm) {
          wx.setClipboardData({
            data: 'spa_manager',
            success: () => {
              wx.showToast({
                title: '微信号已复制',
                icon: 'success'
              });
            }
          });
        }
      }
    });
  },

  // 退出登录
  logout: function () {
    wx.showModal({
      title: '提示',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          // 清除本地存储的token和用户信息
          wx.removeStorageSync('token');
          wx.removeStorageSync('userInfo');
          
          // 跳转到登录页
          wx.reLaunch({
            url: '/pages/login/index'
          });
        }
      }
    });
  }
});
