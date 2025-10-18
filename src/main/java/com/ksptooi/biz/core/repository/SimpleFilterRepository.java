package com.ksptooi.biz.core.repository;

import com.ksptooi.biz.core.model.filter.SimpleFilterPo;
import com.ksptooi.biz.core.model.filter.dto.GetSimpleFilterListDto;
import com.ksptooi.biz.core.model.filter.vo.GetSimpleFilterListVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SimpleFilterRepository extends JpaRepository<SimpleFilterPo, Long> {

    @Query("""
            SELECT new com.ksptooi.biz.core.model.filter.vo.GetSimpleFilterListVo(
                t.id,
                t.name,
                t.direction,
                t.status,
                SIZE(t.triggers),
                SIZE(t.operations),
                t.createTime
            ) FROM SimpleFilterPo t
            WHERE
            (:#{#dto.name} IS NULL OR t.name LIKE CONCAT('%', :#{#dto.name}, '%') )
            AND (:#{#dto.direction} IS NULL OR t.direction  = :#{#dto.direction} )
            AND (:#{#dto.status} IS NULL OR t.status  = :#{#dto.status} )
            ORDER BY t.updateTime DESC
            """)
    Page<GetSimpleFilterListVo> getSimpleFilterList(@Param("dto") GetSimpleFilterListDto dto, Pageable pageable);


    /**
     * 根据请求组ID获取在此组上应用的基本过滤器
     *
     * @param groupId 请求组ID
     * @return 基本过滤器列表
     */
    @Query("""
            SELECT new com.ksptooi.biz.core.model.filter.vo.GetSimpleFilterListVo(
                t.id,
                t.name,
                t.direction,
                t.status,
                SIZE(t.triggers),
                SIZE(t.operations),
                t.createTime
            ) FROM SimpleFilterPo t
            LEFT JOIN t.groups g
            WHERE g.id = :groupId
            ORDER BY t.updateTime DESC
            """)
    List<GetSimpleFilterListVo> getSimpleFilterListByGroupId(@Param("groupId") Long groupId);


    /**
     * 根据ID列表与用户ID获取过滤器
     *
     * @param ids    过滤器ID列表
     * @param userId 用户ID
     * @return 过滤器列表
     */
    @Query("""
            SELECT t FROM SimpleFilterPo t
            WHERE t.id IN :ids AND t.user.id = :userId
            """)
    List<SimpleFilterPo> getSimpleFilterByIds(@Param("ids") List<Long> ids, @Param("userId") Long userId);


}
