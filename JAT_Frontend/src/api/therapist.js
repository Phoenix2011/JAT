import request from '@/utils/request'

// 获取技师列表
export function getTherapistList(query) {
  return request({
    url: '/api/therapist/list',
    method: 'get',
    params: query
  })
}

// 获取技师详细信息
export function getTherapist(id) {
  return request({
    url: '/api/therapist/' + id,
    method: 'get'
  })
}

// 新增技师
export function addTherapist(data) {
  return request({
    url: '/api/therapist',
    method: 'post',
    data: data
  })
}

// 修改技师
export function updateTherapist(data) {
  return request({
    url: '/api/therapist',
    method: 'put',
    data: data
  })
}

// 删除技师
export function deleteTherapist(id) {
  return request({
    url: '/api/therapist/' + id,
    method: 'delete'
  })
}

// 获取技师服务记录
export function getTherapistServiceRecords(therapistId, query) {
  return request({
    url: '/api/therapist/service-records/' + therapistId,
    method: 'get',
    params: query
  })
}

// 获取技师预约时间段
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

// 获取技师业绩统计
export function getTherapistPerformance(therapistId, query) {
  return request({
    url: '/api/therapist/performance/' + therapistId,
    method: 'get',
    params: query
  })
}
