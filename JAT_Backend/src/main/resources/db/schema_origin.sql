-- 创建数据库
CREATE DATABASE IF NOT EXISTS jat DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE jat;

-- 系统用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    role_id INT(11) NOT NULL COMMENT '角色ID',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    phone VARCHAR(20) NOT NULL COMMENT '手机号码',
    gender TINYINT(1) DEFAULT NULL COMMENT '性别：0-未知，1-男，2-女',
    age INT(11) DEFAULT NULL COMMENT '年龄',
    avatar VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    status TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    last_login_time DATETIME DEFAULT NULL COMMENT '最后登录时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注信息',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='系统用户表';

-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id INT(11) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_key VARCHAR(50) NOT NULL COMMENT '角色标识',
    role_sort INT(11) NOT NULL COMMENT '角色排序',
    status TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注信息',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_key (role_key)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='角色表';

-- 权限表
CREATE TABLE IF NOT EXISTS sys_permission (
    id INT(11) NOT NULL AUTO_INCREMENT COMMENT '权限ID',
    parent_id INT(11) DEFAULT NULL COMMENT '父权限ID',
    name VARCHAR(50) NOT NULL COMMENT '权限名称',
    type TINYINT(1) NOT NULL COMMENT '类型：1-菜单，2-按钮',
    permission_key VARCHAR(100) DEFAULT NULL COMMENT '权限标识',
    path VARCHAR(200) DEFAULT NULL COMMENT '路由地址',
    component VARCHAR(255) DEFAULT NULL COMMENT '组件路径',
    icon VARCHAR(100) DEFAULT NULL COMMENT '图标',
    sort INT(11) NOT NULL COMMENT '排序',
    status TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
    PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='权限表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS sys_role_permission (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    role_id INT(11) NOT NULL COMMENT '角色ID',
    permission_id INT(11) NOT NULL COMMENT '权限ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_permission (role_id, permission_id)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='角色权限关联表';

-- 顾客表
CREATE TABLE IF NOT EXISTS customer (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '顾客ID',
    open_id VARCHAR(50) DEFAULT NULL COMMENT '微信OpenID',
    union_id VARCHAR(50) DEFAULT NULL COMMENT '微信UnionID',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    phone VARCHAR(20) NOT NULL COMMENT '手机号码',
    gender TINYINT(1) DEFAULT NULL COMMENT '性别：0-未知，1-男，2-女',
    age INT(11) DEFAULT NULL COMMENT '年龄',
    avatar VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    inviter_id BIGINT(20) DEFAULT NULL COMMENT '邀请人ID',
    invite_code VARCHAR(20) DEFAULT NULL COMMENT '个人邀请码',
    register_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    last_visit_time DATETIME DEFAULT NULL COMMENT '最后访问时间',
    status TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注信息',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_phone (phone),
    UNIQUE KEY uk_invite_code (invite_code),
    KEY idx_inviter_id (inviter_id)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='顾客表';

-- 技师表
CREATE TABLE IF NOT EXISTS therapist (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '技师ID',
    user_id BIGINT(20) NOT NULL COMMENT '关联系统用户ID',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    phone VARCHAR(20) NOT NULL COMMENT '手机号码',
    gender TINYINT(1) DEFAULT NULL COMMENT '性别：0-未知，1-男，2-女',
    age INT(11) DEFAULT NULL COMMENT '年龄',
    avatar VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    level INT(11) NOT NULL DEFAULT 1 COMMENT '技师等级',
    introduction TEXT DEFAULT NULL COMMENT '技师介绍',
    status TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注信息',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_id (user_id),
    UNIQUE KEY uk_phone (phone)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='技师表';

-- 服务类别表
CREATE TABLE IF NOT EXISTS service_category (
    id INT(11) NOT NULL AUTO_INCREMENT COMMENT '类别ID',
    name VARCHAR(50) NOT NULL COMMENT '类别名称',
    sort INT(11) NOT NULL DEFAULT 0 COMMENT '排序',
    status TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
    PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='服务类别表';

-- 服务项目表
CREATE TABLE IF NOT EXISTS service_item (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '服务项目ID',
    category_id INT(11) NOT NULL COMMENT '服务类别ID',
    name VARCHAR(100) NOT NULL COMMENT '服务名称',
    type TINYINT(1) NOT NULL COMMENT '类型：1-单次服务，2-套餐卡项',
    duration INT(11) NOT NULL COMMENT '服务时长（分钟）',
    price DECIMAL(10,2) NOT NULL COMMENT '价格',
    image VARCHAR(255) DEFAULT NULL COMMENT '服务图片URL',
    description TEXT DEFAULT NULL COMMENT '服务描述',
    status TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：0-下架，1-上架',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
    PRIMARY KEY (id),
    KEY idx_category_id (category_id)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='服务项目表';

-- 套餐卡项表
CREATE TABLE IF NOT EXISTS service_package (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '套餐ID',
    service_id BIGINT(20) NOT NULL COMMENT '关联服务项目ID',
    total_times INT(11) NOT NULL COMMENT '总次数',
    valid_days INT(11) NOT NULL COMMENT '有效期（天）',
    discount_rate DECIMAL(5,2) DEFAULT NULL COMMENT '折扣率',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_service_id (service_id)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='套餐卡项表';

-- 提成规则表
CREATE TABLE IF NOT EXISTS commission_rule (
    id INT(11) NOT NULL AUTO_INCREMENT COMMENT '规则ID',
    service_id BIGINT(20) DEFAULT NULL COMMENT '服务项目ID，空表示适用所有服务',
    therapist_level INT(11) DEFAULT NULL COMMENT '技师等级，空表示适用所有等级',
    commission_rate DECIMAL(5,2) NOT NULL COMMENT '提成比例',
    min_amount DECIMAL(10,2) DEFAULT NULL COMMENT '最小金额（阶梯起点）',
    max_amount DECIMAL(10,2) DEFAULT NULL COMMENT '最大金额（阶梯终点）',
    start_date DATE NOT NULL COMMENT '生效开始日期',
    end_date DATE DEFAULT NULL COMMENT '生效结束日期，空表示永久有效',
    status TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注信息',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
    PRIMARY KEY (id),
    KEY idx_service_id (service_id),
    KEY idx_therapist_level (therapist_level)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='提成规则表';

-- 预约表
CREATE TABLE IF NOT EXISTS appointment (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '预约ID',
    customer_id BIGINT(20) NOT NULL COMMENT '顾客ID',
    therapist_id BIGINT(20) NOT NULL COMMENT '技师ID',
    service_id BIGINT(20) NOT NULL COMMENT '服务项目ID',
    appointment_date DATE NOT NULL COMMENT '预约日期',
    start_time TIME NOT NULL COMMENT '开始时间',
    end_time TIME NOT NULL COMMENT '结束时间',
    actual_start_time TIME DEFAULT NULL COMMENT '实际开始时间',
    actual_end_time TIME DEFAULT NULL COMMENT '实际结束时间',
    status TINYINT(1) NOT NULL DEFAULT 0 COMMENT '状态：0-待确认，1-已确认，2-已完成，3-已取消',
    manager_confirm TINYINT(1) NOT NULL DEFAULT 0 COMMENT '店长确认：0-未确认，1-已确认',
    therapist_confirm TINYINT(1) NOT NULL DEFAULT 0 COMMENT '技师确认：0-未确认，1-已确认',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注信息',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
    PRIMARY KEY (id),
    KEY idx_customer_id (customer_id),
    KEY idx_therapist_id (therapist_id),
    KEY idx_service_id (service_id),
    KEY idx_appointment_date (appointment_date)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='预约表';

-- 订单表
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    order_no VARCHAR(50) NOT NULL COMMENT '订单编号',
    customer_id BIGINT(20) NOT NULL COMMENT '顾客ID',
    order_type TINYINT(1) NOT NULL COMMENT '订单类型：1-单次服务，2-套餐购买',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    actual_amount DECIMAL(10,2) NOT NULL COMMENT '实际支付金额',
    payment_method TINYINT(1) NOT NULL COMMENT '支付方式：1-现金，2-微信，3-支付宝，4-其他',
    payment_status TINYINT(1) NOT NULL DEFAULT 0 COMMENT '支付状态：0-未支付，1-已支付，2-已退款',
    payment_time DATETIME DEFAULT NULL COMMENT '支付时间',
    appointment_id BIGINT(20) DEFAULT NULL COMMENT '关联预约ID',
    source TINYINT(1) NOT NULL COMMENT '订单来源：1-小程序，2-线下录入',
    status TINYINT(1) NOT NULL DEFAULT 0 COMMENT '订单状态：0-待支付，1-已完成，2-已取消',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注信息',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_customer_id (customer_id),
    KEY idx_appointment_id (appointment_id)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='订单表';

-- 订单明细表
CREATE TABLE IF NOT EXISTS order_detail (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '明细ID',
    order_id BIGINT(20) NOT NULL COMMENT '订单ID',
    service_id BIGINT(20) NOT NULL COMMENT '服务项目ID',
    service_name VARCHAR(100) NOT NULL COMMENT '服务项目名称',
    quantity INT(11) NOT NULL COMMENT '数量',
    price DECIMAL(10,2) NOT NULL COMMENT '单价',
    amount DECIMAL(10,2) NOT NULL COMMENT '金额',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
    PRIMARY KEY (id),
    KEY idx_order_id (order_id),
    KEY idx_service_id (service_id)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='订单明细表';

-- 顾客卡项表
CREATE TABLE IF NOT EXISTS customer_card (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '卡项ID',
    customer_id BIGINT(20) NOT NULL COMMENT '顾客ID',
    service_id BIGINT(20) NOT NULL COMMENT '服务项目ID',
    order_id BIGINT(20) NOT NULL COMMENT '购买订单ID',
    total_times INT(11) NOT NULL COMMENT '总次数',
    used_times INT(11) NOT NULL DEFAULT 0 COMMENT '已使用次数',
    remaining_times INT(11) NOT NULL COMMENT '剩余次数',
    start_date DATE NOT NULL COMMENT '生效开始日期',
    end_date DATE NOT NULL COMMENT '有效期结束日期',
    status TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：0-未激活，1-正常，2-已过期，3-已用完，4-已退卡',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注信息',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
    PRIMARY KEY (id),
    KEY idx_customer_id (customer_id),
    KEY idx_service_id (service_id),
    KEY idx_order_id (order_id)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='顾客卡项表';

-- 卡项使用记录表
CREATE TABLE IF NOT EXISTS card_usage_record (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    card_id BIGINT(20) NOT NULL COMMENT '顾客卡项ID',
    customer_id BIGINT(20) NOT NULL COMMENT '顾客ID',
    service_id BIGINT(20) NOT NULL COMMENT '服务项目ID',
    therapist_id BIGINT(20) DEFAULT NULL COMMENT '技师ID',
    appointment_id BIGINT(20) DEFAULT NULL COMMENT '预约ID',
    usage_date DATE NOT NULL COMMENT '使用日期',
    usage_time TIME NOT NULL COMMENT '使用时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注信息',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
    PRIMARY KEY (id),
    KEY idx_card_id (card_id),
    KEY idx_customer_id (customer_id),
    KEY idx_service_id (service_id),
    KEY idx_therapist_id (therapist_id),
    KEY idx_appointment_id (appointment_id)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='卡项使用记录表';

-- 服务记录表
CREATE TABLE IF NOT EXISTS service_record (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    therapist_id BIGINT(20) NOT NULL COMMENT '技师ID',
    customer_id BIGINT(20) DEFAULT NULL COMMENT '顾客ID',
    service_id BIGINT(20) NOT NULL COMMENT '服务项目ID',
    appointment_id BIGINT(20) DEFAULT NULL COMMENT '预约ID',
    order_id BIGINT(20) DEFAULT NULL COMMENT '订单ID',
    service_date DATE NOT NULL COMMENT '服务日期',
    start_time TIME NOT NULL COMMENT '开始时间',
    end_time TIME NOT NULL COMMENT '结束时间',
    duration INT(11) NOT NULL COMMENT '服务时长（分钟）',
    amount DECIMAL(10,2) NOT NULL COMMENT '实收金额',
    commission_rate DECIMAL(5,2) NOT NULL COMMENT '提成比例',
    commission_amount DECIMAL(10,2) NOT NULL COMMENT '提成金额',
    status TINYINT(1) NOT NULL DEFAULT 0 COMMENT '状态：0-待确认，1-已确认',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注信息',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
    PRIMARY KEY (id),
    KEY idx_therapist_id (therapist_id),
    KEY idx_customer_id (customer_id),
    KEY idx_service_id (service_id),
    KEY idx_appointment_id (appointment_id),
    KEY idx_order_id (order_id),
    KEY idx_service_date (service_date)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='服务记录表';

-- 邀请关系表
CREATE TABLE IF NOT EXISTS invitation_relation (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '关系ID',
    inviter_id BIGINT(20) NOT NULL COMMENT '邀请人ID',
    invitee_id BIGINT(20) NOT NULL COMMENT '被邀请人ID',
    invite_code VARCHAR(20) NOT NULL COMMENT '使用的邀请码',
    register_time DATETIME NOT NULL COMMENT '注册时间',
    status TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：1-有效，0-无效',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_invitee_id (invitee_id),
    KEY idx_inviter_id (inviter_id),
    KEY idx_invite_code (invite_code)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='邀请关系表';

-- 邀请奖励表
CREATE TABLE IF NOT EXISTS invitation_reward (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '奖励ID',
    invitation_id BIGINT(20) NOT NULL COMMENT '邀请关系ID',
    inviter_id BIGINT(20) NOT NULL COMMENT '邀请人ID',
    invitee_id BIGINT(20) NOT NULL COMMENT '被邀请人ID',
    reward_type TINYINT(1) NOT NULL COMMENT '奖励类型：1-注册奖励，2-消费返利',
    coupon_amount DECIMAL(10,2) DEFAULT NULL COMMENT '代金券金额',
    coupon_expire_date DATE DEFAULT NULL COMMENT '代金券过期日期',
    status TINYINT(1) NOT NULL DEFAULT 0 COMMENT '状态：0-未使用，1-已使用，2-已过期',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
    PRIMARY KEY (id),
    KEY idx_invitation_id (invitation_id),
    KEY idx_inviter_id (inviter_id),
    KEY idx_invitee_id (invitee_id)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='邀请奖励表';

-- 佣金记录表
CREATE TABLE IF NOT EXISTS commission_record (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    inviter_id BIGINT(20) NOT NULL COMMENT '邀请人ID',
    invitee_id BIGINT(20) NOT NULL COMMENT '被邀请人ID',
    order_id BIGINT(20) NOT NULL COMMENT '关联订单ID',
    order_amount DECIMAL(10,2) NOT NULL COMMENT '订单金额',
    commission_rate DECIMAL(5,2) NOT NULL COMMENT '佣金比例',
    commission_amount DECIMAL(10,2) NOT NULL COMMENT '佣金金额',
    frozen_amount DECIMAL(10,2) NOT NULL COMMENT '冻结金额',
    available_amount DECIMAL(10,2) NOT NULL COMMENT '可用金额',
    status TINYINT(1) NOT NULL DEFAULT 0 COMMENT '状态：0-冻结中，1-部分解冻，2-完全解冻',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注信息',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
    PRIMARY KEY (id),
    KEY idx_inviter_id (inviter_id),
    KEY idx_invitee_id (invitee_id),
    KEY idx_order_id (order_id)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='佣金记录表';

-- 佣金解冻记录表
CREATE TABLE IF NOT EXISTS commission_unfreeze_record (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    commission_id BIGINT(20) NOT NULL COMMENT '佣金记录ID',
    inviter_id BIGINT(20) NOT NULL COMMENT '邀请人ID',
    invitee_id BIGINT(20) NOT NULL COMMENT '被邀请人ID',
    card_usage_id BIGINT(20) DEFAULT NULL COMMENT '卡项使用记录ID',
    unfreeze_amount DECIMAL(10,2) NOT NULL COMMENT '解冻金额',
    unfreeze_time DATETIME NOT NULL COMMENT '解冻时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注信息',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
    PRIMARY KEY (id),
    KEY idx_commission_id (commission_id),
    KEY idx_inviter_id (inviter_id),
    KEY idx_invitee_id (invitee_id),
    KEY idx_card_usage_id (card_usage_id)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='佣金解冻记录表';

-- 佣金提现记录表
CREATE TABLE IF NOT EXISTS commission_withdrawal (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    customer_id BIGINT(20) NOT NULL COMMENT '顾客ID',
    withdrawal_amount DECIMAL(10,2) NOT NULL COMMENT '提现金额',
    actual_amount DECIMAL(10,2) NOT NULL COMMENT '实际到账金额',
    discount_rate DECIMAL(5,2) NOT NULL COMMENT '折扣率',
    status TINYINT(1) NOT NULL DEFAULT 0 COMMENT '状态：0-申请中，1-已审核，2-已打款，3-已拒绝',
    apply_time DATETIME NOT NULL COMMENT '申请时间',
    audit_time DATETIME DEFAULT NULL COMMENT '审核时间',
    audit_user_id BIGINT(20) DEFAULT NULL COMMENT '审核人ID',
    payment_time DATETIME DEFAULT NULL COMMENT '打款时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注信息',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
    PRIMARY KEY (id),
    KEY idx_customer_id (customer_id),
    KEY idx_audit_user_id (audit_user_id)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='佣金提现记录表';

-- 系统配置表
CREATE TABLE IF NOT EXISTS sys_config (
    id INT(11) NOT NULL AUTO_INCREMENT COMMENT '配置ID',
    config_key VARCHAR(100) NOT NULL COMMENT '配置键',
    config_value VARCHAR(500) NOT NULL COMMENT '配置值',
    config_type VARCHAR(50) NOT NULL COMMENT '配置类型',
    description VARCHAR(255) DEFAULT NULL COMMENT '配置描述',
    status TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注信息',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_config_key (config_key)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='系统配置表';

-- 图片资源表
CREATE TABLE IF NOT EXISTS image_resource (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '图片ID',
    image_type TINYINT(1) NOT NULL COMMENT '图片类型：1-轮播图，2-服务项目图，3-技师照片，4-门店环境图',
    image_url VARCHAR(255) NOT NULL COMMENT '图片URL',
    title VARCHAR(100) DEFAULT NULL COMMENT '图片标题',
    sort INT(11) NOT NULL DEFAULT 0 COMMENT '排序',
    link_url VARCHAR(255) DEFAULT NULL COMMENT '链接URL',
    status TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注信息',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
    PRIMARY KEY (id),
    KEY idx_image_type (image_type)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='图片资源表';

-- 操作日志表
CREATE TABLE IF NOT EXISTS sys_operation_log (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    user_id BIGINT(20) DEFAULT NULL COMMENT '操作用户ID',
    module VARCHAR(50) DEFAULT NULL COMMENT '操作模块',
    operation VARCHAR(50) DEFAULT NULL COMMENT '操作类型',
    method VARCHAR(100) DEFAULT NULL COMMENT '方法名',
    params TEXT DEFAULT NULL COMMENT '请求参数',
    ip VARCHAR(50) DEFAULT NULL COMMENT '操作IP',
    status TINYINT(1) NOT NULL COMMENT '操作状态：0-失败，1-成功',
    error_msg VARCHAR(2000) DEFAULT NULL COMMENT '错误消息',
    operation_time DATETIME NOT NULL COMMENT '操作时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_operation_time (operation_time)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='操作日志表';

-- 初始化数据
-- 角色数据
INSERT INTO sys_role (id, role_name, role_key, role_sort, status, remark) VALUES 
(1, '超级管理员', 'admin', 1, 1, '超级管理员'),
(2, '店长', 'manager', 2, 1, '店长'),
(3, '技师', 'therapist', 3, 1, '技师');

-- 初始用户数据
INSERT INTO sys_user (id, username, password, role_id, real_name, phone, gender, status, remark) VALUES 
(1, 'admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 1, '超级管理员', '13800000000', 1, 1, '超级管理员'),
(2, 'manager', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 2, '店长', '13800000001', 1, 1, '店长'),
(3, 'therapist', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 3, '技师', '13800000002', 2, 1, '技师');

-- 系统配置数据
INSERT INTO sys_config (config_key, config_value, config_type, description, status) VALUES 
('sys.name', '推拿艾灸馆管理系统', 'system', '系统名称', 1),
('sys.version', '1.0.0', 'system', '系统版本', 1),
('commission.rate', '0.1', 'business', '默认佣金比例', 1),
('withdrawal.discount', '0.8', 'business', '提现折扣率', 1),
('shop.name', '健康推拿艾灸馆', 'business', '店铺名称', 1),
('shop.address', '北京市朝阳区建国路88号', 'business', '店铺地址', 1),
('shop.phone', '010-88888888', 'business', '店铺电话', 1),
('shop.business.hours', '09:00-21:00', 'business', '营业时间', 1);
