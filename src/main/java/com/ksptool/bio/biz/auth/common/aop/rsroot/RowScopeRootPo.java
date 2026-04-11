package com.ksptool.bio.biz.auth.common.aop.rsroot;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;


/**
 * 仅本租户数据权限过滤器基类
 * 继承此类的实体查询时将自动过滤为仅查询当前登录用户所属租户的数据
 *
 * @author KspTool
 * @apiNote 子类需要显式声明 rootId 字段（映射列 root_id）
 */
@MappedSuperclass
@FilterDef(name = "rsRootFilter", parameters = {
        @ParamDef(name = "rootId", type = Long.class)
})
@Filter(
        name = "rsRootFilter",
        condition = "root_id = :rootId"
)
public abstract class RowScopeRootPo {

    @Transient
    @Column(name = "root_id", nullable = false, updatable = false, comment = "所属企业(租户)ID")
    private Long rootId;

}
