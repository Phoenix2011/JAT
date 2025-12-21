package com.massage.spa.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.massage.spa.entity.CommissionRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 佣金记录Mapper接口
 */
@Mapper
public interface CommissionRecordMapper extends BaseMapper<CommissionRecord> {
    
    /**
     * 查询顾客的佣金记录
     * @param customerId 顾客ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 佣金记录列表
     */
    @Select("SELECT cr.*, c.name as invitee_name, o.order_no as order_no " +
            "FROM commission_record cr " +
            "LEFT JOIN invitation_reward ir ON cr.invitation_reward_id = ir.id " +
            "LEFT JOIN customer c ON ir.invitee_customer_id = c.id " +
            "LEFT JOIN orders o ON cr.order_id = o.id " +
            "WHERE cr.customer_id = #{customerId} AND cr.deleted = 0 " +
            "ORDER BY cr.create_time DESC " +
            "LIMIT #{offset}, #{limit}")
    List<Map<String, Object>> selectCommissionRecordsByCustomerId(@Param("customerId") Long customerId,
                                                                @Param("offset") Long offset,
                                                                @Param("limit") Long limit);
    
    /**
     * 统计顾客的佣金总额
     * @param customerId 顾客ID
     * @return 佣金总额
     */
    @Select("SELECT SUM(amount) FROM commission_record " +
            "WHERE customer_id = #{customerId} AND deleted = 0")
    Double sumCommissionByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * 统计顾客的可用佣金
     * @param customerId 顾客ID
     * @return 可用佣金
     */
    @Select("SELECT SUM(amount) FROM commission_record " +
            "WHERE customer_id = #{customerId} AND type = 1 AND deleted = 0")
    Double sumAvailableCommissionByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * 统计顾客的冻结佣金
     * @param customerId 顾客ID
     * @return 冻结佣金
     */
    @Select("SELECT SUM(amount) FROM commission_record " +
            "WHERE customer_id = #{customerId} AND type = 0 AND deleted = 0")
    Double sumFrozenCommissionByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * 统计顾客的已提现佣金
     * @param customerId 顾客ID
     * @return 已提现佣金
     */
    @Select("SELECT SUM(ABS(amount)) FROM commission_record " +
            "WHERE customer_id = #{customerId} AND type = 2 AND deleted = 0")
    Double sumWithdrawnCommissionByCustomerId(@Param("customerId") Long customerId);
}
