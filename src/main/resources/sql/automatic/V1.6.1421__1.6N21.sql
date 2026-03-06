DROP TABLE IF EXISTS gen_dat_source;
CREATE TABLE gen_dat_source(
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

DROP TABLE IF EXISTS gen_tym_schema;
CREATE TABLE gen_tym_schema(
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


DROP TABLE IF EXISTS gen_tym_schema_field;
CREATE TABLE gen_tym_schema_field(
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
