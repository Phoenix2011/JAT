import request from '@/utils/request'

// 登录方法
export function login(data) {
  return request({
    url: '/api/auth/login',
    method: 'post',
    data: data
  })
}

// 获取用户详细信息
export function getInfo() {
  return request({
    url: '/api/auth/info',
    method: 'get'
  })
}

// 退出方法
export function logout() {
  return request({
    url: '/api/auth/logout',
    method: 'post'
  })
}

// 获取用户列表
export function getUserList(query) {
  return request({
    url: '/api/user/list',
    method: 'get',
    params: query
  })
}

// 获取用户详细信息
export function getUser(id) {
  return request({
    url: '/api/user/' + id,
    method: 'get'
  })
}

// 新增用户
export function addUser(data) {
  return request({
    url: '/api/user',
    method: 'post',
    data: data
  })
}

// 修改用户
export function updateUser(data) {
  return request({
    url: '/api/user',
    method: 'put',
    data: data
  })
}

// 删除用户
export function deleteUser(id) {
  return request({
    url: '/api/user/' + id,
    method: 'delete'
  })
}

// 重置密码
export function resetPassword(id, password) {
  return request({
    url: '/api/user/reset-password',
    method: 'put',
    data: {
      userId: id,
      password: password
    }
  })
}

// 获取角色列表
export function getRoleList() {
  return request({
    url: '/api/role/list',
    method: 'get'
  })
}
