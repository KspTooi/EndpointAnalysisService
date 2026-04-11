package com.ksptool.bio.biz.auth.common.aop.rsuser;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;


/**
 * 仅本人数据权限过滤器基类
 * 继承此类的实体查询时将自动过滤为仅查询当前登录用户自己创建的数据
 *
 * @author KspTool
 * @apiNote 子类需要显式声明 creatorId 字段（映射列 creator_id）
 */
@MappedSuperclass
@FilterDef(name = "rsUserFilter", parameters = {
        @ParamDef(name = "userId", type = Long.class)
})
@Filter(
        name = "rsUserFilter",
        condition = "creator_id = :userId"
)
public abstract class RowScopeUserPo {

    @Transient
    @Column(name = "creator_id", nullable = false, updatable = false, comment = "创建者ID")
    private Long creatorId;

}