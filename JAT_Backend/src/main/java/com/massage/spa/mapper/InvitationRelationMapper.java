package com.massage.spa.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.massage.spa.entity.InvitationRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 邀请关系Mapper接口
 */
@Mapper
public interface InvitationRelationMapper extends BaseMapper<InvitationRelation> {
    
    /**
     * 统计邀请人的被邀请人数量
     * @param inviterCustomerId 邀请人顾客ID
     * @return 被邀请人数量
     */
    @Select("SELECT COUNT(*) FROM invitation_relation " +
            "WHERE inviter_customer_id = #{inviterCustomerId} AND deleted = 0")
    Integer countInviteesByInviterId(@Param("inviterCustomerId") Long inviterCustomerId);
    
    /**
     * 查询邀请人的被邀请人列表
     * @param inviterCustomerId 邀请人顾客ID
     * @return 被邀请人列表
     */
    @Select("SELECT ir.*, c.name as invitee_name, c.phone as invitee_phone, c.avatar as invitee_avatar, " +
            "c.register_time as invitee_register_time " +
            "FROM invitation_relation ir " +
            "LEFT JOIN customer c ON ir.invitee_customer_id = c.id " +
            "WHERE ir.inviter_customer_id = #{inviterCustomerId} AND ir.deleted = 0 " +
            "ORDER BY ir.create_time DESC")
    List<Map<String, Object>> selectInviteesByInviterId(@Param("inviterCustomerId") Long inviterCustomerId);
}
