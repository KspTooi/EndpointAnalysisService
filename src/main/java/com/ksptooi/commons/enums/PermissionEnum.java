package com.ksptooi.commons.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 系统权限节点枚举
 */
public enum PermissionEnum {

    // 菜单管理
    MENU_ALL("menu:all", "[菜单]菜单完全访问权限"),
    MENU_LIST("menu:list", "[菜单]菜单列表"),
    MENU_ADD("menu:add", "[菜单]创建菜单"),
    MENU_EDIT("menu:edit", "[菜单]编辑菜单"),
    MENU_REMOVE("menu:remove", "[菜单]移除菜单"),

    // 用户管理
    USER_ALL("user:all", "[用户]用户完全访问权限"),
    USER_LIST("user:list", "[用户]用户列表"),
    USER_ADD("user:add", "[用户]创建用户"),
    USER_EDIT("user:edit", "[用户]编辑用户"),
    USER_REMOVE("user:remove", "[用户]移除用户"),

    // 用户组管理
    GROUP_ALL("group:all", "[用户组]用户组完全访问权限"),
    GROUP_LIST("group:list", "[用户组]用户组列表"),
    GROUP_ADD("group:add", "[用户组]创建用户组"),
    GROUP_EDIT("group:edit", "[用户组]编辑用户组"),
    GROUP_REMOVE("group:remove", "[用户组]删除用户组"),

    // 权限节点管理
    PERMISSION_ALL("permission:all", "[权限节点]权限节点完全访问权限"),
    PERMISSION_LIST("permission:list", "[权限节点]权限节点列表"),
    PERMISSION_ADD("permission:add", "[权限节点]创建权限节点"),
    PERMISSION_EDIT("permission:edit", "[权限节点]编辑权限节点"),
    PERMISSION_REMOVE("permission:remove", "[权限节点]移除权限节点"),

    // 会话管理
    SESSION_ALL("session:all", "[会话]会话完全访问权限"),
    SESSION_LIST("session:list", "[会话]会话列表"),
    SESSION_DETAILS("session:details", "[会话]查看会话详情"),
    SESSION_REMOVE("session:remove", "[会话]关闭会话"),

    // 配置管理
    CONFIG_ALL("config:all", "[配置]配置完全访问权限"),
    CONFIG_LIST("config:list", "[配置]配置列表"),
    CONFIG_ADD("config:add", "[配置]创建配置"),
    CONFIG_EDIT("config:edit", "[配置]编辑配置"),
    CONFIG_REMOVE("config:remove", "[配置]移除配置"),

    // 维护中心
    MAINTAIN_ALL("maintain:all", "[维护中心]维护中心完全访问权限"),
    MAINTAIN_PERMISSION("maintain:permission", "[维护中心]校验系统权限"),
    MAINTAIN_GROUP("maintain:group", "[维护中心]校验系统用户组"),
    MAINTAIN_USER("maintain:user", "[维护中心]校验系统用户"),
    MAINTAIN_CONFIG("maintain:config", "[维护中心]校验系统配置"),
    MAINTAIN_MENU("maintain:menu", "[维护中心]重置菜单"),

    // 中继请求记录
    REQUEST_ALL("request:all", "[中继请求记录]中继请求完全访问权限"),
    REQUEST_LIST("request:list", "[中继请求记录]中继请求记录列表"),
    REQUEST_DETAILS("request:details", "[中继请求记录]预览请求"),
    REQUEST_REPLAY("request:replay", "[中继请求记录]转到重放"),
    REQUEST_SAVE("request:save", "[中继请求记录]保存请求"),

    // 简单请求重放
    REPLAY_ALL("replay:all", "[请求重放]请求重放完全访问权限"),
    REPLAY_LIST("replay:list", "[请求重放]请求重放列表"),
    REPLAY_EXEC("replay:exec", "[请求重放]执行重放"),

    // 端点调试工作台
    RDBG_ALL("rdbg:all", "[端点调试]端点调试工作台完全访问权限"),
    RDBG_LIST("rdbg:list", "[端点调试]端点调试工作台列表"),
    RDBG_ITEM_ADD("rdbg:item:add", "[端点调试]新建菜单项"),
    RDBG_ITEM_EDIT("rdbg:item:edit", "[端点调试]编辑菜单项"),
    RDBG_ITEM_REMOVE("rdbg:item:remove", "[端点调试]移除菜单项"),
    RDBG_GROUP_EDIT("rdbg:group:edit", "[端点调试]编辑请求组属性"),
    RDBG_SEND("rdbg:send", "[端点调试]发送请求"),
    RDBG_DETAILS("rdbg:details", "[端点调试]查看响应详情"),

    // 基本过滤器
    FILTER_ALL("filter:all", "[过滤器]过滤器完全访问权限"),
    FILTER_LIST("filter:list", "[过滤器]过滤器列表"),
    FILTER_ADD("filter:add", "[过滤器]创建过滤器"),
    FILTER_EDIT("filter:edit", "[过滤器]修改过滤器"),
    FILTER_REMOVE("filter:remove", "[过滤器]移除过滤器"),

    // 中继通道
    RELAY_ALL("relay:all", "[中继通道]中继通道完全访问权限"),
    RELAY_LIST("relay:list", "[中继通道]中继通道列表"),
    RELAY_ADD("relay:add", "[中继通道]创建中继通道"),
    RELAY_EDIT("relay:edit", "[中继通道]编辑中继通道"),
    RELAY_REMOVE("relay:remove", "[中继通道]移除中继通道"),
    RELAY_START("relay:start", "[中继通道]启动中继通道"),
    RELAY_STOP("relay:stop", "[中继通道]停止中继通道"),

    // 路由策略
    ROUTE_ALL("route:all", "[路由策略]路由策略完全访问权限"),
    ROUTE_LIST("route:list", "[路由策略]路由策略列表"),
    ROUTE_ADD("route:add", "[路由策略]创建路由策略"),
    ROUTE_EDIT("route:edit", "[路由策略]编辑路由策略"),
    ROUTE_REMOVE("route:remove", "[路由策略]移除路由策略"),

    // 后端服务器
    SERVER_ALL("server:all", "[后端服务器]后端服务器完全访问权限"),
    SERVER_LIST("server:list", "[后端服务器]后端服务器列表"),
    SERVER_ADD("server:add", "[后端服务器]创建后端服务器"),
    SERVER_EDIT("server:edit", "[后端服务器]编辑后端服务器"),
    SERVER_REMOVE("server:remove", "[后端服务器]移除后端服务器"),

    // 请求环境管理
    ENV_ALL("env:all", "[请求环境]请求环境完全访问权限"),
    ENV_LIST("env:list", "[请求环境]请求环境列表"),
    ENV_ADD("env:add", "[请求环境]创建请求环境"),
    ENV_EDIT("env:edit", "[请求环境]编辑请求环境"),
    ENV_REMOVE("env:remove", "[请求环境]移除请求环境"),
    ENV_VAR_ADD("env:var:add", "[请求环境]创建共享存储变量"),
    ENV_VAR_EDIT("env:var:edit", "[请求环境]编辑共享存储变量"),
    ENV_VAR_REMOVE("env:var:remove", "[请求环境]移除共享存储变量"),

    // 端点管理
    ENDPOINT_ALL("endpoint:all", "[端点]端点完全访问权限"),
    ENDPOINT_LIST("endpoint:list", "[端点]端点列表"),
    ENDPOINT_ADD("endpoint:add", "[端点]创建端点"),
    ENDPOINT_EDIT("endpoint:edit", "[端点]编辑端点"),
    ENDPOINT_REMOVE("endpoint:remove", "[端点]移除端点"),
    ENDPOINT_CLEAR_CACHE("endpoint:clear:cache", "[端点]清空端点权限缓存")

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
     *
     * @param code 权限代码
     * @return 对应的枚举值，如果不存在返回null
     */
    public static PermissionEnum getByCode(String code) {
        if (StringUtils.isBlank(code)) {
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