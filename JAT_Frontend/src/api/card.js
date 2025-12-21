import request from '@/utils/request'

// 获取顾客卡项列表
export function getCustomerCardList(query) {
  return request({
    url: '/api/customer-card/list',
    method: 'get',
    params: query
  })
}

// 获取顾客卡项详细信息
export function getCustomerCard(id) {
  return request({
    url: '/api/customer-card/' + id,
    method: 'get'
  })
}

// 添加顾客卡项
export function addCustomerCard(data) {
  return request({
    url: '/api/customer-card',
    method: 'post',
    data: data
  })
}

// 修改顾客卡项
export function updateCustomerCard(data) {
  return request({
    url: '/api/customer-card',
    method: 'put',
    data: data
  })
}

// 删除顾客卡项
export function deleteCustomerCard(id) {
  return request({
    url: '/api/customer-card/' + id,
    method: 'delete'
  })
}

// 使用卡项
export function useCard(id, usedCount, remark) {
  return request({
    url: '/api/customer-card/use/' + id,
    method: 'put',
    params: {
      usedCount: usedCount,
      remark: remark
    }
  })
}

// 获取卡项使用记录
export function getCardUsageRecords(cardId) {
  return request({
    url: '/api/customer-card/usage-records/' + cardId,
    method: 'get'
  })
}

// 获取顾客所有卡项
export function getCustomerCards(customerId) {
  return request({
    url: '/api/customer-card/customer/' + customerId,
    method: 'get'
  })
}

// 获取卡项统计
export function getCardStats() {
  return request({
    url: '/api/customer-card/stats',
    method: 'get'
  })
}
