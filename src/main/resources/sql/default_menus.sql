/*
 Navicat Premium Data Transfer

 Source Server         : 127.0.0.1
 Source Server Type    : MySQL
 Source Server Version : 80300
 Source Host           : 127.0.0.1:3306
 Source Schema         : endpoint_analysis_service

 Target Server Type    : MySQL
 Target Server Version : 80300
 File Encoding         : 65001

 Date: 20/10/2025 00:11:31
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Records of core_resource
-- ----------------------------
INSERT INTO `core_resource` VALUES (1296811154374529024, '2025-10-18 20:24:32.789184', 1, NULL, 0, 'ep:menu', 1, '/menu-manager', NULL, '菜单管理', NULL, '*', 100, '2025-10-18 22:41:34.579043', 1, 1296844586894888960);
INSERT INTO `core_resource` VALUES (1296814804291424256, '2025-10-18 20:39:02.997321', 1, NULL, 0, 'ep:user', 1, '/user-manager', NULL, '用户列表', NULL, '*', 0, '2025-10-18 20:43:18.205544', 1, 1296815828733399040);
INSERT INTO `core_resource` VALUES (1296815660575363072, '2025-10-18 20:42:27.151991', 1, NULL, 0, 'mdi:account-group', 1, '/group-manager', NULL, '用户组', NULL, '*', 1, '2025-10-18 20:44:05.828014', 1, 1296815828733399040);
INSERT INTO `core_resource` VALUES (1296815828733399040, '2025-10-18 20:43:07.243848', 1, NULL, 0, 'mdi:server', 0, NULL, NULL, '用户与授权', NULL, '*', 0, '2025-10-18 20:43:07.243848', 1, NULL);
INSERT INTO `core_resource` VALUES (1296816379793641472, '2025-10-18 20:45:18.626231', 1, NULL, 0, 'bi:lock', 1, '/permission-manager', NULL, '权限管理', NULL, '*', 2, '2025-10-18 20:45:18.626231', 1, 1296815828733399040);
INSERT INTO `core_resource` VALUES (1296816823077048320, '2025-10-18 20:47:04.313181', 1, NULL, 0, 'ep:histogram', 1, '/session-manager', NULL, '会话管理', NULL, '*', 3, '2025-10-18 20:47:18.386680', 1, 1296815828733399040);
INSERT INTO `core_resource` VALUES (1296844586894888960, '2025-10-18 22:37:23.723984', 1, NULL, 0, 'ep:tools', 0, NULL, NULL, '系统配置', NULL, '*', 1, '2025-10-18 22:43:38.969062', 1, NULL);
INSERT INTO `core_resource` VALUES (1296845081621434368, '2025-10-18 22:39:21.675617', 1, NULL, 0, 'mdi:database', 1, '/config-manager', NULL, '配置管理', NULL, '*', 0, '2025-10-18 22:42:11.466594', 1, 1296844586894888960);
INSERT INTO `core_resource` VALUES (1296845417455161344, '2025-10-18 22:40:41.744997', 1, NULL, 0, 'ep:tools', 1, '/application-maintain', NULL, '维护中心', NULL, '*', 1, '2025-10-18 22:41:46.212889', 1, 1296844586894888960);
INSERT INTO `core_resource` VALUES (1296846095959330816, '2025-10-18 22:43:23.512238', 1, NULL, 0, 'ep:sort', 0, NULL, NULL, '请求管理', NULL, '*', 2, '2025-10-18 22:43:44.961148', 1, NULL);
INSERT INTO `core_resource` VALUES (1296846388788858880, '2025-10-18 22:44:33.328874', 1, NULL, 0, 'ep:sort', 1, '/request-manager', NULL, '中继请求记录', NULL, '*', 0, '2025-10-18 22:45:43.233125', 1, 1296846095959330816);
INSERT INTO `core_resource` VALUES (1296847132946468864, '2025-10-18 22:47:30.749032', 1, NULL, 0, 'ep:check', 1, '/replay-request-manager', NULL, '简单请求重放', NULL, '*', 1, '2025-10-18 22:47:37.311766', 1, 1296846095959330816);
INSERT INTO `core_resource` VALUES (1296847552464949248, '2025-10-18 22:49:10.770857', 1, NULL, 0, 'ep:cpu', 1, '/user-request-manager', NULL, '端点调试工作台', NULL, '*', 2, '2025-10-18 22:49:10.770857', 1, 1296846095959330816);
INSERT INTO `core_resource` VALUES (1296847907722498048, '2025-10-18 22:50:35.470327', 1, NULL, 0, 'ep:filter', 1, '/simple-filter-manager', NULL, '基本过滤器', NULL, '*', 3, '2025-10-18 22:50:35.470327', 1, 1296846095959330816);
INSERT INTO `core_resource` VALUES (1296848321385730048, '2025-10-18 22:52:14.095372', 1, NULL, 0, 'bi:globe', 0, NULL, NULL, '中继服务配置', NULL, '*', 3, '2025-10-18 22:52:14.095372', 1, NULL);
INSERT INTO `core_resource` VALUES (1296848520753582080, '2025-10-18 22:53:01.628332', 1, NULL, 0, 'ep:sort', 1, '/relay-server-manager', NULL, '中继通道', NULL, '*', 0, '2025-10-18 22:53:01.628332', 1, 1296848321385730048);
INSERT INTO `core_resource` VALUES (1296849060170436608, '2025-10-18 22:55:10.235902', 1, NULL, 0, 'bi:box', 1, '/route-group', NULL, '路由策略', NULL, '*', 1, '2025-10-19 15:39:18.084689', 1, 1296848321385730048);
INSERT INTO `core_resource` VALUES (1296863476463046656, '2025-10-18 23:52:27.347761', 1, NULL, 0, 'mdi:server', 1, '/route-server', NULL, '后端服务器', NULL, '*', 2, '2025-10-19 02:44:12.339286', 1, 1296848321385730048);
INSERT INTO `core_resource` VALUES (1297179889920774144, '2025-10-19 20:49:46.194251', 1, NULL, 0, 'mdi:archive', 1, '/user-request-env-manager', NULL, '请求环境管理', NULL, '*', 4, '2025-10-19 20:50:07.880998', 1, 1296846095959330816);
INSERT INTO `core_resource` VALUES (1297220002050609152, '2025-10-19 23:29:09.671618', 1, NULL, 0, 'carbon:view', 1, '*', NULL, '资源与权限', NULL, '*', 4, '2025-10-19 23:29:16.782811', 1, 1296815828733399040);

SET FOREIGN_KEY_CHECKS = 1;
