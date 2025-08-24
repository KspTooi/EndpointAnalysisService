--1.迁移所有旧版用户请求

-- 开始一个事务，确保数据迁移的原子性
START TRANSACTION;

-- 声明一个变量用来存储新插入 user_request_tree 表的 ID
SET @new_tree_id = 0;

-- 插入新的 user_request_tree 记录
-- 从 user_request 表中选择所有需要迁移的数据
INSERT INTO `user_request_tree` (
    `user_id`,
    `parent_id`,
    `name`,
    `kind`,
    `seq`,
    `request_id`,
    `group_id`,
    `create_time`,
    `update_time`
)
SELECT
    ur.user_id,
    ur.group_id, -- 将旧的 group_id 作为新树节点的 parent_id
    ur.name,
    1, -- 节点类型 1: 代表用户请求
    ur.seq,
    ur.id, -- 将 user_request 的 id 关联到 request_id
    NULL, -- 因为这是一个请求节点，所以 group_id 为 NULL
    ur.create_time, -- 使用原始的创建时间
    ur.update_time  -- 使用原始的更新时间
FROM
    `user_request` ur
WHERE
    ur.tree_id IS NULL; -- 只迁移尚未关联到树的请求

-- 更新 user_request 表中的 tree_id
-- 使用 user_request.id 和 user_request_tree.request_id 进行关联
UPDATE
    `user_request` ur
    JOIN
    `user_request_tree` urt ON ur.id = urt.request_id AND urt.kind = 1
    SET
        ur.tree_id = urt.id
WHERE
    ur.tree_id IS NULL; -- 同样，只更新尚未关联的记录

-- 提交事务
COMMIT;


--2.为迁移的用户请求生成新的排序
WITH RankedTree AS (
    SELECT
        id,
        -- 按 parent_id 分区，然后按 create_time 升序排序，并生成行号
        ROW_NUMBER() OVER(PARTITION BY parent_id ORDER BY create_time ASC) as new_seq
    FROM
        user_request_tree
)
-- 更新 user_request_tree 表
UPDATE
    user_request_tree urt
-- 将原表与计算了新序号的 CTE 连接起来
    JOIN
    RankedTree rt ON urt.id = rt.id
-- 将旧的 seq 更新为新的 new_seq
    SET urt.seq = rt.new_seq;

-- 3.删除所有旧版组与过滤器关联
DELETE FROM user_request_group_simple_filter WHERE 1=1;
DELETE FROM user_request_group g WHERE g.tree_id IS NULL;

-- 4.处理数据库结构变更
ALTER TABLE user_request DROP COLUMN seq;
ALTER TABLE user_request_group DROP COLUMN parent_id;
ALTER TABLE user_request_group DROP COLUMN seq;
ALTER TABLE user_request MODIFY COLUMN tree_id BIGINT NOT NULL;
ALTER TABLE user_request_group MODIFY COLUMN tree_id BIGINT NOT NULL;
