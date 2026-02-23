package com.ksptool.bio.biz.relay.repository;

import com.ksptool.bio.biz.relay.model.routerule.po.RouteRulePo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRuleRepository extends JpaRepository<RouteRulePo, Long> {

    @Query("""
            SELECT u FROM RouteRulePo u
            LEFT JOIN FETCH u.routeServer
            WHERE
            (:#{#po.id} IS NULL OR u.id  = :#{#po.id} )
            AND (:#{#po.name} IS NULL OR u.name  = :#{#po.name} )
            AND (:#{#po.matchType} IS NULL OR u.matchType  = :#{#po.matchType} )
            AND (:#{#po.matchKey} IS NULL OR u.matchKey  = :#{#po.matchKey} )
            AND (:#{#po.matchOperator} IS NULL OR u.matchOperator  = :#{#po.matchOperator} )
            AND (:#{#po.matchValue} IS NULL OR u.matchValue  = :#{#po.matchValue} )
            AND (:#{#po.remark} IS NULL OR u.remark  = :#{#po.remark} )
            AND (:#{#po.createTime} IS NULL OR u.createTime  = :#{#po.createTime} )
            AND (:#{#po.updateTime} IS NULL OR u.updateTime  = :#{#po.updateTime} )
            ORDER BY u.updateTime DESC
            """)
    Page<RouteRulePo> getRouteRuleList(@Param("po") RouteRulePo po, Pageable pageable);


    /**
     * 根据名称统计路由规则数量
     *
     * @param name 路由规则名称
     * @return 路由规则数量
     */
    @Query("""
                    SELECT COUNT(t) FROM RouteRulePo t
                    WHERE t.name = :name
            """)
    Long countByName(@Param("name") String name);

    /**
     * 根据名称统计路由规则数量 排除指定ID
     *
     * @param name 路由规则名称
     * @param id   需排除的ID
     * @return 路由规则数量
     */
    @Query("""
                    SELECT COUNT(t) FROM RouteRulePo t
                    WHERE t.name = :name AND t.id != :id
            """)
    Long countByNameExcludeId(@Param("name") String name, @Param("id") Long id);

    /**
     * 根据ID列表获取路由规则列表
     *
     * @param ids ID列表
     * @return 路由规则列表
     */
    @Query("""
            SELECT t FROM RouteRulePo t
            WHERE t.id IN :ids
            """)
    List<RouteRulePo> getRouteRuleListByIds(@Param("ids") List<Long> ids);

}
