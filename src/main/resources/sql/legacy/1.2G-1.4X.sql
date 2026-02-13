ALTER TABLE core_user
    ADD COLUMN gender tinyint NULL COMMENT '性别 0:男 1:女 2:不愿透露';

ALTER TABLE core_user
    ADD COLUMN phone varchar(64) NULL COMMENT '手机号码';

ALTER TABLE core_user
    ADD COLUMN avatar_attach_id bigint NULL COMMENT '用户头像附件ID';

ALTER TABLE core_user
    ADD COLUMN delete_time DATETIME NULL COMMENT '删除时间 为NULL未删';

ALTER TABLE core_user
    ADD COLUMN is_system TINYINT NULL COMMENT '内置用户 0:否 1:是';

UPDATE core_user
SET is_system = 0;

ALTER TABLE core_user
    MODIFY COLUMN is_system TINYINT NOT NULL DEFAULT 0 COMMENT '内置用户 0:否 1:是';

UPDATE core_user
SET is_system = 1
WHERE username = "admin"
   OR username = "guest";

CREATE TABLE `rdbg_collection_history`
(
    `id`                  bigint                                  NOT NULL COMMENT '记录ID',
    `company_id`          bigint                                  NOT NULL COMMENT '公司ID',
    `collection_id`       bigint                                  NOT NULL COMMENT '集合ID',
    `req_url`             varchar(320) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '请求URL',
    `req_url_params_json` json                                    NOT NULL COMMENT '请求URL查询参数 类型:RelayParam',
    `req_method`          tinyint                                 NOT NULL COMMENT '请求方法 0:GET 1:POST 2:PUT 3:PATCH 4:DELETE 5:HEAD 6:OPTIONS',
    `req_header_json`     json                                    NOT NULL COMMENT '请求头JSON 类型:RelayHeader',
    `req_body_json`       json                                    NOT NULL COMMENT '请求体JSON 类型:RelayBody',
    `ret_header_json`     json                                    NOT NULL COMMENT '响应头JSON 类型:RelayHeader',
    `ret_body_text`       longtext COLLATE utf8mb4_unicode_ci     NOT NULL COMMENT '响应体文本',
    `ret_http_status`     int      DEFAULT NULL COMMENT 'HTTP状态码 NULL请求尚未完成',
    `biz_status`          tinyint                                 NOT NULL COMMENT '业务状态 0:正常 1:HTTP失败 2:业务失败 3:正在处理 4:EAS内部错误',
    `error_message`       longtext COLLATE utf8mb4_unicode_ci COMMENT 'EAS内部错误消息 NULL未发生错误',
    `req_time`            datetime                                NOT NULL COMMENT '请求发起时间',
    `ret_time`            datetime DEFAULT NULL COMMENT '响应时间',
    `create_time`         datetime                                NOT NULL COMMENT '创建时间',
    `creator_id`          bigint                                  NOT NULL COMMENT '创建人ID',
    `update_time`         datetime                                NOT NULL COMMENT '更新时间',
    `updater_id`          bigint                                  NOT NULL COMMENT '更新人ID',
    `delete_time`         datetime DEFAULT NULL COMMENT '删除时间 NULL未删除',
    PRIMARY KEY (`id`),
    KEY `idx_collection_id` (`collection_id`),
    KEY `idx_company_id` (`company_id`),
    KEY `idx_delete_time` (`delete_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='请求集合历史记录表';