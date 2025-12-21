-- 数据库初始化脚本
-- 基于最新的实体类生成

-- 服务项目表
DROP TABLE IF EXISTS `service_item`;
CREATE TABLE `service_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '服务项目ID',
  `category_id` int(11) DEFAULT NULL COMMENT '服务类别ID',
  `name` varchar(100) NOT NULL COMMENT '服务名称',
  `type` int(11) NOT NULL COMMENT '类型：1-单次服务，2-套餐卡项',
  `duration` int(11) DEFAULT NULL COMMENT '服务时长（分钟）',
  `price` double NOT NULL COMMENT '价格',
  `image` varchar(255) DEFAULT NULL COMMENT '服务图片URL',
  `description` text DEFAULT NULL COMMENT '服务描述',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '状态：0-下架，1-上架',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` int(11) NOT NULL DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
  `sort` int(11) DEFAULT 0 COMMENT '排序',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务项目表';

-- 顾客表
DROP TABLE IF EXISTS `customer`;
CREATE TABLE `customer` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '顾客ID',
  `open_id` varchar(100) DEFAULT NULL COMMENT '微信OpenID',
  `union_id` varchar(100) DEFAULT NULL COMMENT '微信UnionID',
  `name` varchar(50) DEFAULT NULL COMMENT '姓名',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号码',
  `gender` int(11) DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
  `age` int(11) DEFAULT NULL COMMENT '年龄',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `inviter_id` bigint(20) DEFAULT NULL COMMENT '邀请人ID',
  `invite_code` varchar(20) DEFAULT NULL COMMENT '个人邀请码',
  `register_time` datetime DEFAULT NULL COMMENT '注册时间',
  `last_visit_time` datetime DEFAULT NULL COMMENT '最后访问时间',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注信息',
  `deleted` int(11) NOT NULL DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
  `invitation_code` varchar(20) DEFAULT NULL COMMENT '邀请码',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_open_id` (`open_id`),
  UNIQUE KEY `uk_invite_code` (`invite_code`),
  KEY `idx_phone` (`phone`),
  KEY `idx_inviter_id` (`inviter_id`),
  KEY `idx_status` (`status`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='顾客表';

-- 订单表
DROP TABLE IF EXISTS orders;
CREATE TABLE orders (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(50) NOT NULL COMMENT '订单编号',
  `customer_id` bigint(20) NOT NULL COMMENT '顾客ID',
  `order_type` int(11) NOT NULL COMMENT '订单类型：1-单次服务，2-套餐购买',
  `total_amount` double NOT NULL COMMENT '订单总金额',
  `actual_amount` double NOT NULL COMMENT '实际支付金额',
  `payment_method` int(11) DEFAULT NULL COMMENT '支付方式：1-现金，2-微信，3-支付宝，4-其他',
  `payment_status` int(11) NOT NULL DEFAULT 0 COMMENT '支付状态：0-未支付，1-已支付，2-已退款',
  `payment_time` datetime DEFAULT NULL COMMENT '支付时间',
  `appointment_id` bigint(20) DEFAULT NULL COMMENT '关联预约ID',
  `source` int(11) DEFAULT 1 COMMENT '订单来源：1-小程序，2-线下录入',
  `status` int(11) NOT NULL DEFAULT 0 COMMENT '订单状态：0-待支付，1-已完成，2-已取消',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注信息',
  `deleted` int(11) NOT NULL DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
  `payment_amount` double DEFAULT NULL COMMENT '支付金额',
  `cancel_reason` varchar(255) DEFAULT NULL COMMENT '取消原因',
  `cancel_time` datetime DEFAULT NULL COMMENT '取消时间',
  `refund_reason` varchar(255) DEFAULT NULL COMMENT '退款原因',
  `refund_amount` double DEFAULT NULL COMMENT '退款金额',
  `refund_time` datetime DEFAULT NULL COMMENT '退款时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_customer_id` (`customer_id`),
  KEY `idx_appointment_id` (`appointment_id`),
  KEY `idx_status` (`status`),
  KEY `idx_payment_status` (`payment_status`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 订单明细表
DROP TABLE IF EXISTS `order_detail`;
CREATE TABLE `order_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `service_id` bigint(20) NOT NULL COMMENT '服务项目ID',
  `service_name` varchar(100) NOT NULL COMMENT '服务项目名称',
  `quantity` int(11) NOT NULL DEFAULT 1 COMMENT '数量',
  `price` double NOT NULL COMMENT '单价',
  `amount` double NOT NULL COMMENT '金额',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `deleted` int(11) NOT NULL DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_service_id` (`service_id`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- 预约表
DROP TABLE IF EXISTS `appointment`;
CREATE TABLE `appointment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '预约ID',
  `customer_id` bigint(20) NOT NULL COMMENT '顾客ID',
  `therapist_id` bigint(20) DEFAULT NULL COMMENT '技师ID',
  `service_id` bigint(20) NOT NULL COMMENT '服务项目ID',
  `appointment_date` date NOT NULL COMMENT '预约日期',
  `start_time` time NOT NULL COMMENT '开始时间',
  `end_time` time NOT NULL COMMENT '结束时间',
  `actual_start_time` time DEFAULT NULL COMMENT '实际开始时间',
  `actual_end_time` time DEFAULT NULL COMMENT '实际结束时间',
  `cancel_time` datetime DEFAULT NULL COMMENT '取消时间',
  `cancel_reason` varchar(255) DEFAULT NULL COMMENT '取消备注',
  `status` int(11) NOT NULL DEFAULT 0 COMMENT '状态：0-待确认，1-已确认，2-已完成，3-已取消',
  `manager_confirm` int(11) NOT NULL DEFAULT 0 COMMENT '店长确认：0-未确认，1-已确认',
  `therapist_confirm` int(11) NOT NULL DEFAULT 0 COMMENT '技师确认：0-未确认，1-已确认',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注信息',
  `deleted` int(11) NOT NULL DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_customer_id` (`customer_id`),
  KEY `idx_therapist_id` (`therapist_id`),
  KEY `idx_service_id` (`service_id`),
  KEY `idx_appointment_date` (`appointment_date`),
  KEY `idx_status` (`status`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约表';

-- 顾客卡项表
DROP TABLE IF EXISTS `customer_card`;
CREATE TABLE `customer_card` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '卡项ID',
  `customer_id` bigint(20) NOT NULL COMMENT '顾客ID',
  `service_id` bigint(20) NOT NULL COMMENT '服务项目ID',
  `order_id` bigint(20) DEFAULT NULL COMMENT '购买订单ID',
  `total_times` int(11) NOT NULL COMMENT '总次数',
  `used_times` int(11) NOT NULL DEFAULT 0 COMMENT '已使用次数',
  `remaining_times` int(11) NOT NULL COMMENT '剩余次数',
  `start_date` date NOT NULL COMMENT '生效开始日期',
  `end_date` date NOT NULL COMMENT '有效期结束日期',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '状态：0-未激活，1-正常，2-已过期，3-已用完，4-已退卡',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注信息',
  `deleted` int(11) NOT NULL DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
  `used_count` int(11) DEFAULT 0 COMMENT '已使用次数',
  `total_count` int(11) DEFAULT NULL COMMENT '总次数',
  `remaining_count` int(11) DEFAULT NULL COMMENT '剩余次数',
  PRIMARY KEY (`id`),
  KEY `idx_customer_id` (`customer_id`),
  KEY `idx_service_id` (`service_id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_status` (`status`),
  KEY `idx_end_date` (`end_date`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='顾客卡项表';

-- 卡项使用记录表
DROP TABLE IF EXISTS `card_usage_record`;
CREATE TABLE `card_usage_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `card_id` bigint(20) NOT NULL COMMENT '顾客卡项ID',
  `customer_id` bigint(20) NOT NULL COMMENT '顾客ID',
  `service_id` bigint(20) NOT NULL COMMENT '服务项目ID',
  `therapist_id` bigint(20) DEFAULT NULL COMMENT '技师ID',
  `appointment_id` bigint(20) DEFAULT NULL COMMENT '预约ID',
  `usage_date` date NOT NULL COMMENT '使用日期',
  `usage_time` time NOT NULL COMMENT '使用时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注信息',
  `deleted` int(11) NOT NULL DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
  `used_count` int(11) DEFAULT 1 COMMENT '使用次数',
  PRIMARY KEY (`id`),
  KEY `idx_card_id` (`card_id`),
  KEY `idx_customer_id` (`customer_id`),
  KEY `idx_service_id` (`service_id`),
  KEY `idx_therapist_id` (`therapist_id`),
  KEY `idx_appointment_id` (`appointment_id`),
  KEY `idx_usage_date` (`usage_date`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='卡项使用记录表';

-- 邀请关系表
DROP TABLE IF EXISTS `invitation_relation`;
CREATE TABLE `invitation_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '关系ID',
  `inviter_id` bigint(20) NOT NULL COMMENT '邀请人ID',
  `invitee_id` bigint(20) NOT NULL COMMENT '被邀请人ID',
  `invite_code` varchar(20) NOT NULL COMMENT '使用的邀请码',
  `register_time` datetime NOT NULL COMMENT '注册时间',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '状态：1-有效，0-无效',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `deleted` int(11) NOT NULL DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
  `invitee_customer_id` bigint(20) DEFAULT NULL COMMENT '被邀请人顾客ID',
  `inviter_customer_id` bigint(20) DEFAULT NULL COMMENT '邀请人顾客ID',
  `invitation_code` varchar(20) DEFAULT NULL COMMENT '邀请码',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_invitee_id` (`invitee_id`),
  KEY `idx_inviter_id` (`inviter_id`),
  KEY `idx_invite_code` (`invite_code`),
  KEY `idx_status` (`status`),
  KEY `idx_deleted` (`deleted`),
  KEY `idx_invitee_customer_id` (`invitee_customer_id`),
  KEY `idx_inviter_customer_id` (`inviter_customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邀请关系表';

-- 邀请奖励表
DROP TABLE IF EXISTS `invitation_reward`;
CREATE TABLE `invitation_reward` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '奖励ID',
  `invitation_id` bigint(20) NOT NULL COMMENT '邀请关系ID',
  `inviter_id` bigint(20) NOT NULL COMMENT '邀请人ID',
  `invitee_id` bigint(20) NOT NULL COMMENT '被邀请人ID',
  `reward_type` int(11) NOT NULL COMMENT '奖励类型：1-注册奖励，2-消费返利',
  `coupon_amount` decimal(10,2) DEFAULT NULL COMMENT '代金券金额',
  `coupon_expire_date` date DEFAULT NULL COMMENT '代金券过期日期',
  `status` int(11) NOT NULL DEFAULT 0 COMMENT '状态：0-未使用，1-已使用，2-已过期',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` int(11) NOT NULL DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
  `invitation_relation_id` bigint(20) DEFAULT NULL COMMENT '邀请关系ID',
  `invitee_customer_id` bigint(20) DEFAULT NULL COMMENT '被邀请人顾客ID',
  `inviter_customer_id` bigint(20) DEFAULT NULL COMMENT '邀请人顾客ID',
  `order_id` bigint(20) DEFAULT NULL COMMENT '订单ID',
  `order_amount` double DEFAULT NULL COMMENT '订单金额',
  `commission_rate` double DEFAULT NULL COMMENT '佣金比例',
  `commission_amount` double DEFAULT NULL COMMENT '佣金金额',
  `unfreeze_amount` double DEFAULT NULL COMMENT '解冻金额',
  PRIMARY KEY (`id`),
  KEY `idx_invitation_id` (`invitation_id`),
  KEY `idx_inviter_id` (`inviter_id`),
  KEY `idx_invitee_id` (`invitee_id`),
  KEY `idx_reward_type` (`reward_type`),
  KEY `idx_status` (`status`),
  KEY `idx_deleted` (`deleted`),
  KEY `idx_invitation_relation_id` (`invitation_relation_id`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邀请奖励表';

-- 佣金记录表
DROP TABLE IF EXISTS `commission_record`;
CREATE TABLE `commission_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `inviter_id` bigint(20) NOT NULL COMMENT '邀请人ID',
  `invitee_id` bigint(20) NOT NULL COMMENT '被邀请人ID',
  `order_id` bigint(20) DEFAULT NULL COMMENT '关联订单ID',
  `order_amount` decimal(10,2) DEFAULT NULL COMMENT '订单金额',
  `commission_rate` decimal(10,2) DEFAULT NULL COMMENT '佣金比例',
  `commission_amount` decimal(10,2) NOT NULL COMMENT '佣金金额',
  `frozen_amount` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '冻结金额',
  `available_amount` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '可用金额',
  `status` int(11) NOT NULL DEFAULT 0 COMMENT '状态：0-冻结中，1-部分解冻，2-完全解冻',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注信息',
  `deleted` int(11) NOT NULL DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
  `customer_id` bigint(20) DEFAULT NULL COMMENT '顾客ID',
  `invitation_reward_id` bigint(20) DEFAULT NULL COMMENT '邀请奖励ID',
  `amount` double DEFAULT NULL COMMENT '金额',
  `type` int(11) DEFAULT NULL COMMENT '类型',
  `withdrawal_id` bigint(20) DEFAULT NULL COMMENT '提现ID',
  PRIMARY KEY (`id`),
  KEY `idx_inviter_id` (`inviter_id`),
  KEY `idx_invitee_id` (`invitee_id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_deleted` (`deleted`),
  KEY `idx_customer_id` (`customer_id`),
  KEY `idx_invitation_reward_id` (`invitation_reward_id`),
  KEY `idx_withdrawal_id` (`withdrawal_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='佣金记录表';

-- 佣金解冻记录表
DROP TABLE IF EXISTS `commission_unfreeze_record`;
CREATE TABLE `commission_unfreeze_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `commission_id` bigint(20) NOT NULL COMMENT '佣金记录ID',
  `inviter_id` bigint(20) NOT NULL COMMENT '邀请人ID',
  `invitee_id` bigint(20) NOT NULL COMMENT '被邀请人ID',
  `card_usage_id` bigint(20) DEFAULT NULL COMMENT '卡项使用记录ID',
  `unfreeze_amount` decimal(10,2) NOT NULL COMMENT '解冻金额',
  `unfreeze_time` datetime NOT NULL COMMENT '解冻时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注信息',
  `deleted` int(11) NOT NULL DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
  `customer_id` bigint(20) DEFAULT NULL COMMENT '顾客ID',
  `invitation_reward_id` bigint(20) DEFAULT NULL COMMENT '邀请奖励ID',
  `order_id` bigint(20) DEFAULT NULL COMMENT '订单ID',
  `amount` double DEFAULT NULL COMMENT '金额',
  `unfreeze_ratio` double DEFAULT NULL COMMENT '解冻比例',
  PRIMARY KEY (`id`),
  KEY `idx_commission_id` (`commission_id`),
  KEY `idx_inviter_id` (`inviter_id`),
  KEY `idx_invitee_id` (`invitee_id`),
  KEY `idx_card_usage_id` (`card_usage_id`),
  KEY `idx_unfreeze_time` (`unfreeze_time`),
  KEY `idx_deleted` (`deleted`),
  KEY `idx_customer_id` (`customer_id`),
  KEY `idx_invitation_reward_id` (`invitation_reward_id`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='佣金解冻记录表';

-- 佣金提现记录表
DROP TABLE IF EXISTS `commission_withdrawal`;
CREATE TABLE `commission_withdrawal` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `customer_id` bigint(20) NOT NULL COMMENT '顾客ID',
  `withdrawal_amount` decimal(10,2) NOT NULL COMMENT '提现金额',
  `actual_amount` double DEFAULT NULL COMMENT '实际到账金额',
  `discount_rate` decimal(10,2) DEFAULT NULL COMMENT '折扣率',
  `status` int(11) NOT NULL DEFAULT 0 COMMENT '状态：0-申请中，1-已审核，2-已打款，3-已拒绝',
  `apply_time` datetime NOT NULL COMMENT '申请时间',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `audit_user_id` bigint(20) DEFAULT NULL COMMENT '审核人ID',
  `payment_time` datetime DEFAULT NULL COMMENT '打款时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注信息',
  `deleted` int(11) NOT NULL DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
  `amount` double DEFAULT NULL COMMENT '金额',
  `discount_ratio` double DEFAULT NULL COMMENT '折扣比例',
  PRIMARY KEY (`id`),
  KEY `idx_customer_id` (`customer_id`),
  KEY `idx_status` (`status`),
  KEY `idx_apply_time` (`apply_time`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='佣金提现记录表';
