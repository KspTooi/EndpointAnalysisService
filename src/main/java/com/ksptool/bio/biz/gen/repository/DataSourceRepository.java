package com.ksptool.bio.biz.gen.repository;

import com.ksptool.bio.biz.gen.model.datsource.DataSourcePo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DataSourceRepository extends JpaRepository<DataSourcePo, Long> {

    @Query("""
            SELECT u FROM DataSourcePo u
            WHERE
            (:#{#po.id} IS NULL OR u.id  = :#{#po.id} )
            AND (:#{#po.name} IS NULL OR u.name  LIKE CONCAT('%', :#{#po.name}, '%') )
            AND (:#{#po.code} IS NULL OR u.code  LIKE CONCAT('%', :#{#po.code}, '%') )
            AND (:#{#po.kind} IS NULL OR u.kind  = :#{#po.kind} )
            AND (:#{#po.drive} IS NULL OR u.drive  LIKE CONCAT('%', :#{#po.drive}, '%') )
            AND (:#{#po.url} IS NULL OR u.url  LIKE CONCAT('%', :#{#po.url}, '%') )
            AND (:#{#po.username} IS NULL OR u.username  LIKE CONCAT('%', :#{#po.username}, '%') )
            AND (:#{#po.password} IS NULL OR u.password  LIKE CONCAT('%', :#{#po.password}, '%') )
            AND (:#{#po.dbSchema} IS NULL OR u.dbSchema  LIKE CONCAT('%', :#{#po.dbSchema}, '%') )
            AND (:#{#po.createTime} IS NULL OR u.createTime  = :#{#po.createTime} )
            AND (:#{#po.creatorId} IS NULL OR u.creatorId  = :#{#po.creatorId} )
            AND (:#{#po.updateTime} IS NULL OR u.updateTime  = :#{#po.updateTime} )
            AND (:#{#po.updaterId} IS NULL OR u.updaterId  = :#{#po.updaterId} )
            ORDER BY u.updateTime DESC
            """)
    Page<DataSourcePo> getDataSourceList(@Param("po") DataSourcePo po, Pageable pageable);
}
