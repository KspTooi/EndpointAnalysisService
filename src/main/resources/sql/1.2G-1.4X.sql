

ALTER TABLE core_user ADD COLUMN gender tinyint NULL COMMENT '性别 0:男 1:女 2:不愿透露';

ALTER TABLE core_user ADD COLUMN phone varchar(64) NULL COMMENT '手机号码';

ALTER TABLE core_user ADD COLUMN avatar_attach_id bigint NULL COMMENT '用户头像附件ID';