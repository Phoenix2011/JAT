package com.massage.spa.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.lang.Double;
import java.time.LocalDateTime;

/**
 * 订单实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("order")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 顾客ID
     */
    private Long customerId;

    /**
     * 订单类型：1-单次服务，2-套餐购买
     */
    private Integer orderType;

    /**
     * 套餐ID（如果是套餐购买）
     */
    private Long cardId;

    /**
     * 订单金额
     */
    private Double paymentAmount;

    /**
     * 支付方式：1-现金，2-微信，3-支付宝，4-其他
     */
    private Integer paymentMethod;

    /**
     * 支付状态：0-未支付，1-已支付，2-已退款
     */
    private Integer paymentStatus;

    /**
     * 支付时间
     */
    private LocalDateTime paymentTime;

    /**
     * 关联预约ID
     */
    private Long appointmentId;

    /**
     * 订单来源：1-小程序，2-线下录入
     */
    private Integer source;

    /**
     * 订单状态：0-待支付，1-已完成，2-已取消
     */
    private Integer status;

    private String cancelReason;
    private LocalDateTime cancelTime;
    private String refundReason;
    private Double refundAmount;
    private LocalDateTime refundTime;
    /**
     * 备注信息
     */
    private String remark;

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
}
