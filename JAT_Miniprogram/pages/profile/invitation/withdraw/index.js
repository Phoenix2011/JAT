// pages/profile/invitation/withdraw/index.js 更新版 - 使用mock数据进行联调
const app = getApp();
const mockRequest = require('../../../../utils/mock-api');

Page({
  data: {
    availableAmount: '0.00', // 可提现佣金
    withdrawRatio: 85, // 提现比例，默认85%
    inputAmount: '', // 用户输入的提现金额
    consumeAmount: '0.00', // 消耗的佣金金额
    actualAmount: '0.00', // 实际到账金额
    canWithdraw: false // 是否可以提现
  },

  onLoad: function (options) {
    this.getWithdrawInfo();
  },

  // 获取提现相关信息
  getWithdrawInfo: function () {
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
      mockRequest.getWithdrawInfo().then(res => {
        wx.hideLoading();
        if (res.code === 200) {
          const data = res.data || {};
          this.setData({
            availableAmount: data.availableAmount || '0.00',
            withdrawRatio: data.withdrawRatio || 85
          });
        } else {
          wx.showToast({
            title: '获取提现信息失败',
            icon: 'none'
          });
        }
      });
    } else {
      // 使用真实API
      wx.request({
        url: app.globalData.baseUrl + '/api/invitation/commission/withdraw/info',
        method: 'GET',
        header: {
          'Authorization': 'Bearer ' + token
        },
        success: (res) => {
          wx.hideLoading();
          if (res.data.code === 200) {
            const data = res.data.data || {};
            this.setData({
              availableAmount: data.availableAmount || '0.00',
              withdrawRatio: data.withdrawRatio || 85
            });
          } else {
            wx.showToast({
              title: '获取提现信息失败',
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

  // 输入提现金额
  onAmountInput: function (e) {
    const inputAmount = e.detail.value;
    
    // 验证输入金额格式
    if (inputAmount && !/^\d+(\.\d{0,2})?$/.test(inputAmount)) {
      return;
    }

    // 计算消耗佣金和实际到账金额
    let consumeAmount = '0.00';
    let actualAmount = '0.00';
    let canWithdraw = false;

    if (inputAmount) {
      const amount = parseFloat(inputAmount);
      const available = parseFloat(this.data.availableAmount);
      
      if (amount > 0 && amount <= available) {
        consumeAmount = amount.toFixed(2);
        actualAmount = (amount * this.data.withdrawRatio / 100).toFixed(2);
        canWithdraw = true;
      } else if (amount > available) {
        // 如果输入金额超过可用金额，自动调整为可用金额
        consumeAmount = available.toFixed(2);
        actualAmount = (available * this.data.withdrawRatio / 100).toFixed(2);
        canWithdraw = true;
        
        // 更新输入框显示
        this.setData({
          inputAmount: available.toFixed(2)
        });
        
        wx.showToast({
          title: '已自动调整为可用金额',
          icon: 'none'
        });
        
        return;
      }
    }

    this.setData({
      inputAmount: inputAmount,
      consumeAmount: consumeAmount,
      actualAmount: actualAmount,
      canWithdraw: canWithdraw
    });
  },

  // 设置全部提现
  setFullAmount: function () {
    const available = parseFloat(this.data.availableAmount);
    if (available <= 0) {
      wx.showToast({
        title: '暂无可提现佣金',
        icon: 'none'
      });
      return;
    }

    const consumeAmount = available.toFixed(2);
    const actualAmount = (available * this.data.withdrawRatio / 100).toFixed(2);

    this.setData({
      inputAmount: consumeAmount,
      consumeAmount: consumeAmount,
      actualAmount: actualAmount,
      canWithdraw: true
    });
  },

  // 提交提现申请
  submitWithdraw: function () {
    if (!this.data.canWithdraw) return;

    const token = wx.getStorageSync('token');
    if (!token) return;

    wx.showModal({
      title: '确认提现',
      content: `您将提现${this.data.consumeAmount}元佣金，实际到账${this.data.actualAmount}元，确认继续吗？`,
      success: (res) => {
        if (res.confirm) {
          this.doWithdraw();
        }
      }
    });
  },

  // 执行提现操作
  doWithdraw: function () {
    const amount = parseFloat(this.data.consumeAmount);

    wx.showLoading({
      title: '提交中...',
    });

    if (app.globalData.isMockData) {
      // 使用模拟数据
      mockRequest.submitWithdraw(amount).then(res => {
        wx.hideLoading();
        if (res.code === 200) {
          wx.showToast({
            title: '提现申请已提交',
            icon: 'success'
          });
          
          // 延迟返回上一页
          setTimeout(() => {
            wx.navigateBack();
          }, 1500);
        } else {
          wx.showToast({
            title: res.message || '提现失败',
            icon: 'none'
          });
        }
      });
    } else {
      // 使用真实API
      const token = wx.getStorageSync('token');
      wx.request({
        url: app.globalData.baseUrl + '/api/invitation/commission/withdraw',
        method: 'POST',
        header: {
          'Authorization': 'Bearer ' + token,
          'Content-Type': 'application/json'
        },
        data: {
          amount: amount
        },
        success: (res) => {
          wx.hideLoading();
          if (res.data.code === 200) {
            wx.showToast({
              title: '提现申请已提交',
              icon: 'success'
            });
            
            // 延迟返回上一页
            setTimeout(() => {
              wx.navigateBack();
            }, 1500);
          } else {
            wx.showToast({
              title: res.data.message || '提现失败',
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
  }
});
