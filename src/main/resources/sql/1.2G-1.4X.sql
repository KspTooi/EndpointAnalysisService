ALTER TABLE core_user ADD COLUMN gender tinyint NULL COMMENT '性别 0:男 1:女 2:不愿透露';

ALTER TABLE core_user ADD COLUMN phone varchar(64) NULL COMMENT '手机号码';

ALTER TABLE core_user ADD COLUMN avatar_attach_id bigint NULL COMMENT '用户头像附件ID';

ALTER TABLE core_user ADD COLUMN delete_time DATETIME NULL COMMENT '删除时间 为NULL未删';

ALTER TABLE core_user ADD COLUMN is_system TINYINT NULL COMMENT '内置用户 0:否 1:是';

UPDATE core_user SET is_system = 0

ALTER TABLE core_user MODIFY COLUMN is_system TINYINT NOT NULL DEFAULT 0 COMMENT '内置用户 0:否 1:是';

UPDATE core_user SET is_system = 1 WHERE username = "admin" OR username = "guest"