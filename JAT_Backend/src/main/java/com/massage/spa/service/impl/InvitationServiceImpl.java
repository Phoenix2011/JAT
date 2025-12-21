package com.massage.spa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.massage.spa.common.exception.BusinessException;
import com.massage.spa.entity.*;
import com.massage.spa.mapper.*;
import com.massage.spa.service.InvitationService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 邀请返利服务实现类
 */
@Service
public class InvitationServiceImpl implements InvitationService {

    @Autowired
    private InvitationRelationMapper invitationRelationMapper;
    
    @Autowired
    private InvitationRewardMapper invitationRewardMapper;
    
    @Autowired
    private CommissionRecordMapper commissionRecordMapper;
    
    @Autowired
    private CommissionUnfreezeRecordMapper commissionUnfreezeRecordMapper;
    
    @Autowired
    private CommissionWithdrawalMapper commissionWithdrawalMapper;
    
    @Autowired
    private CustomerMapper customerMapper;
    
    @Autowired
    private OrderMapper orderMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createInvitationRelation(Long inviterCustomerId, Long inviteeCustomerId, String invitationCode) {
        // 检查邀请人是否存在
        Customer inviter = customerMapper.selectById(inviterCustomerId);
        if (inviter == null) {
            throw new BusinessException("邀请人不存在");
        }
        
        // 检查被邀请人是否存在
        Customer invitee = customerMapper.selectById(inviteeCustomerId);
        if (invitee == null) {
            throw new BusinessException("被邀请人不存在");
        }
        
        // 检查被邀请人是否已经被邀请过
        LambdaQueryWrapper<InvitationRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InvitationRelation::getInviteeCustomerId, inviteeCustomerId);
        Long count = invitationRelationMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("该用户已经被邀请过");
        }
        
        // 创建邀请关系
        InvitationRelation relation = new InvitationRelation();
        relation.setInviterCustomerId(inviterCustomerId);
        relation.setInviteeCustomerId(inviteeCustomerId);
        relation.setInvitationCode(invitationCode);
        relation.setCreateTime(LocalDateTime.now());
        relation.setDeleted(0);
        
        return invitationRelationMapper.insert(relation) > 0;
    }

    @Override
    public String generateInvitationCode(Long customerId) {
        // 检查顾客是否存在
        Customer customer = customerMapper.selectById(customerId);
        if (customer == null) {
            throw new BusinessException("顾客不存在");
        }
        
        // 生成随机邀请码
        String invitationCode = RandomStringUtils.randomAlphanumeric(6).toUpperCase();
        
        // 检查邀请码是否已存在
        while (checkInvitationCodeExists(invitationCode)) {
            invitationCode = RandomStringUtils.randomAlphanumeric(6).toUpperCase();
        }
        
        // 更新顾客邀请码
        Customer updateCustomer = new Customer();
        updateCustomer.setId(customerId);
        updateCustomer.setInvitationCode(invitationCode);
        updateCustomer.setUpdateTime(LocalDateTime.now());
        
        customerMapper.updateById(updateCustomer);
        
        return invitationCode;
    }

    @Override
    public boolean customizeInvitationCode(Long customerId, String customCode) {
        // 检查顾客是否存在
        Customer customer = customerMapper.selectById(customerId);
        if (customer == null) {
            throw new BusinessException("顾客不存在");
        }
        
        // 检查邀请码格式
        if (StringUtils.isBlank(customCode) || customCode.length() < 4 || customCode.length() > 10) {
            throw new BusinessException("邀请码长度必须在4-10位之间");
        }
        
        // 检查邀请码是否已存在
        if (checkInvitationCodeExists(customCode)) {
            throw new BusinessException("邀请码已存在，请更换");
        }
        
        // 更新顾客邀请码
        Customer updateCustomer = new Customer();
        updateCustomer.setId(customerId);
        updateCustomer.setInvitationCode(customCode);
        updateCustomer.setUpdateTime(LocalDateTime.now());
        
        return customerMapper.updateById(updateCustomer) > 0;
    }

    @Override
    public Long getInviterIdByCode(String invitationCode) {
        if (StringUtils.isBlank(invitationCode)) {
            return null;
        }
        
        // 查询邀请码对应的顾客
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Customer::getInvitationCode, invitationCode);
        wrapper.eq(Customer::getDeleted, 0);
        Customer customer = customerMapper.selectOne(wrapper);
        
        return customer != null ? customer.getId() : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createInvitationReward(Long invitationRelationId, Long orderId, Double orderAmount, Double commissionRate) {
        // 检查邀请关系是否存在
        InvitationRelation relation = invitationRelationMapper.selectById(invitationRelationId);
        if (relation == null) {
            throw new BusinessException("邀请关系不存在");
        }
        
        // 检查订单是否存在
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        // 计算佣金金额
        Double commissionAmount = orderAmount * commissionRate;
        
        // 创建邀请奖励记录
        InvitationReward reward = new InvitationReward();
        reward.setInvitationRelationId(invitationRelationId);
        reward.setInviterCustomerId(relation.getInviterCustomerId());
        reward.setInviteeCustomerId(relation.getInviteeCustomerId());
        reward.setOrderId(orderId);
        reward.setOrderAmount(orderAmount);
        reward.setCommissionRate(commissionRate);
        reward.setCommissionAmount(commissionAmount);
        reward.setStatus(0); // 0-冻结中
        reward.setCreateTime(LocalDateTime.now());
        reward.setUpdateTime(LocalDateTime.now());
        reward.setDeleted(0);
        
        boolean result = invitationRewardMapper.insert(reward) > 0;
        if (!result) {
            throw new BusinessException("创建邀请奖励失败");
        }
        
        // 创建佣金记录
        CommissionRecord commissionRecord = new CommissionRecord();
        commissionRecord.setCustomerId(relation.getInviterCustomerId());
        commissionRecord.setInvitationRewardId(reward.getId());
        commissionRecord.setOrderId(orderId);
        commissionRecord.setAmount(commissionAmount);
        commissionRecord.setType(0); // 0-冻结佣金
        commissionRecord.setCreateTime(LocalDateTime.now());
        commissionRecord.setDeleted(0);
        
        return commissionRecordMapper.insert(commissionRecord) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unfreezeCommission(Long orderId, Double unfreezeRatio) {
        // 查询订单相关的邀请奖励
        LambdaQueryWrapper<InvitationReward> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InvitationReward::getOrderId, orderId);
        wrapper.eq(InvitationReward::getStatus, 0); // 0-冻结中
        wrapper.eq(InvitationReward::getDeleted, 0);
        List<InvitationReward> rewards = invitationRewardMapper.selectList(wrapper);
        
        if (rewards.isEmpty()) {
            return true; // 没有需要解冻的佣金
        }
        
        for (InvitationReward reward : rewards) {
            // 计算解冻金额
            Double unfreezeAmount = reward.getCommissionAmount() * unfreezeRatio;
            
            // 更新邀请奖励状态
            InvitationReward updateReward = new InvitationReward();
            updateReward.setId(reward.getId());
            updateReward.setUnfreezeAmount(reward.getUnfreezeAmount() != null ? reward.getUnfreezeAmount() + unfreezeAmount : unfreezeAmount);
            
            // 如果全部解冻，更新状态为已解冻
            if (updateReward.getUnfreezeAmount() >= reward.getCommissionAmount()) {
                updateReward.setStatus(1); // 1-已解冻
            }
            
            updateReward.setUpdateTime(LocalDateTime.now());
            
            boolean result = invitationRewardMapper.updateById(updateReward) > 0;
            if (!result) {
                throw new BusinessException("更新邀请奖励失败");
            }
            
            // 创建解冻记录
            CommissionUnfreezeRecord unfreezeRecord = new CommissionUnfreezeRecord();
            unfreezeRecord.setCustomerId(reward.getInviterCustomerId());
            unfreezeRecord.setInvitationRewardId(reward.getId());
            unfreezeRecord.setOrderId(orderId);
            unfreezeRecord.setAmount(unfreezeAmount);
            unfreezeRecord.setUnfreezeRatio(unfreezeRatio);
            unfreezeRecord.setCreateTime(LocalDateTime.now());
            unfreezeRecord.setDeleted(0);
            
            result = commissionUnfreezeRecordMapper.insert(unfreezeRecord) > 0;
            if (!result) {
                throw new BusinessException("创建解冻记录失败");
            }
            
            // 创建佣金记录
            CommissionRecord commissionRecord = new CommissionRecord();
            commissionRecord.setCustomerId(reward.getInviterCustomerId());
            commissionRecord.setInvitationRewardId(reward.getId());
            commissionRecord.setOrderId(orderId);
            commissionRecord.setAmount(unfreezeAmount);
            commissionRecord.setType(1); // 1-可用佣金
            commissionRecord.setCreateTime(LocalDateTime.now());
            commissionRecord.setDeleted(0);
            
            result = commissionRecordMapper.insert(commissionRecord) > 0;
            if (!result) {
                throw new BusinessException("创建佣金记录失败");
            }
        }
        
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean withdrawCommission(Long customerId, Double amount, Double discountRatio) {
        // 检查顾客是否存在
        Customer customer = customerMapper.selectById(customerId);
        if (customer == null) {
            throw new BusinessException("顾客不存在");
        }
        
        // 获取可用佣金
        Double availableCommission = getCustomerAvailableCommission(customerId);
        if (availableCommission < amount) {
            throw new BusinessException("可用佣金不足");
        }
        
        // 计算实际到账金额
        Double actualAmount = amount * discountRatio;
        
        // 创建提现记录
        CommissionWithdrawal withdrawal = new CommissionWithdrawal();
        withdrawal.setCustomerId(customerId);
        withdrawal.setAmount(amount);
        withdrawal.setDiscountRatio(discountRatio);
        withdrawal.setActualAmount(actualAmount);
        withdrawal.setStatus(0); // 0-处理中
        withdrawal.setCreateTime(LocalDateTime.now());
        withdrawal.setUpdateTime(LocalDateTime.now());
        withdrawal.setDeleted(0);
        
        boolean result = commissionWithdrawalMapper.insert(withdrawal) > 0;
        if (!result) {
            throw new BusinessException("创建提现记录失败");
        }
        
        // 创建佣金记录
        CommissionRecord commissionRecord = new CommissionRecord();
        commissionRecord.setCustomerId(customerId);
        commissionRecord.setAmount(-amount); // 负数表示减少
        commissionRecord.setType(2); // 2-提现
        commissionRecord.setWithdrawalId(withdrawal.getId());
        commissionRecord.setCreateTime(LocalDateTime.now());
        commissionRecord.setDeleted(0);
        
        return commissionRecordMapper.insert(commissionRecord) > 0;
    }

    @Override
    public Map<String, Object> getCustomerInvitationStats(Long customerId) {
        Map<String, Object> stats = new HashMap<>();
        
        // 获取邀请人数
        Integer inviteeCount = invitationRelationMapper.countInviteesByInviterId(customerId);
        stats.put("inviteeCount", inviteeCount != null ? inviteeCount : 0);
        
        // 获取总返利金额
        Double totalCommission = commissionRecordMapper.sumCommissionByCustomerId(customerId);
        stats.put("totalCommission", totalCommission != null ? totalCommission : 0);
        
        // 获取可用佣金
        Double availableCommission = getCustomerAvailableCommission(customerId);
        stats.put("availableCommission", availableCommission != null ? availableCommission : 0);
        
        // 获取冻结佣金
        Double frozenCommission = getCustomerFrozenCommission(customerId);
        stats.put("frozenCommission", frozenCommission != null ? frozenCommission : 0);
        
        // 获取已提现佣金
        Double withdrawnCommission = getCustomerWithdrawnCommission(customerId);
        stats.put("withdrawnCommission", withdrawnCommission != null ? withdrawnCommission : 0);
        
        // 获取邀请码
        Customer customer = customerMapper.selectById(customerId);
        stats.put("invitationCode", customer != null ? customer.getInvitationCode() : "");
        
        return stats;
    }

    @Override
    public List<Map<String, Object>> getCustomerInvitationList(Long customerId) {
        return invitationRelationMapper.selectInviteesByInviterId(customerId);
    }

    @Override
    public List<Map<String, Object>> getCustomerCommissionRecords(Long customerId, Long current, Long size) {
        return commissionRecordMapper.selectCommissionRecordsByCustomerId(customerId, (current - 1) * size, size);
    }

    @Override
    public Double getCustomerAvailableCommission(Long customerId) {
        return commissionRecordMapper.sumAvailableCommissionByCustomerId(customerId);
    }

    @Override
    public Double getCustomerFrozenCommission(Long customerId) {
        return commissionRecordMapper.sumFrozenCommissionByCustomerId(customerId);
    }

    @Override
    public Double getCustomerWithdrawnCommission(Long customerId) {
        return commissionRecordMapper.sumWithdrawnCommissionByCustomerId(customerId);
    }
    
    /**
     * 检查邀请码是否已存在
     * @param invitationCode 邀请码
     * @return 是否存在
     */
    private boolean checkInvitationCodeExists(String invitationCode) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Customer::getInvitationCode, invitationCode);
        wrapper.eq(Customer::getDeleted, 0);
        Long count = customerMapper.selectCount(wrapper);
        return count > 0;
    }
}
