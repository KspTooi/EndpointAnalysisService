DROP TABLE IF EXISTS EP_STD_WORD;
CREATE TABLE EP_STD_WORD(
                            `id` BIGINT NOT NULL  COMMENT '标准词ID' ,
                            `source_name` VARCHAR(128) NOT NULL  COMMENT '简称' ,
                            `source_name_full` VARCHAR(255)   COMMENT '全称' ,
                            `target_name` VARCHAR(128) NOT NULL  COMMENT '英文简称' ,
                            `target_name_full` VARCHAR(128)   COMMENT '英文全称' ,
                            `remark` text   COMMENT '备注' ,
                            `create_time` DATETIME NOT NULL  COMMENT '创建时间' ,
                            `creator_id` BIGINT NOT NULL  COMMENT '创建人ID' ,
                            `update_time` DATETIME NOT NULL  COMMENT '更新时间' ,
                            `updater_id` BIGINT NOT NULL  COMMENT '更新人ID'
)  COMMENT = '标准词管理';
