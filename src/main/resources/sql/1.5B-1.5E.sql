DROP TABLE IF EXISTS CORE_EXCEL_TEMPLATE;
CREATE TABLE CORE_EXCEL_TEMPLATE
(
    `id`          BIGINT      NOT NULL COMMENT '模板ID',
    `attach_id`   BIGINT      NOT NULL COMMENT '模板附件ID',
    `name`        VARCHAR(32) NOT NULL COMMENT '模板名称',
    `code`        VARCHAR(32) NOT NULL COMMENT '模板标识 唯一',
    `remark`      text COMMENT '模板备注',
    `status`      TINYINT     NOT NULL COMMENT '状态 0:启用 1:禁用',
    `create_time` DATETIME    NOT NULL COMMENT '创建时间',
    `creator_id`  BIGINT      NOT NULL COMMENT '创建人ID',
    `update_time` DATETIME    NOT NULL COMMENT '更新时间',
    `updater_id`  BIGINT      NOT NULL COMMENT '更新人ID',
    `delete_time` DATETIME COMMENT '删除时间 NULL未删除'
) COMMENT = '导入模板表';
