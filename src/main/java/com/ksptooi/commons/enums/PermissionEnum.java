package com.ksptooi.commons.enums;

/**
 * 系统权限节点枚举
 */
public enum PermissionEnum {

    /**
     * 管理台权限(Admin)
     */
    ADMIN_ACCESS("admin:access", "允许访问管理台(admin)"),

    //权限管理
    ADMIN_PERMISSION_VIEW("admin:permission:view", "查看权限列表"),
    ADMIN_PERMISSION_SAVE("admin:permission:save", "新增/编辑权限"),
    ADMIN_PERMISSION_REMOVE("admin:permission:remove", "移除权限"),

    //用户管理
    ADMIN_USER_VIEW("admin:user:view", "查看用户列表"),
    ADMIN_USER_SAVE("admin:user:save", "新增/编辑用户"), 
    ADMIN_USER_DELETE("admin:user:delete", "删除用户"),

    //用户组管理  
    ADMIN_GROUP_VIEW("admin:group:view", "查看用户组"),
    ADMIN_GROUP_SAVE("admin:group:save", "新增/编辑用户组"), 
    ADMIN_GROUP_DELETE("admin:group:delete", "删除用户组"),
    ADMIN_GROUP_ASSIGN("admin:group:assign", "为用户组分配权限"),

    //配置管理
    ADMIN_CONFIG_VIEW("admin:config:view", "查看配置项"),
    ADMIN_CONFIG_SAVE("admin:config:save", "新增/编辑配置项"), 
    ADMIN_CONFIG_REMOVE("admin:config:remove", "移除配置项"),


    //系统维护  
    ADMIN_MAINTAIN_PERMISSION("admin:maintain:permission", "校验系统权限"),
    ADMIN_MAINTAIN_GROUP("admin:maintain:group", "校验系统用户组"),
    ADMIN_MAINTAIN_USER("admin:maintain:user", "校验系统用户"),
    ADMIN_MAINTAIN_CONFIG("admin:maintain:config", "校验系统配置"),
    ADMIN_MAINTAIN_ACTUATOR("admin:maintain:actuator", "访问actuator端点"),

    //会话管理
    ADMIN_SESSION_VIEW("admin:session:view", "查看会话列表"),
    ADMIN_SESSION_CLOSE("admin:session:close", "关闭会话"),

    ;



    private final String code;
    private final String description;

    PermissionEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据权限代码获取枚举值
     * @param code 权限代码
     * @return 对应的枚举值，如果不存在返回null
     */
    public static PermissionEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (PermissionEnum permission : values()) {
            if (permission.getCode().equals(code)) {
                return permission;
            }
        }
        return null;
    }
} 