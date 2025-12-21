import request from '@/utils/request'

// 获取服务列表（通用接口）
export function getServiceList(query) {
  return request({
    url: '/api/service/list',
    method: 'get',
    params: query
  })
}

// 获取服务分类列表
export function getServiceCategoryList(query) {
  return request({
    url: '/api/service-category/list',
    method: 'get',
    params: query
  })
}

// 获取服务分类详细信息
export function getServiceCategory(id) {
  return request({
    url: '/api/service-category/' + id,
    method: 'get'
  })
}

// 新增服务分类
export function addServiceCategory(data) {
  return request({
    url: '/api/service-category',
    method: 'post',
    data: data
  })
}

// 修改服务分类
export function updateServiceCategory(data) {
  return request({
    url: '/api/service-category',
    method: 'put',
    data: data
  })
}

// 删除服务分类
export function deleteServiceCategory(id) {
  return request({
    url: '/api/service-category/' + id,
    method: 'delete'
  })
}

// 获取服务项目列表
export function getServiceItemList(query) {
  return request({
    url: '/api/service-item/list',
    method: 'get',
    params: query
  })
}

// 获取服务项目详细信息
export function getServiceItem(id) {
  return request({
    url: '/api/service-item/' + id,
    method: 'get'
  })
}

// 新增服务项目
export function addServiceItem(data) {
  return request({
    url: '/api/service-item',
    method: 'post',
    data: data
  })
}

// 修改服务项目
export function updateServiceItem(data) {
  return request({
    url: '/api/service-item',
    method: 'put',
    data: data
  })
}

// 删除服务项目
export function deleteServiceItem(id) {
  return request({
    url: '/api/service-item/' + id,
    method: 'delete'
  })
}

// 获取服务套餐列表
export function getServicePackageList(query) {
  return request({
    url: '/api/service-package/list',
    method: 'get',
    params: query
  })
}

// 获取服务套餐详细信息
export function getServicePackage(id) {
  return request({
    url: '/api/service-package/' + id,
    method: 'get'
  })
}

// 新增服务套餐
export function addServicePackage(data) {
  return request({
    url: '/api/service-package',
    method: 'post',
    data: data
  })
}

// 修改服务套餐
export function updateServicePackage(data) {
  return request({
    url: '/api/service-package',
    method: 'put',
    data: data
  })
}

// 删除服务套餐
export function deleteServicePackage(id) {
  return request({
    url: '/api/service-package/' + id,
    method: 'delete'
  })
}
