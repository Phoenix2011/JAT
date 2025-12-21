package com.massage.spa.service;

import com.massage.spa.entity.Therapist;

import java.util.List;

/**
 * 技师服务接口
 */
public interface TherapistService {

    /**
     * 分页查询技师列表
     * @param name 姓名
     * @param phone 手机号
     * @param gender 性别
     * @param status 状态
     * @param current 当前页
     * @param size 每页记录数
     * @return 技师列表
     */
    List<Therapist> listByPage(String name, String phone, Integer gender, Integer status, Long current, Long size);

    /**
     * 获取技师总数
     * @param name 姓名
     * @param phone 手机号
     * @param gender 性别
     * @param status 状态
     * @return 技师总数
     */
    Long count(String name, String phone, Integer gender, Integer status);

    /**
     * 根据ID获取技师
     * @param id 技师ID
     * @return 技师对象
     */
    Therapist getById(Long id);

    /**
     * 根据手机号获取技师
     * @param phone 手机号
     * @return 技师对象
     */
    Therapist getByPhone(String phone);

    /**
     * 添加技师
     * @param therapist 技师对象
     * @return 是否成功
     */
    boolean add(Therapist therapist);

    /**
     * 更新技师
     * @param therapist 技师对象
     * @return 是否成功
     */
    boolean update(Therapist therapist);

    /**
     * 删除技师
     * @param id 技师ID
     * @return 是否成功
     */
    boolean delete(Long id);

    /**
     * 批量删除技师
     * @param ids 技师ID列表
     * @return 是否成功
     */
    boolean batchDelete(List<Long> ids);
    
    /**
     * 修改技师状态
     * @param id 技师ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateStatus(Long id, Integer status);
    
    /**
     * 获取技师服务统计
     * @param id 技师ID
     * @return 服务统计信息
     */
    Object getServiceStats(Long id);
    
    /**
     * 获取技师服务记录
     * @param id 技师ID
     * @param current 当前页
     * @param size 每页记录数
     * @return 服务记录列表
     */
    List<Object> getServiceRecords(Long id, Long current, Long size);
    
    /**
     * 获取技师提成记录
     * @param id 技师ID
     * @param current 当前页
     * @param size 每页记录数
     * @return 提成记录列表
     */
    List<Object> getCommissionRecords(Long id, Long current, Long size);
}
