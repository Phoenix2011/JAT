package com.massage.spa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.massage.spa.common.exception.BusinessException;
import com.massage.spa.entity.CardUsageRecord;
import com.massage.spa.entity.Customer;
import com.massage.spa.entity.CustomerCard;
import com.massage.spa.entity.ServiceItem;
import com.massage.spa.mapper.CardUsageRecordMapper;
import com.massage.spa.mapper.CustomerCardMapper;
import com.massage.spa.mapper.CustomerMapper;
import com.massage.spa.mapper.ServiceItemMapper;
import com.massage.spa.service.CustomerCardService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 顾客卡项服务实现类
 */
@Service
public class CustomerCardServiceImpl implements CustomerCardService {

    @Autowired
    private CustomerCardMapper customerCardMapper;
    
    @Autowired
    private CustomerMapper customerMapper;
    
    @Autowired
    private ServiceItemMapper serviceItemMapper;
    
    @Autowired
    private CardUsageRecordMapper cardUsageRecordMapper;

    @Override
    public List<Map<String, Object>> listByPage(String customerName, String customerPhone, 
                                              Long serviceId, Integer status, Long current, Long size) {
        return customerCardMapper.selectCardsByPage(customerName, customerPhone, serviceId, status, 
                (current - 1) * size, size);
    }

    @Override
    public Long count(String customerName, String customerPhone, Long serviceId, Integer status) {
        return customerCardMapper.countCards(customerName, customerPhone, serviceId, status);
    }

    @Override
    public CustomerCard getById(Long id) {
        return customerCardMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean add(CustomerCard customerCard) {
        // 检查顾客是否存在
        Customer customer = customerMapper.selectById(customerCard.getCustomerId());
        if (customer == null) {
            throw new BusinessException("顾客不存在");
        }
        
        // 检查服务项目是否存在
        ServiceItem serviceItem = serviceItemMapper.selectById(customerCard.getServiceId());
        if (serviceItem == null) {
            throw new BusinessException("服务项目不存在");
        }
        
        // 设置默认值
        customerCard.setUsedCount(0);
        customerCard.setRemainingCount(customerCard.getTotalCount());
        customerCard.setStatus(1); // 1-正常
        customerCard.setCreateTime(LocalDateTime.now());
        customerCard.setUpdateTime(LocalDateTime.now());
        customerCard.setDeleted(0);
        
        return customerCardMapper.insert(customerCard) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(CustomerCard customerCard) {
        // 检查卡项是否存在
        CustomerCard existCard = customerCardMapper.selectById(customerCard.getId());
        if (existCard == null) {
            throw new BusinessException("卡项不存在");
        }
        
        // 检查顾客是否存在
        Customer customer = customerMapper.selectById(customerCard.getCustomerId());
        if (customer == null) {
            throw new BusinessException("顾客不存在");
        }
        
        // 检查服务项目是否存在
        ServiceItem serviceItem = serviceItemMapper.selectById(customerCard.getServiceId());
        if (serviceItem == null) {
            throw new BusinessException("服务项目不存在");
        }
        
        // 设置更新时间
        customerCard.setUpdateTime(LocalDateTime.now());
        
        return customerCardMapper.updateById(customerCard) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        // 检查卡项是否存在
        CustomerCard existCard = customerCardMapper.selectById(id);
        if (existCard == null) {
            throw new BusinessException("卡项不存在");
        }
        
        return customerCardMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean useCard(Long id, Integer usedCount, String remark) {
        // 检查卡项是否存在
        CustomerCard existCard = customerCardMapper.selectById(id);
        if (existCard == null) {
            throw new BusinessException("卡项不存在");
        }
        
        // 检查卡项状态
        if (existCard.getStatus() != 1) {
            throw new BusinessException("卡项状态异常，无法使用");
        }
        
        // 检查剩余次数
        if (existCard.getRemainingCount() < usedCount) {
            throw new BusinessException("卡项剩余次数不足");
        }
        
        // 更新卡项
        CustomerCard customerCard = new CustomerCard();
        customerCard.setId(id);
        customerCard.setUsedCount(existCard.getUsedCount() + usedCount);
        customerCard.setRemainingCount(existCard.getRemainingCount() - usedCount);
        customerCard.setUpdateTime(LocalDateTime.now());
        
        // 如果剩余次数为0，更新状态为已用完
        if (customerCard.getRemainingCount() == 0) {
            customerCard.setStatus(2); // 2-已用完
        }
        
        boolean result = customerCardMapper.updateById(customerCard) > 0;
        if (!result) {
            throw new BusinessException("更新卡项失败");
        }
        
        // 添加使用记录
        CardUsageRecord usageRecord = new CardUsageRecord();
        usageRecord.setCardId(id);
        usageRecord.setCustomerId(existCard.getCustomerId());
        usageRecord.setServiceId(existCard.getServiceId());
        usageRecord.setUsedCount(usedCount);
        usageRecord.setRemark(remark);
        usageRecord.setCreateTime(LocalDateTime.now());
        usageRecord.setDeleted(0);
        
        return cardUsageRecordMapper.insert(usageRecord) > 0;
    }

    @Override
    public List<Map<String, Object>> getCardUsageRecords(Long cardId) {
        return cardUsageRecordMapper.selectUsageRecordsByCardId(cardId);
    }

    @Override
    public List<Map<String, Object>> getCustomerCards(Long customerId) {
        return customerCardMapper.selectCardsByCustomerId(customerId);
    }

    @Override
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 卡项总数
        Integer totalCount = customerCardMapper.countAllCards();
        stats.put("totalCount", totalCount != null ? totalCount : 0);
        
        // 有效卡项数
        Integer activeCount = customerCardMapper.countCardsByStatus(1);
        stats.put("activeCount", activeCount != null ? activeCount : 0);
        
        // 已用完卡项数
        Integer usedUpCount = customerCardMapper.countCardsByStatus(2);
        stats.put("usedUpCount", usedUpCount != null ? usedUpCount : 0);
        
        // 已过期卡项数
        Integer expiredCount = customerCardMapper.countCardsByStatus(3);
        stats.put("expiredCount", expiredCount != null ? expiredCount : 0);
        
        // 已退卡卡项数
        Integer refundedCount = customerCardMapper.countCardsByStatus(4);
        stats.put("refundedCount", refundedCount != null ? refundedCount : 0);
        
        // 卡项总金额
        Double totalAmount = customerCardMapper.sumAllCardAmount();
        stats.put("totalAmount", totalAmount != null ? totalAmount : 0);
        
        return stats;
    }
}
