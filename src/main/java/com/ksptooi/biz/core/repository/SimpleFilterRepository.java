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
            (:#{#dto.name} IS NULL OR t.name  = :#{#dto.name} )
            AND (:#{#dto.direction} IS NULL OR t.direction  = :#{#dto.direction} )
            AND (:#{#dto.status} IS NULL OR t.status  = :#{#dto.status} )
            ORDER BY t.updateTime DESC
            """)
    Page<GetSimpleFilterListVo> getSimpleFilterList(@Param("dto") GetSimpleFilterListDto dto, Pageable pageable);
}
