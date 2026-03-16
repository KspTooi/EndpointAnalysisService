-- 1. 如果存在旧的备份表，先删除（清理环境）
DROP TABLE IF EXISTS `zremovered_assembly_out_model_origin`;

-- 2. 将当前的线上表重命名为备份表
ALTER TABLE `assembly_out_model_origin` RENAME TO `zremovered_assembly_out_model_origin`;

-- 3. 创建结构优化后的新表（新增了 pk 字段）
CREATE TABLE `assembly_out_model_origin` (
                                             `id` bigint NOT NULL COMMENT '主键ID',
                                             `output_schema_id` bigint NOT NULL COMMENT '输出方案ID',
                                             `name` varchar(255) NOT NULL COMMENT '原始字段名',
                                             `kind` varchar(255) NOT NULL COMMENT '原始数据类型',
                                             `length` varchar(255) DEFAULT NULL COMMENT '原始长度',
                                             `require` tinyint NOT NULL COMMENT '原始必填 0:否 1:是',
                                             `remark` varchar(255) DEFAULT NULL COMMENT '原始备注',
                                             `pk` tinyint NOT NULL COMMENT '是否主键 0:否 1:是',
                                             `seq` int NOT NULL COMMENT '原始排序',
                                             `create_time` datetime NOT NULL COMMENT '创建时间',
                                             `creator_id` bigint NOT NULL COMMENT '创建人ID',
                                             `update_time` datetime NOT NULL COMMENT '更新时间',
                                             `updater_id` bigint NOT NULL COMMENT '更新人ID',
                                             PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='输出方案原始模型表';

-- 4. 从备份表中挪动数据，新字段 pk 默认填充为 0
INSERT INTO `assembly_out_model_origin` (
    `id`,
    `output_schema_id`,
    `name`,
    `kind`,
    `length`,
    `require`,
    `remark`,
    `pk`,
    `seq`,
    `create_time`,
    `creator_id`,
    `update_time`,
    `updater_id`
)
SELECT
    `id`,
    `output_schema_id`,
    `name`,
    `kind`,
    `length`,
    `require`,
    `remark`,
    0, -- 新字段 pk 默认为 0
    `seq`,
    `create_time`,
    `creator_id`,
    `update_time`,
    `updater_id`
FROM `zremovered_assembly_out_model_origin`;

-- 1. 删除可能存在的旧备份表
DROP TABLE IF EXISTS `zremovered_assembly_out_model_poly`;

-- 2. 重命名当前表为备份表
ALTER TABLE `assembly_out_model_poly` RENAME TO `zremovered_assembly_out_model_poly`;

-- 3. 创建新表（增加了 pk 字段，并更新了 policy_crud_json 的注释）
CREATE TABLE `assembly_out_model_poly` (
                                           `id` bigint NOT NULL COMMENT '主键ID',
                                           `output_schema_id` bigint NOT NULL COMMENT '输出方案ID',
                                           `output_model_origin_id` bigint NOT NULL COMMENT '原始字段ID',
                                           `name` varchar(255) NOT NULL COMMENT '聚合字段名',
                                           `kind` varchar(255) NOT NULL COMMENT '聚合数据类型',
                                           `length` varchar(255) DEFAULT NULL COMMENT '聚合长度',
                                           `require` tinyint NOT NULL COMMENT '聚合必填 0:否 1:是',
                                           `policy_crud_json` json NOT NULL COMMENT '聚合可见性策略 ADD、EDIT、DETAILS、LIST_DTO、LIST_VO',
                                           `policy_query` tinyint NOT NULL COMMENT '聚合查询策略 0:等于 1:模糊',
                                           `policy_view` tinyint NOT NULL COMMENT '聚合显示策略 0:文本框 1:文本域 2:下拉 3:单 4:多 5:LD 6:LDT',
                                           `pk` tinyint NOT NULL COMMENT '是否主键 0:否 1:是',
                                           `remark` varchar(80) DEFAULT NULL COMMENT '聚合字段备注',
                                           `seq` int NOT NULL COMMENT '聚合排序',
                                           `create_time` datetime NOT NULL COMMENT '创建时间',
                                           `creator_id` bigint NOT NULL COMMENT '创建人ID',
                                           `update_time` datetime NOT NULL COMMENT '更新时间',
                                           `updater_id` bigint NOT NULL COMMENT '更新人ID',
                                           PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='输出方案聚合模型表';

-- 4. 挪动数据：新字段 pk 默认为 0
INSERT INTO `assembly_out_model_poly` (
    `id`,
    `output_schema_id`,
    `output_model_origin_id`,
    `name`,
    `kind`,
    `length`,
    `require`,
    `policy_crud_json`,
    `policy_query`,
    `policy_view`,
    `pk`,
    `remark`,
    `seq`,
    `create_time`,
    `creator_id`,
    `update_time`,
    `updater_id`
)
SELECT
    `id`,
    `output_schema_id`,
    `output_model_origin_id`,
    `name`,
    `kind`,
    `length`,
    `require`,
    `policy_crud_json`,
    `policy_query`,
    `policy_view`,
    0, -- 新增 pk 字段，默认设为 0
    `remark`,
    `seq`,
    `create_time`,
    `creator_id`,
    `update_time`,
    `updater_id`
FROM `zremovered_assembly_out_model_poly`;