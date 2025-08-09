package com.ksptooi.commons.enums;

/**
 * 系统全局配置枚举
 */
public enum GlobalConfigEnum {

    APPLICATION_VERSION("application.version","1.0A","应用程序版本"),
    ALLOW_USER_REGISTER("allow.user.register","false","允许用户注册账号 true-允许 -false禁止"),
    ;

    /**
     * 配置键
     */
    private final String key;

    /**
     * 默认值
     */
    private final String defaultValue;

    /**
     * 配置描述
     */
    private final String description;

    GlobalConfigEnum(String key, String defaultValue, String description) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getDescription() {
        return description;
    }
} 