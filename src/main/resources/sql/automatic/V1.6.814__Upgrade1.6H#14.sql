SET FOREIGN_KEY_CHECKS = 0;

-- =============================================
-- 1. 删除可能存在的带有 new_ 前缀的新表
-- =============================================
DROP TABLE IF EXISTS `new_core_user`;
DROP TABLE IF EXISTS `zremoved_core_user`;

-- =============================================
-- 2. 创建新表 (前缀加 new_)
-- =============================================
CREATE TABLE `new_core_user` (
                                 `id` BIGINT NOT NULL COMMENT '用户ID',
                                 `username` VARCHAR(80) NOT NULL COMMENT '用户名',
                                 `password` VARCHAR(1280) NOT NULL COMMENT '密码',
                                 `nickname` VARCHAR(80) COMMENT '昵称',
                                 `gender` TINYINT COMMENT '性别 0:男 1:女 2:不愿透露',
                                 `phone` VARCHAR(80) COMMENT '手机号码',
                                 `email` VARCHAR(80) COMMENT '邮箱',
                                 `login_count` INT NOT NULL COMMENT '登录次数',
                                 `status` TINYINT NOT NULL COMMENT '用户状态 0:正常 1:封禁',
                                 `last_login_time` DATETIME COMMENT '最后登录时间',
                                 `root_id` BIGINT COMMENT '所属企业ID',
                                 `root_name` VARCHAR(32) COMMENT '所属企业名称',
                                 `dept_id` BIGINT COMMENT '部门ID',
                                 `dept_name` VARCHAR(255) COMMENT '部门名称',
                                 `active_company_id` BIGINT COMMENT '已激活的公司ID(兼容字段)',
                                 `active_env_id` BIGINT COMMENT '已激活的环境ID(兼容字段)',
                                 `avatar_attach_id` BIGINT COMMENT '用户头像附件ID',
                                 `is_system` TINYINT NOT NULL DEFAULT 0 COMMENT '内置用户 0:否 1:是',
                                 `data_version` BIGINT NOT NULL COMMENT '数据版本号',
                                 `create_time` DATETIME NOT NULL COMMENT '创建时间',
                                 `creator_id` BIGINT NOT NULL COMMENT '创建人ID',
                                 `update_time` DATETIME NOT NULL COMMENT '修改时间',
                                 `updater_id` BIGINT NOT NULL COMMENT '更新人ID',
                                 `delete_time` DATETIME COMMENT '删除时间 为NULL未删',
                                 PRIMARY KEY (id)
) COMMENT = '用户表';

-- =============================================
-- 3. 挪动旧表数据到新表 (字段一一对应)
-- =============================================
INSERT INTO `new_core_user` (
    id, username, password, nickname, gender, phone, email,
    login_count, status, last_login_time, root_id, root_name,
    dept_id, dept_name, active_company_id, active_env_id,
    avatar_attach_id, is_system, data_version, create_time,
    creator_id, update_time, updater_id, delete_time
)
SELECT
    id, username, password, nickname, gender, phone, email,
    login_count, status, last_login_time, root_id, root_name,
    dept_id, dept_name, active_company_id, active_env_id,
    avatar_attach_id, is_system, 0, create_time,
    0, update_time, 0, delete_time
FROM `core_user`;

-- =============================================
-- 4. 重命名旧表，增加 zremoved_ 前缀
-- =============================================
ALTER TABLE `core_user` RENAME TO `zremoved_core_user`;

-- =============================================
-- 5. 重命名新表，去掉 new_ 前缀
-- =============================================
ALTER TABLE `new_core_user` RENAME TO `core_user`;

SET FOREIGN_KEY_CHECKS = 1;