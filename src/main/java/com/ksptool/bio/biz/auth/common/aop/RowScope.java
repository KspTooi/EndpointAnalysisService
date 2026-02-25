package com.ksptool.bio.biz.auth.common.aop;

import java.lang.annotation.*;

/**
 * 标记需要启用行级数据权限过滤的 Service 方法。
 * 被标注的方法在执行前会自动激活 Hibernate rowScopeFilter 过滤器。
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RowScope {

    /**
     * 是否需要用户绑定租户 为true时如果用户未绑定租户则抛出异常
     * @return 是否需要用户绑定租户
     */
    boolean requireRoot() default false;

}
