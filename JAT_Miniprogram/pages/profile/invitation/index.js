// pages/profile/invitation/index.js 更新版 - 使用mock数据进行联调
const app = getApp();
const mockRequest = require('../../../utils/mock-api');

Page({
  data: {
    canInputInvitation: false, // 是否可以填写邀请码（仅未消费用户可见）
    inputInvitationCode: '', // 用户输入的邀请码
    invitationCode: '', // 我的邀请码
    commissionInfo: {
      totalAmount: '0.00', // 佣金获取总额
      availableAmount: '0.00', // 可用佣金
      frozenAmount: '0.00' // 冻结佣金
    },
    activeTab: 'commission', // 当前激活的标签页：commission-佣金获取记录，unfreeze-佣金解冻记录，withdraw-提现记录
    commissionRecords: [], // 佣金获取记录
    unfreezeRecords: [], // 佣金解冻记录
    withdrawRecords: [] // 提现记录
  },

  onLoad: function (options) {
    this.checkInvitationStatus();
    this.getInvitationCode();
    this.getCommissionInfo();
    this.getCommissionRecords();
  },

  onShow: function () {
    // 每次页面显示时刷新数据，确保数据最新
    this.checkInvitationStatus();
    this.getCommissionInfo();
    this.getCommissionRecords();
  },

  // 检查用户是否可以填写邀请码
  checkInvitationStatus: function () {
    const token = wx.getStorageSync('token');
    if (!token) {
      wx.redirectTo({
        url: '/pages/login/index'
      });
      return;
    }

    // 模拟检查状态 - 假设新用户可以填写邀请码
    this.setData({
      canInputInvitation: true
    });
  },

  // 获取我的邀请码
  getInvitationCode: function () {
    const token = wx.getStorageSync('token');
    if (!token) return;

    if (app.globalData.isMockData) {
      // 使用模拟数据
      mockRequest.getInvitationCode().then(res => {
        if (res.code === 200) {
          this.setData({
            invitationCode: res.data.code || ''
          });
        }
      });
    } else {
      // 使用真实API
      wx.request({
        url: app.globalData.baseUrl + '/api/invitation/code',
        method: 'GET',
        header: {
          'Authorization': 'Bearer ' + token
        },
        success: (res) => {
          if (res.data.code === 200) {
            this.setData({
              invitationCode: res.data.data.code || ''
            });
          }
        }
      });
    }
  },

  // 获取佣金信息
  getCommissionInfo: function () {
    const token = wx.getStorageSync('token');
    if (!token) return;

    if (app.globalData.isMockData) {
      // 使用模拟数据
      mockRequest.getCommissionInfo().then(res => {
        if (res.code === 200) {
          this.setData({
            commissionInfo: res.data || {
              totalAmount: '0.00',
              availableAmount: '0.00',
              frozenAmount: '0.00'
            }
          });
        }
      });
    } else {
      // 使用真实API
      wx.request({
        url: app.globalData.baseUrl + '/api/invitation/commission/summary',
        method: 'GET',
        header: {
          'Authorization': 'Bearer ' + token
        },
        success: (res) => {
          if (res.data.code === 200) {
            this.setData({
              commissionInfo: res.data.data || {
                totalAmount: '0.00',
                availableAmount: '0.00',
                frozenAmount: '0.00'
              }
            });
          }
        }
      });
    }
  },

  // 获取佣金记录
  getCommissionRecords: function () {
    const token = wx.getStorageSync('token');
    if (!token) return;

    const tab = this.data.activeTab;
    
    wx.showLoading({
      title: '加载中...',
    });

    if (app.globalData.isMockData) {
      // 使用模拟数据
      let promise;
      
      switch (tab) {
        case 'commission':
          promise = mockRequest.getCommissionRecords();
          break;
        case 'unfreeze':
          promise = mockRequest.getUnfreezeRecords();
          break;
        case 'withdraw':
          promise = mockRequest.getWithdrawRecords();
          break;
      }
      
      promise.then(res => {
        wx.hideLoading();
        if (res.code === 200) {
          const data = res.data || [];
          
          switch (tab) {
            case 'commission':
              this.setData({ commissionRecords: data });
              break;
            case 'unfreeze':
              this.setData({ unfreezeRecords: data });
              break;
            case 'withdraw':
              this.setData({ withdrawRecords: data });
              break;
          }
        } else {
          wx.showToast({
            title: '获取记录失败',
            icon: 'none'
          });
        }
      });
    } else {
      // 使用真实API
      let url = '';

      switch (tab) {
        case 'commission':
          url = '/api/invitation/commission/records';
          break;
        case 'unfreeze':
          url = '/api/invitation/commission/unfreeze/records';
          break;
        case 'withdraw':
          url = '/api/invitation/commission/withdraw/records';
          break;
      }

      wx.request({
        url: app.globalData.baseUrl + url,
        method: 'GET',
        header: {
          'Authorization': 'Bearer ' + token
        },
        success: (res) => {
          wx.hideLoading();
          if (res.data.code === 200) {
            const data = res.data.data || [];
            
            switch (tab) {
              case 'commission':
                this.setData({ commissionRecords: data });
                break;
              case 'unfreeze':
                this.setData({ unfreezeRecords: data });
                break;
              case 'withdraw':
                this.setData({ withdrawRecords: data });
                break;
            }
          } else {
            wx.showToast({
              title: '获取记录失败',
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

  // 输入邀请码
  onInvitationInput: function (e) {
    this.setData({
      inputInvitationCode: e.detail.value
    });
  },

  // 提交邀请码
  submitInvitationCode: function () {
    if (!this.data.inputInvitationCode) return;

    const token = wx.getStorageSync('token');
    if (!token) return;

    wx.showLoading({
      title: '提交中...',
    });

    if (app.globalData.isMockData) {
      // 使用模拟数据
      mockRequest.bindInvitationCode(this.data.inputInvitationCode).then(res => {
        wx.hideLoading();
        if (res.code === 200) {
          wx.showToast({
            title: '绑定成功',
            icon: 'success'
          });
          // 刷新状态
          this.checkInvitationStatus();
        } else {
          wx.showToast({
            title: res.message || '绑定失败',
            icon: 'none'
          });
        }
      });
    } else {
      // 使用真实API
      wx.request({
        url: app.globalData.baseUrl + '/api/invitation/bind',
        method: 'POST',
        header: {
          'Authorization': 'Bearer ' + token,
          'Content-Type': 'application/json'
        },
        data: {
          invitationCode: this.data.inputInvitationCode
        },
        success: (res) => {
          wx.hideLoading();
          if (res.data.code === 200) {
            wx.showToast({
              title: '绑定成功',
              icon: 'success'
            });
            // 刷新状态
            this.checkInvitationStatus();
          } else {
            wx.showToast({
              title: res.data.message || '绑定失败',
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

  // 复制邀请码
  copyInvitationCode: function () {
    if (!this.data.invitationCode) {
      wx.showToast({
        title: '邀请码获取失败',
        icon: 'none'
      });
      return;
    }

    wx.setClipboardData({
      data: this.data.invitationCode,
      success: () => {
        wx.showToast({
          title: '复制成功',
          icon: 'success'
        });
      }
    });
  },

  // 切换标签页
  switchTab: function (e) {
    const tab = e.currentTarget.dataset.tab;
    this.setData({
      activeTab: tab
    });
    this.getCommissionRecords();
  },

  // 跳转到提现页面
  navigateToWithdraw: function () {
    if (!this.data.commissionInfo.availableAmount || this.data.commissionInfo.availableAmount <= 0) {
      wx.showToast({
        title: '暂无可提现佣金',
        icon: 'none'
      });
      return;
    }

    wx.navigateTo({
      url: '/pages/profile/invitation/withdraw/index'
    });
  }
});
