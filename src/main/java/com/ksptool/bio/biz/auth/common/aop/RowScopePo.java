package com.ksptool.bio.biz.auth.common.aop;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

@MappedSuperclass
@FilterDef(name = "rowScopeFilter", parameters = {
        @ParamDef(name = "rsMax", type = Integer.class),
        @ParamDef(name = "userId", type = Long.class),
        @ParamDef(name = "rootId", type = Long.class),
        @ParamDef(name = "deptIds", type = Long.class)
})
@Filter(
        name = "rowScopeFilter",
        condition = """
                ( :rsMax = 1 AND root_id = :rootId )
                OR ( :rsMax IN (2, 3, 5) AND dept_id IN (:deptIds) )
                OR ( :rsMax = 4 AND creator_id = :userId )
                """
)
public abstract class RowScopePo {

    @Column(name = "root_id", nullable = false, updatable = false, comment = "所属企业(租户)ID")
    private Long rootId;

    @Column(name = "dept_id", nullable = false, updatable = false, comment = "所属部门ID")
    private Long deptId;

    @Column(name = "creator_id", nullable = false, updatable = false, comment = "创建者ID")
    private Long creatorId;

}