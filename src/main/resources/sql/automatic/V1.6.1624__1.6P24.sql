DROP TABLE IF EXISTS assembly_data_source;
CREATE TABLE assembly_data_source(
                                     `id` BIGINT NOT NULL  COMMENT '主键ID' ,
                                     `name` VARCHAR(32) NOT NULL  COMMENT '数据源名称' ,
                                     `code` VARCHAR(32) NOT NULL  COMMENT '数据源编码' ,
                                     `kind` TINYINT NOT NULL  COMMENT '数据源类型 0:MYSQL' ,
                                     `drive` VARCHAR(80) NOT NULL  COMMENT 'JDBC驱动' ,
                                     `url` TEXT NOT NULL  COMMENT '连接字符串' ,
                                     `username` VARCHAR(320)   COMMENT '连接用户名' ,
                                     `password` VARCHAR(1280)   COMMENT '连接密码' ,
                                     `db_schema` VARCHAR(80) NOT NULL  COMMENT '默认模式' ,
                                     `create_time` DATETIME NOT NULL  COMMENT '创建时间' ,
                                     `creator_id` BIGINT NOT NULL  COMMENT '创建人ID' ,
                                     `update_time` DATETIME NOT NULL  COMMENT '更新时间' ,
                                     `updater_id` BIGINT NOT NULL  COMMENT '更新人ID' ,
                                     PRIMARY KEY (id)
)  COMMENT = '数据源表';

DROP TABLE IF EXISTS assembly_tym_schema;
CREATE TABLE assembly_tym_schema(
                                    `id` BIGINT NOT NULL  COMMENT '主键ID' ,
                                    `name` VARCHAR(32) NOT NULL  COMMENT '方案名称' ,
                                    `code` VARCHAR(32) NOT NULL  COMMENT '方案编码' ,
                                    `map_source` VARCHAR(32) NOT NULL  COMMENT '映射源' ,
                                    `map_target` VARCHAR(32) NOT NULL  COMMENT '映射目标' ,
                                    `type_count` INT NOT NULL  COMMENT '类型数量' ,
                                    `default_type` VARCHAR(80) NOT NULL  COMMENT '默认类型' ,
                                    `seq` INT NOT NULL  COMMENT '排序' ,
                                    `remark` TEXT   COMMENT '备注' ,
                                    `create_time` DATETIME NOT NULL  COMMENT '创建时间' ,
                                    `creator_id` BIGINT NOT NULL  COMMENT '创建人ID' ,
                                    `update_time` DATETIME NOT NULL  COMMENT '更新时间' ,
                                    `updater_id` BIGINT NOT NULL  COMMENT '更新人ID' ,
                                    PRIMARY KEY (id)
)  COMMENT = '类型映射方案表';

DROP TABLE IF EXISTS assembly_tym_schema_field;
CREATE TABLE assembly_tym_schema_field(
                                          `id` BIGINT NOT NULL  COMMENT '主键ID' ,
                                          `type_schema_id` BIGINT NOT NULL  COMMENT '类型映射方案ID' ,
                                          `source` VARCHAR(80) NOT NULL  COMMENT '匹配源类型' ,
                                          `target` VARCHAR(80) NOT NULL  COMMENT '匹配目标类型' ,
                                          `seq` INT NOT NULL  COMMENT '排序' ,
                                          `create_time` DATETIME NOT NULL  COMMENT '创建时间' ,
                                          `creator_id` BIGINT NOT NULL  COMMENT '创建人ID' ,
                                          `update_time` DATETIME NOT NULL  COMMENT '更新时间' ,
                                          `updater_id` BIGINT NOT NULL  COMMENT '更新人ID' ,
                                          PRIMARY KEY (id)
)  COMMENT = '类型映射方案字段表';

DROP TABLE IF EXISTS assembly_scm;
CREATE TABLE assembly_scm(
                             `id` BIGINT NOT NULL  COMMENT '主键ID' ,
                             `name` VARCHAR(32) NOT NULL  COMMENT 'SCM名称' ,
                             `project_name` VARCHAR(80)   COMMENT '项目名称' ,
                             `code` VARCHAR(32) NOT NULL  COMMENT 'SCM编码' ,
                             `scm_url` VARCHAR(1000) NOT NULL  COMMENT 'SCM仓库地址' ,
                             `scm_auth_kind` TINYINT NOT NULL  COMMENT 'SCM认证方式 0:公开 1:账号密码 2:SSH KEY 3:PAT' ,
                             `scm_username` TEXT   COMMENT 'SCM用户名' ,
                             `scm_password` TEXT   COMMENT 'SCM密码' ,
                             `scm_pk` TEXT   COMMENT 'SSH KEY' ,
                             `scm_branch` VARCHAR(80) NOT NULL  COMMENT 'SCM分支' ,
                             `remark` TEXT   COMMENT 'SCM备注' ,
                             `create_time` DATETIME NOT NULL  COMMENT '创建时间' ,
                             `creator_id` BIGINT NOT NULL  COMMENT '创建人ID' ,
                             `update_time` DATETIME NOT NULL  COMMENT '更新时间' ,
                             `updater_id` BIGINT NOT NULL  COMMENT '更新人ID' ,
                             PRIMARY KEY (id)
)  COMMENT = 'SCM表';

DROP TABLE IF EXISTS assembly_out_schema;
CREATE TABLE assembly_out_schema(
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

DROP TABLE IF EXISTS assembly_out_model_origin;
CREATE TABLE assembly_out_model_origin(
    `id` BIGINT NOT NULL  COMMENT '主键ID' ,
    `output_schema_id` BIGINT NOT NULL  COMMENT '输出方案ID' ,
    `name` VARCHAR(255) NOT NULL  COMMENT '原始字段名' ,
    `kind` VARCHAR(255) NOT NULL  COMMENT '原始数据类型' ,
    `length` VARCHAR(255)   COMMENT '原始长度' ,
    `require` TINYINT NOT NULL  COMMENT '原始必填 0:否 1:是' ,
    `remark` VARCHAR(255)   COMMENT '原始备注' ,
    `pk` TINYINT NOT NULL  COMMENT '是否主键 0:否 1:是' ,
    `seq` INT NOT NULL  COMMENT '原始排序' ,
    `create_time` DATETIME NOT NULL  COMMENT '创建时间' ,
    `creator_id` BIGINT NOT NULL  COMMENT '创建人ID' ,
    `update_time` DATETIME NOT NULL  COMMENT '更新时间' ,
    `updater_id` BIGINT NOT NULL  COMMENT '更新人ID' ,
    PRIMARY KEY (id)
)  COMMENT = '输出方案原始模型表';


DROP TABLE IF EXISTS assembly_out_model_poly;
CREATE TABLE assembly_out_model_poly(
    `id` BIGINT NOT NULL  COMMENT '主键ID' ,
    `output_schema_id` BIGINT NOT NULL  COMMENT '输出方案ID' ,
    `output_model_origin_id` BIGINT NOT NULL  COMMENT '原始字段ID' ,
    `name` VARCHAR(255) NOT NULL  COMMENT '聚合字段名' ,
    `kind` VARCHAR(255) NOT NULL  COMMENT '聚合数据类型' ,
    `length` VARCHAR(255)   COMMENT '聚合长度' ,
    `require` TINYINT NOT NULL  COMMENT '聚合必填 0:否 1:是' ,
    `policy_crud_json` JSON NOT NULL  COMMENT '聚合可见性策略 ADD、EDIT、LIST_QUERY、LIST_VIEW' ,
    `policy_query` TINYINT NOT NULL  COMMENT '聚合查询策略 0:等于 1:模糊' ,
    `policy_view` TINYINT NOT NULL  COMMENT '聚合显示策略 0:文本框 1:文本域 2:下拉 3:单 4:多 5:LD 6:LDT' ,
    `pk` TINYINT NOT NULL  COMMENT '是否主键 0:否 1:是' ,
    `remark` VARCHAR(80) NOT NULL  COMMENT '聚合字段备注' ,
    `seq` INT NOT NULL  COMMENT '聚合排序' ,
    `create_time` DATETIME NOT NULL  COMMENT '创建时间' ,
    `creator_id` BIGINT NOT NULL  COMMENT '创建人ID' ,
    `update_time` DATETIME NOT NULL  COMMENT '更新时间' ,
    `updater_id` BIGINT NOT NULL  COMMENT '更新人ID' ,
    PRIMARY KEY (id)
)  COMMENT = '输出方案聚合模型表';
