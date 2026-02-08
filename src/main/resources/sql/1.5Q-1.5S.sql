RENAME TABLE audit_login TO audit_login_rcd;

RENAME TABLE rdbg_request_log TO rdbg_request_rcd;
RENAME TABLE ep_document_sync_log TO ep_document_sync_rcd;

DROP TABLE IF EXISTS core_registry;
CREATE TABLE core_registry(
                              `id` BIGINT NOT NULL  COMMENT 'ID' ,
                              `parent_id` BIGINT   COMMENT '父级项ID NULL顶级' ,
                              `key_path` VARCHAR(1024) NOT NULL  COMMENT '条目Key的全路径' ,
                              `kind` TINYINT NOT NULL  COMMENT '类型 0:节点 1:条目' ,
                              `nkey` VARCHAR(128) NOT NULL  COMMENT '节点Key' ,
                              `nvalue_kind` TINYINT   COMMENT '数据类型 0:字串 1:整数 2:浮点 3:日期(LDT)' ,
                              `nvalue` LONGTEXT   COMMENT '节点Value' ,
                              `label` VARCHAR(32)   COMMENT '节点标签' ,
                              `remark` TEXT   COMMENT '说明' ,
                              `metadata` JSON   COMMENT '元数据JSON' ,
                              `is_system` TINYINT   COMMENT '内置值 0:否 1:是' ,
                              `status` TINYINT NOT NULL  COMMENT '状态 0:正常 1:停用' ,
                              `seq` INT NOT NULL  COMMENT '排序' ,
                              `create_time` DATETIME NOT NULL  COMMENT '创建时间' ,
                              `creator_id` BIGINT NOT NULL  COMMENT '创建人ID' ,
                              `update_time` DATETIME NOT NULL  COMMENT '更新时间' ,
                              `updater_id` BIGINT NOT NULL  COMMENT '更新人ID' ,
                              `delete_time` DATETIME   COMMENT '删除时间 NULL未删' ,
                              PRIMARY KEY (id)
)  COMMENT = '注册表';
