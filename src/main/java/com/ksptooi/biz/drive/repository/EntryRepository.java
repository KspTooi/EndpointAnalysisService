package com.ksptooi.biz.drive.repository;

import com.ksptooi.biz.drive.model.po.EntryPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface EntryRepository extends JpaRepository<EntryPo, Long>{


    @Query("""
    SELECT u FROM EntryPo u
    WHERE
    (:parentId IS NULL OR u.parent.id = :parentId) AND
    u.companyId = :companyId AND
    (:#{#keyword} IS NULL OR u.name LIKE CONCAT('%', :keyword, '%'))
    ORDER BY u.updateTime DESC
    """)
    Page<EntryPo> getEntryList(@Param("parentId") Long parentId, @Param("keyword") String keyword, @Param("companyId") Long companyId, Pageable pageable);

    @Query("""
    SELECT COUNT(u) FROM EntryPo u
    WHERE
    u.parent.id = :parentId AND
    u.companyId = :companyId AND
    u.name = :name
    """)
    long countByName(@Param("companyId") Long companyId,@Param("parentId") Long parentId, @Param("name") String name);

    @Query("""
    SELECT COUNT(u) FROM EntryPo u
    WHERE
    u.parent.id = :parentId AND
    u.companyId = :companyId AND
    u.name = :name AND
    u.id != :id
    """)
    long countByNameIgnoreId(@Param("companyId") Long companyId,@Param("parentId") Long parentId, @Param("name") String name, @Param("id") Long id);

}
