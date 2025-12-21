package com.massage.spa.service;

import com.massage.spa.entity.Customer;

import java.util.List;
import java.util.Map;

/**
 * 顾客服务接口
 */
public interface CustomerService {

    List<Customer> listByPage(Long id, String name, String phone, Long current, Long size);

    Long count(Long id, String name, String phone);

    /**
     * 根据ID获取顾客
     * @param id 顾客ID
     * @return 顾客对象
     */
    Customer getById(Long id);
    Map<String, Object> getExtInfoById(Long id);
    List<Customer> getByIdOrName(String param);

    /**
     * 根据手机号获取顾客
     * @param phone 手机号
     * @return 顾客对象
     */
    Customer getByPhone(String phone);

    /**
     * 添加顾客
     * @param customer 顾客对象
     * @return 是否成功
     */
    boolean add(Customer customer);

    /**
     * 更新顾客
     * @param customer 顾客对象
     * @return 是否成功
     */
    boolean update(Customer customer);

    /**
     * 删除顾客
     * @param id 顾客ID
     * @return 是否成功
     */
    boolean delete(Long id);

    /**
     * 批量删除顾客
     * @param ids 顾客ID列表
     * @return 是否成功
     */
    boolean batchDelete(List<Long> ids);
    
    /**
     * 获取顾客消费统计
     * @param id 顾客ID
     * @return 消费统计信息
     */
    Object getConsumptionStats(Long id);
    
    /**
     * 获取顾客卡项列表
     * @param id 顾客ID
     * @return 卡项列表
     */
    List<Object> getCustomerCards(Long id);
    
    /**
     * 获取顾客消费记录
     * @param id 顾客ID
     * @param current 当前页
     * @param size 每页记录数
     * @return 消费记录列表
     */
    List<Map<String, Object>> getConsumptionRecords(Long id, Long current, Long size);
}
