UPDATE core_org SET org_path_ids = (root_id + '') WHERE org_path_ids IS NULL;

ALTER TABLE core_org MODIFY COLUMN org_path_ids varchar(1024) NOT NULL COMMENT '从顶级组织到当前组织的ID列表 以,分割' AFTER parent_id;