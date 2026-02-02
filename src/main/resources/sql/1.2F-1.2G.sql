ALTER TABLE core_user
    ADD COLUMN active_company_id BIGINT NULL COMMENT '已激活的公司ID';

-- 添加唯一索引防止同一用户在同一公司重复插入
CREATE UNIQUE INDEX uk_company_user ON core_company_member (company_id, user_id, deleted_time);

-- 移除rdbg_user_env表的active字段
ALTER TABLE rdbg_user_env
    DROP COLUMN active;

-- 在core_user表中添加active_env_id字段
ALTER TABLE core_user
    ADD COLUMN active_env_id BIGINT NULL COMMENT '已激活的环境ID';
