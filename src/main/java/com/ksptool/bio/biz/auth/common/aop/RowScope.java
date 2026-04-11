package com.ksptool.bio.biz.auth.common.aop;

import java.lang.annotation.*;

/**
 * 🥰标准动态数据权限注解
 * <p>
 * 标注在 Service 方法或类上，根据当前登录用户的 rsMax 权限级别动态过滤查询结果。
 * 权限级别：0-全部 | 1-本租户 | 2-本部门及以下 | 3-本部门 | 4-仅本人 | 5-指定部门
 * <p>
 * 使用步骤：
 * 1.实体类继承 {@link RowScopePo}，数据库表必须有 root_id、dept_id、creator_id 字段
 * 2.在 Service 方法或类上添加此注解 {@link RowScope}
 *
 * 注意：拥有超级权限（*:*:*）的用户不受此过滤器约束，可查询全部数据。
 * @author KspTool
 * @since 2026-03-15
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
