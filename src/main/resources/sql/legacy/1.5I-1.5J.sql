ALTER TABLE ep_std_word
    ADD COLUMN delete_time DATETIME NULL COMMENT '删除时间 NULL未删';


ALTER TABLE ep_std_word
    ADD COLUMN source_name_py_idx VARCHAR(320) NULL COMMENT '简称 拼音首字母索引';

ALTER TABLE `ep_std_word`
    ADD UNIQUE INDEX `uk_source_name_delete` (`source_name`, `delete_time`);