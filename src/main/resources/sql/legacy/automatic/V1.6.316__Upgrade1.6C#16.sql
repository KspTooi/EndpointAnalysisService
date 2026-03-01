-- ================================================= =
-- 1. 重命名旧表作为备份
-- ================================================= =
ALTER TABLE `auth_group` RENAME TO `zremoved_auth_group`;

-- ================================================= =
-- 2. 创建新表
-- ================================================= =
CREATE TABLE `auth_group` (
                              `id` BIGINT NOT NULL COMMENT '组ID',
                              `code` VARCHAR(32) NOT NULL COMMENT '组标识，如：admin、developer等',
                              `name` VARCHAR(32) NOT NULL COMMENT '组名称，如：管理员组、开发者组等',
                              `remark` TEXT COMMENT '组描述',
                              `status` TINYINT NOT NULL COMMENT '组状态:0:禁用，1:启用',
                              `seq` INT NOT NULL COMMENT '排序号',
                              `row_scope` TINYINT NOT NULL DEFAULT 0 COMMENT '数据范围 0:全部 1:本公司/租户及以下 2:本部门及以下 3:本部门 4:仅本人 5:指定部门',
                              `is_system` TINYINT NOT NULL COMMENT '系统内置组 0:否 1:是',
                              `create_time` DATETIME NOT NULL COMMENT '创建时间',
                              `creator_id` BIGINT NOT NULL COMMENT '创建人ID',
                              `update_time` DATETIME NOT NULL COMMENT '修改时间',
                              `updater_id` BIGINT NOT NULL COMMENT '修改人ID',
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `uk_code` (`code`)
) COMMENT = '用户组';

-- ================================================= =
-- 3. 挪动数据
-- ================================================= =
INSERT INTO `auth_group` (
    `id`,
    `code`,
    `name`,
    `remark`,
    `status`,
    `seq`,
    `row_scope`, -- 新增字段
    `is_system`,
    `create_time`,
    `creator_id`,
    `update_time`,
    `updater_id`
)
SELECT
    `id`,
    `code`,
    `name`,
    `remark`,
    `status`,
    `seq`,
    0,           -- 为旧数据填充 row_scope 的初始值（0:全部）
    `is_system`,
    `create_time`,
    `creator_id`,
    `update_time`,
    `updater_id`
FROM `zremoved_auth_group`;

