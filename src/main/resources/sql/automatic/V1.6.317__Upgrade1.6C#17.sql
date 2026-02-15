DROP TABLE IF EXISTS auth_group_dept;
CREATE TABLE `auth_group_dept` (
                                   `group_id` bigint NOT NULL COMMENT '组ID',
                                   `dept_id` bigint NOT NULL COMMENT '部ID',
                                   `create_time` datetime NOT NULL COMMENT '创建时间',
                                   PRIMARY KEY (`group_id`,`dept_id`),
                                   UNIQUE KEY `group_id` (`group_id`,`dept_id`)
) COMMENT='GD表';