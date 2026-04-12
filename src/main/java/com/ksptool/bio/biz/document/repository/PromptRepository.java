package com.ksptool.bio.biz.document.repository;

import com.ksptool.bio.biz.document.model.prompt.PromptPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PromptRepository extends JpaRepository<PromptPo, Long> {

    @Query("""
            SELECT u FROM PromptPo u
            WHERE
            (:#{#po.name} IS NULL OR u.name LIKE CONCAT('%', :#{#po.name}, '%'))
            AND (:#{#po.tags} IS NULL OR u.tags LIKE CONCAT('%', :#{#po.tags}, '%'))
            ORDER BY u.updateTime DESC
            """ )
    Page<PromptPo> getPromptList(@Param("po") PromptPo po, Pageable pageable);
}
