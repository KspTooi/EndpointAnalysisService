-- ================================================= --
-- 1. 删除可能存在的旧备份表 (zremovered_core_attach)
-- ================================================= --
DROP TABLE IF EXISTS `zremovered_core_attach`;

-- ================================================= --
-- 2. 重命名当前旧表，增加前缀 zremovered_
-- ================================================= --
RENAME TABLE `core_attach` TO `zremovered_core_attach`;

-- ================================================= --
-- 3. 创建新表 core_attach
-- (已修正 id 字段类型并根据旧表定义补全了约束)
-- ================================================= --
CREATE TABLE `core_attach` (
                               `id` BIGINT NOT NULL COMMENT '文件ID',
                               `root_id` BIGINT NOT NULL COMMENT '租户ID',
                               `dept_id` BIGINT NOT NULL COMMENT '部门ID',
                               `name` VARCHAR(128) NOT NULL COMMENT '文件原始名称',
                               `kind` VARCHAR(64) NOT NULL COMMENT '文件业务类型',
                               `suffix` VARCHAR(128) DEFAULT NULL COMMENT '扩展名',
                               `path` VARCHAR(256) NOT NULL COMMENT '文件路径',
                               `sha256` VARCHAR(320) NOT NULL COMMENT '文件摘要',
                               `total_size` BIGINT NOT NULL COMMENT '总大小',
                               `receive_size` BIGINT NOT NULL COMMENT '已接收大小',
                               `status` TINYINT NOT NULL COMMENT '状态 0:预检文件 1:区块不完整 2:有效',
                               `verify_time` DATETIME DEFAULT NULL COMMENT '校验时间',
                               `create_time` DATETIME NOT NULL COMMENT '创建时间',
                               `creator_id` BIGINT NOT NULL COMMENT '创建人',
                               PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='文件附件表';

-- ================================================= --
-- 4. 挪数据：从备份表向新表迁移
-- root_id 和 dept_id 初始化为 -1
-- ================================================= --
INSERT INTO `core_attach` (
    `id`,
    `root_id`,
    `dept_id`,
    `name`,
    `kind`,
    `suffix`,
    `path`,
    `sha256`,
    `total_size`,
    `receive_size`,
    `status`,
    `verify_time`,
    `create_time`,
    `creator_id`
)
SELECT
    `id`,
    -1,           -- 初始化 root_id 为 -1
    -1,           -- 初始化 dept_id 为 -1
    `name`,
    `kind`,
    `suffix`,
    `path`,
    `sha256`,
    `total_size`,
    `receive_size`,
    `status`,
    `verify_time`,
    `create_time`,
    `creator_id`
FROM `zremovered_core_attach`;