package com.massage.spa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.massage.spa.common.exception.BusinessException;
import com.massage.spa.entity.Customer;
import com.massage.spa.mapper.CustomerCardMapper;
import com.massage.spa.mapper.CustomerMapper;
import com.massage.spa.mapper.OrderMapper;
import com.massage.spa.service.CustomerService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 顾客服务实现类
 */
@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerMapper customerMapper;
    
    @Autowired
    private CustomerCardMapper customerCardMapper;
    
    @Autowired
    private OrderMapper orderMapper;

    @Override
    public List<Customer> listByPage(Long id, String name, String phone, Long current, Long size) {
        Page<Customer> page = new Page<>(current, size);
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(id != null, Customer::getId, id)
                .like(StringUtils.isNotBlank(name), Customer::getName, name)
                .like(StringUtils.isNotBlank(phone), Customer::getPhone, phone)
                .orderByDesc(Customer::getId);
        Page<Customer> resultPage = customerMapper.selectPage(page, wrapper);
        return resultPage.getRecords();
    }

    @Override
    public Long count(Long id, String name, String phone) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(id != null, Customer::getGender, id)
                .like(StringUtils.isNotBlank(name), Customer::getName, name)
                .like(StringUtils.isNotBlank(phone), Customer::getPhone, phone);
        return customerMapper.selectCount(wrapper);
    }

    @Override
    public Customer getById(Long id) {
        return customerMapper.selectById(id);
    }

    public Map<String, Object> getExtInfoById(Long id) {
        Map<String, Object> customerInfo = orderMapper.getSummaryById(id);
        Customer customer = customerMapper.selectById(id);
        customerInfo.put("id", customer.getId());
        customerInfo.put("name", customer.getName());
        customerInfo.put("phone", customer.getPhone());
        customerInfo.put("gender", customer.getGender());
        customerInfo.put("age", customer.getAge());
        customerInfo.put("createTime", customer.getCreateTime());
        customerInfo.put("updateTime", customer.getUpdateTime());
        customerInfo.put("lastVisitTime", customer.getLastVisitTime());
        customerInfo.put("remark", customer.getRemark());
        return customerInfo;
    }

    public List<Customer> getByIdOrName(String param) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Customer::getName, param)
                .or().like(Customer::getId, param);
        return customerMapper.selectList(wrapper);
    }

    @Override
    public Customer getByPhone(String phone) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Customer::getPhone, phone);
        return customerMapper.selectOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean add(Customer customer) {
        if (customer.getPhone() != null && !customer.getPhone().isEmpty()) {
            // 检查手机号是否存在
            Customer existCustomer = getByPhone(customer.getPhone());
            if (existCustomer != null) {
                throw new BusinessException("手机号已存在");
            }
        }
        
        // 设置默认值
        customer.setCreateTime(LocalDateTime.now());
        customer.setUpdateTime(LocalDateTime.now());
        customer.setDeleted(0);
        
        return customerMapper.insert(customer) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(Customer customer) {
        // 检查顾客是否存在
        Customer existCustomer = customerMapper.selectById(customer.getId());
        if (existCustomer == null) {
            throw new BusinessException("顾客不存在");
        }
        
        // 检查手机号是否重复
//        if (!existCustomer.getPhone().equals(customer.getPhone())) {
//            Customer phoneCustomer = getByPhone(customer.getPhone());
//            if (phoneCustomer != null) {
//                throw new BusinessException("手机号已存在");
//            }
//        }
        
        // 设置更新时间
        customer.setUpdateTime(LocalDateTime.now());
        
        return customerMapper.updateById(customer) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        // 检查顾客是否存在
        Customer existCustomer = customerMapper.selectById(id);
        if (existCustomer == null) {
            throw new BusinessException("顾客不存在");
        }
        
        return customerMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDelete(List<Long> ids) {
        return customerMapper.deleteBatchIds(ids) > 0;
    }

    @Override
    public Object getConsumptionStats(Long id) {
        // 检查顾客是否存在
        Customer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new BusinessException("顾客不存在");
        }
        
        // 查询消费统计
        Map<String, Object> stats = new HashMap<>();
        
        // 总消费金额
        Double totalAmount = orderMapper.selectTotalAmountByCustomerId(id);
        stats.put("totalAmount", totalAmount != null ? totalAmount : 0);
        
        // 消费次数
        Integer orderCount = orderMapper.selectCountByCustomerId(id);
        stats.put("orderCount", orderCount != null ? orderCount : 0);
        
        // 卡项数量
        Integer cardCount = customerCardMapper.selectCountByCustomerId(id);
        stats.put("cardCount", cardCount != null ? cardCount : 0);
        
        // 最近消费时间
        LocalDateTime lastOrderTime = orderMapper.selectLastOrderTimeByCustomerId(id);
        stats.put("lastOrderTime", lastOrderTime);
        
        return stats;
    }

    @Override
    public List<Object> getCustomerCards(Long id) {
        // 检查顾客是否存在
        Customer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new BusinessException("顾客不存在");
        }
        
        // 查询顾客卡项
        return customerCardMapper.selectCardDetailsByCustomerId(id);
    }

    @Override
    public List<Map<String, Object>> getConsumptionRecords(Long id, Long current, Long size) {
        // 检查顾客是否存在
        Customer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new BusinessException("顾客不存在");
        }
        
        // 查询消费记录
        return orderMapper.selectOrderDetailsByCustomerId(id, (current - 1) * size, size);
    }
}
