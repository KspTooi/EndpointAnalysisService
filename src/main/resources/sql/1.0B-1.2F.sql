
-- 用于既有系统1.0B->1.2F(Later)的版本升级迁移 仅限MYSQL数据库 不支持H2DB

-- 处理配置表
ALTER TABLE config RENAME TO core_config;

-- 处理资源表
ALTER TABLE `resource` ADD COLUMN `menu_btn_id` VARCHAR(64) DEFAULT NULL COMMENT '按钮ID(menuKind = 2时必填)';
ALTER TABLE `resource` ADD COLUMN `menu_hidden` TINYINT(1) DEFAULT NULL COMMENT '是否隐藏 0:否 1:是(menuKind = 1/2时生效)';
UPDATE `resource` SET menu_hidden = 0;
ALTER TABLE `resource` RENAME TO core_resource;

-- 处理用户组表
ALTER TABLE `groups` RENAME TO core_group;
ALTER TABLE `group_permission` RENAME TO core_group_permission;

-- 处理权限表
ALTER TABLE `permissions` RENAME TO core_permission;

-- 处理用户会话表
ALTER TABLE `user_session` RENAME TO core_user_session;

-- 处理用户表
ALTER TABLE `users` RENAME TO core_user;
ALTER TABLE `user_group` RENAME TO core_user_group;

-- 处理重放请求表
ALTER TABLE `replay_requets` RENAME TO relay_replay_request;

-- 处理路由规则表
ALTER TABLE `route_rule` RENAME TO relay_route_rule;

-- 处理路由服务器表
ALTER TABLE `route_server` RENAME TO relay_route_server;

-- 处理简单过滤器表
ALTER TABLE `simple_filter` RENAME TO rdbg_simple_filter;
ALTER TABLE `user_request_group_simple_filter` RENAME TO rdbg_simple_filter_group;


-- 处理用户请求表
ALTER TABLE `user_request` RENAME TO rdbg_user_request;

-- 处理用户请求环境表
ALTER TABLE `user_request_env` RENAME TO rdbg_user_env;

-- 处理用户请求环境共享存储表
ALTER TABLE `user_request_env_storage` RENAME TO rdbg_user_env_storage;

-- 处理用户请求组表
ALTER TABLE `rdbg_user_request_group` RENAME TO rdbg_request_group;

-- 处理用户请求树表
ALTER TABLE `user_request_tree` RENAME TO rdbg_request_tree;

-- 处理用户请求记录表
ALTER TABLE `user_request_log` RENAME TO rdbg_request_log;