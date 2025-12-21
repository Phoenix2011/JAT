import request from '@/utils/request'

// 生成邀请码
export function generateInvitationCode(customerId) {
  return request({
    url: '/api/invitation/generate-code/' + customerId,
    method: 'post'
  })
}

// 自定义邀请码
export function customizeInvitationCode(customerId, customCode) {
  return request({
    url: '/api/invitation/customize-code/' + customerId,
    method: 'put',
    params: {
      customCode: customCode
    }
  })
}

// 创建邀请关系
export function createInvitationRelation(inviterCustomerId, inviteeCustomerId, invitationCode) {
  return request({
    url: '/api/invitation/relation',
    method: 'post',
    params: {
      inviterCustomerId: inviterCustomerId,
      inviteeCustomerId: inviteeCustomerId,
      invitationCode: invitationCode
    }
  })
}

// 创建邀请奖励
export function createInvitationReward(invitationRelationId, orderId, orderAmount, commissionRate) {
  return request({
    url: '/api/invitation/reward',
    method: 'post',
    params: {
      invitationRelationId: invitationRelationId,
      orderId: orderId,
      orderAmount: orderAmount,
      commissionRate: commissionRate
    }
  })
}

// 解冻佣金
export function unfreezeCommission(orderId, unfreezeRatio) {
  return request({
    url: '/api/invitation/unfreeze',
    method: 'put',
    params: {
      orderId: orderId,
      unfreezeRatio: unfreezeRatio
    }
  })
}

// 提现佣金
export function withdrawCommission(customerId, amount, discountRatio) {
  return request({
    url: '/api/invitation/withdraw',
    method: 'post',
    params: {
      customerId: customerId,
      amount: amount,
      discountRatio: discountRatio
    }
  })
}

// 获取顾客邀请统计
export function getCustomerInvitationStats(customerId) {
  return request({
    url: '/api/invitation/stats/' + customerId,
    method: 'get'
  })
}

// 获取顾客邀请列表
export function getCustomerInvitationList(customerId) {
  return request({
    url: '/api/invitation/list/' + customerId,
    method: 'get'
  })
}

// 获取顾客佣金记录
export function getCustomerCommissionRecords(customerId, query) {
  return request({
    url: '/api/invitation/commission-records/' + customerId,
    method: 'get',
    params: query
  })
}
