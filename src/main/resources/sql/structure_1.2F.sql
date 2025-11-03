/*
 Navicat Premium Data Transfer

 Source Server         : 192.168.10.200
 Source Server Type    : MySQL
 Source Server Version : 80036
 Source Host           : 192.168.10.200:3306
 Source Schema         : endpoint_analysis_service

 Target Server Type    : MySQL
 Target Server Version : 80036
 File Encoding         : 65001

 Date: 03/11/2025 13:39:09
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for core_company
-- ----------------------------
DROP TABLE IF EXISTS `core_company`;
CREATE TABLE `core_company`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '公司ID',
  `create_time` datetime(6) NOT NULL COMMENT '创建时间',
  `deleted_time` datetime(6) NULL DEFAULT NULL COMMENT '移除时间 为null代表未删除',
  `description` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '公司描述',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公司名',
  `update_time` datetime(6) NOT NULL COMMENT '更新时间',
  `founder_id` bigint NOT NULL COMMENT '创始人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '公司成员' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for core_company_member
-- ----------------------------
DROP TABLE IF EXISTS `core_company_member`;
CREATE TABLE `core_company_member`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `deleted_time` datetime(6) NULL DEFAULT NULL COMMENT '删除时间 为NULL时代表未删除',
  `joined_time` datetime(6) NOT NULL COMMENT '加入时间',
  `role` tinyint NULL DEFAULT NULL COMMENT '职务 0:CEO 1:成员',
  `company_id` bigint NOT NULL COMMENT '公司',
  `user_id` bigint NOT NULL COMMENT '用户',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for core_config
-- ----------------------------
DROP TABLE IF EXISTS `core_config`;
CREATE TABLE `core_config`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '配置键',
  `config_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '配置值',
  `create_time` datetime(6) NOT NULL COMMENT '创建时间',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '配置描述',
  `update_time` datetime(6) NOT NULL COMMENT '更新时间',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID 为空表示全局配置',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_config`(`user_id`, `config_key`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

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
  UNIQUE INDEX `UKjtf8i69dpn27xhpqhnk20gjox`(`code`) USING BTREE,
  UNIQUE INDEX `UKne8ra7mq5cagp5gx87d49ift2`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 32 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户组' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for core_group_permission
-- ----------------------------
DROP TABLE IF EXISTS `core_group_permission`;
CREATE TABLE `core_group_permission`  (
  `group_id` bigint NOT NULL,
  `permission_id` bigint NOT NULL,
  PRIMARY KEY (`group_id`, `permission_id`) USING BTREE,
  INDEX `FKbg3iyfbnf6or1qxh7951yuo9s`(`permission_id`) USING BTREE,
  CONSTRAINT `FKbg3iyfbnf6or1qxh7951yuo9s` FOREIGN KEY (`permission_id`) REFERENCES `core_permission` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKkodk47oq26uyh8v0bpncw81mm` FOREIGN KEY (`group_id`) REFERENCES `core_group` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

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
  UNIQUE INDEX `UK7lcb6glmvwlro3p2w2cewxtvd`(`code`) USING BTREE,
  UNIQUE INDEX `UKpnvtwliis6p05pn6i3ndjrqt2`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 198 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '拥有此权限的用户组' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for core_resource
-- ----------------------------
DROP TABLE IF EXISTS `core_resource`;
CREATE TABLE `core_resource`  (
  `id` bigint NOT NULL COMMENT '主键id',
  `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
  `creator_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '资源描述',
  `kind` int NOT NULL COMMENT '资源类型 0:菜单 1:接口',
  `menu_icon` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '菜单图标(menuKind = 1时生效)',
  `menu_kind` int NULL DEFAULT NULL COMMENT '菜单类型 0:目录 1:菜单 2:按钮',
  `menu_path` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '菜单路径',
  `menu_query_param` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '菜单查询参数(menuKind = 1时生效)',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '资源名',
  `path` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '接口路径',
  `permission` varchar(320) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '所需权限',
  `seq` int NOT NULL COMMENT '排序',
  `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
  `updater_id` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `parent_id` bigint NULL DEFAULT NULL COMMENT '父级ID',
  `menu_btn_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '按钮ID(menuKind = 2时必填)',
  `menu_hidden` tinyint NULL DEFAULT NULL COMMENT '是否隐藏 0:否 1:是(menuKind = 1/2时生效)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for core_user
-- ----------------------------
DROP TABLE IF EXISTS `core_user`;
CREATE TABLE `core_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `create_time` datetime(6) NOT NULL COMMENT '创建时间',
  `email` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `last_login_time` datetime(6) NULL DEFAULT NULL COMMENT '最后登录时间',
  `login_count` int NOT NULL COMMENT '登录次数',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `password` varchar(1280) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `status` int NOT NULL COMMENT '用户状态 0:正常 1:封禁',
  `update_time` datetime(6) NOT NULL COMMENT '修改时间',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UKr43af9ap4edm43mmtq01oddj6`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户所属的用户组' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for core_user_group
-- ----------------------------
DROP TABLE IF EXISTS `core_user_group`;
CREATE TABLE `core_user_group`  (
  `user_id` bigint NOT NULL,
  `group_id` bigint NOT NULL,
  PRIMARY KEY (`user_id`, `group_id`) USING BTREE,
  INDEX `FKniig0y7hlk62uysagn1v3ed31`(`group_id`) USING BTREE,
  CONSTRAINT `FK7k9ade3lqbo483u9vuryxmm34` FOREIGN KEY (`user_id`) REFERENCES `core_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKniig0y7hlk62uysagn1v3ed31` FOREIGN KEY (`group_id`) REFERENCES `core_group` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for core_user_session
-- ----------------------------
DROP TABLE IF EXISTS `core_user_session`;
CREATE TABLE `core_user_session`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '会话ID',
  `create_time` datetime(6) NOT NULL COMMENT '创建时间',
  `expires_at` datetime(6) NOT NULL COMMENT '过期时间',
  `permissions` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户权限JSON',
  `token` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Token',
  `update_time` datetime(6) NOT NULL COMMENT '修改时间',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK8gq4v10ega75qhpjsj51fh13v`(`token`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 58 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

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
  INDEX `FKg4rnta0msk27ufko6a7e5t83j`(`relay_server_id`) USING BTREE,
  CONSTRAINT `FKg4rnta0msk27ufko6a7e5t83j` FOREIGN KEY (`relay_server_id`) REFERENCES `relay_server` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '端点文档版本' ROW_FORMAT = Dynamic;

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
  INDEX `FK55p2imuhod9xij71kubgyjvbx`(`ep_doc_id`) USING BTREE,
  INDEX `FKgrd9wkvqpw90v285yn8uib41r`(`relay_server_id`) USING BTREE,
  INDEX `FKntdfny15bq79d4357paeec3sd`(`ep_doc_version_id`) USING BTREE,
  CONSTRAINT `FK55p2imuhod9xij71kubgyjvbx` FOREIGN KEY (`ep_doc_id`) REFERENCES `ep_document` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKgrd9wkvqpw90v285yn8uib41r` FOREIGN KEY (`relay_server_id`) REFERENCES `relay_server` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKntdfny15bq79d4357paeec3sd` FOREIGN KEY (`ep_doc_version_id`) REFERENCES `ep_document_version` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 290 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ep_document_schema
-- ----------------------------
DROP TABLE IF EXISTS `ep_document_schema`;
CREATE TABLE `ep_document_schema`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '端点Schema ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ep_document_sync_log
-- ----------------------------
DROP TABLE IF EXISTS `ep_document_sync_log`;
CREATE TABLE `ep_document_sync_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '端点文档拉取记录ID',
  `create_time` datetime(6) NULL DEFAULT NULL COMMENT '拉取时间',
  `hash` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文档MD5值',
  `new_version_created` int NOT NULL COMMENT '是否创建了新版本 0:否 1:是',
  `new_version_num` bigint NULL DEFAULT NULL COMMENT '新版本号，如果创建了新版本，则记录新版本号',
  `pull_url` varchar(320) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '拉取地址',
  `status` int NOT NULL COMMENT '状态 0:成功 1:失败',
  `ep_doc_id` bigint NOT NULL COMMENT '端点文档ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FKd8xy10l7mqscx0q4hgx945e8n`(`ep_doc_id`) USING BTREE,
  CONSTRAINT `FKd8xy10l7mqscx0q4hgx945e8n` FOREIGN KEY (`ep_doc_id`) REFERENCES `ep_document` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

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
  UNIQUE INDEX `UKnsonk6m090osov34kdhw2607q`(`hash`) USING BTREE,
  INDEX `FK1g2qrppt9nl7jr2ngghvotsns`(`ep_doc_id`) USING BTREE,
  INDEX `FK17ddp1yioxh4o5h8ra10y70dm`(`relay_server_id`) USING BTREE,
  CONSTRAINT `FK17ddp1yioxh4o5h8ra10y70dm` FOREIGN KEY (`relay_server_id`) REFERENCES `relay_server` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FK1g2qrppt9nl7jr2ngghvotsns` FOREIGN KEY (`ep_doc_id`) REFERENCES `ep_document` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '端点文档接口' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for rdbg_request
-- ----------------------------
DROP TABLE IF EXISTS `rdbg_request`;
CREATE TABLE `rdbg_request`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户请求ID',
  `group_id` bigint NULL DEFAULT NULL COMMENT '请求组ID',
  `request_id` bigint NULL DEFAULT NULL COMMENT '原始请求ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户自定义请求名称',
  `method` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '请求方法',
  `url` varchar(320) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '请求URL',
  `request_headers` json NOT NULL COMMENT '请求头JSON',
  `request_body_type` varchar(320) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '请求体类型 JSON、表单数据、二进制',
  `request_body` json NULL COMMENT '请求体JSON',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `tree_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 221 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户请求记录' ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB AUTO_INCREMENT = 136 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '请求组中的请求' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for rdbg_request_log
-- ----------------------------
DROP TABLE IF EXISTS `rdbg_request_log`;
CREATE TABLE `rdbg_request_log`  (
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
  UNIQUE INDEX `UKljwt1u5dtge3g3nvjqomgrytj`(`request_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1575 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户请求记录' ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB AUTO_INCREMENT = 216 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户请求树' ROW_FORMAT = Dynamic;

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
  INDEX `FKaat92cuc4vbfqr5ghidg7xp4s`(`user_id`) USING BTREE,
  CONSTRAINT `FKaat92cuc4vbfqr5ghidg7xp4s` FOREIGN KEY (`user_id`) REFERENCES `core_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '过滤器所属的请求组' ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '过滤器操作' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for rdbg_simple_filter_request_group
-- ----------------------------
DROP TABLE IF EXISTS `rdbg_simple_filter_request_group`;
CREATE TABLE `rdbg_simple_filter_request_group`  (
  `group_id` bigint NOT NULL,
  `simple_filter_id` bigint NOT NULL,
  INDEX `FKt4f9n6t5dgkekaoflh6rborb1`(`simple_filter_id`) USING BTREE,
  INDEX `FKkvpa9pk50fenh8aplugouwbkm`(`group_id`) USING BTREE,
  CONSTRAINT `FKkvpa9pk50fenh8aplugouwbkm` FOREIGN KEY (`group_id`) REFERENCES `rdbg_request_group` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKt4f9n6t5dgkekaoflh6rborb1` FOREIGN KEY (`simple_filter_id`) REFERENCES `rdbg_simple_filter` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB AUTO_INCREMENT = 28 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '过滤器触发器' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for rdbg_user_env
-- ----------------------------
DROP TABLE IF EXISTS `rdbg_user_env`;
CREATE TABLE `rdbg_user_env`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '环境ID',
  `active` tinyint NOT NULL COMMENT '激活状态 0:启用 1:禁用',
  `create_time` datetime(6) NOT NULL COMMENT '创建时间',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '环境名',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '描述',
  `update_time` datetime(6) NOT NULL COMMENT '更新时间',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '环境共享存储' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for rdbg_user_env_storage
-- ----------------------------
DROP TABLE IF EXISTS `rdbg_user_env_storage`;
CREATE TABLE `rdbg_user_env_storage`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '共享存储ID',
  `create_time` datetime(6) NOT NULL COMMENT '创建时间',
  `init_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '初始值',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '变量名',
  `status` tinyint NOT NULL COMMENT '状态 0:启用 1:禁用',
  `update_time` datetime(6) NOT NULL COMMENT '更新时间',
  `value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '当前值',
  `env_id` bigint NOT NULL COMMENT '环境ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

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
  UNIQUE INDEX `UK9xwqeg7w0p1765p26kcn3kdnw`(`request_id`) USING BTREE,
  INDEX `FKpw5cr13kyp4yqlhkmimq0tx95`(`original_request_id`) USING BTREE,
  INDEX `FKfibbqima8h13npden7uiqejws`(`relay_server_id`) USING BTREE,
  INDEX `FK6vmcbcpprfa856dsc0t4k4eux`(`user_id`) USING BTREE,
  INDEX `IDX_CREATE_TIME`(`create_time`) USING BTREE,
  CONSTRAINT `FK6vmcbcpprfa856dsc0t4k4eux` FOREIGN KEY (`user_id`) REFERENCES `core_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKfibbqima8h13npden7uiqejws` FOREIGN KEY (`relay_server_id`) REFERENCES `relay_server` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKpw5cr13kyp4yqlhkmimq0tx95` FOREIGN KEY (`original_request_id`) REFERENCES `relay_request` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 483 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

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
  UNIQUE INDEX `UKqfbp1xsjy7lwq8ich6s0o4x16`(`request_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_relay_server_id`(`relay_server_id`) USING BTREE,
  CONSTRAINT `FKfgjbfvxueqqqcg01mitu358h0` FOREIGN KEY (`relay_server_id`) REFERENCES `relay_server` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2291134 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '重放请求' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for relay_request_copy1
-- ----------------------------
DROP TABLE IF EXISTS `relay_request_copy1`;
CREATE TABLE `relay_request_copy1`  (
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
  UNIQUE INDEX `UKqfbp1xsjy7lwq8ich6s0o4x16`(`request_id`) USING BTREE,
  INDEX `FKfgjbfvxueqqqcg01mitu358h0`(`relay_server_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  CONSTRAINT `relay_request_copy1_ibfk_1` FOREIGN KEY (`relay_server_id`) REFERENCES `relay_server` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 227386 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '重放请求' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for relay_route_rule
-- ----------------------------
DROP TABLE IF EXISTS `relay_route_rule`;
CREATE TABLE `relay_route_rule`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '路由规则ID',
  `create_time` datetime(6) NOT NULL COMMENT '创建时间',
  `match_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '匹配键',
  `match_operator` int NULL DEFAULT NULL COMMENT '匹配操作 0:等于',
  `match_type` tinyint NOT NULL COMMENT '匹配类型 0:全部 1:IP地址',
  `match_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '匹配值',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '路由策略名',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '策略描述',
  `update_time` datetime(6) NOT NULL COMMENT '更新时间',
  `route_server_id` bigint NULL DEFAULT NULL COMMENT '目标服务器',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '路由规则' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for relay_route_server
-- ----------------------------
DROP TABLE IF EXISTS `relay_route_server`;
CREATE TABLE `relay_route_server`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '路由服务器ID',
  `create_time` datetime(6) NOT NULL COMMENT '创建时间',
  `host` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '服务器主机',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '服务器名称',
  `port` int NOT NULL COMMENT '服务器端口',
  `remark` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `status` tinyint NOT NULL COMMENT '服务器状态 0:禁用 1:启用',
  `update_time` datetime(6) NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1010 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '路由规则' ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '请求记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for relay_server_route
-- ----------------------------
DROP TABLE IF EXISTS `relay_server_route`;
CREATE TABLE `relay_server_route`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '路由表ID',
  `create_time` datetime(6) NOT NULL COMMENT '创建时间',
  `seq` int NOT NULL COMMENT '权重',
  `update_time` datetime(6) NOT NULL COMMENT '更新时间',
  `relay_server_id` bigint NULL DEFAULT NULL COMMENT '中继服务器ID',
  `route_rule_id` bigint NULL DEFAULT NULL COMMENT '路由策略ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
