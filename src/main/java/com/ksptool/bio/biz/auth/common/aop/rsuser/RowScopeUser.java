package com.ksptool.bio.biz.auth.common.aop.rsuser;

import java.lang.annotation.*;


/**
 * 🥰静态仅本人数据权限注解
 * <p>
 * 标注在 Service 方法或类上，执行前自动激活 Hibernate rsUserFilter 过滤器，
 * 将查询结果限制为当前登录用户自己创建的数据（creator_id = 当前用户ID）。
 * <p>
 * 使用步骤：
 * 1.实体类继承 {@link RowScopeUserPo}，并且数据库表必须有 creator_id 字段：
 * 2.在 Service 方法或类上添加此注解 {@link RowScopeUser}
 * 
 * 注意：此注解不受用户角色的 rsMax 权限级别影响，也不会受超级权限（*:*:*）的影响，始终强制过滤为仅本人数据，
 * 适用于个人私有资源（如草稿、收藏、个人配置等）。
 * @author KspTool
 * @since 2026-03-15
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RowScopeUser {


}
