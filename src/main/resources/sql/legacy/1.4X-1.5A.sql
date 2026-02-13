ALTER TABLE core_user
    ADD COLUMN dept_id bigint NULL COMMENT '部门ID';
ALTER TABLE core_user
    ADD COLUMN dept_name varchar(32) NULL COMMENT '部门名称';

DROP TABLE IF EXISTS core_dept;
CREATE TABLE core_dept
(
    `id`             BIGINT   NOT NULL COMMENT '部门ID',
    `parent_id`      BIGINT COMMENT '父级部门 NULL为顶级',
    `name`           VARCHAR(32) COMMENT '部门名',
    `principal_id`   BIGINT COMMENT '负责人ID',
    `principal_name` VARCHAR(255) COMMENT '负责人名称',
    `status`         TINYINT  NOT NULL COMMENT '部门状态 0:正常 1:禁用',
    `seq`            INT      NOT NULL COMMENT '排序',
    `create_time`    DATETIME NOT NULL COMMENT '创建时间',
    `creator_id`     BIGINT   NOT NULL COMMENT '创建人ID',
    `update_time`    DATETIME NOT NULL COMMENT '更新时间',
    `updater_id`     BIGINT   NOT NULL COMMENT '更新人ID',
    `delete_time`    DATETIME COMMENT '删除时间 NULL未删除',
    PRIMARY KEY (id)
) COMMENT = '部门表';


