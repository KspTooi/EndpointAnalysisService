/*
 Navicat Premium Dump SQL

 Source Server         : 127.0.0.1
 Source Server Type    : MySQL
 Source Server Version : 80300 (8.3.0)
 Source Host           : 127.0.0.1:3306
 Source Schema         : endpoint_analysis_service

 Target Server Type    : MySQL
 Target Server Version : 80300 (8.3.0)
 File Encoding         : 65001

 Date: 12/02/2026 02:07:24
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for audit_error_rcd
-- ----------------------------
DROP TABLE IF EXISTS `audit_error_rcd`;
CREATE TABLE `audit_error_rcd`  (
  `id` bigint NOT NULL COMMENT '错误ID',
  `error_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '错误代码',
  `request_uri` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '请求地址',
  `user_id` bigint NULL DEFAULT NULL COMMENT '操作人ID',
  `user_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作人用户名',
  `error_type` varchar(320) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '异常类型',
  `error_message` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '异常简述',
  `error_stack_trace` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '完整堆栈信息',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统错误记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for audit_login_rcd
-- ----------------------------
DROP TABLE IF EXISTS `audit_login_rcd`;
CREATE TABLE `audit_login_rcd`  (
  `id` bigint NOT NULL COMMENT '登录日志主键',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户账号',
  `login_kind` tinyint NOT NULL COMMENT '登录方式 0:用户名密码',
  `ip_addr` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '登录 IP',
  `location` varchar(320) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'IP 归属地',
  `browser` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '浏览器/客户端指纹',
  `os` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '操作系统',
  `status` tinyint NOT NULL COMMENT '状态: 0:成功 1:失败',
  `message` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '提示消息',
  `create_time` datetime NOT NULL COMMENT '创建时间'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '登录审计日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for core_attach
-- ----------------------------
DROP TABLE IF EXISTS `core_attach`;
CREATE TABLE `core_attach`  (
  `id` bigint NOT NULL,
  `create_time` datetime(6) NOT NULL COMMENT '创建时间',
  `creator_id` bigint NOT NULL COMMENT '创建人ID',
  `kind` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件类型',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件名',
  `path` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件路径',
  `receive_size` bigint NOT NULL COMMENT '已接收大小',
  `sha256` varchar(320) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件SHA256',
  `status` int NOT NULL COMMENT '状态 0:预检文件 1:区块不完整 2:校验中 3:有效',
  `suffix` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件后缀',
  `total_size` bigint NOT NULL COMMENT '文件总大小',
  `verify_time` datetime(6) NULL DEFAULT NULL COMMENT '校验时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '分块数据' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for core_attach_chunk
-- ----------------------------
DROP TABLE IF EXISTS `core_attach_chunk`;
CREATE TABLE `core_attach_chunk`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `chunk_id` bigint NOT NULL COMMENT '分块ID',
  `create_time` datetime(6) NOT NULL COMMENT '创建时间',
  `attach_id` bigint NOT NULL COMMENT '附件ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_attach_chunk`(`attach_id` ASC, `chunk_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '附件分块数据' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for core_company
-- ----------------------------
DROP TABLE IF EXISTS `core_company`;
CREATE TABLE `core_company`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '公司ID',
  `founder_id` bigint NULL DEFAULT NULL COMMENT '创始人用户ID',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '公司名',
  `description` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '公司描述',
  `deleted_time` datetime NULL DEFAULT NULL COMMENT '移除时间 为null代表未删除',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for core_company_member
-- ----------------------------
DROP TABLE IF EXISTS `core_company_member`;
CREATE TABLE `core_company_member`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `company_id` bigint NOT NULL COMMENT '公司ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role` tinyint NULL DEFAULT NULL COMMENT '职务 0:CEO 1:成员',
  `joined_time` datetime NOT NULL COMMENT '加入时间',
  `deleted_time` datetime NULL DEFAULT NULL COMMENT '删除时间 为NULL时代表未删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_company_user`(`company_id` ASC, `user_id` ASC, `deleted_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 38 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for core_config
-- ----------------------------
DROP TABLE IF EXISTS `core_config`;
CREATE TABLE `core_config`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '配置键',
  `config_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '配置值',
  `create_time` datetime(6) NOT NULL COMMENT '创建时间',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '配置描述',
  `update_time` datetime(6) NOT NULL COMMENT '更新时间',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID 为null表示全局配置',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_config`(`user_id` ASC, `config_key` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for core_dept
-- ----------------------------
DROP TABLE IF EXISTS `core_dept`;
CREATE TABLE `core_dept`  (
  `id` bigint NOT NULL COMMENT '部门ID',
  `parent_id` bigint NULL DEFAULT NULL COMMENT '父级部门 NULL为顶级',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '部门名',
  `principal_id` bigint NULL DEFAULT NULL COMMENT '负责人ID',
  `principal_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '负责人名称',
  `status` tinyint NOT NULL COMMENT '部门状态 0:正常 1:禁用',
  `seq` int NOT NULL COMMENT '排序',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `creator_id` bigint NOT NULL COMMENT '创建人ID',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `updater_id` bigint NOT NULL COMMENT '更新人ID',
  `delete_time` datetime NULL DEFAULT NULL COMMENT '删除时间 NULL未删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '部门表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for core_excel_template
-- ----------------------------
DROP TABLE IF EXISTS `core_excel_template`;
CREATE TABLE `core_excel_template`  (
  `id` bigint NOT NULL COMMENT '模板ID',
  `attach_id` bigint NOT NULL COMMENT '模板附件ID',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模板名称',
  `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模板标识 唯一',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '模板备注',
  `status` tinyint NOT NULL COMMENT '状态 0:启用 1:禁用',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `creator_id` bigint NOT NULL COMMENT '创建人ID',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `updater_id` bigint NOT NULL COMMENT '更新人ID',
  `delete_time` datetime NULL DEFAULT NULL COMMENT '删除时间 NULL未删除'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '导入模板表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for core_group
-- ----------------------------
DROP TABLE IF EXISTS `core_group`;
CREATE TABLE `core_group`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '组ID',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '组标识，如：admin、developer等',
  `create_time` datetime(6) NOT NULL COMMENT '创建时间',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '组描述',
  `is_system` bit(1) NOT NULL COMMENT '是否系统内置组（内置组不可删除）',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '组名称，如：管理员组、开发者组等',
  `sort_order` int NOT NULL COMMENT '排序号',
  `status` int NOT NULL COMMENT '组状态：0-禁用，1-启用',
  `update_time` datetime(6) NOT NULL COMMENT '修改时间',
  `update_user_id` bigint NULL DEFAULT NULL COMMENT '修改人ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UKjtf8i69dpn27xhpqhnk20gjox`(`code` ASC) USING BTREE,
  UNIQUE INDEX `UKne8ra7mq5cagp5gx87d49ift2`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 44 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户组' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for core_group_permission
-- ----------------------------
DROP TABLE IF EXISTS `core_group_permission`;
CREATE TABLE `core_group_permission`  (
  `group_id` bigint NOT NULL,
  `permission_id` bigint NOT NULL,
  PRIMARY KEY (`group_id`, `permission_id`) USING BTREE,
  INDEX `FKbg3iyfbnf6or1qxh7951yuo9s`(`permission_id` ASC) USING BTREE,
  CONSTRAINT `FKbg3iyfbnf6or1qxh7951yuo9s` FOREIGN KEY (`permission_id`) REFERENCES `core_permission` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKkodk47oq26uyh8v0bpncw81mm` FOREIGN KEY (`group_id`) REFERENCES `core_group` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for core_notice
-- ----------------------------
DROP TABLE IF EXISTS `core_notice`;
CREATE TABLE `core_notice`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `title` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标题',
  `kind` tinyint NOT NULL COMMENT '种类: 0公告, 1业务提醒, 2私信',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '通知内容',
  `priority` tinyint NOT NULL COMMENT '优先级: 0:低 1:中 2:高',
  `category` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '业务类型/分类',
  `sender_id` bigint NULL DEFAULT NULL COMMENT '发送人ID (NULL为系统)',
  `sender_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '发送人姓名',
  `target_kind` tinyint NOT NULL COMMENT '接收人类型 0:全员 1:指定部门 2:指定用户',
  `target_count` int NULL DEFAULT NULL COMMENT '预计接收人数',
  `forward` varchar(320) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '跳转URL/路由地址',
  `params` json NULL COMMENT '动态参数 (JSON格式)',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '消息通知表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for core_notice_rcd
-- ----------------------------
DROP TABLE IF EXISTS `core_notice_rcd`;
CREATE TABLE `core_notice_rcd`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `notice_id` bigint NOT NULL COMMENT '关联通知ID',
  `user_id` bigint NOT NULL COMMENT '接收人用户ID',
  `read_time` datetime NULL DEFAULT NULL COMMENT '读取时间 (NULL代表未读)',
  `create_time` datetime NOT NULL COMMENT '下发时间',
  `delete_time` datetime NULL DEFAULT NULL COMMENT '删除时间 (NULL代表未删)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户通知记录(接收状态)' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for core_notice_template
-- ----------------------------
DROP TABLE IF EXISTS `core_notice_template`;
CREATE TABLE `core_notice_template`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模板名称',
  `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模板唯一编码 (业务调用用)',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模板内容 (含占位符)',
  `status` tinyint NOT NULL COMMENT '状态: 0启用, 1禁用',
  `remark` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `creator_id` bigint NOT NULL COMMENT '创建人ID',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `updater_id` bigint NOT NULL COMMENT '更新人ID',
  `delete_time` datetime NULL DEFAULT NULL COMMENT '删除时间 NULL未删',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_code`(`code` ASC) USING BTREE COMMENT '确保编码唯一'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '通知模板表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for core_org
-- ----------------------------
DROP TABLE IF EXISTS `core_org`;
CREATE TABLE `core_org`  (
  `id` bigint NOT NULL COMMENT '主键id',
  `root_id` bigint NOT NULL COMMENT '一级组织ID',
  `parent_id` bigint NULL DEFAULT NULL COMMENT '上级组织ID NULL顶级组织',
  `org_path_ids` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '从顶级组织到当前组织的ID列表 以,分割',
  `kind` tinyint NOT NULL COMMENT '0:部门 1:企业',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '组织机构名称',
  `principal_id` bigint NULL DEFAULT NULL COMMENT '主管ID',
  `principal_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '主管名称',
  `seq` int NOT NULL DEFAULT 0 COMMENT '排序',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `creator_id` bigint NOT NULL COMMENT '创建人id',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `updater_id` bigint NOT NULL COMMENT '更新人id',
  `delete_time` datetime NULL DEFAULT NULL COMMENT '删除时间 NULL未删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '组织机构-核心' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for core_permission
-- ----------------------------
DROP TABLE IF EXISTS `core_permission`;
CREATE TABLE `core_permission`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限标识，如：system:user:view',
  `create_time` datetime(6) NOT NULL COMMENT '创建时间',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '权限描述',
  `is_system` int NOT NULL COMMENT '是否系统权限 0:非系统权限 1:系统权限',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限名称，如：查看用户',
  `sort_order` int NOT NULL COMMENT '排序号',
  `update_time` datetime(6) NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK7lcb6glmvwlro3p2w2cewxtvd`(`code` ASC) USING BTREE,
  UNIQUE INDEX `UKpnvtwliis6p05pn6i3ndjrqt2`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 118 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '拥有此权限的用户组' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for core_registry
-- ----------------------------
DROP TABLE IF EXISTS `core_registry`;
CREATE TABLE `core_registry`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `parent_id` bigint NULL DEFAULT NULL COMMENT '父级项ID NULL顶级',
  `key_path` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '条目Key的全路径',
  `kind` tinyint NOT NULL COMMENT '类型 0:节点 1:条目',
  `nkey` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '节点Key',
  `nvalue_kind` tinyint NULL DEFAULT NULL COMMENT '数据类型 0:字串 1:整数 2:浮点 3:日期(LDT)',
  `nvalue` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '节点Value',
  `label` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '节点标签',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '说明',
  `metadata` json NULL COMMENT '元数据JSON',
  `is_system` tinyint NULL DEFAULT NULL COMMENT '内置值 0:否 1:是',
  `status` tinyint NOT NULL COMMENT '状态 0:正常 1:停用',
  `seq` int NOT NULL COMMENT '排序',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `creator_id` bigint NOT NULL COMMENT '创建人ID',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `updater_id` bigint NOT NULL COMMENT '更新人ID',
  `delete_time` datetime NULL DEFAULT NULL COMMENT '删除时间 NULL未删',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '注册表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for core_resource
-- ----------------------------
DROP TABLE IF EXISTS `core_resource`;
CREATE TABLE `core_resource`  (
  `id` bigint NOT NULL COMMENT '主键id',
  `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
  `creator_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '资源描述',
  `kind` int NOT NULL COMMENT '资源类型 0:菜单 1:接口',
  `menu_icon` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '菜单图标',
  `menu_kind` int NULL DEFAULT NULL COMMENT '菜单类型 0:目录 1:菜单 2:按钮',
  `menu_path` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '菜单路径',
  `menu_query_param` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '菜单查询参数',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '资源名',
  `path` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '接口路径',
  `permission` varchar(320) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '所需权限',
  `seq` int NOT NULL COMMENT '排序',
  `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
  `updater_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `parent_id` bigint NULL DEFAULT NULL COMMENT '父级ID',
  `menu_btn_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '按钮ID(menuKind = 2时必填)',
  `menu_hidden` tinyint NULL DEFAULT NULL COMMENT '是否隐藏 0:否 1:是(menuKind = 1/2时生效)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for core_user
-- ----------------------------
DROP TABLE IF EXISTS `core_user`;
CREATE TABLE `core_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(1280) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `email` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `login_count` int NOT NULL COMMENT '登录次数',
  `status` int NOT NULL COMMENT '用户状态 0:正常 1:封禁',
  `last_login_time` datetime(6) NULL DEFAULT NULL COMMENT '最后登录时间',
  `active_company_id` bigint NULL DEFAULT NULL COMMENT '已激活的公司 为null时表示未激活任何公司',
  `active_env_id` bigint NULL DEFAULT NULL COMMENT '已激活的环境ID',
  `create_time` datetime(6) NOT NULL COMMENT '创建时间',
  `update_time` datetime(6) NOT NULL COMMENT '修改时间',
  `gender` tinyint NULL DEFAULT NULL COMMENT '性别 0:男 1:女 2:不愿透露',
  `phone` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号码',
  `avatar_attach_id` bigint NULL DEFAULT NULL COMMENT '用户头像附件ID',
  `delete_time` datetime NULL DEFAULT NULL COMMENT '删除时间 为NULL未删',
  `is_system` tinyint NOT NULL DEFAULT 0 COMMENT '内置用户 0:否 1:是',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '部门ID',
  `dept_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '部门名称',
  `root_id` bigint NULL DEFAULT NULL COMMENT '所属企业ID',
  `root_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '所属企业名称',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 55 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户所属的用户组' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for core_user_group
-- ----------------------------
DROP TABLE IF EXISTS `core_user_group`;
CREATE TABLE `core_user_group`  (
  `user_id` bigint NOT NULL,
  `group_id` bigint NOT NULL,
  PRIMARY KEY (`user_id`, `group_id`) USING BTREE,
  INDEX `FKniig0y7hlk62uysagn1v3ed31`(`group_id` ASC) USING BTREE,
  CONSTRAINT `FK7k9ade3lqbo483u9vuryxmm34` FOREIGN KEY (`user_id`) REFERENCES `core_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKniig0y7hlk62uysagn1v3ed31` FOREIGN KEY (`group_id`) REFERENCES `core_group` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for core_user_session
-- ----------------------------
DROP TABLE IF EXISTS `core_user_session`;
CREATE TABLE `core_user_session`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '会话ID',
  `session_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户凭据SessionID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `root_id` bigint NULL DEFAULT NULL COMMENT '所属企业ID',
  `root_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '所属企业名',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '所属部门ID',
  `dept_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '所属部门名',
  `company_id` bigint NULL DEFAULT NULL COMMENT '公司ID',
  `permissions` json NOT NULL COMMENT '用户权限代码JSON',
  `expires_at` datetime NOT NULL COMMENT '过期时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1337883530587410433 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户会话表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for drive_entry
-- ----------------------------
DROP TABLE IF EXISTS `drive_entry`;
CREATE TABLE `drive_entry`  (
  `id` bigint NOT NULL COMMENT '条目ID',
  `company_id` bigint NOT NULL COMMENT '团队ID',
  `parent_id` bigint NULL DEFAULT NULL COMMENT '父级ID 为NULL顶级',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '条目名称',
  `kind` int NULL DEFAULT NULL COMMENT '条目类型 0:文件 1:文件夹',
  `attach_id` bigint NULL DEFAULT NULL COMMENT '文件附件ID 文件夹为NULL',
  `attach_size` bigint NULL DEFAULT NULL COMMENT '文件附件大小 文件夹为0',
  `attach_suffix` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件附件类型 文件夹为NULL',
  `attach_status` tinyint NULL DEFAULT NULL COMMENT '文件附件状态 0:预检文件 1:区块不完整 2:校验中 3:有效',
  `create_time` datetime(6) NOT NULL COMMENT '创建时间',
  `update_time` datetime(6) NOT NULL COMMENT '更新时间',
  `delete_time` datetime(6) NULL DEFAULT NULL COMMENT '删除时间 为NULL未删除',
  `creator_id` bigint NOT NULL COMMENT '创建人',
  `updater_id` bigint NOT NULL COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ep_document
-- ----------------------------
DROP TABLE IF EXISTS `ep_document`;
CREATE TABLE `ep_document`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '端点文档ID',
  `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
  `doc_pull_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文档拉取URL',
  `pull_time` datetime(6) NULL DEFAULT NULL COMMENT '拉取时间',
  `relay_server_id` bigint NULL DEFAULT NULL COMMENT '中继通道ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FKg4rnta0msk27ufko6a7e5t83j`(`relay_server_id` ASC) USING BTREE,
  CONSTRAINT `FKg4rnta0msk27ufko6a7e5t83j` FOREIGN KEY (`relay_server_id`) REFERENCES `relay_server` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '端点文档版本' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for ep_document_operation
-- ----------------------------
DROP TABLE IF EXISTS `ep_document_operation`;
CREATE TABLE `ep_document_operation`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '端点文档操作ID',
  `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '接口描述',
  `method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '请求方法',
  `operation_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '唯一操作ID',
  `path` varchar(320) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '接口路径',
  `req_body_json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '请求体JSON',
  `req_query_json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '请求参数JSON',
  `res_body_json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '响应体JSON',
  `res_query_json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '响应列表JSON',
  `summary` varchar(320) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '接口摘要',
  `tag` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '接口标签',
  `ep_doc_id` bigint NOT NULL COMMENT '端点文档ID',
  `relay_server_id` bigint NOT NULL COMMENT '中继通道ID',
  `ep_doc_version_id` bigint NOT NULL COMMENT '端点文档版本ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK55p2imuhod9xij71kubgyjvbx`(`ep_doc_id` ASC) USING BTREE,
  INDEX `FKgrd9wkvqpw90v285yn8uib41r`(`relay_server_id` ASC) USING BTREE,
  INDEX `FKntdfny15bq79d4357paeec3sd`(`ep_doc_version_id` ASC) USING BTREE,
  CONSTRAINT `FK55p2imuhod9xij71kubgyjvbx` FOREIGN KEY (`ep_doc_id`) REFERENCES `ep_document` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKgrd9wkvqpw90v285yn8uib41r` FOREIGN KEY (`relay_server_id`) REFERENCES `relay_server` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKntdfny15bq79d4357paeec3sd` FOREIGN KEY (`ep_doc_version_id`) REFERENCES `ep_document_version` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 290 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for ep_document_schema
-- ----------------------------
DROP TABLE IF EXISTS `ep_document_schema`;
CREATE TABLE `ep_document_schema`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '端点Schema ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for ep_document_sync_rcd
-- ----------------------------
DROP TABLE IF EXISTS `ep_document_sync_rcd`;
CREATE TABLE `ep_document_sync_rcd`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '端点文档拉取记录ID',
  `create_time` datetime(6) NULL DEFAULT NULL COMMENT '拉取时间',
  `hash` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文档MD5值',
  `new_version_created` int NOT NULL COMMENT '是否创建了新版本 0:否 1:是',
  `new_version_num` bigint NULL DEFAULT NULL COMMENT '新版本号，如果创建了新版本，则记录新版本号',
  `pull_url` varchar(320) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '拉取地址',
  `status` int NOT NULL COMMENT '状态 0:成功 1:失败',
  `ep_doc_id` bigint NOT NULL COMMENT '端点文档ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FKd8xy10l7mqscx0q4hgx945e8n`(`ep_doc_id` ASC) USING BTREE,
  CONSTRAINT `FKd8xy10l7mqscx0q4hgx945e8n` FOREIGN KEY (`ep_doc_id`) REFERENCES `ep_document` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for ep_document_version
-- ----------------------------
DROP TABLE IF EXISTS `ep_document_version`;
CREATE TABLE `ep_document_version`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '端点文档版本ID',
  `api_total` int NOT NULL COMMENT '接口总数',
  `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
  `doc_json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '原始文档JSON',
  `hash` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'HASH',
  `is_latest` int NOT NULL COMMENT '是否最新版本 0:否 1:是',
  `is_persisted` int NOT NULL COMMENT '是否已持久化 0:否 1:是',
  `version` bigint NOT NULL COMMENT '版本号',
  `ep_doc_id` bigint NOT NULL COMMENT '端点文档ID',
  `relay_server_id` bigint NOT NULL COMMENT '中继通道ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UKnsonk6m090osov34kdhw2607q`(`hash` ASC) USING BTREE,
  INDEX `FK1g2qrppt9nl7jr2ngghvotsns`(`ep_doc_id` ASC) USING BTREE,
  INDEX `FK17ddp1yioxh4o5h8ra10y70dm`(`relay_server_id` ASC) USING BTREE,
  CONSTRAINT `FK17ddp1yioxh4o5h8ra10y70dm` FOREIGN KEY (`relay_server_id`) REFERENCES `relay_server` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FK1g2qrppt9nl7jr2ngghvotsns` FOREIGN KEY (`ep_doc_id`) REFERENCES `ep_document` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '端点文档接口' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for ep_site
-- ----------------------------
DROP TABLE IF EXISTS `ep_site`;
CREATE TABLE `ep_site`  (
  `id` bigint NULL DEFAULT NULL COMMENT '站点ID',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '站点名称',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '地址',
  `username` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账户',
  `password` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码',
  `remark` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `seq` int NOT NULL COMMENT '排序',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `creator_id` bigint NOT NULL COMMENT '创建人ID',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `updater_id` bigint NOT NULL COMMENT '更新人ID',
  `name_py_idx` varchar(320) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '站点名称-用于查询的拼音首字母索引',
  `username_py_idx` varchar(320) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账户-用于查询的拼音首字母索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '站点' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ep_std_word
-- ----------------------------
DROP TABLE IF EXISTS `ep_std_word`;
CREATE TABLE `ep_std_word`  (
  `id` bigint NOT NULL COMMENT '标准词ID',
  `source_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '简称',
  `source_name_full` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '全称',
  `target_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '英文简称',
  `target_name_full` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '英文全称',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '备注',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `creator_id` bigint NOT NULL COMMENT '创建人ID',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `updater_id` bigint NOT NULL COMMENT '更新人ID',
  `delete_time` datetime NULL DEFAULT NULL COMMENT '删除时间 NULL未删',
  `source_name_py_idx` varchar(320) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '简称 拼音首字母索引',
  UNIQUE INDEX `uk_source_name_delete`(`source_name` ASC, `delete_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '标准词管理' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qrtz_blob_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_blob_triggers`;
CREATE TABLE `qrtz_blob_triggers`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `TRIGGER_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `TRIGGER_GROUP` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `BLOB_DATA` blob NULL,
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
  INDEX `SCHED_NAME`(`SCHED_NAME` ASC, `TRIGGER_NAME` ASC, `TRIGGER_GROUP` ASC) USING BTREE,
  CONSTRAINT `qrtz_blob_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qrtz_calendars
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_calendars`;
CREATE TABLE `qrtz_calendars`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `CALENDAR_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `CALENDAR` blob NOT NULL,
  PRIMARY KEY (`SCHED_NAME`, `CALENDAR_NAME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qrtz_cron_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_cron_triggers`;
CREATE TABLE `qrtz_cron_triggers`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `TRIGGER_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `TRIGGER_GROUP` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `CRON_EXPRESSION` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `TIME_ZONE_ID` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
  CONSTRAINT `qrtz_cron_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qrtz_fired_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_fired_triggers`;
CREATE TABLE `qrtz_fired_triggers`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `ENTRY_ID` varchar(95) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `TRIGGER_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `TRIGGER_GROUP` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `INSTANCE_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `FIRED_TIME` bigint NOT NULL,
  `SCHED_TIME` bigint NOT NULL,
  `PRIORITY` int NOT NULL,
  `STATE` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `JOB_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `JOB_GROUP` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `IS_NONCONCURRENT` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `REQUESTS_RECOVERY` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`, `ENTRY_ID`) USING BTREE,
  INDEX `IDX_QRTZ_FT_TRIG_INST_NAME`(`SCHED_NAME` ASC, `INSTANCE_NAME` ASC) USING BTREE,
  INDEX `IDX_QRTZ_FT_INST_JOB_REQ_RCVRY`(`SCHED_NAME` ASC, `INSTANCE_NAME` ASC, `REQUESTS_RECOVERY` ASC) USING BTREE,
  INDEX `IDX_QRTZ_FT_J_G`(`SCHED_NAME` ASC, `JOB_NAME` ASC, `JOB_GROUP` ASC) USING BTREE,
  INDEX `IDX_QRTZ_FT_JG`(`SCHED_NAME` ASC, `JOB_GROUP` ASC) USING BTREE,
  INDEX `IDX_QRTZ_FT_T_G`(`SCHED_NAME` ASC, `TRIGGER_NAME` ASC, `TRIGGER_GROUP` ASC) USING BTREE,
  INDEX `IDX_QRTZ_FT_TG`(`SCHED_NAME` ASC, `TRIGGER_GROUP` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qrtz_job_details
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_job_details`;
CREATE TABLE `qrtz_job_details`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `JOB_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `JOB_GROUP` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `DESCRIPTION` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `JOB_CLASS_NAME` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `IS_DURABLE` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `IS_NONCONCURRENT` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `IS_UPDATE_DATA` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `REQUESTS_RECOVERY` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `JOB_DATA` blob NULL,
  PRIMARY KEY (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) USING BTREE,
  INDEX `IDX_QRTZ_J_REQ_RECOVERY`(`SCHED_NAME` ASC, `REQUESTS_RECOVERY` ASC) USING BTREE,
  INDEX `IDX_QRTZ_J_GRP`(`SCHED_NAME` ASC, `JOB_GROUP` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qrtz_locks
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_locks`;
CREATE TABLE `qrtz_locks`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `LOCK_NAME` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`SCHED_NAME`, `LOCK_NAME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qrtz_paused_trigger_grps
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_paused_trigger_grps`;
CREATE TABLE `qrtz_paused_trigger_grps`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `TRIGGER_GROUP` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_GROUP`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qrtz_scheduler_state
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_scheduler_state`;
CREATE TABLE `qrtz_scheduler_state`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `INSTANCE_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `LAST_CHECKIN_TIME` bigint NOT NULL,
  `CHECKIN_INTERVAL` bigint NOT NULL,
  PRIMARY KEY (`SCHED_NAME`, `INSTANCE_NAME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qrtz_simple_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_simple_triggers`;
CREATE TABLE `qrtz_simple_triggers`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `TRIGGER_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `TRIGGER_GROUP` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `REPEAT_COUNT` bigint NOT NULL,
  `REPEAT_INTERVAL` bigint NOT NULL,
  `TIMES_TRIGGERED` bigint NOT NULL,
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
  CONSTRAINT `qrtz_simple_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qrtz_simprop_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_simprop_triggers`;
CREATE TABLE `qrtz_simprop_triggers`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `TRIGGER_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `TRIGGER_GROUP` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `STR_PROP_1` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `STR_PROP_2` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `STR_PROP_3` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `INT_PROP_1` int NULL DEFAULT NULL,
  `INT_PROP_2` int NULL DEFAULT NULL,
  `LONG_PROP_1` bigint NULL DEFAULT NULL,
  `LONG_PROP_2` bigint NULL DEFAULT NULL,
  `DEC_PROP_1` decimal(13, 4) NULL DEFAULT NULL,
  `DEC_PROP_2` decimal(13, 4) NULL DEFAULT NULL,
  `BOOL_PROP_1` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `BOOL_PROP_2` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
  CONSTRAINT `qrtz_simprop_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qrtz_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_triggers`;
CREATE TABLE `qrtz_triggers`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `TRIGGER_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `TRIGGER_GROUP` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `JOB_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `JOB_GROUP` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `DESCRIPTION` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `NEXT_FIRE_TIME` bigint NULL DEFAULT NULL,
  `PREV_FIRE_TIME` bigint NULL DEFAULT NULL,
  `PRIORITY` int NULL DEFAULT NULL,
  `TRIGGER_STATE` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `TRIGGER_TYPE` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `START_TIME` bigint NOT NULL,
  `END_TIME` bigint NULL DEFAULT NULL,
  `CALENDAR_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `MISFIRE_INSTR` smallint NULL DEFAULT NULL,
  `JOB_DATA` blob NULL,
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
  INDEX `IDX_QRTZ_T_J`(`SCHED_NAME` ASC, `JOB_NAME` ASC, `JOB_GROUP` ASC) USING BTREE,
  INDEX `IDX_QRTZ_T_JG`(`SCHED_NAME` ASC, `JOB_GROUP` ASC) USING BTREE,
  INDEX `IDX_QRTZ_T_C`(`SCHED_NAME` ASC, `CALENDAR_NAME` ASC) USING BTREE,
  INDEX `IDX_QRTZ_T_G`(`SCHED_NAME` ASC, `TRIGGER_GROUP` ASC) USING BTREE,
  INDEX `IDX_QRTZ_T_STATE`(`SCHED_NAME` ASC, `TRIGGER_STATE` ASC) USING BTREE,
  INDEX `IDX_QRTZ_T_N_STATE`(`SCHED_NAME` ASC, `TRIGGER_NAME` ASC, `TRIGGER_GROUP` ASC, `TRIGGER_STATE` ASC) USING BTREE,
  INDEX `IDX_QRTZ_T_N_G_STATE`(`SCHED_NAME` ASC, `TRIGGER_GROUP` ASC, `TRIGGER_STATE` ASC) USING BTREE,
  INDEX `IDX_QRTZ_T_NEXT_FIRE_TIME`(`SCHED_NAME` ASC, `NEXT_FIRE_TIME` ASC) USING BTREE,
  INDEX `IDX_QRTZ_T_NFT_ST`(`SCHED_NAME` ASC, `TRIGGER_STATE` ASC, `NEXT_FIRE_TIME` ASC) USING BTREE,
  INDEX `IDX_QRTZ_T_NFT_MISFIRE`(`SCHED_NAME` ASC, `MISFIRE_INSTR` ASC, `NEXT_FIRE_TIME` ASC) USING BTREE,
  INDEX `IDX_QRTZ_T_NFT_ST_MISFIRE`(`SCHED_NAME` ASC, `MISFIRE_INSTR` ASC, `NEXT_FIRE_TIME` ASC, `TRIGGER_STATE` ASC) USING BTREE,
  INDEX `IDX_QRTZ_T_NFT_ST_MISFIRE_GRP`(`SCHED_NAME` ASC, `MISFIRE_INSTR` ASC, `NEXT_FIRE_TIME` ASC, `TRIGGER_GROUP` ASC, `TRIGGER_STATE` ASC) USING BTREE,
  CONSTRAINT `qrtz_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) REFERENCES `qrtz_job_details` (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qt_task
-- ----------------------------
DROP TABLE IF EXISTS `qt_task`;
CREATE TABLE `qt_task`  (
  `id` bigint NOT NULL COMMENT '任务ID',
  `group_id` bigint NULL DEFAULT NULL COMMENT '任务分组ID',
  `group_name` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '任务分组名',
  `name` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务名',
  `kind` tinyint NOT NULL COMMENT '0:本地BEAN 1:远程HTTP',
  `cron` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'CRON表达式',
  `target` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调用目标(BEAN代码或HTTP地址)',
  `target_param` json NULL COMMENT '调用参数JSON',
  `req_method` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求方法',
  `concurrent` tinyint NOT NULL COMMENT '并发执行 0:允许 1:禁止',
  `policy_misfire` tinyint NOT NULL COMMENT '过期策略 0:放弃执行 1:立即执行 2:全部执行',
  `policy_error` tinyint NOT NULL COMMENT '失败策略 0:默认 1:自动暂停',
  `policy_rcd` tinyint NOT NULL COMMENT '日志策略 0:全部 1:仅异常 2:不记录',
  `expire_time` datetime NULL DEFAULT NULL COMMENT '任务有效期截止',
  `last_exec_status` tinyint NULL DEFAULT NULL COMMENT '上次状态 0:成功 1:异常',
  `last_start_time` datetime NULL DEFAULT NULL COMMENT '上次开始时间',
  `last_end_time` datetime NULL DEFAULT NULL COMMENT '上次结束时间',
  `status` tinyint NOT NULL COMMENT '0:正常 1:暂停 2:暂停(异常)',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `creator_id` bigint NOT NULL COMMENT '创建人ID',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `updater_id` bigint NOT NULL COMMENT '更新人ID',
  `delete_time` datetime NULL DEFAULT NULL COMMENT '删除时间 NULL未删',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '任务调度表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qt_task_group
-- ----------------------------
DROP TABLE IF EXISTS `qt_task_group`;
CREATE TABLE `qt_task_group`  (
  `id` bigint NOT NULL,
  `name` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '分组名',
  `remark` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分组备注',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `creator_id` bigint NOT NULL COMMENT '创建人ID',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `updater_id` bigint NOT NULL COMMENT '更新人ID',
  `delete_time` datetime NULL DEFAULT NULL COMMENT '删除时间 NULL未删',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '任务分组' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qt_task_rcd
-- ----------------------------
DROP TABLE IF EXISTS `qt_task_rcd`;
CREATE TABLE `qt_task_rcd`  (
  `id` bigint NOT NULL COMMENT '调度日志ID',
  `task_id` bigint NOT NULL COMMENT '任务ID',
  `task_name` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务名',
  `group_name` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分组名',
  `target` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调用目标',
  `target_param` json NULL COMMENT '调用目标参数',
  `target_result` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '调用目标返回内容(错误时为异常堆栈)',
  `status` tinyint NOT NULL COMMENT '运行状态 0:正常 1:失败 2:超时 3:已调度',
  `start_time` datetime NOT NULL COMMENT '运行开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '运行结束时间',
  `cost_time` int NULL DEFAULT NULL COMMENT '耗时(MS)',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '任务调度日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for rdbg_collection
-- ----------------------------
DROP TABLE IF EXISTS `rdbg_collection`;
CREATE TABLE `rdbg_collection`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `parent_id` bigint NULL DEFAULT NULL COMMENT '父级ID NULL顶级',
  `company_id` bigint NOT NULL COMMENT '公司ID',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '集合名称',
  `kind` tinyint NOT NULL COMMENT '集合类型 0:请求 1:组',
  `req_url` varchar(320) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求URL',
  `req_url_params_json` json NULL COMMENT '请求URL参数JSON',
  `req_method` tinyint NULL DEFAULT NULL COMMENT '请求方法 0:GET 1:POST 2:PUT 3:PATCH 4:DELETE 5:HEAD 6:OPTIONS',
  `req_header_json` json NULL COMMENT '请求头JSON',
  `req_body_kind` tinyint NULL DEFAULT NULL COMMENT '请求体类型 0:空 1:form-data 2:raw-text 3:raw-javascription 4:raw-json 5:raw-html 6:raw-xml 7:binary 8:x-www-form-urlencoded',
  `req_body_json` json NULL COMMENT '请求体JSON',
  `seq` int NOT NULL COMMENT '排序',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `creator_id` bigint NOT NULL COMMENT '创建人ID',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `updater_id` bigint NOT NULL COMMENT '更新人ID',
  `delete_time` datetime NULL DEFAULT NULL COMMENT '删除时间 为NULL未删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for rdbg_collection_history
-- ----------------------------
DROP TABLE IF EXISTS `rdbg_collection_history`;
CREATE TABLE `rdbg_collection_history`  (
  `id` bigint NOT NULL COMMENT '记录ID',
  `company_id` bigint NOT NULL COMMENT '公司ID',
  `collection_id` bigint NOT NULL COMMENT '集合ID',
  `req_url` varchar(320) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '请求URL',
  `req_url_params_json` json NOT NULL COMMENT '请求URL查询参数 类型:RelayParam',
  `req_method` tinyint NOT NULL COMMENT '请求方法 0:GET 1:POST 2:PUT 3:PATCH 4:DELETE 5:HEAD 6:OPTIONS',
  `req_header_json` json NOT NULL COMMENT '请求头JSON 类型:RelayHeader',
  `req_body_json` json NOT NULL COMMENT '请求体JSON 类型:RelayBody',
  `ret_header_json` json NOT NULL COMMENT '响应头JSON 类型:RelayHeader',
  `ret_body_text` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '响应体文本',
  `ret_http_status` int NULL DEFAULT NULL COMMENT 'HTTP状态码 NULL请求尚未完成',
  `biz_status` tinyint NOT NULL COMMENT '业务状态 0:正常 1:HTTP失败 2:业务失败 3:正在处理 4:EAS内部错误',
  `error_message` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'EAS内部错误消息 NULL未发生错误',
  `req_time` datetime NOT NULL COMMENT '请求发起时间',
  `ret_time` datetime NULL DEFAULT NULL COMMENT '响应时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `creator_id` bigint NOT NULL COMMENT '创建人ID',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `updater_id` bigint NOT NULL COMMENT '更新人ID',
  `delete_time` datetime NULL DEFAULT NULL COMMENT '删除时间 NULL未删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_collection_id`(`collection_id` ASC) USING BTREE,
  INDEX `idx_company_id`(`company_id` ASC) USING BTREE,
  INDEX `idx_delete_time`(`delete_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '请求集合历史记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for rdbg_request
-- ----------------------------
DROP TABLE IF EXISTS `rdbg_request`;
CREATE TABLE `rdbg_request`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户请求ID',
  `group_id` bigint NULL DEFAULT NULL COMMENT '请求组ID',
  `request_id` bigint NULL DEFAULT NULL COMMENT '原始请求ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户自定义请求名称',
  `method` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '请求方法',
  `url` varchar(320) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '请求URL',
  `request_headers` json NULL COMMENT '请求头JSON',
  `request_body_type` varchar(320) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '请求体类型 JSON、表单数据、二进制',
  `request_body` json NULL COMMENT '请求体JSON',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `tree_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_tree_id`(`tree_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 228 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户请求记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for rdbg_request_group
-- ----------------------------
DROP TABLE IF EXISTS `rdbg_request_group`;
CREATE TABLE `rdbg_request_group`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '请求组名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '请求组描述',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `tree_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 136 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '请求组中的请求' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for rdbg_request_rcd
-- ----------------------------
DROP TABLE IF EXISTS `rdbg_request_rcd`;
CREATE TABLE `rdbg_request_rcd`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户请求记录ID',
  `user_request_id` bigint NOT NULL COMMENT '用户请求ID',
  `request_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '请求ID',
  `method` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '请求方法',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '请求URL',
  `source` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '来源',
  `request_headers` json NULL COMMENT '请求头JSON',
  `request_body_length` int NOT NULL COMMENT '请求体长度',
  `request_body_type` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '请求体类型 JSON、表单数据、二进制',
  `request_body` json NULL COMMENT '请求体JSON',
  `response_headers` json NULL COMMENT '响应头JSON',
  `response_body_length` int NOT NULL COMMENT '响应体长度',
  `response_body_type` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '响应体类型 JSON、表单数据、二进制',
  `response_body` json NULL COMMENT '响应体JSON',
  `status_code` int NOT NULL COMMENT 'HTTP响应状态码 -1为请求失败',
  `redirect_url` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '重定向URL 301、302、303、307、308',
  `status` int NOT NULL COMMENT '状态 0:正常 1:HTTP失败 2:业务失败 3:连接超时',
  `create_time` datetime(6) NOT NULL COMMENT '发起请求时间',
  `response_time` datetime(6) NULL DEFAULT NULL COMMENT '响应时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UKljwt1u5dtge3g3nvjqomgrytj`(`request_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1397 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户请求记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for rdbg_request_tree
-- ----------------------------
DROP TABLE IF EXISTS `rdbg_request_tree`;
CREATE TABLE `rdbg_request_tree`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` datetime(6) NOT NULL COMMENT '创建时间',
  `kind` int NOT NULL COMMENT '节点类型 0:请求组 1:用户请求',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '节点名称',
  `seq` int NOT NULL COMMENT '排序',
  `update_time` datetime(6) NOT NULL COMMENT '更新时间',
  `parent_id` bigint NULL DEFAULT NULL COMMENT '父级ID 为null表示根节点',
  `group_id` bigint NULL DEFAULT NULL COMMENT '挂载的请求组',
  `request_id` bigint NULL DEFAULT NULL COMMENT '挂载的请求',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 212 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户请求树' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for rdbg_simple_filter
-- ----------------------------
DROP TABLE IF EXISTS `rdbg_simple_filter`;
CREATE TABLE `rdbg_simple_filter`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` datetime(6) NOT NULL COMMENT '创建时间',
  `direction` tinyint NOT NULL COMMENT '过滤器方向 0:请求过滤器 1:响应过滤器',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '过滤器名称',
  `status` tinyint NOT NULL COMMENT '状态 0:启用 1:禁用',
  `update_time` datetime(6) NOT NULL COMMENT '更新时间',
  `user_id` bigint NULL DEFAULT NULL COMMENT '所属用户 为null表示系统过滤器',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FKaat92cuc4vbfqr5ghidg7xp4s`(`user_id` ASC) USING BTREE,
  CONSTRAINT `FKaat92cuc4vbfqr5ghidg7xp4s` FOREIGN KEY (`user_id`) REFERENCES `core_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '过滤器所属的请求组' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for rdbg_simple_filter_operation
-- ----------------------------
DROP TABLE IF EXISTS `rdbg_simple_filter_operation`;
CREATE TABLE `rdbg_simple_filter_operation`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '操作ID',
  `create_time` datetime(6) NOT NULL COMMENT '创建时间',
  `f` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '原始键',
  `kind` tinyint NOT NULL COMMENT '类型 0:持久化 1:缓存 2:注入缓存 3.注入持久化 4:覆写URL',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作名称',
  `seq` int NOT NULL COMMENT '排序',
  `t` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '目标键',
  `target` tinyint NOT NULL COMMENT '目标 0:标头 1:JSON载荷 2:URL(仅限kind=4)',
  `update_time` datetime(6) NOT NULL COMMENT '更新时间',
  `filter_id` bigint NOT NULL COMMENT '过滤器ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '过滤器操作' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for rdbg_simple_filter_request_group
-- ----------------------------
DROP TABLE IF EXISTS `rdbg_simple_filter_request_group`;
CREATE TABLE `rdbg_simple_filter_request_group`  (
  `group_id` bigint NOT NULL,
  `simple_filter_id` bigint NOT NULL,
  INDEX `FKt4f9n6t5dgkekaoflh6rborb1`(`simple_filter_id` ASC) USING BTREE,
  INDEX `FKkvpa9pk50fenh8aplugouwbkm`(`group_id` ASC) USING BTREE,
  CONSTRAINT `FKkvpa9pk50fenh8aplugouwbkm` FOREIGN KEY (`group_id`) REFERENCES `rdbg_request_group` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKt4f9n6t5dgkekaoflh6rborb1` FOREIGN KEY (`simple_filter_id`) REFERENCES `rdbg_simple_filter` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for rdbg_simple_filter_trigger
-- ----------------------------
DROP TABLE IF EXISTS `rdbg_simple_filter_trigger`;
CREATE TABLE `rdbg_simple_filter_trigger`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '触发器ID',
  `create_time` datetime(6) NOT NULL COMMENT '创建时间',
  `kind` tinyint NOT NULL COMMENT '条件 0:包含 1:不包含 2:等于 3:不等于 4:总是触发',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '触发器名称',
  `seq` int NOT NULL COMMENT '排序',
  `target` tinyint NOT NULL COMMENT '目标 0:标头 1:JSON载荷 2:URL 3:HTTP方法 4:总是触发',
  `tk` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '目标键',
  `tv` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '比较值',
  `update_time` datetime(6) NOT NULL COMMENT '更新时间',
  `filter_id` bigint NOT NULL COMMENT '过滤器ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '过滤器触发器' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for rdbg_user_env
-- ----------------------------
DROP TABLE IF EXISTS `rdbg_user_env`;
CREATE TABLE `rdbg_user_env`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '环境ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '环境名',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '描述',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for rdbg_user_env_storage
-- ----------------------------
DROP TABLE IF EXISTS `rdbg_user_env_storage`;
CREATE TABLE `rdbg_user_env_storage`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '共享存储ID',
  `env_id` bigint NOT NULL COMMENT '环境ID',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '变量名',
  `init_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '初始值',
  `value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '当前值',
  `status` tinyint NOT NULL COMMENT '状态 0:启用 1:禁用',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for relay_replay_request
-- ----------------------------
DROP TABLE IF EXISTS `relay_replay_request`;
CREATE TABLE `relay_replay_request`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `method` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '请求方法',
  `redirect_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '重定向URL 301、302、303、307、308',
  `request_body` json NULL COMMENT '请求体JSON',
  `request_body_length` int NOT NULL COMMENT '请求体长度',
  `request_body_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '请求体类型 JSON、表单数据、二进制',
  `request_headers` json NULL COMMENT '请求头JSON',
  `request_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '请求ID',
  `create_time` datetime(6) NOT NULL COMMENT '发起请求时间',
  `response_body` json NULL COMMENT '响应体JSON',
  `response_body_length` int NOT NULL COMMENT '响应体长度',
  `response_body_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '响应体类型 JSON、表单数据、二进制',
  `response_headers` json NULL COMMENT '响应头JSON',
  `response_time` datetime(6) NULL DEFAULT NULL COMMENT '响应时间',
  `source` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '来源',
  `status` int NOT NULL COMMENT '状态 0:正常 1:HTTP失败 2:业务失败 3:连接超时',
  `status_code` int NOT NULL COMMENT 'HTTP响应状态码 -1为请求失败',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '请求URL',
  `original_request_id` bigint NOT NULL COMMENT '原始请求',
  `relay_server_id` bigint NOT NULL COMMENT '中继服务器ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK9xwqeg7w0p1765p26kcn3kdnw`(`request_id` ASC) USING BTREE,
  INDEX `FKpw5cr13kyp4yqlhkmimq0tx95`(`original_request_id` ASC) USING BTREE,
  INDEX `FKfibbqima8h13npden7uiqejws`(`relay_server_id` ASC) USING BTREE,
  INDEX `FK6vmcbcpprfa856dsc0t4k4eux`(`user_id` ASC) USING BTREE,
  CONSTRAINT `FK6vmcbcpprfa856dsc0t4k4eux` FOREIGN KEY (`user_id`) REFERENCES `core_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKfibbqima8h13npden7uiqejws` FOREIGN KEY (`relay_server_id`) REFERENCES `relay_server` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKpw5cr13kyp4yqlhkmimq0tx95` FOREIGN KEY (`original_request_id`) REFERENCES `relay_request` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 452 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for relay_request
-- ----------------------------
DROP TABLE IF EXISTS `relay_request`;
CREATE TABLE `relay_request`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `method` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '请求方法',
  `redirect_url` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '重定向URL 301、302、303、307、308',
  `request_body` json NULL COMMENT '请求体JSON',
  `request_body_length` int NOT NULL COMMENT '请求体长度',
  `request_body_type` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '请求体类型 JSON、表单数据、二进制',
  `request_headers` json NULL COMMENT '请求头JSON',
  `request_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '请求ID',
  `create_time` datetime(6) NOT NULL COMMENT '发起请求时间',
  `response_body` json NULL COMMENT '响应体JSON',
  `response_body_length` int NOT NULL COMMENT '响应体长度',
  `response_body_type` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '响应体类型 JSON、表单数据、二进制',
  `response_headers` json NULL COMMENT '响应头JSON',
  `response_time` datetime(6) NULL DEFAULT NULL COMMENT '响应时间',
  `source` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '来源',
  `status` int NOT NULL COMMENT '状态 0:正常 1:HTTP失败 2:业务失败 3:连接超时',
  `status_code` int NOT NULL COMMENT 'HTTP响应状态码 -1为请求失败',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '请求URL',
  `relay_server_id` bigint NOT NULL COMMENT '中继服务器ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UKqfbp1xsjy7lwq8ich6s0o4x16`(`request_id` ASC) USING BTREE,
  INDEX `FKfgjbfvxueqqqcg01mitu358h0`(`relay_server_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_relay_server_id`(`relay_server_id` ASC) USING BTREE,
  CONSTRAINT `FKfgjbfvxueqqqcg01mitu358h0` FOREIGN KEY (`relay_server_id`) REFERENCES `relay_server` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 206638 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '重放请求' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for relay_route_rule
-- ----------------------------
DROP TABLE IF EXISTS `relay_route_rule`;
CREATE TABLE `relay_route_rule`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '路由规则ID',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '路由策略名',
  `match_type` int NULL DEFAULT NULL COMMENT '匹配类型 0:全部 1:IP地址',
  `match_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '匹配键',
  `match_operator` int NULL DEFAULT NULL COMMENT '匹配操作 0:等于',
  `match_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '匹配值',
  `route_server_id` bigint NOT NULL COMMENT '目标服务器ID',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '策略描述',
  `create_time` datetime(6) NOT NULL COMMENT '创建时间',
  `update_time` datetime(6) NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK7ucjd3yiw5mp66eko0yqn2tw0`(`route_server_id` ASC) USING BTREE,
  CONSTRAINT `FK7ucjd3yiw5mp66eko0yqn2tw0` FOREIGN KEY (`route_server_id`) REFERENCES `relay_route_server` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '路由规则' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for relay_route_server
-- ----------------------------
DROP TABLE IF EXISTS `relay_route_server`;
CREATE TABLE `relay_route_server`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '路由服务器ID',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '服务器名称',
  `host` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '服务器主机',
  `port` int NOT NULL COMMENT '服务器端口',
  `remark` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `status` tinyint NOT NULL COMMENT '服务器状态 0:禁用 1:启用',
  `create_time` datetime(6) NOT NULL COMMENT '创建时间',
  `update_time` datetime(6) NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '路由服务器' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for relay_server
-- ----------------------------
DROP TABLE IF EXISTS `relay_server`;
CREATE TABLE `relay_server`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '中继服务器ID',
  `auto_start` tinyint NOT NULL COMMENT '自动运行 0:否 1:是',
  `biz_error_code_field` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '业务错误码字段(JSONPath)',
  `biz_error_strategy` tinyint NOT NULL COMMENT '业务错误策略 0:由HTTP状态码决定 1:由业务错误码决定',
  `biz_success_code_value` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '业务成功码值(正确时返回的值)',
  `create_time` datetime(6) NOT NULL COMMENT '创建时间',
  `error_message` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '启动失败原因',
  `forward_url` varchar(320) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '桥接目标URL',
  `host` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '中继服务器主机',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '中继服务器名称',
  `override_redirect` tinyint NOT NULL COMMENT '覆盖桥接目标的重定向 0:否 1:是',
  `override_redirect_url` varchar(320) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '覆盖桥接目标的重定向URL',
  `port` int NOT NULL COMMENT '中继服务器端口',
  `request_id_header_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '请求ID头名称',
  `request_id_strategy` tinyint NOT NULL COMMENT '请求ID策略 0:随机生成 1:从请求头获取',
  `status` tinyint NOT NULL COMMENT '中继服务器状态 0:已禁用 1:未启动 2:运行中 3:启动失败',
  `update_time` datetime(6) NOT NULL COMMENT '更新时间',
  `forward_type` tinyint NOT NULL COMMENT '桥接目标类型 0:直接 1:路由',
  `deleted_time` datetime NULL DEFAULT NULL COMMENT '移除时间 为null代表未删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '请求记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for relay_server_route
-- ----------------------------
DROP TABLE IF EXISTS `relay_server_route`;
CREATE TABLE `relay_server_route`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '路由表ID',
  `relay_server_id` bigint NOT NULL COMMENT '中继服务器ID',
  `route_rule_id` bigint NOT NULL COMMENT '路由策略ID',
  `seq` int NOT NULL COMMENT '权重',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 73 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
