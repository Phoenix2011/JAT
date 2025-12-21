package com.massage.spa.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 卡项使用记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("card_usage_record")
public class CardUsageRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 顾客卡项ID
     */
    private Long cardId;

    /**
     * 顾客ID
     */
    private Long customerId;

    /**
     * 服务项目ID
     */
    private Long serviceId;

    /**
     * 技师ID
     */
    private Long therapistId;

    /**
     * 预约ID
     */
    private Long appointmentId;

    private Integer usedCount;

    /**
     * 使用日期
     */
    private LocalDate usageDate;

    /**
     * 使用时间
     */
    private LocalTime usageTime;

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


}
