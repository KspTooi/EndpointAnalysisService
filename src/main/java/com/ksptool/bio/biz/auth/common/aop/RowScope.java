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


}
