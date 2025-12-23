package com.massage.spa.service;

import com.massage.spa.entity.Order;
import com.massage.spa.entity.OrderDetail;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 订单服务接口
 */
public interface OrderService {

    /**
     * 分页查询订单列表
     * @param orderNo 订单编号
     * @param customerName 顾客姓名
     * @param customerPhone 顾客手机号
     * @param status 状态
     * @param current 当前页
     * @param size 每页记录数
     * @return 订单列表
     */
    List<Map<String, Object>> listByPage(String orderNo, String customerName, String customerPhone, LocalDate startDate, LocalDate endDate,
                                         Integer status, Long current, Long size);

    /**
     * 获取订单总数
     * @param orderNo 订单编号
     * @param customerName 顾客姓名
     * @param customerPhone 顾客手机号
     * @param status 状态
     * @return 订单总数
     */
    Long count(String orderNo, Long customerId, String customerName, String customerPhone, LocalDate startDate, LocalDate endDate, Integer status);

    /**
     * 根据ID获取订单
     * @param id 订单ID
     * @return 订单对象
     */
    Order getById(Long id);
    
    /**
     * 根据订单ID获取订单详情
     * @param orderId 订单ID
     * @return 订单详情列表
     */
    List<OrderDetail> getOrderDetails(Long orderId);

    /**
     * 创建订单
     * @param order 订单对象
     * @param orderDetails 订单详情列表
     * @return 是否成功
     */
    boolean create(Order order, List<OrderDetail> orderDetails);

    /**
     * 支付订单
     * @param id 订单ID
     * @param paymentMethod 支付方式
     * @param paymentAmount 支付金额
     * @return 是否成功
     */
    boolean pay(Long id, Integer paymentMethod, Double paymentAmount);

    /**
     * 取消订单
     * @param id 订单ID
     * @param cancelReason 取消原因
     * @return 是否成功
     */
    boolean cancel(Long id, String cancelReason);
    
    /**
     * 退款订单
     * @param id 订单ID
     * @param refundReason 退款原因
     * @param refundAmount 退款金额
     * @return 是否成功
     */
    boolean refund(Long id, String refundReason, Double refundAmount);
    
    /**
     * 获取订单统计
     * @return 统计数据
     */
    Map<String, Object> getStats();
    
    /**
     * 获取今日订单统计
     * @return 统计数据
     */
    Map<String, Object> getTodayStats();
    
    /**
     * 获取本月订单统计
     * @return 统计数据
     */
    Map<String, Object> getMonthStats();

    List<Map<String, Object>> getOrdersByCustomerId(Long customerId, Long pageNum, Long pageSize);
}
