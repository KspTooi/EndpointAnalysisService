DROP TABLE IF EXISTS gen_out_blueprint;
CREATE TABLE gen_out_blueprint(
                                  `id` BIGINT NOT NULL  COMMENT '主键ID' ,
                                  `name` VARCHAR(32) NOT NULL  COMMENT '蓝图名称' ,
                                  `project_name` VARCHAR(80)   COMMENT '项目名称' ,
                                  `code` VARCHAR(32) NOT NULL  COMMENT '蓝图编码' ,
                                  `scm_url` VARCHAR(1000) NOT NULL  COMMENT 'SCM仓库地址' ,
                                  `scm_auth_kind` TINYINT NOT NULL  COMMENT 'SCM认证方式 0:公开 1:账号密码 2:SSH KEY 3:PAT' ,
                                  `scm_username` TEXT   COMMENT 'SCM用户名' ,
                                  `scm_password` TEXT   COMMENT 'SCM密码' ,
                                  `scm_pk` TEXT   COMMENT 'SSH KEY' ,
                                  `scm_branch` VARCHAR(80) NOT NULL  COMMENT 'SCM分支' ,
                                  `scm_base_path` VARCHAR(1280) NOT NULL  COMMENT '基准路径' ,
                                  `remark` TEXT   COMMENT '蓝图备注' ,
                                  `create_time` DATETIME NOT NULL  COMMENT '创建时间' ,
                                  `creator_id` BIGINT NOT NULL  COMMENT '创建人ID' ,
                                  `update_time` DATETIME NOT NULL  COMMENT '更新时间' ,
                                  `updater_id` BIGINT NOT NULL  COMMENT '更新人ID' ,
                                  PRIMARY KEY (id)
)  COMMENT = '输出蓝图表';
