DROP TABLE IF EXISTS qt_task_rcd;
CREATE TABLE qt_task_rcd(
                            `id` BIGINT NOT NULL  COMMENT '调度日志ID' ,
                            `task_id` BIGINT NOT NULL  COMMENT '任务ID' ,
                            `task_name` VARCHAR(80) NOT NULL  COMMENT '任务名' ,
                            `group_name` VARCHAR(80)   COMMENT '分组名' ,
                            `target` VARCHAR(1000) NOT NULL  COMMENT '调用目标' ,
                            `target_param` JSON   COMMENT '调用目标参数' ,
                            `target_result` LONGTEXT   COMMENT '调用目标返回内容(错误时为异常堆栈)' ,
                            `status` TINYINT NOT NULL  COMMENT '运行状态 0:正常 1:失败 2:超时 3:已调度' ,
                            `start_time` DATETIME NOT NULL  COMMENT '运行开始时间' ,
                            `end_time` DATETIME   COMMENT '运行结束时间' ,
                            `cost_time` INT   COMMENT '耗时(MS)' ,
                            `create_time` DATETIME NOT NULL  COMMENT '创建时间' ,
                            PRIMARY KEY (id)
)  COMMENT = '任务调度日志表';

DROP TABLE IF EXISTS qt_task;
CREATE TABLE qt_task(
                        `id` BIGINT NOT NULL  COMMENT '任务ID' ,
                        `group_id` BIGINT   COMMENT '任务分组ID' ,
                        `group_name` VARCHAR(80)   COMMENT '任务分组名' ,
                        `name` VARCHAR(80) NOT NULL  COMMENT '任务名' ,
                        `kind` TINYINT NOT NULL  COMMENT '0:本地BEAN 1:远程HTTP' ,
                        `cron` VARCHAR(64) NOT NULL  COMMENT 'CRON表达式' ,
                        `target` VARCHAR(1000) NOT NULL  COMMENT '调用目标(BEAN代码或HTTP地址)' ,
                        `target_param` JSON   COMMENT '调用参数JSON' ,
                        `req_method` VARCHAR(32)   COMMENT '请求方法' ,
                        `concurrent` TINYINT NOT NULL  COMMENT '并发执行 0:允许 1:禁止' ,
                        `policy_misfire` TINYINT NOT NULL  COMMENT '过期策略 0:放弃执行 1:立即执行 2:全部执行' ,
                        `policy_error` VARCHAR(255)   COMMENT '失败策略 0:默认 1:自动暂停' ,
                        `policy_rcd` VARCHAR(255)   COMMENT '日志策略 0:全部 1:仅异常 2:不记录' ,
                        `expire_time` DATETIME   COMMENT '任务有效期截止' ,
                        `last_exec_status` TINYINT   COMMENT '上次状态 0:成功 1:异常' ,
                        `last_start_time` DATETIME   COMMENT '上次开始时间' ,
                        `last_end_time` DATETIME   COMMENT '上次结束时间' ,
                        `status` TINYINT NOT NULL  COMMENT '0:正常 1:暂停 2:暂停(异常)' ,
                        `create_time` DATETIME NOT NULL  COMMENT '创建时间' ,
                        `creator_id` BIGINT NOT NULL  COMMENT '创建人ID' ,
                        `update_time` DATETIME NOT NULL  COMMENT '更新时间' ,
                        `updater_id` BIGINT NOT NULL  COMMENT '更新人ID' ,
                        `delete_time` DATETIME   COMMENT '删除时间 NULL未删' ,
                        PRIMARY KEY (id)
)  COMMENT = '任务调度表';
