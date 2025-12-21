package com.massage.spa.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 服务记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("service_record")
public class ServiceRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 顾客ID
     */
    private Long customerId;

    /**
     * 技师ID
     */
    private Long therapistId;

    /**
     * 服务项目ID
     */
    private Long serviceId;

    /**
     * 预约ID
     */
    private Long appointmentId;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 服务日期
     */
    private LocalDate serviceDate;

    /**
     * 开始时间
     */
    private LocalTime startTime;

    /**
     * 结束时间
     */
    private LocalTime endTime;

    /**
     * 服务时长（分钟）
     */
    private Integer duration;

    /**
     * 实收金额
     */
    private BigDecimal amount;

    /**
     * 提成比例
     */
    private BigDecimal commissionRate;

    /**
     * 提成金额
     */
    private BigDecimal commissionAmount;

    /**
     * 状态：0-待确认，1-已确认
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
