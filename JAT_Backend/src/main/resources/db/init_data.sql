-- 初始化数据SQL脚本
-- 基于Controller和Schema文件生成
-- 包含权限、角色、用户、菜单及必要业务数据

-- 清空相关表数据
DELETE FROM sys_role_permission;
DELETE FROM sys_permission;
DELETE FROM sys_role;
DELETE FROM sys_user;
DELETE FROM service_category;

-- 初始化角色表
INSERT INTO sys_role (id, role_name, role_key, role_sort, status, create_time, update_time, remark, deleted) VALUES
(1, '超级管理员', 'admin', 1, 1, NOW(), NOW(), '超级管理员，拥有所有权限', 0),
(2, '店长', 'manager', 2, 1, NOW(), NOW(), '店长，拥有大部分管理权限', 0),
(3, '技师', 'therapist', 3, 1, NOW(), NOW(), '技师，拥有有限的操作权限', 0);

-- 初始化系统用户表
INSERT INTO sys_user (id, username, password, role_id, real_name, phone, gender, age, avatar, status, last_login_time, create_time, update_time, remark, deleted) VALUES
(1, 'admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 1, '系统管理员', '13800000000', 1, 30, NULL, 1, NULL, NOW(), NOW(), '超级管理员账号', 0),
(2, 'manager', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 2, '店长', '13800000001', 1, 35, NULL, 1, NULL, NOW(), NOW(), '店长账号', 0),
(3, 'therapist', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 3, '技师', '13800000002', 2, 28, NULL, 1, NULL, NOW(), NOW(), '技师账号', 0);

-- 初始化权限表（菜单和按钮）
-- 一级菜单
INSERT INTO sys_permission (id, parent_id, name, type, permission_key, path, component, icon, sort, status, create_time, update_time, deleted) VALUES
(1, 0, '系统管理', 1, NULL, '/system', 'Layout', 'system', 1, 1, NOW(), NOW(), 0),
(2, 0, '顾客管理', 1, NULL, '/customer', 'Layout', 'user', 2, 1, NOW(), NOW(), 0),
(3, 0, '技师管理', 1, NULL, '/therapist', 'Layout', 'people', 3, 1, NOW(), NOW(), 0),
(4, 0, '服务管理', 1, NULL, '/service', 'Layout', 'service', 4, 1, NOW(), NOW(), 0),
(5, 0, '预约管理', 1, NULL, '/appointment', 'Layout', 'calendar', 5, 1, NOW(), NOW(), 0),
(6, 0, '订单管理', 1, NULL, '/order', 'Layout', 'shopping', 6, 1, NOW(), NOW(), 0),
(7, 0, '卡项管理', 1, NULL, '/card', 'Layout', 'card', 7, 1, NOW(), NOW(), 0),
(8, 0, '邀请返利', 1, NULL, '/invitation', 'Layout', 'share', 8, 1, NOW(), NOW(), 0),
(9, 0, '统计分析', 1, NULL, '/statistics', 'Layout', 'chart', 9, 1, NOW(), NOW(), 0);

-- 系统管理子菜单
INSERT INTO sys_permission (id, parent_id, name, type, permission_key, path, component, icon, sort, status, create_time, update_time, deleted) VALUES
(101, 1, '用户管理', 1, 'system:user:list', 'user', 'system/user/index', 'user', 1, 1, NOW(), NOW(), 0),
(102, 1, '角色管理', 1, 'system:role:list', 'role', 'system/role/index', 'peoples', 2, 1, NOW(), NOW(), 0),
(103, 1, '菜单管理', 1, 'system:menu:list', 'menu', 'system/menu/index', 'tree-table', 3, 1, NOW(), NOW(), 0),
(104, 1, '系统配置', 1, 'system:config:list', 'config', 'system/config/index', 'edit', 4, 1, NOW(), NOW(), 0);

-- 顾客管理子菜单
INSERT INTO sys_permission (id, parent_id, name, type, permission_key, path, component, icon, sort, status, create_time, update_time, deleted) VALUES
(201, 2, '顾客列表', 1, 'customer:list', 'index', 'customer/index', 'user', 1, 1, NOW(), NOW(), 0);

-- 技师管理子菜单
INSERT INTO sys_permission (id, parent_id, name, type, permission_key, path, component, icon, sort, status, create_time, update_time, deleted) VALUES
(301, 3, '技师列表', 1, 'therapist:list', 'index', 'therapist/index', 'people', 1, 1, NOW(), NOW(), 0);

-- 服务管理子菜单
INSERT INTO sys_permission (id, parent_id, name, type, permission_key, path, component, icon, sort, status, create_time, update_time, deleted) VALUES
(401, 4, '服务项目', 1, 'service:list', 'index', 'service/index', 'list', 1, 1, NOW(), NOW(), 0),
(402, 4, '服务类别', 1, 'service-category:list', 'category', 'service/category', 'nested', 2, 1, NOW(), NOW(), 0);

-- 预约管理子菜单
INSERT INTO sys_permission (id, parent_id, name, type, permission_key, path, component, icon, sort, status, create_time, update_time, deleted) VALUES
(501, 5, '预约列表', 1, 'appointment:list', 'index', 'appointment/index', 'list', 1, 1, NOW(), NOW(), 0),
(502, 5, '预约日历', 1, 'appointment:calendar', 'calendar', 'appointment/calendar', 'calendar', 2, 1, NOW(), NOW(), 0);

-- 订单管理子菜单
INSERT INTO sys_permission (id, parent_id, name, type, permission_key, path, component, icon, sort, status, create_time, update_time, deleted) VALUES
(601, 6, '订单列表', 1, 'order:list', 'index', 'order/index', 'list', 1, 1, NOW(), NOW(), 0);

-- 卡项管理子菜单
INSERT INTO sys_permission (id, parent_id, name, type, permission_key, path, component, icon, sort, status, create_time, update_time, deleted) VALUES
(701, 7, '卡项列表', 1, 'customer-card:list', 'index', 'card/index', 'list', 1, 1, NOW(), NOW(), 0);

-- 邀请返利子菜单
INSERT INTO sys_permission (id, parent_id, name, type, permission_key, path, component, icon, sort, status, create_time, update_time, deleted) VALUES
(801, 8, '邀请关系', 1, 'invitation:list', 'relation', 'invitation/relation', 'tree', 1, 1, NOW(), NOW(), 0),
(802, 8, '佣金记录', 1, 'invitation:commission', 'commission', 'invitation/commission', 'money', 2, 1, NOW(), NOW(), 0),
(803, 8, '提现管理', 1, 'invitation:withdraw', 'withdraw', 'invitation/withdraw', 'pay', 3, 1, NOW(), NOW(), 0);

-- 统计分析子菜单
INSERT INTO sys_permission (id, parent_id, name, type, permission_key, path, component, icon, sort, status, create_time, update_time, deleted) VALUES
(901, 9, '营业统计', 1, 'statistics:business', 'business', 'statistics/business', 'chart', 1, 1, NOW(), NOW(), 0),
(902, 9, '顾客统计', 1, 'statistics:customer', 'customer', 'statistics/customer', 'peoples', 2, 1, NOW(), NOW(), 0),
(903, 9, '技师业绩', 1, 'statistics:therapist', 'therapist', 'statistics/therapist', 'people', 3, 1, NOW(), NOW(), 0);

-- 用户管理按钮权限
INSERT INTO sys_permission (id, parent_id, name, type, permission_key, path, component, icon, sort, status, create_time, update_time, deleted) VALUES
(10101, 101, '用户查询', 2, 'system:user:query', NULL, NULL, NULL, 1, 1, NOW(), NOW(), 0),
(10102, 101, '用户新增', 2, 'system:user:add', NULL, NULL, NULL, 2, 1, NOW(), NOW(), 0),
(10103, 101, '用户修改', 2, 'system:user:edit', NULL, NULL, NULL, 3, 1, NOW(), NOW(), 0),
(10104, 101, '用户删除', 2, 'system:user:remove', NULL, NULL, NULL, 4, 1, NOW(), NOW(), 0);

-- 角色管理按钮权限
INSERT INTO sys_permission (id, parent_id, name, type, permission_key, path, component, icon, sort, status, create_time, update_time, deleted) VALUES
(10201, 102, '角色查询', 2, 'system:role:query', NULL, NULL, NULL, 1, 1, NOW(), NOW(), 0),
(10202, 102, '角色新增', 2, 'system:role:add', NULL, NULL, NULL, 2, 1, NOW(), NOW(), 0),
(10203, 102, '角色修改', 2, 'system:role:edit', NULL, NULL, NULL, 3, 1, NOW(), NOW(), 0),
(10204, 102, '角色删除', 2, 'system:role:remove', NULL, NULL, NULL, 4, 1, NOW(), NOW(), 0);

-- 菜单管理按钮权限
INSERT INTO sys_permission (id, parent_id, name, type, permission_key, path, component, icon, sort, status, create_time, update_time, deleted) VALUES
(10301, 103, '菜单查询', 2, 'system:menu:query', NULL, NULL, NULL, 1, 1, NOW(), NOW(), 0),
(10302, 103, '菜单新增', 2, 'system:menu:add', NULL, NULL, NULL, 2, 1, NOW(), NOW(), 0),
(10303, 103, '菜单修改', 2, 'system:menu:edit', NULL, NULL, NULL, 3, 1, NOW(), NOW(), 0),
(10304, 103, '菜单删除', 2, 'system:menu:remove', NULL, NULL, NULL, 4, 1, NOW(), NOW(), 0);

-- 顾客管理按钮权限
INSERT INTO sys_permission (id, parent_id, name, type, permission_key, path, component, icon, sort, status, create_time, update_time, deleted) VALUES
(20101, 201, '顾客查询', 2, 'customer:query', NULL, NULL, NULL, 1, 1, NOW(), NOW(), 0),
(20102, 201, '顾客新增', 2, 'customer:add', NULL, NULL, NULL, 2, 1, NOW(), NOW(), 0),
(20103, 201, '顾客修改', 2, 'customer:edit', NULL, NULL, NULL, 3, 1, NOW(), NOW(), 0),
(20104, 201, '顾客删除', 2, 'customer:remove', NULL, NULL, NULL, 4, 1, NOW(), NOW(), 0);

-- 技师管理按钮权限
INSERT INTO sys_permission (id, parent_id, name, type, permission_key, path, component, icon, sort, status, create_time, update_time, deleted) VALUES
(30101, 301, '技师查询', 2, 'therapist:query', NULL, NULL, NULL, 1, 1, NOW(), NOW(), 0),
(30102, 301, '技师新增', 2, 'therapist:add', NULL, NULL, NULL, 2, 1, NOW(), NOW(), 0),
(30103, 301, '技师修改', 2, 'therapist:edit', NULL, NULL, NULL, 3, 1, NOW(), NOW(), 0),
(30104, 301, '技师删除', 2, 'therapist:remove', NULL, NULL, NULL, 4, 1, NOW(), NOW(), 0);

-- 服务项目按钮权限
INSERT INTO sys_permission (id, parent_id, name, type, permission_key, path, component, icon, sort, status, create_time, update_time, deleted) VALUES
(40101, 401, '服务查询', 2, 'service:query', NULL, NULL, NULL, 1, 1, NOW(), NOW(), 0),
(40102, 401, '服务新增', 2, 'service:add', NULL, NULL, NULL, 2, 1, NOW(), NOW(), 0),
(40103, 401, '服务修改', 2, 'service:edit', NULL, NULL, NULL, 3, 1, NOW(), NOW(), 0),
(40104, 401, '服务删除', 2, 'service:remove', NULL, NULL, NULL, 4, 1, NOW(), NOW(), 0);

-- 预约管理按钮权限
INSERT INTO sys_permission (id, parent_id, name, type, permission_key, path, component, icon, sort, status, create_time, update_time, deleted) VALUES
(50101, 501, '预约查询', 2, 'appointment:query', NULL, NULL, NULL, 1, 1, NOW(), NOW(), 0),
(50102, 501, '预约新增', 2, 'appointment:add', NULL, NULL, NULL, 2, 1, NOW(), NOW(), 0),
(50103, 501, '预约修改', 2, 'appointment:edit', NULL, NULL, NULL, 3, 1, NOW(), NOW(), 0),
(50104, 501, '预约取消', 2, 'appointment:cancel', NULL, NULL, NULL, 4, 1, NOW(), NOW(), 0),
(50105, 501, '预约完成', 2, 'appointment:complete', NULL, NULL, NULL, 5, 1, NOW(), NOW(), 0);

-- 订单管理按钮权限
INSERT INTO sys_permission (id, parent_id, name, type, permission_key, path, component, icon, sort, status, create_time, update_time, deleted) VALUES
(60101, 601, '订单查询', 2, 'order:query', NULL, NULL, NULL, 1, 1, NOW(), NOW(), 0),
(60102, 601, '订单新增', 2, 'order:add', NULL, NULL, NULL, 2, 1, NOW(), NOW(), 0),
(60103, 601, '订单修改', 2, 'order:edit', NULL, NULL, NULL, 3, 1, NOW(), NOW(), 0),
(60104, 601, '订单取消', 2, 'order:cancel', NULL, NULL, NULL, 4, 1, NOW(), NOW(), 0),
(60105, 601, '订单退款', 2, 'order:refund', NULL, NULL, NULL, 5, 1, NOW(), NOW(), 0);

-- 卡项管理按钮权限
INSERT INTO sys_permission (id, parent_id, name, type, permission_key, path, component, icon, sort, status, create_time, update_time, deleted) VALUES
(70101, 701, '卡项查询', 2, 'customer-card:query', NULL, NULL, NULL, 1, 1, NOW(), NOW(), 0),
(70102, 701, '卡项新增', 2, 'customer-card:add', NULL, NULL, NULL, 2, 1, NOW(), NOW(), 0),
(70103, 701, '卡项修改', 2, 'customer-card:edit', NULL, NULL, NULL, 3, 1, NOW(), NOW(), 0),
(70104, 701, '卡项删除', 2, 'customer-card:remove', NULL, NULL, NULL, 4, 1, NOW(), NOW(), 0),
(70105, 701, '卡项使用', 2, 'customer-card:use', NULL, NULL, NULL, 5, 1, NOW(), NOW(), 0);

-- 邀请返利按钮权限
INSERT INTO sys_permission (id, parent_id, name, type, permission_key, path, component, icon, sort, status, create_time, update_time, deleted) VALUES
(80101, 801, '邀请查询', 2, 'invitation:query', NULL, NULL, NULL, 1, 1, NOW(), NOW(), 0),
(80102, 801, '邀请新增', 2, 'invitation:add', NULL, NULL, NULL, 2, 1, NOW(), NOW(), 0),
(80103, 801, '邀请修改', 2, 'invitation:edit', NULL, NULL, NULL, 3, 1, NOW(), NOW(), 0);

-- 统计分析按钮权限
INSERT INTO sys_permission (id, parent_id, name, type, permission_key, path, component, icon, sort, status, create_time, update_time, deleted) VALUES
(90101, 901, '营业统计查询', 2, 'statistics:business:query', NULL, NULL, NULL, 1, 1, NOW(), NOW(), 0),
(90201, 902, '顾客统计查询', 2, 'statistics:customer:query', NULL, NULL, NULL, 1, 1, NOW(), NOW(), 0),
(90301, 903, '技师业绩查询', 2, 'statistics:therapist:query', NULL, NULL, NULL, 1, 1, NOW(), NOW(), 0);

-- 角色权限关联表（超级管理员拥有所有权限）
-- 先插入所有菜单权限
INSERT INTO sys_role_permission (role_id, permission_id, create_time)
SELECT 1, id, NOW() FROM sys_permission;

-- 店长权限（除了系统管理的部分高级权限外，拥有大部分业务权限）
-- 菜单权限
INSERT INTO sys_role_permission (role_id, permission_id, create_time) VALUES
-- 顾客管理
(2, 2, NOW()), (2, 201, NOW()),
-- 技师管理
(2, 3, NOW()), (2, 301, NOW()),
-- 服务管理
(2, 4, NOW()), (2, 401, NOW()), (2, 402, NOW()),
-- 预约管理
(2, 5, NOW()), (2, 501, NOW()), (2, 502, NOW()),
-- 订单管理
(2, 6, NOW()), (2, 601, NOW()),
-- 卡项管理
(2, 7, NOW()), (2, 701, NOW()),
-- 邀请返利
(2, 8, NOW()), (2, 801, NOW()), (2, 802, NOW()), (2, 803, NOW()),
-- 统计分析
(2, 9, NOW()), (2, 901, NOW()), (2, 902, NOW()), (2, 903, NOW());

-- 店长按钮权限
INSERT INTO sys_role_permission (role_id, permission_id, create_time) VALUES
-- 顾客管理按钮
(2, 20101, NOW()), (2, 20102, NOW()), (2, 20103, NOW()), (2, 20104, NOW()),
-- 技师管理按钮
(2, 30101, NOW()), (2, 30102, NOW()), (2, 30103, NOW()), (2, 30104, NOW()),
-- 服务管理按钮
(2, 40101, NOW()), (2, 40102, NOW()), (2, 40103, NOW()), (2, 40104, NOW()),
-- 预约管理按钮
(2, 50101, NOW()), (2, 50102, NOW()), (2, 50103, NOW()), (2, 50104, NOW()), (2, 50105, NOW()),
-- 订单管理按钮
(2, 60101, NOW()), (2, 60102, NOW()), (2, 60103, NOW()), (2, 60104, NOW()), (2, 60105, NOW()),
-- 卡项管理按钮
(2, 70101, NOW()), (2, 70102, NOW()), (2, 70103, NOW()), (2, 70104, NOW()), (2, 70105, NOW()),
-- 邀请返利按钮
(2, 80101, NOW()), (2, 80102, NOW()), (2, 80103, NOW()),
-- 统计分析按钮
(2, 90101, NOW()), (2, 90201, NOW()), (2, 90301, NOW());

-- 技师权限（只有有限的查询和操作权限）
-- 菜单权限
INSERT INTO sys_role_permission (role_id, permission_id, create_time) VALUES
-- 预约管理
(3, 5, NOW()), (3, 501, NOW()), (3, 502, NOW()),
-- 顾客管理（只读）
(3, 2, NOW()), (3, 201, NOW()),
-- 服务管理（只读）
(3, 4, NOW()), (3, 401, NOW());

-- 技师按钮权限
INSERT INTO sys_role_permission (role_id, permission_id, create_time) VALUES
-- 预约管理按钮（只有查询和完成权限）
(3, 50101, NOW()), (3, 50105, NOW()),
-- 顾客管理按钮（只有查询权限）
(3, 20101, NOW()),
-- 服务管理按钮（只有查询权限）
(3, 40101, NOW());

-- 初始化服务类别表
INSERT INTO service_category (id, name, sort, status, create_time, update_time, deleted) VALUES
(1, '推拿按摩', 1, 1, NOW(), NOW(), 0),
(2, '艾灸理疗', 2, 1, NOW(), NOW(), 0),
(3, '足疗保健', 3, 1, NOW(), NOW(), 0),
(4, '中医调理', 4, 1, NOW(), NOW(), 0),
(5, '套餐卡项', 5, 1, NOW(), NOW(), 0);

-- 初始化服务项目表
INSERT INTO service_item (id, category_id, name, type, duration, price, image, description, status, create_time, update_time, deleted, sort) VALUES
(1, 1, '全身推拿', 1, 60, 198.00, NULL, '全身推拿按摩，疏通经络，缓解疲劳', 1, NOW(), NOW(), 0, 1),
(2, 1, '颈肩推拿', 1, 30, 98.00, NULL, '颈肩重点推拿，缓解颈椎疲劳', 1, NOW(), NOW(), 0, 2),
(3, 1, '腰背推拿', 1, 30, 98.00, NULL, '腰背重点推拿，缓解腰椎疲劳', 1, NOW(), NOW(), 0, 3),
(4, 2, '艾灸调理', 1, 45, 128.00, NULL, '艾灸理疗，温经散寒', 1, NOW(), NOW(), 0, 4),
(5, 2, '拔罐理疗', 1, 30, 88.00, NULL, '拔罐理疗，排毒养颜', 1, NOW(), NOW(), 0, 5),
(6, 3, '足底按摩', 1, 45, 108.00, NULL, '足底按摩，疏通经络', 1, NOW(), NOW(), 0, 6),
(7, 3, '足浴养生', 1, 60, 138.00, NULL, '足浴养生，促进血液循环', 1, NOW(), NOW(), 0, 7),
(8, 4, '中医诊脉', 1, 20, 68.00, NULL, '中医诊脉，辨证施治', 1, NOW(), NOW(), 0, 8),
(9, 4, '刮痧排毒', 1, 30, 88.00, NULL, '刮痧排毒，活血化瘀', 1, NOW(), NOW(), 0, 9),
(10, 5, '全身推拿卡', 2, 60, 1680.00, NULL, '全身推拿10次卡，有效期3个月', 1, NOW(), NOW(), 0, 10),
(11, 5, '艾灸调理卡', 2, 45, 980.00, NULL, '艾灸调理10次卡，有效期3个月', 1, NOW(), NOW(), 0, 11),
(12, 5, '足疗养生卡', 2, 60, 1080.00, NULL, '足疗养生10次卡，有效期3个月', 1, NOW(), NOW(), 0, 12);

-- 初始化技师表
INSERT INTO therapist (id, user_id, name, phone, gender, age, avatar, level, introduction, status, create_time, update_time, remark, deleted) VALUES
(1, 3, '张技师', '13800000002', 2, 28, NULL, 1, '专业推拿按摩技师，从业5年', 1, NOW(), NOW(), '资深推拿技师', 0),
(2, NULL, '李技师', '13800000003', 1, 32, NULL, 2, '专业艾灸理疗技师，从业8年', 1, NOW(), NOW(), '资深艾灸技师', 0),
(3, NULL, '王技师', '13800000004', 2, 26, NULL, 1, '专业足疗保健技师，从业3年', 1, NOW(), NOW(), '资深足疗技师', 0);

-- 初始化系统配置表（如果存在）
-- 这里可以添加系统配置的初始化数据，如佣金比例、积分规则等
