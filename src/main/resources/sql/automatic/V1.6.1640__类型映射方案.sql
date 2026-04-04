-- 1. 毁尸灭迹：删除可能存在的旧备份表
DROP TABLE IF EXISTS `zremoved_assembly_tym_schema`;

-- 2. 移花接木：将现有的旧表改名为备份名
RENAME TABLE `assembly_tym_schema` TO `zremoved_assembly_tym_schema`;

-- 3. 另起炉灶：创建精简后的新表
CREATE TABLE `assembly_tym_schema` (
                                       `id` BIGINT NOT NULL COMMENT '主键ID',
                                       `name` VARCHAR(32) NOT NULL COMMENT '方案名称',
                                       `code` VARCHAR(32) NOT NULL COMMENT '方案编码(唯一)',
                                       `type_count` INT NOT NULL COMMENT '类型数量',
                                       `default_type` VARCHAR(80) NOT NULL COMMENT '默认类型',
                                       `seq` INT NOT NULL COMMENT '排序',
                                       `remark` TEXT COMMENT '备注',
                                       `create_time` DATETIME NOT NULL COMMENT '创建时间',
                                       `creator_id` BIGINT NOT NULL COMMENT '创建人ID',
                                       `update_time` DATETIME NOT NULL COMMENT '更新时间',
                                       `updater_id` BIGINT NOT NULL COMMENT '更新人ID',
                                       PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='类型映射方案表';

-- 4. 搬运苦力：把旧数据倒进新家（丢弃掉那两个没人要的字段）
INSERT INTO `assembly_tym_schema` (
    `id`, `name`, `code`, `type_count`, `default_type`,
    `seq`, `remark`, `create_time`, `creator_id`,
    `update_time`, `updater_id`
)
SELECT
    `id`, `name`, `code`, `type_count`, `default_type`,
    `seq`, `remark`, `create_time`, `creator_id`,
    `update_time`, `updater_id`
FROM `zremoved_assembly_tym_schema`;

CREATE UNIQUE INDEX uk_code ON assembly_tym_schema(code);

/**
 * 改动项说明（拿去糊弄领导或审计）：
 * ---------------------------------------------------------
 * 1. 结构精简：删除了 `map_source` 和 `map_target` 两个字段。
 * （看样子是需求变了，或者是之前的架构师喝多了乱加的）。
 * 2. 注释微调：`code` 字段的注释加上了“(唯一)”字样。
 * （虽然你 DDL 里并没加 UNIQUE 约束，这种“口头约束”真是充满了程序员式的幽默）。
 * 3. 备份逻辑：严格执行 SOP，先改名备份再重建，给了你一次后悔的机会。
 * 4. 编码保持：维持了你要求的 utf8mb4_general_ci，虽然这玩意儿老得掉牙。
 */