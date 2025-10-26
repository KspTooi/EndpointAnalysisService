
-- 用于既有系统1.0B->1.2F(Later)的版本升级迁移 仅限MYSQL数据库 不支持H2DB

-- 处理核心表
ALTER TABLE config RENAME TO core_config;
ALTER TABLE `groups` RENAME TO core_group;
ALTER TABLE `group_permission` RENAME TO core_group_permission;
ALTER TABLE `permissions` RENAME TO core_permission;
ALTER TABLE `resource` RENAME TO core_resource;
ALTER TABLE `users` RENAME TO core_user;
ALTER TABLE `user_group` RENAME TO core_user_group;
ALTER TABLE `user_session` RENAME TO core_user_session;

--处理文档模块表(暂无变更)

--处理调试模块表(RDBG)
ALTER TABLE `user_request` RENAME TO rdbg_request;
ALTER TABLE `user_request_group` RENAME TO rdbg_request_group;
ALTER TABLE `user_request_log` RENAME TO rdbg_request_log;
ALTER TABLE `user_request_tree` RENAME TO rdbg_request_tree;
ALTER TABLE `simple_filter` RENAME TO rdbg_simple_filter;
ALTER TABLE `simple_filter_operation` RENAME TO rdbg_simple_filter_operation;
ALTER TABLE `user_request_group_simple_filter` RENAME TO rdbg_simple_filter_request_group;
ALTER TABLE `simple_filter_trigger` RENAME TO rdbg_simple_filter_trigger;
ALTER TABLE `user_request_env` RENAME TO rdbg_user_env;
ALTER TABLE `user_request_env_storage` RENAME TO rdbg_user_env_storage;

--处理中继模块表(RELAY)
ALTER TABLE `replay_requets` RENAME TO relay_replay_request;
ALTER TABLE `relay_request` RENAME TO relay_request;
ALTER TABLE `route_rule` RENAME TO relay_route_rule;
ALTER TABLE `route_server` RENAME TO relay_route_server;
ALTER TABLE `relay_server` RENAME TO relay_server;
ALTER TABLE `relay_server_route` RENAME TO relay_server_route;



-- 后处理资源表
ALTER TABLE `core_resource` ADD COLUMN `menu_btn_id` VARCHAR(64) DEFAULT NULL COMMENT '按钮ID(menuKind = 2时必填)';
ALTER TABLE `core_resource` ADD COLUMN `menu_hidden` TINYINT(1) DEFAULT NULL COMMENT '是否隐藏 0:否 1:是(menuKind = 1/2时生效)';
UPDATE `core_resource` SET menu_hidden = 0;



