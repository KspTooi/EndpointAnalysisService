-- ==========================================================
-- SQL 迁移脚本: drive_entry 表结构升级
-- 流程:
-- 1. 删除旧的备份表 zremoved_drive_entry (如果存在)
-- 2. 将当前 drive_entry 重命名为 zremoved_drive_entry
-- 3. 创建全新的 drive_entry 表
-- 4. 从备份表中迁移数据，并将 root_id 和 dept_id 初始化为 -1
-- ==========================================================

-- 1. 删除 zremoved_旧表
DROP TABLE IF EXISTS `zremoved_drive_entry`;

-- 2. 旧表重命名增加前缀 zremoved_
-- 注意：执行此步前请确保没有其他进程正在写入该表
RENAME TABLE `drive_entry` TO `zremoved_drive_entry`;

-- 3. 创建新表
CREATE TABLE `drive_entry` (
                               `id` BIGINT NOT NULL COMMENT '条目ID',
                               `root_id` BIGINT NOT NULL COMMENT '租户ID',
                               `dept_id` BIGINT NOT NULL COMMENT '部门ID',
                               `parent_id` BIGINT COMMENT '父级ID 为NULL顶级',
                               `name` VARCHAR(128) NOT NULL COMMENT '条目名称',
                               `kind` INT COMMENT '条目类型 0:文件 1:文件夹',
                               `attach_id` BIGINT COMMENT '文件附件ID 文件夹为NULL',
                               `attach_size` BIGINT COMMENT '文件附件大小 文件夹为0',
                               `attach_suffix` VARCHAR(128) COMMENT '文件附件类型 文件夹为NULL',
                               `attach_status` TINYINT COMMENT '文件附件状态 0:预检文件 1:区块不完整 2:校验中 3:有效',
                               `create_time` DATETIME NOT NULL COMMENT '创建时间',
                               `creator_id` BIGINT NOT NULL COMMENT '创建人',
                               `update_time` DATETIME NOT NULL COMMENT '更新时间',
                               `updater_id` BIGINT NOT NULL COMMENT '更新人',
                               `delete_time` DATETIME COMMENT '删除时间 为NULL未删除',
                               PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT = '云盘实体';

-- 4. 挪动旧表数据到新表 (rootId 和 deptId 都为 -1)
INSERT INTO `drive_entry` (
    `id`,
    `root_id`,
    `dept_id`,
    `parent_id`,
    `name`,
    `kind`,
    `attach_id`,
    `attach_size`,
    `attach_suffix`,
    `attach_status`,
    `create_time`,
    `creator_id`,
    `update_time`,
    `updater_id`,
    `delete_time`
)
SELECT
    `id`,
    -1,            -- root_id 固定为 -1
    -1,            -- dept_id 固定为 -1
    `parent_id`,
    `name`,
    `kind`,
    `attach_id`,
    `attach_size`,
    `attach_suffix`,
    `attach_status`,
    `create_time`,
    `creator_id`,
    `update_time`,
    `updater_id`,
    `delete_time`
FROM `zremoved_drive_entry`;

-- ==========================================================
-- 迁移完成。建议手动检查数据一致性后再决定是否彻底删除 zremoved_drive_entry。
-- ==========================================================