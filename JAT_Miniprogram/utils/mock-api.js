// 模拟API数据交互的工具类
const mockApi = {
  // 用户信息
  userInfo: {
    avatarUrl: '/assets/icons/default-avatar.png',
    nickName: '张三',
    userId: '10086',
    balance: '128.50',
    phone: '13800138000'
  },
  
  // 卡项列表
  cards: [
    {
      id: '1',
      name: '全身推拿套餐',
      type: 'multiple',
      remainingCount: 8,
      totalCount: 10,
      validUntil: '2025-12-31',
      price: '1280.00',
      purchaseDate: '2025-01-15',
      usageRecords: [
        {
          id: '101',
          serviceName: '全身推拿',
          useTime: '2025-03-15 14:30',
          therapistName: '李师傅'
        },
        {
          id: '102',
          serviceName: '全身推拿',
          useTime: '2025-04-02 16:00',
          therapistName: '王师傅'
        }
      ]
    },
    {
      id: '2',
      name: '肩颈调理',
      type: 'single',
      remainingCount: 0,
      totalCount: 1,
      validUntil: '2025-06-30',
      price: '168.00',
      purchaseDate: '2025-02-20',
      usageRecords: [
        {
          id: '201',
          serviceName: '肩颈调理',
          useTime: '2025-02-25 10:00',
          therapistName: '赵师傅'
        }
      ]
    },
    {
      id: '3',
      name: '艾灸疗程卡',
      type: 'multiple',
      remainingCount: 5,
      totalCount: 5,
      validUntil: '2025-11-30',
      price: '680.00',
      purchaseDate: '2025-05-01',
      usageRecords: []
    }
  ],
  
  // 佣金信息
  commissionInfo: {
    totalAmount: '320.00',
    availableAmount: '128.50',
    frozenAmount: '191.50',
    withdrawRatio: 85
  },
  
  // 邀请码
  invitationCode: 'ZHANG123',
  
  // 佣金获取记录
  commissionRecords: [
    {
      id: '1',
      inviteeNickname: '李**',
      createTime: '2025-03-10 15:20',
      orderDesc: '购买全身推拿套餐',
      amount: '128.00',
      status: 'unfrozen'
    },
    {
      id: '2',
      inviteeNickname: '王**',
      createTime: '2025-04-05 09:30',
      orderDesc: '购买艾灸疗程卡',
      amount: '68.00',
      status: 'frozen'
    },
    {
      id: '3',
      inviteeNickname: '赵**',
      createTime: '2025-05-12 14:15',
      orderDesc: '购买足疗套餐',
      amount: '124.00',
      status: 'frozen'
    }
  ],
  
  // 佣金解冻记录
  unfreezeRecords: [
    {
      id: '1',
      inviteeNickname: '李**',
      unfreezeTime: '2025-03-15 16:30',
      serviceDesc: '使用全身推拿服务',
      unfreezeAmount: '12.80',
      unfreezeRatio: '10'
    },
    {
      id: '2',
      inviteeNickname: '李**',
      unfreezeTime: '2025-04-02 11:20',
      serviceDesc: '使用全身推拿服务',
      unfreezeAmount: '12.80',
      unfreezeRatio: '10'
    },
    {
      id: '3',
      inviteeNickname: '李**',
      unfreezeTime: '2025-04-20 15:45',
      serviceDesc: '使用全身推拿服务',
      unfreezeAmount: '12.80',
      unfreezeRatio: '10'
    }
  ],
  
  // 提现记录
  withdrawRecords: [
    {
      id: '1',
      createTime: '2025-04-10 14:30',
      requestAmount: '100.00',
      actualAmount: '85.00',
      status: 'success'
    },
    {
      id: '2',
      createTime: '2025-05-05 16:20',
      requestAmount: '50.00',
      actualAmount: '42.50',
      status: 'pending'
    }
  ],
  
  // 预约记录
  appointments: [
    {
      id: '1',
      serviceName: '全身推拿',
      therapistName: '李师傅',
      appointmentTime: '2025-05-25 14:30',
      status: 'upcoming'
    },
    {
      id: '2',
      serviceName: '艾灸调理',
      therapistName: '王师傅',
      appointmentTime: '2025-05-15 10:00',
      status: 'completed'
    }
  ],
  
  // 消费记录
  consumptions: [
    {
      id: '1',
      time: '2025-04-02 16:00',
      type: 'card',
      desc: '使用全身推拿套餐',
      amount: '128.00'
    },
    {
      id: '2',
      time: '2025-03-15 14:30',
      type: 'card',
      desc: '使用全身推拿套餐',
      amount: '128.00'
    },
    {
      id: '3',
      time: '2025-02-25 10:00',
      type: 'single',
      desc: '购买肩颈调理服务',
      amount: '168.00'
    }
  ]
};

// 模拟API请求
const mockRequest = {
  // 获取用户信息
  getUserInfo: function() {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve({
          code: 200,
          data: mockApi.userInfo
        });
      }, 300);
    });
  },
  
  // 获取卡项列表
  getCards: function() {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve({
          code: 200,
          data: mockApi.cards
        });
      }, 300);
    });
  },
  
  // 获取卡项详情
  getCardDetail: function(cardId) {
    return new Promise((resolve) => {
      setTimeout(() => {
        const card = mockApi.cards.find(item => item.id === cardId);
        resolve({
          code: 200,
          data: card
        });
      }, 300);
    });
  },
  
  // 转赠卡项
  transferCard: function(cardId, receiverPhone) {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve({
          code: 200,
          message: '转赠成功'
        });
      }, 500);
    });
  },
  
  // 获取佣金信息
  getCommissionInfo: function() {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve({
          code: 200,
          data: mockApi.commissionInfo
        });
      }, 300);
    });
  },
  
  // 获取邀请码
  getInvitationCode: function() {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve({
          code: 200,
          data: {
            code: mockApi.invitationCode
          }
        });
      }, 300);
    });
  },
  
  // 绑定邀请码
  bindInvitationCode: function(code) {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve({
          code: 200,
          message: '绑定成功'
        });
      }, 500);
    });
  },
  
  // 获取佣金记录
  getCommissionRecords: function() {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve({
          code: 200,
          data: mockApi.commissionRecords
        });
      }, 300);
    });
  },
  
  // 获取佣金解冻记录
  getUnfreezeRecords: function() {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve({
          code: 200,
          data: mockApi.unfreezeRecords
        });
      }, 300);
    });
  },
  
  // 获取提现记录
  getWithdrawRecords: function() {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve({
          code: 200,
          data: mockApi.withdrawRecords
        });
      }, 300);
    });
  },
  
  // 获取提现信息
  getWithdrawInfo: function() {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve({
          code: 200,
          data: {
            availableAmount: mockApi.commissionInfo.availableAmount,
            withdrawRatio: mockApi.commissionInfo.withdrawRatio
          }
        });
      }, 300);
    });
  },
  
  // 提交提现申请
  submitWithdraw: function(amount) {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve({
          code: 200,
          message: '提现申请已提交'
        });
      }, 500);
    });
  },
  
  // 获取预约记录
  getAppointments: function() {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve({
          code: 200,
          data: mockApi.appointments
        });
      }, 300);
    });
  },
  
  // 获取消费记录
  getConsumptions: function() {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve({
          code: 200,
          data: mockApi.consumptions
        });
      }, 300);
    });
  }
};

module.exports = mockRequest;
