DROP TABLE IF EXISTS core_user_session;
CREATE TABLE core_user_session(
                                  `id` BIGINT AUTO_INCREMENT COMMENT '会话ID' ,
                                  `session_id` VARCHAR(128) NOT NULL  COMMENT '用户凭据SessionID' ,
                                  `user_id` BIGINT NOT NULL  COMMENT '用户ID' ,
                                  `root_id` BIGINT NOT NULL  COMMENT '所属企业ID' ,
                                  `root_name` VARCHAR(32) NOT NULL  COMMENT '所属企业名' ,
                                  `dept_id` BIGINT NOT NULL  COMMENT '所属部门ID' ,
                                  `dept_name` VARCHAR(32) NOT NULL  COMMENT '所属部门名' ,
                                  `company_id` BIGINT   COMMENT '公司ID' ,
                                  `permissions` JSON NOT NULL  COMMENT '用户权限代码JSON' ,
                                  `expires_at` DATETIME NOT NULL  COMMENT '过期时间' ,
                                  `create_time` DATETIME NOT NULL  COMMENT '创建时间' ,
                                  `update_time` DATETIME NOT NULL  COMMENT '修改时间' ,
                                  PRIMARY KEY (id)
)  COMMENT = '用户会话表';


DROP TABLE IF EXISTS audit_login;
CREATE TABLE audit_login(
                            `id` BIGINT NOT NULL  COMMENT '登录日志主键' ,
                            `user_id` BIGINT NULL  COMMENT '用户ID' ,
                            `username` VARCHAR(50) NOT NULL  COMMENT '用户账号' ,
                            `login_kind` TINYINT NOT NULL  COMMENT '登录方式 0:用户名密码' ,
                            `ip_addr` VARCHAR(128) NOT NULL  COMMENT '登录 IP' ,
                            `location` VARCHAR(320) NOT NULL  COMMENT 'IP 归属地' ,
                            `browser` VARCHAR(255) NOT NULL  COMMENT '浏览器/客户端指纹' ,
                            `os` VARCHAR(128) NOT NULL  COMMENT '操作系统' ,
                            `status` TINYINT NOT NULL  COMMENT '状态: 0:成功 1:失败' ,
                            `message` VARCHAR(128) NOT NULL  COMMENT '提示消息' ,
                            `create_time` DATETIME NOT NULL  COMMENT '创建时间'
)  COMMENT = '登录审计日志';