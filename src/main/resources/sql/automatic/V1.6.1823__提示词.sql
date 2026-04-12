DROP TABLE IF EXISTS ep_prompt;
CREATE TABLE ep_prompt(
                          `id` BIGINT NOT NULL  COMMENT '主键ID' ,
                          `root_id` BIGINT NOT NULL  COMMENT '租户ID' ,
                          `dept_id` BIGINT NOT NULL  COMMENT '部门ID' ,
                          `name` VARCHAR(80) NOT NULL  COMMENT '名称' ,
                          `tags` JSON NOT NULL  COMMENT '标签(CTJ)' ,
                          `content` LONGTEXT   COMMENT '内容' ,
                          `param_count` INT NOT NULL  COMMENT '参数数量' ,
                          `version` INT NOT NULL  COMMENT '版本号' ,
                          `create_time` DATETIME NOT NULL  COMMENT '创建时间' ,
                          `creator_id` BIGINT NOT NULL  COMMENT '创建人ID' ,
                          `update_time` DATETIME NOT NULL  COMMENT '更新时间' ,
                          `updater_id` BIGINT NOT NULL  COMMENT '更新人ID' ,
                          `delete_time` DATETIME   COMMENT '删除时间' ,
                          PRIMARY KEY (id)
)  COMMENT = 'Prompt表';

CREATE UNIQUE INDEX uk_name ON ep_prompt(name,delete_time);
