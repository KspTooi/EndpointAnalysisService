package com.ksptool.bio.commons.enums;

import lombok.Getter;

/**
 * 注册表初始配置枚举
 */
@Getter
public enum Registry {

    // ==================== 系统配置 ====================
    SYSTEM_APP_NAME("system.config.appName", "应用名称", "EndpointAnalysisService"),
    SYSTEM_APP_VERSION("system.config.appVersion", "应用版本", "1.0.0"),
    SYSTEM_ENVIRONMENT("system.config.environment", "运行环境", "production"),
    SYSTEM_TIMEZONE("system.config.timezone", "时区", "Asia/Shanghai"),
    SYSTEM_LANGUAGE("system.config.language", "语言", "zh_CN"),

    // ==================== 服务配置 ====================
    SERVICE_PORT("system.service.port", "服务端口", "8080"),
    SERVICE_CONTEXT_PATH("system.service.contextPath", "上下文路径", "/api"),
    SERVICE_MAX_THREADS("system.service.maxThreads", "最大线程数", "200"),
    SERVICE_TIMEOUT("system.service.timeout", "服务超时时间(秒)", "30"),
    SERVICE_ENABLE_CORS("system.service.enableCors", "是否启用跨域", "true"),

    // ==================== 数据库配置 ====================
    DATABASE_MAX_CONNECTIONS("system.database.maxConnections", "最大连接数", "100"),
    DATABASE_MIN_CONNECTIONS("system.database.minConnections", "最小连接数", "10"),
    DATABASE_CONNECTION_TIMEOUT("system.database.connectionTimeout", "连接超时时间(秒)", "30"),
    DATABASE_IDLE_TIMEOUT("system.database.idleTimeout", "空闲超时时间(秒)", "600"),
    DATABASE_MAX_LIFETIME("system.database.maxLifetime", "最大生命周期(秒)", "1800"),

    // ==================== 缓存配置 ====================
    CACHE_ENABLE("system.cache.enable", "是否启用缓存", "true"),
    CACHE_TTL("system.cache.ttl", "缓存过期时间(秒)", "3600"),
    CACHE_MAX_SIZE("system.cache.maxSize", "缓存最大条目数", "10000"),
    CACHE_EVICTION_POLICY("system.cache.evictionPolicy", "缓存淘汰策略", "LRU"),

    // ==================== 日志配置 ====================
    LOG_LEVEL("system.log.level", "日志级别", "INFO"),
    LOG_MAX_FILE_SIZE("system.log.maxFileSize", "日志文件最大大小(MB)", "100"),
    LOG_MAX_HISTORY("system.log.maxHistory", "日志保留天数", "30"),
    LOG_ENABLE_CONSOLE("system.log.enableConsole", "是否输出到控制台", "true"),

    // ==================== 安全配置 ====================
    SECURITY_JWT_SECRET("system.security.jwtSecret", "JWT密钥", "your-secret-key-change-in-production"),
    SECURITY_JWT_EXPIRATION("system.security.jwtExpiration", "JWT过期时间(秒)", "86400"),
    SECURITY_PASSWORD_MIN_LENGTH("system.security.passwordMinLength", "密码最小长度", "8"),
    SECURITY_MAX_LOGIN_ATTEMPTS("system.security.maxLoginAttempts", "最大登录尝试次数", "5"),
    SECURITY_LOCK_TIME("system.security.lockTime", "账户锁定时间(分钟)", "30"),

    // ==================== 文件上传配置 ====================
    UPLOAD_MAX_FILE_SIZE("system.upload.maxFileSize", "单文件最大大小(MB)", "50"),
    UPLOAD_MAX_REQUEST_SIZE("system.upload.maxRequestSize", "请求最大大小(MB)", "100"),
    UPLOAD_ALLOWED_EXTENSIONS("system.upload.allowedExtensions", "允许的文件扩展名", "jpg,jpeg,png,pdf,doc,docx,xls,xlsx"),
    UPLOAD_STORAGE_PATH("system.upload.storagePath", "文件存储路径", "/data/uploads"),

    // ==================== 任务调度配置 ====================
    SCHEDULER_POOL_SIZE("system.scheduler.poolSize", "调度线程池大小", "10"),
    SCHEDULER_ENABLE("system.scheduler.enable", "是否启用任务调度", "true"),
    SCHEDULER_AWAIT_TERMINATION("system.scheduler.awaitTermination", "等待终止时间(秒)", "60"),

    // ==================== 邮件配置 ====================
    MAIL_ENABLE("system.mail.enable", "是否启用邮件", "false"),
    MAIL_HOST("system.mail.host", "邮件服务器地址", "smtp.example.com"),
    MAIL_PORT("system.mail.port", "邮件服务器端口", "587"),
    MAIL_USERNAME("system.mail.username", "邮件用户名", ""),
    MAIL_FROM("system.mail.from", "发件人地址", "noreply@example.com"),

    // ==================== 监控配置 ====================
    MONITOR_ENABLE("system.monitor.enable", "是否启用监控", "true"),
    MONITOR_INTERVAL("system.monitor.interval", "监控间隔(秒)", "60"),
    MONITOR_ALERT_THRESHOLD("system.monitor.alertThreshold", "告警阈值(%)", "80"),

    // ==================== 业务配置 ====================
    BUSINESS_MAX_RETRY("business.config.maxRetry", "最大重试次数", "3"),
    BUSINESS_RETRY_INTERVAL("business.config.retryInterval", "重试间隔(秒)", "5"),
    BUSINESS_BATCH_SIZE("business.config.batchSize", "批处理大小", "100"),
    BUSINESS_ENABLE_ASYNC("business.config.enableAsync", "是否启用异步处理", "true"),

    // ==================== API配置 ====================
    API_RATE_LIMIT("system.api.rateLimit", "API速率限制(次/分钟)", "1000"),
    API_ENABLE_SWAGGER("system.api.enableSwagger", "是否启用Swagger", "true"),
    API_TIMEOUT("system.api.timeout", "API超时时间(秒)", "30"),

    // ==================== 第三方服务配置 ====================
    THIRD_PARTY_TIMEOUT("system.thirdParty.timeout", "第三方服务超时(秒)", "10"),
    THIRD_PARTY_MAX_RETRY("system.thirdParty.maxRetry", "第三方服务最大重试", "3"),
    ;

    /**
     * 注册表键路径
     */
    private final String keyPath;

    /**
     * 标签(显示名称)
     */
    private final String label;

    /**
     * 默认值
     */
    private final String defaultValue;

    Registry(String keyPath, String label, String defaultValue) {
        this.keyPath = keyPath;
        this.label = label;
        this.defaultValue = defaultValue;
    }

    /**
     * 根据keyPath查找枚举
     *
     * @param keyPath 键路径
     * @return 枚举值,不存在返回null
     */
    public static Registry fromKeyPath(String keyPath) {
        if (keyPath == null) {
            return null;
        }

        for (Registry registry : values()) {
            if (registry.keyPath.equals(keyPath)) {
                return registry;
            }
        }

        return null;
    }

    /**
     * 判断keyPath是否为预定义的注册表项
     *
     * @param keyPath 键路径
     * @return 是否存在
     */
    public static boolean exists(String keyPath) {
        return fromKeyPath(keyPath) != null;
    }
}
