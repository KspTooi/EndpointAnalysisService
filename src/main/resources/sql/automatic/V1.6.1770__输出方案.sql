DROP PROCEDURE IF EXISTS add_unique_index_uk_name;

DELIMITER //

CREATE PROCEDURE add_unique_index_uk_name()
BEGIN
    -- 检查索引是否已经存在
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.statistics 
        WHERE table_schema = DATABASE()         -- 当前数据库
          AND table_name = 'assembly_op_schema' -- 你的表名
          AND index_name = 'uk_name'            -- 你的索引名
    ) THEN
        -- 如果不存在，则创建索引
ALTER TABLE assembly_op_schema ADD UNIQUE INDEX uk_name (name);
END IF;
END //

DELIMITER ;

-- 执行存储过程
CALL add_unique_index_uk_name();

-- 执行完后删除存储过程（可选）
DROP PROCEDURE add_unique_index_uk_name;