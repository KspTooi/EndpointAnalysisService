package com.ksptooi.commons.utils;

import xyz.downgoon.snowflake.Snowflake;
import org.springframework.stereotype.Component;

/**
 * ID生成器工具类
 */
public class IdWorker {

    private static final Snowflake snowflake;

    static {
        // 数据中心ID和工作节点ID都设为1，实际使用时应该根据实际部署情况配置
        snowflake = new Snowflake(1, 1);
    }

    /**
     * 生成唯一ID
     *
     * @return 返回生成的ID
     */
    public static long nextId() {
        return snowflake.nextId();
    }

    /**
     * 生成唯一ID（字符串形式）
     *
     * @return 返回字符串形式的ID
     */
    public static String nextIdStr() {
        return String.valueOf(nextId());
    }
} 