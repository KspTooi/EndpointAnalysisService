-- =================================================================================
-- 1. 删除可能存在的旧备份表
-- =================================================================================
DROP TABLE IF EXISTS `zremovred_assembly_op_schema`;

-- =================================================================================
-- 2. 重命名当前旧表，增加 zremovred_ 前缀
-- =================================================================================
RENAME TABLE `assembly_op_schema` TO `zremovred_assembly_op_schema`;

-- =================================================================================
-- 3. 创建新表 (根据最新定义)
-- =================================================================================
CREATE TABLE `assembly_op_schema` (
                                      `id` BIGINT NOT NULL COMMENT '主键ID',
                                      `data_source_id` BIGINT COMMENT '数据源ID',
                                      `type_schema_id` BIGINT COMMENT '类型映射方案ID',
                                      `input_scm_id` BIGINT COMMENT '输入SCM ID',
                                      `output_scm_id` BIGINT COMMENT '输出SCM ID',
                                      `name` VARCHAR(32) NOT NULL COMMENT '输出方案名称',
                                      `model_name` VARCHAR(255) NOT NULL COMMENT '模型名称',
                                      `model_remark` VARCHAR(80) NOT NULL COMMENT '模型描述',
                                      `biz_domain` VARCHAR(80) NOT NULL COMMENT '业务域',
                                      `table_name` VARCHAR(80) COMMENT '数据源表名',
                                      `remove_table_prefix` VARCHAR(80) COMMENT '移除表前缀',
                                      `perm_code_prefix` VARCHAR(32) NOT NULL COMMENT '权限码前缀',
                                      `policy_override` TINYINT NOT NULL COMMENT '写出策略 0:不覆盖 1:覆盖',
                                      `base_input` VARCHAR(320) NOT NULL COMMENT '输入基准路径',
                                      `base_output` VARCHAR(320) NOT NULL COMMENT '输出基准路径',
                                      `remark` TEXT COMMENT '备注',
                                      `field_count_origin` INT NOT NULL COMMENT '字段数(原始)',
                                      `field_count_poly` INT NOT NULL COMMENT '字段数(聚合)',
                                      `create_time` DATETIME NOT NULL COMMENT '创建时间',
                                      `creator_id` BIGINT NOT NULL COMMENT '创建人ID',
                                      `update_time` DATETIME NOT NULL COMMENT '更新时间',
                                      `updater_id` BIGINT NOT NULL COMMENT '更新人ID',
                                      PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='输出方案表';

-- =================================================================================
-- 4. 挪动数据 (处理新增的 NOT NULL 字段)
-- =================================================================================
INSERT INTO `assembly_op_schema` (
    `id`, `data_source_id`, `type_schema_id`, `input_scm_id`, `output_scm_id`,
    `name`, `model_name`, `model_remark`, `biz_domain`, `table_name`,
    `remove_table_prefix`, `perm_code_prefix`, `policy_override`,
    `base_input`, `base_output`, `remark`, `field_count_origin`,
    `field_count_poly`, `create_time`, `creator_id`, `update_time`, `updater_id`
)
SELECT
    `id`, `data_source_id`, `type_schema_id`, `input_scm_id`, `output_scm_id`,
    `name`, `model_name`,
    '', -- model_remark: 旧数据初始化为空字符串
    '', -- biz_domain: 旧数据初始化为空字符串
    `table_name`, `remove_table_prefix`, `perm_code_prefix`, `policy_override`,
    `base_input`, `base_output`, `remark`, `field_count_origin`,
    `field_count_poly`, `create_time`, `creator_id`, `update_time`, `updater_id`
FROM `zremovred_assembly_op_schema`;

-- =================================================================================
-- 修改项说明 (Notes)
-- =================================================================================
/*
1. 新增字段 (Additions):
   - `model_remark`: VARCHAR(80), NOT NULL, 含义为 '模型描述'。
   - `biz_domain`: VARCHAR(80), NOT NULL, 含义为 '业务域'。

2. 数据兼容性处理:
   - 由于新字段 `model_remark` 和 `biz_domain` 设置为 NOT NULL 且旧表无数据，
     迁移过程中将这两项默认值设置为空字符串 `''` 以确保插入成功。

3. 字符集优化:
   - 将原有的 `utf8mb4_general_ci` 变更为 MySQL 8.0 推荐的 `utf8mb4_0900_ai_ci`（表级别）。

4. 流程确认:
   - 原表数据已完整重命名为 `zremovred_assembly_op_schema` 进行物理备份。
   - 数据导入完成后，请检查业务功能是否需要对 `biz_domain` 进行初始化补丁更新。
*/