ALTER TABLE `gen_out_schema` MODIFY COLUMN `remove_table_prefix` varchar(80) NULL COMMENT '移除表前缀';

DROP TABLE IF EXISTS gen_out_model_poly;
CREATE TABLE gen_out_model_poly(
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
    `remark` VARCHAR(80)   COMMENT '聚合字段备注' ,
    `seq` INT NOT NULL  COMMENT '聚合排序' ,
    `create_time` DATETIME NOT NULL  COMMENT '创建时间' ,
    `creator_id` BIGINT NOT NULL  COMMENT '创建人ID' ,
    `update_time` DATETIME NOT NULL  COMMENT '更新时间' ,
    `updater_id` BIGINT NOT NULL  COMMENT '更新人ID' ,
    PRIMARY KEY (id)
)  COMMENT = '输出方案聚合模型表';