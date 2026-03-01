ALTER TABLE `auth_group` MODIFY COLUMN `id` BIGINT NOT NULL COMMENT '组ID';
ALTER TABLE `auth_permission` MODIFY COLUMN `id` BIGINT NOT NULL COMMENT '权限ID';

-- 处理 用户-组 关联表
DROP TABLE IF EXISTS `auth_user_group`;
CREATE TABLE `auth_user_group` (
                                   `user_id` bigint NOT NULL COMMENT '用户ID',
                                   `group_id` bigint NOT NULL COMMENT '用户组ID',
                                   PRIMARY KEY (`user_id`,`group_id`),
                                   UNIQUE KEY `user_id` (`user_id`,`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='UG表';

-- 处理 组-权限 关联表
DROP TABLE IF EXISTS `auth_group_permission`;
CREATE TABLE `auth_group_permission` (
                                         `group_id` bigint NOT NULL COMMENT '用户组ID',
                                         `permission_id` bigint NOT NULL COMMENT '权限ID',
                                         PRIMARY KEY (`group_id`,`permission_id`),
                                         UNIQUE KEY `group_id` (`group_id`,`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='GP表';

-- 迁移用户组数据
INSERT INTO `auth_user_group` (`user_id`, `group_id`)
SELECT `user_id`, `group_id` FROM `core_user_group`;

-- 迁移组权限数据
INSERT INTO `auth_group_permission` (`group_id`, `permission_id`)
SELECT `group_id`, `permission_id` FROM `core_group_permission`;

-- 重命名旧表
RENAME TABLE `core_user_group` TO `zremoved_core_user_group`;
RENAME TABLE `core_group_permission` TO `zremoved_core_group_permission`;