DROP TABLE IF EXISTS auth_user_session;
CREATE TABLE auth_user_session(
                                  `id` BIGINT NOT NULL  COMMENT '会话ID' ,
                                  `session_id` VARCHAR(128) NOT NULL  COMMENT '用户凭据' ,
                                  `user_id` BIGINT NOT NULL  COMMENT '用户ID' ,
                                  `root_id` BIGINT   COMMENT '所属企业ID' ,
                                  `root_name` VARCHAR(32)   COMMENT '所属企业名' ,
                                  `dept_id` BIGINT   COMMENT '所属部门ID' ,
                                  `dept_name` VARCHAR(32)   COMMENT '所属部门名' ,
                                  `company_id` BIGINT   COMMENT '公司ID' ,
                                  `permissions` JSON NOT NULL  COMMENT '用户权限代码JSON' ,
                                  `rs_max` TINYINT NOT NULL  COMMENT '最大RowScope等级 0:全部 1:本公司/租户及以下 2:本部门及以下 3:本部门 4:仅本人 5:指定部门' ,
                                  `rs_allow_depts` JSON NOT NULL  COMMENT 'RowScope允许访问的部门IDS' ,
                                  `expires_at` DATETIME NOT NULL  COMMENT '会话过期时间' ,
                                  `create_time` DATETIME NOT NULL  COMMENT '创建时间' ,
                                  `creator_id` BIGINT NOT NULL  COMMENT '创建者ID(用于兼容RS数据权限 必须与UserId一致)' ,
                                  `update_time` DATETIME NOT NULL  COMMENT '修改时间' ,
                                  PRIMARY KEY (id)
)  COMMENT = '用户会话表';