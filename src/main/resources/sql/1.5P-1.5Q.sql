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
                            `target_kind` TINYINT NOT NULL  COMMENT '接收人类型 0:全员 1:指定部门 2:指定用户' ,
                            `target_count` VARCHAR(255)   COMMENT '预计接收人数' ,
                            `forward` VARCHAR(320)   COMMENT '跳转URL/路由地址' ,
                            `params` JSON   COMMENT '动态参数 (JSON格式)' ,
                            `create_time` DATETIME NOT NULL  COMMENT '创建时间' ,
                            PRIMARY KEY (id)
)  COMMENT = '消息通知表';
