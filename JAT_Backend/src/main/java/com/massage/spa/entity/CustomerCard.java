package com.massage.spa.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 顾客卡项实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("customer_card")
public class CustomerCard implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 卡项ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 顾客ID
     */
    private Long customerId;

    /**
     * 服务项目ID
     */
    private Long serviceId;

    /**
     * 购买订单ID
     */
    private Long orderId;

    private Integer totalCount;
    private Integer usedCount;
    private Integer remainingCount;
    private Double amount;
    private Double remainingAmount;

    /**
     * 生效开始日期
     */
    private LocalDate startDate;

    /**
     * 有效期结束日期
     */
    private LocalDate endDate;

    /**
     * 状态：0-未激活，1-正常，2-已过期，3-已用完，4-已退卡
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
     * 备注信息
     */
    private String remark;

    /**
     * 删除标志：0-未删除，1-已删除
     */
    @TableLogic
    private Integer deleted;

}
