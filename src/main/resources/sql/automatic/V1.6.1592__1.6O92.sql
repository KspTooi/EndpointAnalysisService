
ALTER TABLE `core_notice` MODIFY COLUMN `sender_id` bigint NULL COMMENT '发送人ID (NULL为系统)' AFTER `category`;
UPDATE core_notice SET sender_name = '系统' WHERE sender_id IS NULL;
