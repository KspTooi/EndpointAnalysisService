DROP TABLE IF EXISTS core_notice;
CREATE TABLE core_notice(
                            `id` BIGINT NOT NULL  COMMENT '主键ID' ,
                            `title` VARCHAR(32) NOT NULL  COMMENT '标题' ,
                            `kind` TINYINT NOT NULL  COMMENT '种类: 0公告, 1业务提醒, 2私信' ,
                            `content` LONGTEXT   COMMENT '通知内容' ,
                            `priority` TINYINT NOT NULL  COMMENT '优先级: 0:低 1:中 2:高' ,
                            `category` VARCHAR(32)   COMMENT '业务类型/分类' ,
                            `sender_id` BIGINT   COMMENT '发送人ID (NULL为系统)' ,
                            `sender_name` VARCHAR(32)   COMMENT '发送人姓名' ,
                            `forward` VARCHAR(320)   COMMENT '跳转URL/路由地址' ,
                            `params` JSON   COMMENT '动态参数 (JSON格式)' ,
                            `create_time` DATETIME NOT NULL  COMMENT '创建时间' ,
                            PRIMARY KEY (id),
                            KEY `idx_create_time` (`create_time`),
                            KEY `idx_kind` (`kind`)
)  COMMENT = '消息表';


DROP TABLE IF EXISTS core_notice_rcd;
CREATE TABLE core_notice_rcd(
                                `id` BIGINT NOT NULL  COMMENT '主键ID' ,
                                `notice_id` BIGINT NOT NULL  COMMENT '关联通知ID' ,
                                `user_id` BIGINT NOT NULL  COMMENT '接收人用户ID' ,
                                `read_time` DATETIME NOT NULL  COMMENT '读取时间 (NULL代表未读)' ,
                                `create_time` DATETIME NOT NULL  COMMENT '下发时间' ,
                                `delete_time` DATETIME   COMMENT '删除时间 (NULL代表未删)' ,
                                PRIMARY KEY (id),
                                KEY `idx_user_read_status` (`user_id`, `read_time`, `delete_time`),
                                KEY `idx_notice_id` (`notice_id`)
)  COMMENT = '用户通知记录(接收状态)';


DROP TABLE IF EXISTS core_notice_template;
CREATE TABLE core_notice_template(
                                     `id` BIGINT NOT NULL  COMMENT '主键ID' ,
                                     `name` VARCHAR(32) NOT NULL  COMMENT '模板名称' ,
                                     `code` VARCHAR(32) NOT NULL  COMMENT '模板唯一编码 (业务调用用)' ,
                                     `content` LONGTEXT NOT NULL  COMMENT '模板内容 (含占位符)' ,
                                     `status` TINYINT NOT NULL  COMMENT '状态: 0启用, 1禁用' ,
                                     `remark` VARCHAR(1000)   COMMENT '备注' ,
                                     `create_time` DATETIME NOT NULL  COMMENT '创建时间' ,
                                     `creator_id` BIGINT NOT NULL  COMMENT '创建人ID' ,
                                     `update_time` DATETIME NOT NULL  COMMENT '更新时间' ,
                                     `updater_id` BIGINT NOT NULL  COMMENT '更新人ID' ,
                                     `delete_time` DATETIME   COMMENT '删除时间 NULL未删' ,
                                     PRIMARY KEY (id),
                                     UNIQUE KEY `uk_code` (`code`) COMMENT '确保编码唯一'
)  COMMENT = '通知模板表';
