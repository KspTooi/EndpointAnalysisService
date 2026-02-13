DROP TABLE `rdbg_request`;
CREATE TABLE `rdbg_request` (
                                `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户请求ID',
                                `group_id` bigint DEFAULT NULL COMMENT '请求组ID',
                                `request_id` bigint DEFAULT NULL COMMENT '原始请求ID',
                                `user_id` bigint NOT NULL COMMENT '用户ID',
                                `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '用户自定义请求名称',
                                `method` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '请求方法',
                                `url` varchar(320) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '请求URL',
                                `request_headers` json DEFAULT NULL COMMENT '请求头JSON',
                                `request_body_type` varchar(320) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '请求体类型 JSON、表单数据、二进制',
                                `request_body` json DEFAULT NULL COMMENT '请求体JSON',
                                `create_time` datetime NOT NULL COMMENT '创建时间',
                                `update_time` datetime NOT NULL COMMENT '更新时间',
                                `tree_id` bigint NOT NULL,
                                PRIMARY KEY (`id`) USING BTREE,
                                UNIQUE KEY `uk_tree_id` (`tree_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=228 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='用户请求记录';