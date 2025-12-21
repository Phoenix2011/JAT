import request from '@/utils/request'

// 获取订单列表
export function getOrderList(query) {
  return request({
    url: '/api/order/list',
    method: 'get',
    params: query
  })
}

// 获取订单详细信息
export function getOrder(id) {
  return request({
    url: '/api/order/' + id,
    method: 'get'
  })
}

// 获取订单明细
export function getOrderDetails(orderId) {
  return request({
    url: '/api/order/details/' + orderId,
    method: 'get'
  })
}

// 创建订单
export function createOrder(data) {
  return request({
    url: '/api/order',
    method: 'post',
    data: data
  })
}

// 支付订单
export function payOrder(id, paymentMethod, paymentAmount) {
  return request({
    url: '/api/order/pay/' + id,
    method: 'put',
    params: {
      paymentMethod: paymentMethod,
      paymentAmount: paymentAmount
    }
  })
}

// 取消订单
export function cancelOrder(id, cancelReason) {
  return request({
    url: '/api/order/cancel/' + id,
    method: 'put',
    params: {
      cancelReason: cancelReason
    }
  })
}

// 退款订单
export function refundOrder(id, refundReason, refundAmount) {
  return request({
    url: '/api/order/refund/' + id,
    method: 'put',
    params: {
      refundReason: refundReason,
      refundAmount: refundAmount
    }
  })
}

// 获取订单统计
export function getOrderStats() {
  return request({
    url: '/api/order/stats',
    method: 'get'
  })
}

// 获取今日订单统计
export function getTodayOrderStats() {
  return request({
    url: '/api/order/today-stats',
    method: 'get'
  })
}

// 获取本月订单统计
export function getMonthOrderStats() {
  return request({
    url: '/api/order/month-stats',
    method: 'get'
  })
}
