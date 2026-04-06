DROP TABLE IF EXISTS `assembly_out_model_origin`;
DROP TABLE IF EXISTS `assembly_out_model_poly`;
DROP TABLE IF EXISTS assembly_out_schema;

DROP TABLE IF EXISTS assembly_op_schema;
CREATE TABLE assembly_op_schema(
                                   `id` BIGINT NOT NULL  COMMENT '主键ID' ,
                                   `data_source_id` BIGINT   COMMENT '数据源ID' ,
                                   `type_schema_id` BIGINT   COMMENT '类型映射方案ID' ,
                                   `input_scm_id` BIGINT   COMMENT '输入SCM ID' ,
                                   `output_scm_id` BIGINT   COMMENT '输出SCM ID' ,
                                   `name` VARCHAR(32) NOT NULL  COMMENT '输出方案名称' ,
                                   `model_name` VARCHAR(255) NOT NULL  COMMENT '模型名称' ,
                                   `table_name` VARCHAR(80)   COMMENT '数据源表名' ,
                                   `remove_table_prefix` VARCHAR(80)   COMMENT '移除表前缀' ,
                                   `perm_code_prefix` VARCHAR(32) NOT NULL  COMMENT '权限码前缀' ,
                                   `policy_override` TINYINT NOT NULL  COMMENT '写出策略 0:不覆盖 1:覆盖' ,
                                   `base_input` VARCHAR(320) NOT NULL  COMMENT '输入基准路径' ,
                                   `base_output` VARCHAR(320) NOT NULL  COMMENT '输出基准路径' ,
                                   `remark` TEXT   COMMENT '备注' ,
                                   `field_count_origin` INT NOT NULL  COMMENT '字段数(原始)' ,
                                   `field_count_poly` INT NOT NULL  COMMENT '字段数(聚合)' ,
                                   `create_time` DATETIME NOT NULL  COMMENT '创建时间' ,
                                   `creator_id` BIGINT NOT NULL  COMMENT '创建人ID' ,
                                   `update_time` DATETIME NOT NULL  COMMENT '更新时间' ,
                                   `updater_id` BIGINT NOT NULL  COMMENT '更新人ID' ,
                                   PRIMARY KEY (id)
)  COMMENT = '输出方案表';

DROP TABLE IF EXISTS assembly_raw_model;
CREATE TABLE assembly_raw_model(
                                   `id` BIGINT NOT NULL  COMMENT '主键ID' ,
                                   `output_schema_id` BIGINT NOT NULL  COMMENT '输出方案ID' ,
                                   `name` VARCHAR(255) NOT NULL  COMMENT '字段名' ,
                                   `data_type` VARCHAR(255) NOT NULL  COMMENT '数据类型' ,
                                   `length` VARCHAR(255)   COMMENT '长度' ,
                                   `require` TINYINT NOT NULL  COMMENT '必填 0:否 1:是' ,
                                   `remark` VARCHAR(255)   COMMENT '备注' ,
                                   `pk` TINYINT NOT NULL  COMMENT '是否主键 0:否 1:是' ,
                                   `seq` INT NOT NULL  COMMENT '排序' ,
                                   `create_time` DATETIME NOT NULL  COMMENT '创建时间' ,
                                   `creator_id` BIGINT NOT NULL  COMMENT '创建人ID' ,
                                   `update_time` DATETIME NOT NULL  COMMENT '更新时间' ,
                                   `updater_id` BIGINT NOT NULL  COMMENT '更新人ID' ,
                                   PRIMARY KEY (id)
)  COMMENT = '原始模型表';

DROP TABLE IF EXISTS assembly_poly_model;
CREATE TABLE assembly_poly_model(
                                    `id` BIGINT NOT NULL  COMMENT '主键ID' ,
                                    `output_schema_id` BIGINT NOT NULL  COMMENT '输出方案ID' ,
                                    `name` VARCHAR(255) NOT NULL  COMMENT '字段名' ,
                                    `data_type` VARCHAR(255) NOT NULL  COMMENT '数据类型' ,
                                    `length` VARCHAR(255)   COMMENT '长度' ,
                                    `require` TINYINT NOT NULL  COMMENT '必填 0:否 1:是' ,
                                    `policy_crud_json` JSON NOT NULL  COMMENT '可见性策略 ADD、EDIT、LIST_QUERY、LIST_VIEW' ,
                                    `policy_query` TINYINT NOT NULL  COMMENT '查询策略 0:等于' ,
                                    `policy_view` TINYINT NOT NULL  COMMENT '显示策略 0:文本框 1:文本域 2:下拉 3:单 4:多 5:LD 6:LDT' ,
                                    `pk` TINYINT NOT NULL  COMMENT '是否主键 0:否 1:是' ,
                                    `remark` VARCHAR(80) NOT NULL  COMMENT '字段备注' ,
                                    `seq` INT NOT NULL  COMMENT '排序' ,
                                    `create_time` DATETIME NOT NULL  COMMENT '创建时间' ,
                                    `creator_id` BIGINT NOT NULL  COMMENT '创建人ID' ,
                                    `update_time` DATETIME NOT NULL  COMMENT '更新时间' ,
                                    `updater_id` BIGINT NOT NULL  COMMENT '更新人ID' ,
                                    PRIMARY KEY (id)
)  COMMENT = '聚合模型表';
