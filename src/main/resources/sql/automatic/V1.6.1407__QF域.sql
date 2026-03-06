DROP TABLE IF EXISTS qf_model_group;
CREATE TABLE qf_model_group(
                               `id` BIGINT NOT NULL  COMMENT '主键ID' ,
                               `root_id` BIGINT NOT NULL  COMMENT '所属企业/租户ID' ,
                               `dept_id` BIGINT NOT NULL  COMMENT '所属部门ID' ,
                               `name` VARCHAR(80) NOT NULL  COMMENT '组名称' ,
                               `code` VARCHAR(32) NOT NULL  COMMENT '组编码' ,
                               `remark` TEXT   COMMENT '备注' ,
                               `seq` INT NOT NULL  COMMENT '排序' ,
                               `create_time` DATETIME NOT NULL  COMMENT '创建时间' ,
                               `creator_id` BIGINT NOT NULL  COMMENT '创建人ID' ,
                               `update_time` DATETIME NOT NULL  COMMENT '更新时间' ,
                               `updater_id` BIGINT NOT NULL  COMMENT '更新人ID' ,
                               `delete_time` DATETIME   COMMENT '删除时间 NULL未删' ,
                               PRIMARY KEY (id)
)  COMMENT = '流程模型分组';


DROP TABLE IF EXISTS qf_model;
CREATE TABLE qf_model(
                         `id` BIGINT NOT NULL  COMMENT '主键ID' ,
                         `root_id` BIGINT NOT NULL  COMMENT '所属企业/租户ID' ,
                         `dept_id` BIGINT NOT NULL  COMMENT '所属部门ID' ,
                         `active_deploy_id` BIGINT NOT NULL  COMMENT '该模型生效的部署ID' ,
                         `group_id` BIGINT NOT NULL  COMMENT '模型组ID' ,
                         `name` VARCHAR(80) NOT NULL  COMMENT '模型名称' ,
                         `code` VARCHAR(32) NOT NULL  COMMENT '模型编码' ,
                         `bpmn_xml` LONGTEXT   COMMENT '模型BPMN XML' ,
                         `data_version` INT NOT NULL  COMMENT '模型版本号' ,
                         `status` TINYINT NOT NULL  COMMENT '模型状态 0:草稿 1:已部署 2:历史' ,
                         `seq` INT NOT NULL  COMMENT '排序' ,
                         `create_time` DATETIME NOT NULL  COMMENT '创建时间' ,
                         `creator_id` BIGINT NOT NULL  COMMENT '创建人ID' ,
                         `update_time` DATETIME NOT NULL  COMMENT '更新时间' ,
                         `updater_id` BIGINT NOT NULL  COMMENT '更新人ID' ,
                         `delete_time` DATETIME   COMMENT '删除时间 NULL未删' ,
                         PRIMARY KEY (id)
)  COMMENT = '流程模型';

DROP TABLE IF EXISTS qf_model_deploy_rcd;
CREATE TABLE qf_model_deploy_rcd(
                                    `id` BIGINT NOT NULL  COMMENT '主键ID' ,
                                    `root_id` BIGINT NOT NULL  COMMENT '所属企业/租户ID' ,
                                    `dept_id` BIGINT NOT NULL  COMMENT '所属部门ID' ,
                                    `model_id` BIGINT NOT NULL  COMMENT '模型ID' ,
                                    `name` VARCHAR(80) NOT NULL  COMMENT '模型名称' ,
                                    `code` VARCHAR(32) NOT NULL  COMMENT '模型编码' ,
                                    `bpmn_xml` LONGTEXT   COMMENT '模型BPMN XML' ,
                                    `data_version` INT NOT NULL  COMMENT '模型版本号' ,
                                    `eng_deployment_id` VARCHAR(128)   COMMENT '引擎"部署ID"(部署失败为NULL)' ,
                                    `eng_process_def_id` VARCHAR(128)   COMMENT '引擎"流程ID"(部署失败为NULL)' ,
                                    `eng_deploy_result` LONGTEXT NOT NULL  COMMENT '引擎返回的部署结果' ,
                                    `status` TINYINT NOT NULL  COMMENT '部署状态 0:正常 1:部署失败' ,
                                    `create_time` DATETIME NOT NULL  COMMENT '创建时间' ,
                                    `creator_id` BIGINT NOT NULL  COMMENT '创建人ID' ,
                                    `update_time` DATETIME NOT NULL  COMMENT '更新时间' ,
                                    `updater_id` BIGINT NOT NULL  COMMENT '更新人ID' ,
                                    `delete_time` DATETIME   COMMENT '删除时间 NULL未删' ,
                                    PRIMARY KEY (id)
)  COMMENT = '流程模型部署历史';
