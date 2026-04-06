DROP TABLE IF EXISTS assembly_raw_model;
CREATE TABLE assembly_raw_model(
                                   `id` BIGINT NOT NULL  COMMENT '主键ID' ,
                                   `output_schema_id` BIGINT NOT NULL  COMMENT '输出方案ID' ,
                                   `name` VARCHAR(255) NOT NULL  COMMENT '字段名' ,
                                   `data_type` VARCHAR(255) NOT NULL  COMMENT '数据类型' ,
                                   `length` INT   COMMENT '长度' ,
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
