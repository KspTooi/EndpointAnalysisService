package com.ksptool.bio.biz.document.repository;

import com.ksptool.bio.biz.document.model.prompt.PromptPo;
import com.ksptool.bio.biz.document.model.prompt.dto.GetPromptListDto;
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
            (:#{#dto.name} IS NULL OR u.name LIKE CONCAT('%', :#{#dto.name}, '%'))
            AND :#{#dto.tags} IS NULL OR CAST(u.tags AS String) LIKE CONCAT('%', :#{#dto.tags}, '%'))
            AND u.version = (SELECT MAX(u2.version) FROM PromptPo u2 WHERE u2.name = u.name)
            ORDER BY u.createTime DESC
            """ )
    Page<PromptPo> getPromptList(@Param("po") GetPromptListDto dto, Pageable pageable);

    /**
     * 根据名称统计提示词数量 排除指定ID
     *
     * @param name 提示词名称
     * @param id   提示词ID
     * @return 提示词数量
     */
    @Query("""
            SELECT COUNT(t) FROM PromptPo t
            WHERE t.name = :name
            AND (:id IS NULL OR t.id != :id)
            AND t.version = (SELECT MAX(t2.version) FROM PromptPo t2 WHERE t2.name = t.name)
            """ )
    int countByNameExcludeId(@Param("name") String name, @Param("id") Long id);
}
