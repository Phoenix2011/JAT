package com.massage.spa.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 邀请奖励实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("invitation_reward")
public class InvitationReward implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 奖励ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 邀请关系ID
     */
    private Long invitationId;

    /**
     * 邀请人ID
     */
    private Long inviterId;

    /**
     * 被邀请人ID
     */
    private Long inviteeId;

    /**
     * 奖励类型：1-注册奖励，2-消费返利
     */
    private Integer rewardType;

    /**
     * 代金券金额
     */
    private BigDecimal couponAmount;

    /**
     * 代金券过期日期
     */
    private LocalDate couponExpireDate;

    /**
     * 状态：0-未使用，1-已使用，2-已过期
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 删除标志：0-未删除，1-已删除
     */
    @TableLogic
    private Integer deleted;

    private Long invitationRelationId;
    private Long InviteeCustomerId;
    private Long InviterCustomerId;
    private Long orderId;
    private Double orderAmount;
    private Double commissionRate;
    private Double commissionAmount;

    private Double unfreezeAmount;




}
