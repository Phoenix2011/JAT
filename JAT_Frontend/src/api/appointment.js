import request from '@/utils/request'

// 获取预约列表
export function getAppointmentList(query) {
  return request({
    url: '/api/appointment/list',
    method: 'get',
    params: query
  })
}

// 根据日期获取预约列表
export function getAppointmentsByDate(param) {
  return request({
    url: '/api/appointment/by-date',
    method: 'get',
    params: param
  })
}

// 更新预约状态
export function updateAppointmentStatus(id, status) {
  return request({
    url: `/api/appointment/${id}/status`,
    method: 'put',
    params: { status }
  })
}

// 获取预约详细信息
export function getAppointment(id) {
  return request({
    url: '/api/appointment/' + id,
    method: 'get'
  })
}

// 新增预约
export function addAppointment(data) {
  return request({
    url: '/api/appointment',
    method: 'post',
    data: data
  })
}

// 修改预约
export function updateAppointment(data) {
  return request({
    url: '/api/appointment',
    method: 'put',
    data: data
  })
}

// 取消预约
export function cancelAppointment(id, cancelReason) {
  return request({
    url: '/api/appointment/cancel/' + id,
    method: 'put',
    params: {
      cancelReason: cancelReason
    }
  })
}

// 完成预约
export function completeAppointment(id) {
  return request({
    url: '/api/appointment/complete/' + id,
    method: 'put'
  })
}

// 获取技师某日的预约时间段
export function getTherapistTimeSlots(therapistId, date) {
  return request({
    url: '/api/appointment/time-slots',
    method: 'get',
    params: {
      therapistId: therapistId,
      date: date
    }
  })
}

// 获取今日预约统计
export function getTodayAppointmentStats() {
  return request({
    url: '/api/appointment/today-stats',
    method: 'get'
  })
}

// 获取未来一周预约统计
export function getWeekAppointmentStats() {
  return request({
    url: '/api/appointment/week-stats',
    method: 'get'
  })
}
