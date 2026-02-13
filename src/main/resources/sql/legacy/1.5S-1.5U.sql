DROP TABLE IF EXISTS qt_task_rcd;
CREATE TABLE qt_task_rcd(
                            `id` BIGINT NOT NULL  COMMENT '调度日志ID' ,
                            `task_id` BIGINT NOT NULL  COMMENT '任务ID' ,
                            `task_name` VARCHAR(80) NOT NULL  COMMENT '任务名' ,
                            `group_name` VARCHAR(80)   COMMENT '分组名' ,
                            `target` VARCHAR(1000) NOT NULL  COMMENT '调用目标' ,
                            `target_param` JSON   COMMENT '调用目标参数' ,
                            `target_result` LONGTEXT   COMMENT '调用目标返回内容(错误时为异常堆栈)' ,
                            `status` TINYINT   COMMENT '运行状态 0:正常 1:失败 2:超时 3:已调度' ,
                            `start_time` DATETIME NOT NULL  COMMENT '运行开始时间' ,
                            `end_time` DATETIME   COMMENT '运行结束时间' ,
                            `cost_time` INT   COMMENT '耗时(MS)' ,
                            `create_time` DATETIME NOT NULL  COMMENT '创建时间' ,
                            PRIMARY KEY (id)
)  COMMENT = '任务调度日志表';
