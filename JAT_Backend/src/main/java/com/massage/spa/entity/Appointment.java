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
 * 预约实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("appointment")
public class Appointment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 预约ID
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
     * 预约日期
     */
    private LocalDate appointmentDate;

    /**
     * 开始时间
     */
    private LocalTime startTime;

    /**
     * 结束时间
     */
    private LocalTime endTime;

    /**
     * 实际开始时间
     */
    private LocalTime actualStartTime;

    /**
     * 实际结束时间
     */
    private LocalTime actualEndTime;

    /**
     * 取消时间
     */
    private LocalDateTime cancelTime;

    /**
     * 取消备注
     */
    private String cancelReason;

    /**
     * 状态：0-待确认，1-已确认，2-已完成，3-已取消
     */
    private Integer status;

    /**
     * 店长确认：0-未确认，1-已确认
     */
    private Integer managerConfirm;

    /**
     * 技师确认：0-未确认，1-已确认
     */
    private Integer therapistConfirm;

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
