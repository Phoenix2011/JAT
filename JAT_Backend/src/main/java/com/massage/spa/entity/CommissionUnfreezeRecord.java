package com.massage.spa.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 佣金解冻记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("commission_unfreeze_record")
public class CommissionUnfreezeRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 佣金记录ID
     */
    private Long commissionId;

    /**
     * 邀请人ID
     */
    private Long inviterId;

    /**
     * 被邀请人ID
     */
    private Long inviteeId;

    /**
     * 卡项使用记录ID
     */
    private Long cardUsageId;

    /**
     * 解冻金额
     */
    private BigDecimal unfreezeAmount;

    /**
     * 解冻时间
     */
    private LocalDateTime unfreezeTime;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 备注信息
     */
    private String remark;

    /**
     * 删除标志：0-未删除，1-已删除
     */
    @TableLogic
    private Integer deleted;

    private Long customerId;
    private Long invitationRewardId;
    private Long orderId;
    private Double amount;
    private Double unfreezeRatio;

}
