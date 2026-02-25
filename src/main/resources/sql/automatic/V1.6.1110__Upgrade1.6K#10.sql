DROP TABLE IF EXISTS drive_space;
CREATE TABLE `drive_space` (
                               `id` bigint NOT NULL COMMENT '空间ID',
                               `root_id` bigint NOT NULL COMMENT '租户ID',
                               `dept_id` bigint NOT NULL COMMENT '部门ID',
                               `name` varchar(80) COLLATE utf8mb4_general_ci NOT NULL COMMENT '空间名称',
                               `remark` text COLLATE utf8mb4_general_ci COMMENT '空间描述',
                               `quota_limit` bigint NOT NULL COMMENT '配额限制(bytes)',
                               `quota_used` bigint NOT NULL COMMENT '已用配额(bytes)',
                               `status` tinyint NOT NULL COMMENT '状态 0:正常 1:归档',
                               `create_time` datetime NOT NULL COMMENT '创建时间',
                               `creator_id` bigint NOT NULL COMMENT '创建人',
                               `update_time` datetime NOT NULL COMMENT '更新时间',
                               `updater_id` bigint NOT NULL COMMENT '更新人',
                               `delete_time` datetime DEFAULT NULL COMMENT '删除时间 为NULL未删除',
                               PRIMARY KEY (`id`)
) COMMENT='云盘空间';

DROP TABLE IF EXISTS drive_space_member;
CREATE TABLE `drive_space_member` (
                                      `id` bigint NOT NULL COMMENT '云盘空间成员ID',
                                      `drive_space_id` bigint NOT NULL COMMENT '云盘空间ID',
                                      `member_kind` tinyint NOT NULL COMMENT '成员类型 0:用户 1:部门',
                                      `member_id` bigint NOT NULL COMMENT '成员ID',
                                      `role` tinyint NOT NULL COMMENT '成员角色 0:主管理员 1:行政管理员 2:编辑者 3:查看者',
                                      `create_time` datetime NOT NULL COMMENT '加入时间',
                                      `creator_id` bigint NOT NULL COMMENT '邀请人/操作人ID',
                                      `update_time` datetime NOT NULL COMMENT '更新时间',
                                      `updater_id` bigint NOT NULL COMMENT '更新人',
                                      PRIMARY KEY (`id`),
                                      UNIQUE KEY `uk_space_member` (`id`,`member_kind`,`member_id`),
                                      KEY `idx_member` (`member_kind`,`member_id`)
) COMMENT='云盘空间成员';