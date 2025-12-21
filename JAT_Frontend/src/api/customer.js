import request from '@/utils/request'

// 搜索顾客
export function searchCustomers(query) {
  return request({
    url: '/api/customer/search',
    method: 'get',
    params: query
  })
}

// 获取顾客列表
export function getCustomerList(query) {
  return request({
    url: '/api/customer/list',
    method: 'get',
    params: query
  })
}

// 获取顾客详细信息
export function getCustomer(id) {
  return request({
    url: '/api/customer/' + id,
    method: 'get'
  })
}

// 新增顾客
export function addCustomer(data) {
  return request({
    url: '/api/customer',
    method: 'post',
    data: data
  })
}

// 修改顾客
export function updateCustomer(data) {
  return request({
    url: '/api/customer',
    method: 'put',
    data: data
  })
}

// 删除顾客
export function deleteCustomer(id) {
  return request({
    url: '/api/customer/' + id,
    method: 'delete'
  })
}

// 获取顾客卡项列表
export function getCustomerCards(customerId) {
  return request({
    url: '/api/customer-card/customer/' + customerId,
    method: 'get'
  })
}

// 获取顾客消费记录
export function getCustomerOrders(customerId, query) {
  return request({
    url: '/api/order/customer/' + customerId,
    method: 'get',
    params: query
  })
}

// 获取顾客预约记录
export function getCustomerAppointments(customerId) {
  return request({
    url: '/api/appointment/customer/' + customerId,
    method: 'get'
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
