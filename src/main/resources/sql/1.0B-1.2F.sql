
-- 用于既有系统1.0B->1.2F(Later)的版本升级迁移 仅限MYSQL数据库 不支持H2DB

-- 处理配置表
ALTER TABLE config RENAME TO core_config;

-- 处理资源表
ALTER TABLE `resource` ADD COLUMN `menu_btn_id` VARCHAR(64) DEFAULT NULL COMMENT '按钮ID(menuKind = 2时必填)';
ALTER TABLE `resource` ADD COLUMN `menu_hidden` TINYINT(1) DEFAULT NULL COMMENT '是否隐藏 0:否 1:是(menuKind = 1/2时生效)';
UPDATE `resource` SET menu_hidden = 0;
ALTER TABLE `resource` RENAME TO core_resource;