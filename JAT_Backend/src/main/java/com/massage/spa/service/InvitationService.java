package com.massage.spa.service;

import com.massage.spa.entity.InvitationRelation;
import com.massage.spa.entity.InvitationReward;

import java.util.List;
import java.util.Map;

/**
 * 邀请返利服务接口
 */
public interface InvitationService {

    /**
     * 创建邀请关系
     * @param inviterCustomerId 邀请人顾客ID
     * @param inviteeCustomerId 被邀请人顾客ID
     * @param invitationCode 邀请码
     * @return 是否成功
     */
    boolean createInvitationRelation(Long inviterCustomerId, Long inviteeCustomerId, String invitationCode);

    /**
     * 生成邀请码
     * @param customerId 顾客ID
     * @return 邀请码
     */
    String generateInvitationCode(Long customerId);

    /**
     * 自定义邀请码
     * @param customerId 顾客ID
     * @param customCode 自定义邀请码
     * @return 是否成功
     */
    boolean customizeInvitationCode(Long customerId, String customCode);

    /**
     * 根据邀请码获取邀请人ID
     * @param invitationCode 邀请码
     * @return 邀请人顾客ID
     */
    Long getInviterIdByCode(String invitationCode);

    /**
     * 创建邀请奖励
     * @param invitationRelationId 邀请关系ID
     * @param orderId 订单ID
     * @param orderAmount 订单金额
     * @param commissionRate 佣金比例
     * @return 是否成功
     */
    boolean createInvitationReward(Long invitationRelationId, Long orderId, Double orderAmount, Double commissionRate);

    /**
     * 解冻佣金
     * @param orderId 订单ID
     * @param unfreezeRatio 解冻比例
     * @return 是否成功
     */
    boolean unfreezeCommission(Long orderId, Double unfreezeRatio);

    /**
     * 提现佣金
     * @param customerId 顾客ID
     * @param amount 提现金额
     * @param discountRatio 折扣比例
     * @return 是否成功
     */
    boolean withdrawCommission(Long customerId, Double amount, Double discountRatio);

    /**
     * 获取顾客邀请统计
     * @param customerId 顾客ID
     * @return 统计数据
     */
    Map<String, Object> getCustomerInvitationStats(Long customerId);

    /**
     * 获取顾客邀请列表
     * @param customerId 顾客ID
     * @return 邀请列表
     */
    List<Map<String, Object>> getCustomerInvitationList(Long customerId);

    /**
     * 获取顾客佣金记录
     * @param customerId 顾客ID
     * @param current 当前页
     * @param size 每页记录数
     * @return 佣金记录列表
     */
    List<Map<String, Object>> getCustomerCommissionRecords(Long customerId, Long current, Long size);

    /**
     * 获取顾客可用佣金
     * @param customerId 顾客ID
     * @return 可用佣金
     */
    Double getCustomerAvailableCommission(Long customerId);

    /**
     * 获取顾客冻结佣金
     * @param customerId 顾客ID
     * @return 冻结佣金
     */
    Double getCustomerFrozenCommission(Long customerId);

    /**
     * 获取顾客已提现佣金
     * @param customerId 顾客ID
     * @return 已提现佣金
     */
    Double getCustomerWithdrawnCommission(Long customerId);
}
