DROP TABLE IF EXISTS core_post;
CREATE TABLE core_post(
                          `id` BIGINT NOT NULL  COMMENT '岗位ID' ,
                          `name` VARCHAR(32) NOT NULL  COMMENT '岗位名称' ,
                          `code` VARCHAR(32) NOT NULL  COMMENT '岗位编码' ,
                          `seq` INT NOT NULL  COMMENT '岗位排序' ,
                          `create_time` DATETIME NOT NULL  COMMENT '创建时间' ,
                          `creator_id` BIGINT NOT NULL  COMMENT '创建人ID' ,
                          `update_time` DATETIME NOT NULL  COMMENT '更新时间' ,
                          `updater_id` BIGINT NOT NULL  COMMENT '更新人ID' ,
                          `delete_time` DATETIME   COMMENT '删除时间 NULL未删' ,
                          PRIMARY KEY (id)
)  COMMENT = '岗位表';