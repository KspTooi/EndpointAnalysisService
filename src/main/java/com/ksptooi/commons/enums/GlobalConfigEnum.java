package com.ksptooi.commons.enums;

/**
 * 系统全局配置枚举
 */
public enum GlobalConfigEnum {

    ALLOW_INSTALL_WIZARD("allow.install.wizard","false","第一次进入时显示安装向导 true-允许执行安装向导 false-不执行安装向导"),
    ALLOW_INSTALL_WIZARD_UPGRADED("allow.install.wizard.upgraded","true","应用程序版本落后时是否允许执行升级向导 true-允许 false-拒绝"),

    APPLICATION_VERSION("application.version","1.0A","应用程序版本"),
    ALLOW_USER_REGISTER("allow.user.register","false","允许用户注册账号 true-允许 -false禁止"),

    ENDPOINT_ACCESS_DENIED("endpoint.access.denied","false","当端点未进行权限配置时禁止访问 true-禁止 false-允许"),

    RDBG_MAX_RESPONSE_SIZE("rdbg.max.response.size","10","端点调试工作台最大响应体大小 单位:MB 默认10MB"),
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