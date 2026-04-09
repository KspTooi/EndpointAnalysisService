-- 原kind为 0:部门 1:企业
-- 现kind为 0:企业(租户) 1:子企业 2:部门 3:班组


-- 先更新 企业为10 部门为20
UPDATE core_org SET kind = 10 WHERE kind = 1;
UPDATE core_org SET kind = 0 WHERE kind = 20;

-- 修改为正确值
UPDATE core_org SET kind = 0 WHERE kind = 10;
UPDATE core_org SET kind = 2 WHERE kind = 20;
