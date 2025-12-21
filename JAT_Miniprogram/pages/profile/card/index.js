// pages/profile/card/index.js 更新版 - 使用mock数据进行联调
const app = getApp();
const mockRequest = require('../../../utils/mock-api');

Page({
  data: {
    cards: [], // 所有卡项
    displayCards: [], // 当前显示的卡项
    showAll: false, // 是否显示所有卡项（包括已用完的）
    showDetailModal: false, // 是否显示卡项详情弹窗
    showTransferModal: false, // 是否显示转赠弹窗
    currentCard: {}, // 当前选中的卡项
    receiverPhone: '', // 接收人手机号
    confirmPhone: '', // 确认手机号
    canTransfer: false // 是否可以转赠
  },

  onLoad: function (options) {
    this.getCardList();
  },

  onShow: function () {
    // 每次页面显示时刷新卡项列表，确保数据最新
    this.getCardList();
  },

  // 获取卡项列表
  getCardList: function () {
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
      mockRequest.getCards().then(res => {
        wx.hideLoading();
        if (res.code === 200) {
          const cards = res.data || [];
          this.setData({
            cards: cards,
            displayCards: this.filterCards(cards, this.data.showAll)
          });
        } else {
          wx.showToast({
            title: '获取卡项失败',
            icon: 'none'
          });
        }
      });
    } else {
      // 使用真实API
      wx.request({
        url: app.globalData.baseUrl + '/api/customer/cards',
        method: 'GET',
        header: {
          'Authorization': 'Bearer ' + token
        },
        success: (res) => {
          wx.hideLoading();
          if (res.data.code === 200) {
            const cards = res.data.data || [];
            this.setData({
              cards: cards,
              displayCards: this.filterCards(cards, this.data.showAll)
            });
          } else {
            wx.showToast({
              title: '获取卡项失败',
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

  // 筛选卡项
  filterCards: function (cards, showAll) {
    if (showAll) {
      return cards;
    } else {
      return cards.filter(card => card.remainingCount > 0);
    }
  },

  // 筛选条件变更
  filterChange: function (e) {
    const showAll = e.detail.value.includes('showAll');
    this.setData({
      showAll: showAll,
      displayCards: this.filterCards(this.data.cards, showAll)
    });
  },

  // 显示卡项详情
  showCardDetail: function (e) {
    const cardId = e.currentTarget.dataset.id;
    
    wx.showLoading({
      title: '加载中...',
    });

    if (app.globalData.isMockData) {
      // 使用模拟数据
      mockRequest.getCardDetail(cardId).then(res => {
        wx.hideLoading();
        if (res.code === 200) {
          this.setData({
            currentCard: res.data,
            showDetailModal: true
          });
        } else {
          wx.showToast({
            title: '获取卡项详情失败',
            icon: 'none'
          });
        }
      });
    } else {
      // 使用真实API
      const token = wx.getStorageSync('token');
      wx.request({
        url: app.globalData.baseUrl + `/api/customer/cards/${cardId}/detail`,
        method: 'GET',
        header: {
          'Authorization': 'Bearer ' + token
        },
        success: (res) => {
          wx.hideLoading();
          if (res.data.code === 200) {
            this.setData({
              currentCard: res.data.data,
              showDetailModal: true
            });
          } else {
            wx.showToast({
              title: '获取卡项详情失败',
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

  // 隐藏卡项详情弹窗
  hideCardDetail: function () {
    this.setData({
      showDetailModal: false
    });
  },

  // 显示转赠弹窗
  transferCard: function (e) {
    const cardId = e.currentTarget.dataset.id;
    const card = this.data.cards.find(item => item.id === cardId);
    
    // 检查卡项是否可转赠
    if (card.remainingCount <= 0) {
      wx.showToast({
        title: '已用完的卡项不可转赠',
        icon: 'none'
      });
      return;
    }

    this.setData({
      currentCard: card,
      showTransferModal: true,
      receiverPhone: '',
      confirmPhone: '',
      canTransfer: false
    });
  },

  // 隐藏转赠弹窗
  hideTransferModal: function () {
    this.setData({
      showTransferModal: false
    });
  },

  // 输入接收人手机号
  inputReceiverPhone: function (e) {
    const receiverPhone = e.detail.value;
    const confirmPhone = this.data.confirmPhone;
    
    this.setData({
      receiverPhone: receiverPhone,
      canTransfer: receiverPhone && confirmPhone && receiverPhone === confirmPhone
    });
  },

  // 输入确认手机号
  inputConfirmPhone: function (e) {
    const confirmPhone = e.detail.value;
    const receiverPhone = this.data.receiverPhone;
    
    this.setData({
      confirmPhone: confirmPhone,
      canTransfer: receiverPhone && confirmPhone && receiverPhone === confirmPhone
    });
  },

  // 确认转赠
  confirmTransfer: function () {
    if (!this.data.canTransfer) {
      return;
    }

    const cardId = this.data.currentCard.id;
    const receiverPhone = this.data.receiverPhone;

    wx.showLoading({
      title: '处理中...',
    });

    if (app.globalData.isMockData) {
      // 使用模拟数据
      mockRequest.transferCard(cardId, receiverPhone).then(res => {
        wx.hideLoading();
        if (res.code === 200) {
          wx.showToast({
            title: '转赠成功',
            icon: 'success'
          });
          this.hideTransferModal();
          this.getCardList(); // 刷新卡项列表
        } else {
          wx.showToast({
            title: res.message || '转赠失败',
            icon: 'none'
          });
        }
      });
    } else {
      // 使用真实API
      const token = wx.getStorageSync('token');
      wx.request({
        url: app.globalData.baseUrl + '/api/customer/cards/transfer',
        method: 'POST',
        header: {
          'Authorization': 'Bearer ' + token,
          'Content-Type': 'application/json'
        },
        data: {
          cardId: cardId,
          receiverPhone: receiverPhone
        },
        success: (res) => {
          wx.hideLoading();
          if (res.data.code === 200) {
            wx.showToast({
              title: '转赠成功',
              icon: 'success'
            });
            this.hideTransferModal();
            this.getCardList(); // 刷新卡项列表
          } else {
            wx.showToast({
              title: res.data.message || '转赠失败',
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

  // 跳转到购买卡项页面
  navigateToBuy: function () {
    wx.navigateTo({
      url: '/pages/service/index?type=card'
    });
  }
});
