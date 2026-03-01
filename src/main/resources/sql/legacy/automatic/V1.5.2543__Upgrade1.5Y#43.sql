-- 1. 移除新表 (如果已存在)
DROP TABLE IF EXISTS `auth_permission`;

-- 2. 创建新表 `auth_permission` (已移除 AUTO_INCREMENT)
CREATE TABLE `auth_permission` (
                                   `id` int NOT NULL COMMENT '权限ID', -- 移除了 AUTO_INCREMENT
                                   `name` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '权限名称，如：查看用户',
                                   `code` varchar(320) COLLATE utf8mb4_general_ci NOT NULL COMMENT '权限标识，如：system:user:view',
                                   `remark` text COLLATE utf8mb4_general_ci COMMENT '权限描述',
                                   `seq` int NOT NULL COMMENT '排序号',
                                   `is_system` tinyint NOT NULL COMMENT '系统内置权限 0:否 1:是',
                                   `create_time` datetime NOT NULL COMMENT '创建时间',
                                   `creator_id` bigint NOT NULL COMMENT '创建人',
                                   `update_time` datetime NOT NULL COMMENT '修改时间',
                                   `updater_id` bigint NOT NULL COMMENT '修改人',
                                   PRIMARY KEY (`id`),
                                   UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='权限码表';

-- 3. 挪动旧表数据到新表 (包含 ID 迁移)
INSERT INTO `auth_permission` (
    `id`,
    `name`,
    `code`,
    `remark`,
    `seq`,
    `is_system`,
    `create_time`,
    `creator_id`,
    `update_time`,
    `updater_id`
)
SELECT
    `id`,                -- 显式迁移原有的 ID
    LEFT(`name`, 32),    -- 截断处理：旧 50 -> 新 32
    `code`,
    `description`,
    `sort_order`,
    `is_system`,
    `create_time`,
    0,                   -- 旧表无此字段，默认填充 0
    `update_time`,
    0                    -- 旧表无此字段，默认填充 0
FROM `core_permission`;

-- 4. 重命名旧表
RENAME TABLE `core_permission` TO `zremoved_core_permission`;