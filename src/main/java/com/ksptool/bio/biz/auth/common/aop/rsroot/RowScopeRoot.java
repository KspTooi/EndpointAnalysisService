package com.ksptool.bio.biz.auth.common.aop.rsroot;

import java.lang.annotation.*;


/**
 * 静态仅本租户数据权限注解
 * <p>
 * 标注在 Service 方法或类上，执行前自动激活 Hibernate rsRootFilter 过滤器，
 * 将查询结果限制为当前登录用户所属租户的数据（root_id = 当前用户 rootId）。
 * <p>
 * 使用步骤：
 * 1.实体类继承 {@link RowScopeRootPo}，并且数据库表必须有 root_id 字段
 * 2.在 Service 方法或类上添加此注解 {@link RowScopeRoot}
 *
 * 注意：此注解不受用户角色的 rsMax 权限级别影响，始终强制过滤为仅本租户数据，
 * 用户未绑定租户时将抛出异常。
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RowScopeRoot {


}
