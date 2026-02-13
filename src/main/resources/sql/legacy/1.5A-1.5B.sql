
DROP TABLE IF EXISTS CORE_DEPT;

ALTER TABLE core_user DROP COLUMN dept_id;
ALTER TABLE core_user DROP COLUMN dept_name;

ALTER TABLE core_user ADD COLUMN dept_id bigint NULL COMMENT '部门ID';
ALTER TABLE core_user ADD COLUMN dept_name varchar(32) NULL COMMENT '部门名称';

ALTER TABLE core_user ADD COLUMN root_id bigint NULL COMMENT '所属企业ID';
ALTER TABLE core_user ADD COLUMN root_name varchar(32) NULL COMMENT '所属企业名称';

CREATE TABLE `core_org` (
                            `id` bigint NOT NULL COMMENT '主键id',
                            `root_id` bigint NOT NULL COMMENT '一级组织ID',
                            `parent_id` bigint DEFAULT NULL COMMENT '上级组织ID NULL顶级组织',
                            `org_path_ids` varchar(1024) DEFAULT NULL COMMENT '从顶级组织到当前组织的ID列表 以,分割',
                            `kind` tinyint NOT NULL COMMENT '0:部门 1:企业',
                            `name` varchar(128) NOT NULL COMMENT '组织机构名称',
                            `principal_id` bigint DEFAULT NULL COMMENT '主管ID',
                            `principal_name` varchar(32) DEFAULT NULL COMMENT '主管名称',
                            `seq` int NOT NULL DEFAULT '0' COMMENT '排序',
                            `create_time` datetime NOT NULL COMMENT '创建时间',
                            `creator_id` bigint NOT NULL COMMENT '创建人id',
                            `update_time` datetime NOT NULL COMMENT '更新时间',
                            `updater_id` bigint NOT NULL COMMENT '更新人id',
                            `delete_time` datetime DEFAULT NULL COMMENT '删除时间 NULL未删除',
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='组织机构-核心';
