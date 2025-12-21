package com.massage.spa.service;

import com.massage.spa.entity.CustomerCard;

import java.util.List;
import java.util.Map;

/**
 * 顾客卡项服务接口
 */
public interface CustomerCardService {

    /**
     * 分页查询顾客卡项列表
     * @param customerName 顾客姓名
     * @param customerPhone 顾客手机号
     * @param serviceId 服务项目ID
     * @param status 状态
     * @param current 当前页
     * @param size 每页记录数
     * @return 顾客卡项列表
     */
    List<Map<String, Object>> listByPage(String customerName, String customerPhone, 
                                        Long serviceId, Integer status, Long current, Long size);

    /**
     * 获取顾客卡项总数
     * @param customerName 顾客姓名
     * @param customerPhone 顾客手机号
     * @param serviceId 服务项目ID
     * @param status 状态
     * @return 顾客卡项总数
     */
    Long count(String customerName, String customerPhone, Long serviceId, Integer status);

    /**
     * 根据ID获取顾客卡项
     * @param id 顾客卡项ID
     * @return 顾客卡项对象
     */
    CustomerCard getById(Long id);

    /**
     * 添加顾客卡项
     * @param customerCard 顾客卡项对象
     * @return 是否成功
     */
    boolean add(CustomerCard customerCard);

    /**
     * 更新顾客卡项
     * @param customerCard 顾客卡项对象
     * @return 是否成功
     */
    boolean update(CustomerCard customerCard);

    /**
     * 删除顾客卡项
     * @param id 顾客卡项ID
     * @return 是否成功
     */
    boolean delete(Long id);
    
    /**
     * 使用卡项
     * @param id 顾客卡项ID
     * @param usedCount 使用次数
     * @param remark 备注
     * @return 是否成功
     */
    boolean useCard(Long id, Integer usedCount, String remark);
    
    /**
     * 获取卡项使用记录
     * @param cardId 卡项ID
     * @return 使用记录列表
     */
    List<Map<String, Object>> getCardUsageRecords(Long cardId);
    
    /**
     * 获取顾客所有卡项
     * @param customerId 顾客ID
     * @return 卡项列表
     */
    List<Map<String, Object>> getCustomerCards(Long customerId);
    
    /**
     * 获取卡项统计
     * @return 统计数据
     */
    Map<String, Object> getStats();
}
