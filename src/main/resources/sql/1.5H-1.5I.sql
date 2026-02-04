ALTER TABLE ep_site
    ADD COLUMN name_py_idx VARCHAR(320) NULL COMMENT '站点名称-用于查询的拼音首字母索引';


ALTER TABLE ep_site
    ADD COLUMN username_py_idx VARCHAR(320) NULL COMMENT '账户-用于查询的拼音首字母索引';