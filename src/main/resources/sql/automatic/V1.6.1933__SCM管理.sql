-- =================================================================================
-- 1. 删除可能已存在的 zremovred_ 备份表（防止重命名冲突）
-- =================================================================================
DROP TABLE IF EXISTS `zremovred_assembly_scm`;

-- =================================================================================
-- 2. 重命名当前旧表，增加 zremovred_ 前缀
-- =================================================================================
RENAME TABLE `assembly_scm` TO `zremovred_assembly_scm`;

-- =================================================================================
-- 3. 创建新表结构
-- =================================================================================
CREATE TABLE `assembly_scm` (
                                `id` BIGINT NOT NULL COMMENT '主键ID',
                                `name` VARCHAR(32) NOT NULL COMMENT 'SCM名称(唯一)',
                                `project_name` VARCHAR(80) COMMENT '项目名称',
                                `scm_url` VARCHAR(1000) NOT NULL COMMENT 'SCM仓库地址',
                                `scm_auth_kind` TINYINT NOT NULL COMMENT 'SCM认证方式 0:公开 1:账号密码 2:SSH KEY 3:PAT',
                                `scm_username` TEXT COMMENT 'SCM用户名',
                                `scm_password` TEXT COMMENT 'SCM密码',
                                `scm_pk` TEXT COMMENT 'SSH KEY',
                                `scm_branch` VARCHAR(80) NOT NULL COMMENT 'SCM分支',
                                `remark` TEXT COMMENT 'SCM备注',
                                `create_time` DATETIME NOT NULL COMMENT '创建时间',
                                `creator_id` BIGINT NOT NULL COMMENT '创建人ID',
                                `update_time` DATETIME NOT NULL COMMENT '更新时间',
                                `updater_id` BIGINT NOT NULL COMMENT '更新人ID',
                                PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT = 'SCM表';

-- 创建新的联合唯一索引
CREATE UNIQUE INDEX uk_scm_name ON assembly_scm(`name`, `creator_id`);

-- =================================================================================
-- 4. 挪动数据（从备份表迁移回新表）
-- =================================================================================
INSERT INTO `assembly_scm` (
    `id`, `name`, `project_name`, `scm_url`, `scm_auth_kind`,
    `scm_username`, `scm_password`, `scm_pk`, `scm_branch`,
    `remark`, `create_time`, `creator_id`, `update_time`, `updater_id`
)
SELECT
    `id`, `name`, `project_name`, `scm_url`, `scm_auth_kind`,
    `scm_username`, `scm_password`, `scm_pk`, `scm_branch`,
    `remark`, `create_time`, `creator_id`, `update_time`, `updater_id`
FROM `zremovred_assembly_scm`;

-- =================================================================================
-- 改动项说明（Notes）：
-- 1. 唯一约束变更：由原来的唯一索引 `uk_scm_name (name)` 变更为联合唯一索引 `uk_scm_name (name, creator_id)`。
--    这意味着同一名称在不同 creator_id 下现在可以重复，仅在同一 creator_id 下必须唯一。
-- 2. 索引定义方式：新脚本将唯一索引的创建从 CREATE TABLE 语句内移出，改为显式 CREATE UNIQUE INDEX 语句。
-- 3. 语法规范：统一了字段引用符，确保在 MySQL 8.0 环境下的兼容性。
-- 4. 数据完整性：通过 RENAME 方式先备份再插入，确保了在迁移失败时可以通过备份表快速回滚。
-- =================================================================================