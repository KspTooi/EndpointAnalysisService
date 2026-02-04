DROP TABLE IF EXISTS ep_site;
CREATE TABLE ep_site
(
    `id`          BIGINT COMMENT '站点ID',
    `name`        VARCHAR(32) NOT NULL COMMENT '站点名称',
    `address`     VARCHAR(255) COMMENT '地址',
    `username`    VARCHAR(500) COMMENT '账户',
    `password`    VARCHAR(500) COMMENT '密码',
    `remark`      VARCHAR(1000) COMMENT '备注',
    `seq`         INT         NOT NULL COMMENT '排序',
    `create_time` DATETIME    NOT NULL COMMENT '创建时间',
    `creator_id`  BIGINT      NOT NULL COMMENT '创建人ID',
    `update_time` DATETIME    NOT NULL COMMENT '更新时间',
    `updater_id`  BIGINT      NOT NULL COMMENT '更新人ID'
) COMMENT = '站点';
