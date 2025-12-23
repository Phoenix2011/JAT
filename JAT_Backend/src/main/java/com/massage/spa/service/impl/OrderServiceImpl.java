package com.massage.spa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.massage.spa.common.exception.BusinessException;
import com.massage.spa.entity.Customer;
import com.massage.spa.entity.Order;
import com.massage.spa.entity.OrderDetail;
import com.massage.spa.entity.ServiceItem;
import com.massage.spa.mapper.CustomerMapper;
import com.massage.spa.mapper.OrderDetailMapper;
import com.massage.spa.mapper.OrderMapper;
import com.massage.spa.mapper.ServiceItemMapper;
import com.massage.spa.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 订单服务实现类
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    
    @Autowired
    private CustomerMapper customerMapper;
    
    @Autowired
    private ServiceItemMapper serviceItemMapper;

    @Override
    public List<Map<String, Object>> listByPage(String orderNo, String customerName, String customerPhone, LocalDate startDate, LocalDate endDate,
                                                Integer status, Long current, Long size) {
        return orderMapper.selectOrdersByPage(orderNo, customerName, customerPhone, startDate, endDate, status,
                (current - 1) * size, size);
    }

    @Override
    public Long count(String orderNo, Long customerId, String customerName, String customerPhone, LocalDate startDate, LocalDate endDate, Integer status) {
        return orderMapper.countOrders(orderNo, customerId, customerName, customerPhone, startDate, endDate, status);
    }

    @Override
    public Order getById(Long id) {
        return orderMapper.selectById(id);
    }

    @Override
    public List<OrderDetail> getOrderDetails(Long orderId) {
        LambdaQueryWrapper<OrderDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderDetail::getOrderId, orderId);
        return orderDetailMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean create(Order order, List<OrderDetail> orderDetails) {
        // 检查顾客是否存在
        Customer customer = customerMapper.selectById(order.getCustomerId());
        if (customer == null) {
            throw new BusinessException("顾客不存在");
        }
        
        // 生成订单编号
        String orderNo = generateOrderNo();
        order.setOrderNo(orderNo);
        
        // 计算订单金额
        double totalAmount = 0;
        for (OrderDetail detail : orderDetails) {
            // 检查服务项目是否存在
            ServiceItem serviceItem = serviceItemMapper.selectById(detail.getServiceId());
            if (serviceItem == null) {
                throw new BusinessException("服务项目不存在");
            }
            
            // 设置服务项目名称和价格
            detail.setServiceName(serviceItem.getName());
            detail.setPrice(serviceItem.getPrice());
            
            // 计算小计金额
            double amount = detail.getPrice() * detail.getQuantity();
            detail.setAmount(amount);
            
            // 累加总金额
            totalAmount += amount;
        }
        
        // 设置订单金额
//        order.setTotalAmount(totalAmount);
//        order.setActualAmount(totalAmount); // 实际金额默认等于总金额，可能会有折扣
        
        // 设置订单状态和时间
        order.setStatus(0); // 0-待支付
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        order.setDeleted(0);
        
        // 保存订单
        boolean result = orderMapper.insert(order) > 0;
        if (!result) {
            throw new BusinessException("创建订单失败");
        }
        
        // 保存订单详情
        for (OrderDetail detail : orderDetails) {
            detail.setOrderId(order.getId());
            detail.setCreateTime(LocalDateTime.now());
            detail.setDeleted(0);
            
            result = orderDetailMapper.insert(detail) > 0;
            if (!result) {
                throw new BusinessException("创建订单详情失败");
            }
        }
        
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean pay(Long id, Integer paymentMethod, Double paymentAmount) {
        // 检查订单是否存在
        Order existOrder = orderMapper.selectById(id);
        if (existOrder == null) {
            throw new BusinessException("订单不存在");
        }
        
        // 检查订单状态
        if (existOrder.getStatus() != 0) {
            throw new BusinessException("订单状态不正确，无法支付");
        }
        
        // 检查支付金额
//        if (paymentAmount < existOrder.getActualAmount()) {
//            throw new BusinessException("支付金额不足");
//        }
        
        // 更新订单状态
        Order order = new Order();
        order.setId(id);
        order.setStatus(1); // 1-已支付
        order.setPaymentMethod(paymentMethod);
        order.setPaymentAmount(paymentAmount);
        order.setPaymentTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        
        return orderMapper.updateById(order) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancel(Long id, String cancelReason) {
        // 检查订单是否存在
        Order existOrder = orderMapper.selectById(id);
        if (existOrder == null) {
            throw new BusinessException("订单不存在");
        }
        
        // 检查订单状态
        if (existOrder.getStatus() != 0) {
            throw new BusinessException("只有待支付的订单可以取消");
        }
        
        // 更新订单状态
        Order order = new Order();
        order.setId(id);
        order.setStatus(3); // 3-已取消
        order.setCancelReason(cancelReason);
        order.setCancelTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        
        return orderMapper.updateById(order) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean refund(Long id, String refundReason, Double refundAmount) {
        // 检查订单是否存在
        Order existOrder = orderMapper.selectById(id);
        if (existOrder == null) {
            throw new BusinessException("订单不存在");
        }
        
        // 检查订单状态
        if (existOrder.getStatus() != 1 && existOrder.getStatus() != 2) {
            throw new BusinessException("只有已支付或已完成的订单可以退款");
        }
        
        // 检查退款金额
        if (refundAmount > existOrder.getPaymentAmount()) {
            throw new BusinessException("退款金额不能大于支付金额");
        }
        
        // 更新订单状态
        Order order = new Order();
        order.setId(id);
        order.setStatus(4); // 4-已退款
        order.setRefundReason(refundReason);
        order.setRefundAmount(refundAmount);
        order.setRefundTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        
        return orderMapper.updateById(order) > 0;
    }

    @Override
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 订单总数
        Integer totalCount = orderMapper.countAllOrders();
        stats.put("totalCount", totalCount != null ? totalCount : 0);
        
        // 订单总金额
        Double totalAmount = orderMapper.sumAllOrderAmount();
        stats.put("totalAmount", totalAmount != null ? totalAmount : 0);
        
        // 各状态订单数量
        Map<String, Object> statusStats = new HashMap<>();
        for (int i = 0; i <= 4; i++) {
            Integer count = orderMapper.countOrdersByStatus(i);
            statusStats.put("status" + i, count != null ? count : 0);
        }
        stats.put("statusStats", statusStats);
        
        return stats;
    }

    @Override
    public Map<String, Object> getTodayStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 今日订单总数
        Integer totalCount = orderMapper.countTodayOrders();
        stats.put("totalCount", totalCount != null ? totalCount : 0);
        
        // 今日订单总金额
        Double totalAmount = orderMapper.sumTodayOrderAmount();
        stats.put("totalAmount", totalAmount != null ? totalAmount : 0);
        
        // 今日各状态订单数量
        Map<String, Object> statusStats = new HashMap<>();
        for (int i = 0; i <= 4; i++) {
            Integer count = orderMapper.countTodayOrdersByStatus(i);
            statusStats.put("status" + i, count != null ? count : 0);
        }
        stats.put("statusStats", statusStats);
        
        return stats;
    }

    @Override
    public Map<String, Object> getMonthStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 本月订单总数
        Integer totalCount = orderMapper.countMonthOrders();
        stats.put("totalCount", totalCount != null ? totalCount : 0);
        
        // 本月订单总金额
        Double totalAmount = orderMapper.sumMonthOrderAmount();
        stats.put("totalAmount", totalAmount != null ? totalAmount : 0);
        
        // 本月每天订单数量和金额
        List<Map<String, Object>> dailyStats = orderMapper.getDailyStatsOfMonth();
        stats.put("dailyStats", dailyStats);
        
        return stats;
    }

    @Override
    public List<Map<String, Object>> getOrdersByCustomerId(Long customerId, Long pageNum, Long pageSize) {
        return orderMapper.selectOrderDetailsByCustomerId(customerId, (pageNum - 1) * pageSize, pageSize);
    }

    /**
     * 生成订单编号
     * @return 订单编号
     */
    private String generateOrderNo() {
        // 生成格式：年月日时分秒+4位随机数
        String dateStr = LocalDateTime.now().toString().replaceAll("[^0-9]", "").substring(0, 14);
        String randomStr = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 4);
        return dateStr + randomStr;
    }
}
