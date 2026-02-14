-- 1. 如果新表已存在则删除 (确保干净的环境)
DROP TABLE IF EXISTS `auth_group`;

-- 2. 创建新表 `auth_group`
CREATE TABLE `auth_group` (
                              `id` int NOT NULL COMMENT '组ID',
                              `code` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '组标识，如：admin、developer等',
                              `name` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '组名称，如：管理员组、开发者组等',
                              `remark` text COLLATE utf8mb4_general_ci COMMENT '组描述',
                              `status` tinyint NOT NULL COMMENT '组状态:0:禁用，1:启用',
                              `seq` int NOT NULL COMMENT '排序号',
                              `is_system` tinyint NOT NULL COMMENT '系统内置组 0:否 1:是',
                              `create_time` datetime NOT NULL COMMENT '创建时间',
                              `creator_id` bigint NOT NULL COMMENT '创建人ID',
                              `update_time` datetime NOT NULL COMMENT '修改时间',
                              `updater_id` bigint NOT NULL COMMENT '修改人ID',
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户组';

-- 3. 挪动旧表数据到新表
-- 注意：由于新表部分字段为 NOT NULL 而旧表为 NULL，这里使用了 IFNULL 处理
-- 同时也处理了 bit(1) 到 tinyint 的转换
INSERT INTO `auth_group` (
    `id`,
    `code`,
    `name`,
    `remark`,
    `status`,
    `seq`,
    `is_system`,
    `create_time`,
    `creator_id`,
    `update_time`,
    `updater_id`
)
SELECT
    `id`,
    LEFT(`code`, 32),              -- 截断处理，旧表50，新表32
    LEFT(`name`, 32),              -- 截断处理，旧表50，新表32
    `description`,
    `status`,
    `sort_order`,
    CAST(`is_system` AS UNSIGNED), -- bit(1) 转为 tinyint
    `create_time`,
    IFNULL(`create_user_id`, 0),   -- 处理新表 NOT NULL 约束
    `update_time`,
    IFNULL(`update_user_id`, 0)    -- 处理新表 NOT NULL 约束
FROM `core_group`;

-- 4. 重命名旧表
RENAME TABLE `core_group` TO `zremoved_core_group`;