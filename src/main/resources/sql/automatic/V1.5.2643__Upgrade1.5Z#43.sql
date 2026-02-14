-- ========================================================
-- 1. 重命名旧表 (加上 zremoved_ 前缀)
-- ========================================================
RENAME TABLE `core_post` TO `zremoved_core_post`;


-- ========================================================
-- 2. 创建新表 (增加了 status, remark 字段及唯一索引)
-- ========================================================
CREATE TABLE `core_post` (
                             `id` bigint NOT NULL COMMENT '岗位ID',
                             `name` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '岗位名称',
                             `code` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '岗位编码',
                             `seq` int NOT NULL COMMENT '岗位排序',
                             `status` tinyint NOT NULL COMMENT '0:启用 1:停用',
                             `remark` text COLLATE utf8mb4_general_ci COMMENT '备注',
                             `create_time` datetime NOT NULL COMMENT '创建时间',
                             `creator_id` bigint NOT NULL COMMENT '创建人ID',
                             `update_time` datetime NOT NULL COMMENT '更新时间',
                             `updater_id` bigint NOT NULL COMMENT '更新人ID',
                             `delete_time` datetime DEFAULT NULL COMMENT '删除时间 NULL未删',
                             PRIMARY KEY (`id`),
                             UNIQUE KEY `code` (`code`,`delete_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='岗位表';


-- ========================================================
-- 3. 挪旧表数据到新表 (字段显式映射)
-- ========================================================
INSERT INTO `core_post` (
    `id`,
    `name`,
    `code`,
    `seq`,
    `status`,
    `remark`,
    `create_time`,
    `creator_id`,
    `update_time`,
    `updater_id`,
    `delete_time`
)
SELECT
    `id`,
    `name`,
    `code`,
    `seq`,
    0,         -- status: 默认设为 0 (启用)
    NULL,        -- remark: 默认设为NULL
    `create_time`,
    `creator_id`,
    `update_time`,
    `updater_id`,
    `delete_time`
FROM `zremoved_core_post`;