DROP TABLE IF EXISTS core_menu;
CREATE TABLE core_menu(
                          `id` BIGINT NOT NULL  COMMENT '主键ID' ,
                          `root_id` BIGINT NOT NULL  COMMENT '租户ID' ,
                          `dept_id` BIGINT NOT NULL  COMMENT '部门ID' ,
                          `parent_id` BIGINT   COMMENT '父级项ID' ,
                          `name` VARCHAR(32) NOT NULL  COMMENT '菜单项名' ,
                          `kind` TINYINT NOT NULL  COMMENT '菜单项类型 0:目录 1:菜单 2:按钮' ,
                          `path` VARCHAR(80)   COMMENT '指向路径' ,
                          `icon` VARCHAR(80) NOT NULL  COMMENT '菜单图标' ,
                          `hide` TINYINT NOT NULL  COMMENT '隐藏 0:否 1:是' ,
                          `permission_code` VARCHAR(500)   COMMENT '所需权限码(多个逗号)' ,
                          `seq` INT NOT NULL  COMMENT '排序' ,
                          `remark` TEXT   COMMENT '备注' ,
                          `create_time` DATETIME NOT NULL  COMMENT '创建时间' ,
                          `creator_id` BIGINT NOT NULL  COMMENT '创建人ID' ,
                          `update_time` DATETIME NOT NULL  COMMENT '更新时间' ,
                          `updater_id` BIGINT NOT NULL  COMMENT '更新人ID' ,
                          `delete_time` DATETIME   COMMENT '删除时间' ,
                          PRIMARY KEY (id)
)  COMMENT = '菜单表';
