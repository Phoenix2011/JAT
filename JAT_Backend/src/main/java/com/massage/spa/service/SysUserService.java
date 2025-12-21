package com.massage.spa.service;

import com.massage.spa.entity.SysUser;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统用户服务接口
 */
public interface SysUserService {

    /**
     * 根据用户名获取用户
     * @param username 用户名
     * @return 用户对象
     */
    SysUser getByUsername(String username);

    /**
     * 获取用户权限列表
     * @param userId 用户ID
     * @return 权限列表
     */
    List<String> getUserPermissions(Long userId);

    /**
     * 更新用户登录时间
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean updateLoginTime(Long userId);

    /**
     * 分页查询用户列表
     * @param username 用户名
     * @param realName 真实姓名
     * @param phone 手机号
     * @param status 状态
     * @param current 当前页
     * @param size 每页记录数
     * @return 用户列表
     */
    List<SysUser> listByPage(String username, String realName, String phone, Integer status, Long current, Long size);

    /**
     * 获取用户总数
     * @param username 用户名
     * @param realName 真实姓名
     * @param phone 手机号
     * @param status 状态
     * @return 用户总数
     */
    Long count(String username, String realName, String phone, Integer status);

    /**
     * 根据ID获取用户
     * @param id 用户ID
     * @return 用户对象
     */
    SysUser getById(Long id);

    /**
     * 添加用户
     * @param sysUser 用户对象
     * @return 是否成功
     */
    boolean add(SysUser sysUser);

    /**
     * 更新用户
     * @param sysUser 用户对象
     * @return 是否成功
     */
    boolean update(SysUser sysUser);

    /**
     * 删除用户
     * @param id 用户ID
     * @return 是否成功
     */
    boolean delete(Long id);

    /**
     * 批量删除用户
     * @param ids 用户ID列表
     * @return 是否成功
     */
    boolean batchDelete(List<Long> ids);

    /**
     * 修改密码
     * @param id 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否成功
     */
    boolean updatePassword(Long id, String oldPassword, String newPassword);

    /**
     * 重置密码
     * @param id 用户ID
     * @return 是否成功
     */
    boolean resetPassword(Long id);

    /**
     * 修改用户状态
     * @param id 用户ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateStatus(Long id, Integer status);
}
