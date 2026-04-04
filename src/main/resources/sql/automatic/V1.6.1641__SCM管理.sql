-- 1. 既然有新的了，旧的备份就该进垃圾桶，别占着磁盘空间
DROP TABLE IF EXISTS `zremovred_assembly_scm`;

-- 2. 给现有的倒霉蛋打上“待删除”标签，腾出表名位置
ALTER TABLE `assembly_scm` RENAME TO `zremovred_assembly_scm`;

-- 3. 创建这个所谓“优化后”的新表
CREATE TABLE `assembly_scm` (
                                `id` BIGINT NOT NULL COMMENT '主键ID',
                                `name` VARCHAR(32) NOT NULL COMMENT 'SCM名称(唯一)',
                                `project_name` VARCHAR(80) COMMENT '项目名称',
                                `scm_url` VARCHAR(1000) NOT NULL COMMENT 'SCM仓库地址',
                                `scm_auth_kind` TINYINT NOT NULL COMMENT 'SCM认证方式 0:公开 1:账号密码 2:SSH KEY 3:PAT',
                                `scm_username` TEXT COMMENT 'SCM用户名',
                                `scm_password` TEXT COMMENT 'SCM密码',
                                `scm_pk` TEXT COMMENT 'SSH KEY',
                                `scm_branch` VARCHAR(80) NOT NULL COMMENT 'SCM分支',
                                `remark` TEXT COMMENT 'SCM备注',
                                `create_time` DATETIME NOT NULL COMMENT '创建时间',
                                `creator_id` BIGINT NOT NULL COMMENT '创建人ID',
                                `update_time` DATETIME NOT NULL COMMENT '更新时间',
                                `updater_id` BIGINT NOT NULL COMMENT '更新人ID',
                                PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT = 'SCM表';

-- 补上那个为了防止重名打架的唯一索引
CREATE UNIQUE INDEX uk_scm_name ON assembly_scm(name);

-- 4. 数据大搬家，顺便把那个多余的 'code' 字段给扔了
INSERT INTO `assembly_scm` (
    `id`, `name`, `project_name`, `scm_url`, `scm_auth_kind`,
    `scm_username`, `scm_password`, `scm_pk`, `scm_branch`,
    `remark`, `create_time`, `creator_id`, `update_time`, `updater_id`
)
SELECT
    `id`, `name`, `project_name`, `scm_url`, `scm_auth_kind`,
    `scm_username`, `scm_password`, `scm_pk`, `scm_branch`,
    `remark`, `create_time`, `creator_id`, `update_time`, `updater_id`
FROM `zremovred_assembly_scm`;

/************************************************************
 * 注释说明（改动项）：
 * 1. 彻底弃用了 `code` 字段。希望你的业务逻辑里没再到处引用它，否则你会死得很惨。
 * 2. `name` 字段增加了唯一索引 `uk_scm_name`。如果旧数据里有重名的，上面的 INSERT 语句会直接报错，建议跑脚本前先自查。
 * 3. 移除了旧 DDL 中冗余的 `COLLATE` 声明，保持脚本清爽。
 * 4. 备份表统一加了 `zremovred_` 前缀，别到时候找不到祖宗在哪。
 ************************************************************/