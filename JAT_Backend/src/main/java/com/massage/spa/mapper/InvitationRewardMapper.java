package com.massage.spa.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.massage.spa.entity.InvitationReward;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 邀请奖励Mapper接口
 */
@Mapper
public interface InvitationRewardMapper extends BaseMapper<InvitationReward> {
    
    /**
     * 查询邀请人的奖励列表
     * @param inviterCustomerId 邀请人顾客ID
     * @return 奖励列表
     */
    @Select("SELECT ir.*, c.name as invitee_name, o.order_no as order_no " +
            "FROM invitation_reward ir " +
            "LEFT JOIN customer c ON ir.invitee_customer_id = c.id " +
            "LEFT JOIN orders o ON ir.order_id = o.id " +
            "WHERE ir.inviter_customer_id = #{inviterCustomerId} AND ir.deleted = 0 " +
            "ORDER BY ir.create_time DESC")
    List<Map<String, Object>> selectRewardsByInviterId(@Param("inviterCustomerId") Long inviterCustomerId);
    
    /**
     * 统计邀请人的奖励总额
     * @param inviterCustomerId 邀请人顾客ID
     * @return 奖励总额
     */
    @Select("SELECT SUM(commission_amount) FROM invitation_reward " +
            "WHERE inviter_customer_id = #{inviterCustomerId} AND deleted = 0")
    Double sumRewardsByInviterId(@Param("inviterCustomerId") Long inviterCustomerId);
    
    /**
     * 统计邀请人的已解冻奖励总额
     * @param inviterCustomerId 邀请人顾客ID
     * @return 已解冻奖励总额
     */
    @Select("SELECT SUM(unfreeze_amount) FROM invitation_reward " +
            "WHERE inviter_customer_id = #{inviterCustomerId} AND deleted = 0")
    Double sumUnfrozenRewardsByInviterId(@Param("inviterCustomerId") Long inviterCustomerId);
}
