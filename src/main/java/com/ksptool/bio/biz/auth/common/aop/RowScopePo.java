package com.ksptool.bio.biz.auth.common.aop;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;


/**
 * 行级数据权限过滤器
 * 用于在Hibernate查询时根据用户权限过滤数据
 * 支持以下几种情况:
 * 1. 全部权限: rsMax = 0
 * 2. 本公司/租户及以下: rsMax = 1
 * 3. 本部门及以下: rsMax = 2
 * 4. 本部门: rsMax = 3
 * 5. 仅本人: rsMax = 4
 * 6. 指定部门: rsMax = 5
 * <p>
 * 请注意: 尽管在这个类中已经预定义了三个数据权限所必须的参数，但根据编码规范，继承此类的子类还需要显式声明这些参数。
 *
 */
@MappedSuperclass
@FilterDef(name = "rsFilter", parameters = {
        @ParamDef(name = "rsMax", type = Integer.class),
        @ParamDef(name = "userId", type = Long.class),
        @ParamDef(name = "rootId", type = Long.class),
        @ParamDef(name = "deptIds", type = Long.class)
})
@Filter(
        name = "rsFilter",
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