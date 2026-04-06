DROP TABLE IF EXISTS assembly_poly_model;
CREATE TABLE assembly_poly_model(
                                    `id` BIGINT NOT NULL  COMMENT '主键ID' ,
                                    `output_schema_id` BIGINT NOT NULL  COMMENT '输出方案ID' ,
                                    `name` VARCHAR(255) NOT NULL  COMMENT '字段名' ,
                                    `data_type` VARCHAR(255) NOT NULL  COMMENT '数据类型' ,
                                    `length` INT   COMMENT '长度' ,
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
