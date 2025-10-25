
-- 用于既有系统1.0B->1.2F(Later)的版本升级迁移 仅限MYSQL数据库 不支持H2DB

ALTER TABLE config RENAME TO core_config;

