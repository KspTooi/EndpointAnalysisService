INSERT INTO `core_menu` (
    `id`,
    `root_id`,
    `dept_id`,
    `parent_id`,
    `name`,
    `kind`,
    `path`,
    `icon`,
    `hide`,
    `permission_code`,
    `seq`,
    `remark`,
    `create_time`,
    `creator_id`,
    `update_time`,
    `updater_id`
)
SELECT
    `id`,
    0,                                      -- root_id: 旧表无此字段，默认初始化为0
    0,                                      -- dept_id: 旧表无此字段，默认初始化为0
    `parent_id`,
    LEFT(`name`, 32),                       -- name: 原128长度截断至32
    IFNULL(`menu_kind`, 0),                 -- kind: 对应原 menu_kind
    IFNULL(LEFT(`menu_path`, 80), ''),      -- path: 对应原 menu_path
    IFNULL(LEFT(`menu_icon`, 80), ''),      -- icon: 对应原 menu_icon
    IFNULL(`menu_hidden`, 0),               -- hide: 对应原 menu_hidden
    `permission`,                           -- permission_code: 对应原 permission
    `seq`,
    `description`,                          -- remark: 对应原 description
    IFNULL(`create_time`, NOW()),
    IFNULL(`creator_id`, 0),
    IFNULL(`update_time`, NOW()),
    IFNULL(`updater_id`, 0)
FROM `core_resource`
WHERE `kind` = 0; -- 只迁移菜单类型的数据，接口(1)数据在此逻辑中被舍弃