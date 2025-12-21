package com.massage.spa.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.massage.spa.entity.CustomerCard;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 顾客卡项Mapper接口
 */
@Mapper
public interface CustomerCardMapper extends BaseMapper<CustomerCard> {
    
    /**
     * 分页查询顾客卡项列表
     * @param customerName 顾客姓名
     * @param customerPhone 顾客手机号
     * @param serviceId 服务项目ID
     * @param status 状态
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 顾客卡项列表
     */
    @Select("<script>" +
            "SELECT cc.*, c.name as customer_name, c.phone as customer_phone, " +
            "s.name as service_name " +
            "FROM customer_card cc " +
            "LEFT JOIN customer c ON cc.customer_id = c.id " +
            "LEFT JOIN service_item s ON cc.service_id = s.id " +
            "WHERE cc.deleted = 0 " +
            "<if test='customerName != null and customerName != \"\"'> AND c.name LIKE CONCAT('%', #{customerName}, '%') </if> " +
            "<if test='customerPhone != null and customerPhone != \"\"'> AND c.phone LIKE CONCAT('%', #{customerPhone}, '%') </if> " +
            "<if test='serviceId != null'> AND cc.service_id = #{serviceId} </if> " +
            "<if test='status != null'> AND cc.status = #{status} </if> " +
            "ORDER BY cc.create_time DESC " +
            "LIMIT #{offset}, #{limit}" +
            "</script>")
    List<Map<String, Object>> selectCardsByPage(@Param("customerName") String customerName,
                                              @Param("customerPhone") String customerPhone,
                                              @Param("serviceId") Long serviceId,
                                              @Param("status") Integer status,
                                              @Param("offset") Long offset,
                                              @Param("limit") Long limit);
    
    /**
     * 统计顾客卡项总数
     * @param customerName 顾客姓名
     * @param customerPhone 顾客手机号
     * @param serviceId 服务项目ID
     * @param status 状态
     * @return 顾客卡项总数
     */
    @Select("<script>" +
            "SELECT COUNT(*) " +
            "FROM customer_card cc " +
            "LEFT JOIN customer c ON cc.customer_id = c.id " +
            "LEFT JOIN service_item s ON cc.service_id = s.id " +
            "WHERE cc.deleted = 0 " +
            "<if test='customerName != null and customerName != \"\"'> AND c.name LIKE CONCAT('%', #{customerName}, '%') </if> " +
            "<if test='customerPhone != null and customerPhone != \"\"'> AND c.phone LIKE CONCAT('%', #{customerPhone}, '%') </if> " +
            "<if test='serviceId != null'> AND cc.service_id = #{serviceId} </if> " +
            "<if test='status != null'> AND cc.status = #{status} </if> " +
            "</script>")
    Long countCards(@Param("customerName") String customerName,
                   @Param("customerPhone") String customerPhone,
                   @Param("serviceId") Long serviceId,
                   @Param("status") Integer status);
    
    /**
     * 查询顾客的卡项列表
     * @param customerId 顾客ID
     * @return 卡项列表
     */
    @Select("SELECT cc.*, s.name as service_name, s.description as service_description " +
            "FROM customer_card cc " +
            "LEFT JOIN service_item s ON cc.service_id = s.id " +
            "WHERE cc.customer_id = #{customerId} AND cc.deleted = 0 " +
            "ORDER BY cc.create_time DESC")
    List<Map<String, Object>> selectCardsByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * 统计所有卡项数量
     * @return 卡项数量
     */
    @Select("SELECT COUNT(*) FROM customer_card WHERE deleted = 0")
    Integer countAllCards();
    
    /**
     * 统计指定状态的卡项数量
     * @param status 状态
     * @return 卡项数量
     */
    @Select("SELECT COUNT(*) FROM customer_card WHERE status = #{status} AND deleted = 0")
    Integer countCardsByStatus(@Param("status") Integer status);
    
    /**
     * 统计所有卡项金额
     * @return 卡项金额
     */
    @Select("SELECT SUM(amount) FROM customer_card WHERE deleted = 0")
    Double sumAllCardAmount();

    /**
     * 根据顾客ID统计卡项数量
     * @param id 顾客ID
     * @return 卡项数量
     */
    @Select("SELECT COUNT(*) FROM customer_card WHERE customer_id = #{id} AND deleted = 0")
    Integer selectCountByCustomerId(@Param("id") Long id);

    /**
     * 根据顾客ID查询卡项详情列表
     * @param id 顾客ID
     * @return 卡项详情列表
     */
    @Select("SELECT cc.*, s.name as service_name, s.description as service_description, " +
            "s.image as service_image, s.price as service_price, " +
            "(SELECT COUNT(*) FROM card_usage_record cur WHERE cur.card_id = cc.id AND cur.deleted = 0) as usage_count " +
            "FROM customer_card cc " +
            "LEFT JOIN service_item s ON cc.service_id = s.id " +
            "WHERE cc.customer_id = #{id} AND cc.deleted = 0 " +
            "ORDER BY cc.status ASC, cc.create_time DESC")
    List<Object> selectCardDetailsByCustomerId(@Param("id") Long id);
}
