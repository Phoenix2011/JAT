package com.massage.spa.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.massage.spa.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 订单Mapper接口
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    
    //language=none
    @Select("<script>" +
            "SELECT o.*, c.name as customer_name, c.phone as customer_phone " +
            "FROM orders o " +
            "LEFT JOIN customer c ON o.customer_id = c.id " +
            "WHERE o.deleted = 0 " +
            "<if test='orderNo != null and orderNo != \"\"'> AND o.order_no LIKE CONCAT('%', #{orderNo}, '%') </if> " +
            "<if test='customerName != null and customerName != \"\"'> AND c.name LIKE CONCAT('%', #{customerName}, '%') </if> " +
            "<if test='customerPhone != null and customerPhone != \"\"'> AND c.phone LIKE CONCAT('%', #{customerPhone}, '%') </if> " +
            "<if test='startDate != null'> AND date(o.create_time) between #{startDate} and #{endDate} </if> " +
            "<if test='status != null'> AND o.status = #{status} </if> " +
            "ORDER BY o.create_time DESC " +
            "LIMIT #{offset}, #{limit}" +
            "</script>")
    List<Map<String, Object>> selectOrdersByPage(@Param("orderNo") String orderNo,
                                               @Param("customerName") String customerName,
                                               @Param("customerPhone") String customerPhone,
                                               @Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate,
                                               @Param("status") Integer status,
                                               @Param("offset") Long offset,
                                               @Param("limit") Long limit);

    //language=none
    @Select("<script>" +
            "SELECT COUNT(*) " +
            "FROM orders o " +
            "LEFT JOIN customer c ON o.customer_id = c.id " +
            "WHERE o.deleted = 0 " +
            "<if test='orderNo != null and orderNo != \"\"'> AND o.order_no LIKE CONCAT('%', #{orderNo}, '%') </if> " +
            "<if test='customerId != null'> AND o.customer_id = #{customerId} </if> " +
            "<if test='customerName != null and customerName != \"\"'> AND c.name LIKE CONCAT('%', #{customerName}, '%') </if> " +
            "<if test='customerPhone != null and customerPhone != \"\"'> AND c.phone LIKE CONCAT('%', #{customerPhone}, '%') </if> " +
            "<if test='startDate != null'> AND date(o.create_time) between #{startDate} and #{endDate} </if> " +
            "<if test='status != null'> AND o.status = #{status} </if> " +
            "</script>")
    Long countOrders(@Param("orderNo") String orderNo,
                    @Param("customerId") Long customerId,
                    @Param("customerName") String customerName,
                    @Param("customerPhone") String customerPhone,
                     @Param("startDate") LocalDate startDate,
                     @Param("endDate") LocalDate endDate,
                    @Param("status") Integer status);
    
    /**
     * 统计所有订单数量
     * @return 订单数量
     */
    @Select("SELECT COUNT(*) FROM orders WHERE deleted = 0")
    Integer countAllOrders();
    
    /**
     * 统计所有订单金额
     * @return 订单金额
     */
    @Select("SELECT SUM(payment_amount) FROM orders WHERE status IN (1, 2) AND deleted = 0")
    Double sumAllOrderAmount();
    
    /**
     * 统计指定状态的订单数量
     * @param status 状态
     * @return 订单数量
     */
    @Select("SELECT COUNT(*) FROM orders WHERE status = #{status} AND deleted = 0")
    Integer countOrdersByStatus(@Param("status") Integer status);
    
    /**
     * 统计今日订单数量
     * @return 订单数量
     */
    @Select("SELECT COUNT(*) FROM orders WHERE DATE(create_time) = CURDATE() AND deleted = 0")
    Integer countTodayOrders();
    
    /**
     * 统计今日订单金额
     * @return 订单金额
     */
    @Select("SELECT SUM(payment_amount) FROM orders WHERE DATE(create_time) = CURDATE() AND status IN (1, 2) AND deleted = 0")
    Double sumTodayOrderAmount();
    
    /**
     * 统计今日指定状态的订单数量
     * @param status 状态
     * @return 订单数量
     */
    @Select("SELECT COUNT(*) FROM orders WHERE DATE(create_time) = CURDATE() AND status = #{status} AND deleted = 0")
    Integer countTodayOrdersByStatus(@Param("status") Integer status);
    
    /**
     * 统计本月订单数量
     * @return 订单数量
     */
    @Select("SELECT COUNT(*) FROM orders WHERE YEAR(create_time) = YEAR(CURDATE()) AND MONTH(create_time) = MONTH(CURDATE()) AND deleted = 0")
    Integer countMonthOrders();
    
    /**
     * 统计本月订单金额
     * @return 订单金额
     */
    @Select("SELECT SUM(payment_amount) FROM orders WHERE YEAR(create_time) = YEAR(CURDATE()) AND MONTH(create_time) = MONTH(CURDATE()) AND status IN (1, 2) AND deleted = 0")
    Double sumMonthOrderAmount();
    
    /**
     * 获取本月每天的订单统计
     * @return 每天订单统计
     */
    @Select("SELECT DATE(create_time) as date, COUNT(*) as count, SUM(payment_amount) as amount " +
            "FROM orders " +
            "WHERE YEAR(create_time) = YEAR(CURDATE()) AND MONTH(create_time) = MONTH(CURDATE()) " +
            "AND status IN (1, 2) AND deleted = 0 " +
            "GROUP BY DATE(create_time) " +
            "ORDER BY DATE(create_time)")
    List<Map<String, Object>> getDailyStatsOfMonth();

    /**
     * 根据顾客ID查询订单总金额
     * @param id 顾客ID
     * @return 订单总金额
     */
    @Select("SELECT SUM(payment_amount) FROM orders WHERE customer_id = #{id} AND status = 1 AND deleted = 0")
    Double selectTotalAmountByCustomerId(@Param("id") Long id);

    /**
     * 根据顾客ID统计订单数量
     * @param id 顾客ID
     * @return 订单数量
     */
    @Select("SELECT COUNT(*) FROM orders WHERE customer_id = #{id} AND deleted = 0")
    Integer selectCountByCustomerId(@Param("id") Long id);

    /**
     * 根据顾客ID查询最后一次下单时间
     * @param id 顾客ID
     * @return 最后一次下单时间
     */
    @Select("SELECT create_time FROM orders WHERE customer_id = #{id} AND deleted = 0 ORDER BY create_time DESC LIMIT 1")
    LocalDateTime selectLastOrderTimeByCustomerId(@Param("id") Long id);

    /**
     * 根据顾客ID分页查询订单详情
     * @param id 顾客ID
     * @param offset 偏移量
     * @param size 每页大小
     * @return 订单详情列表
     */
    @Select("SELECT o.*, " +
            "(SELECT GROUP_CONCAT(od.service_name SEPARATOR ', ') FROM order_detail od WHERE od.order_id = o.id AND od.deleted = 0) as service_names " +
            "FROM orders o " +
            "WHERE o.customer_id = #{id} AND o.deleted = 0 " +
            "ORDER BY o.create_time DESC " +
            "LIMIT #{offset}, #{size}")
    List<Map<String, Object>> selectOrderDetailsByCustomerId(@Param("id") Long id, @Param("offset") long offset, @Param("size") Long size);


    @Select("select count(1) as visitCount, sum(payment_amount) as totalSpent from orders where customer_id = #{id}")
    Map<String, Object> getSummaryById(@Param("id") Long id);
}
