package com.ksptool.bio.biz.prompt.repository;

import com.ksptool.bio.biz.prompt.model.PromptPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface PromptRepository extends JpaRepository<PromptPo, Long>{

    @Query("""
    SELECT u FROM PromptPo u
    WHERE
    AND (:#{#po.name} IS NULL OR u.name LIKE CONCAT('%', :#{#po.name}, '%'))
    AND (:#{#po.tags} IS NULL OR u.tags LIKE CONCAT('%', :#{#po.tags}, '%'))
    ORDER BY u.updateTime DESC
    """)
    Page<PromptPo> getPromptList(@Param("po") PromptPo po, Pageable pageable);
}
