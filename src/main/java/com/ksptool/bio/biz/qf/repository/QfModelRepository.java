package com.ksptool.bio.biz.qf.repository;

import com.ksptool.bio.biz.qf.model.qfmodel.QfModelPo;
import com.ksptool.bio.biz.qf.model.qfmodelgroup.QfModelGroupPo;
import com.ksptool.bio.biz.qf.model.qfmodel.dto.GetQfModelListDto;
import com.ksptool.bio.biz.qf.model.qfmodel.vo.GetQfModelListVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface QfModelRepository extends JpaRepository<QfModelPo, Long>{

    @Query("""
    SELECT new com.ksptool.bio.biz.qf.model.qfmodel.vo.GetQfModelListVo(
        u.id,
        g.name,
        u.name,
        u.code,
        u.version,
        u.status,
        u.seq,
        u.createTime
    )
    FROM QfModelPo u
    LEFT JOIN QfModelGroupPo g ON g.id = u.groupId
    WHERE
    (:#{#dto.name} IS NULL OR u.name LIKE CONCAT('%', :#{#dto.name}, '%'))
    AND (:#{#dto.code} IS NULL OR u.code LIKE CONCAT('%', :#{#dto.code}, '%'))
    AND (:#{#dto.groupName} IS NULL OR g.name LIKE CONCAT('%', :#{#dto.groupName}, '%'))
    ORDER BY u.seq ASC, u.createTime DESC
    """)
    Page<GetQfModelListVo> getQfModelList(@Param("dto") GetQfModelListDto dto, Pageable pageable);

    /**
     * 根据编码统计流程模型数量 排除指定ID
     *
     * @param code 流程模型编码
     * @param id   流程模型ID
     * @return 流程模型数量
     */
    @Query("""
    SELECT COUNT(t) FROM QfModelPo t
    WHERE t.code = :code AND (:id IS NULL OR t.id != :id)
    """)
    Long countByCodeExcludeId(@Param("code") String code, @Param("id") Long id);

    /**
     * 根据模型分组ID统计流程模型数量
     *
     * @param groupId 模型分组ID
     * @return 流程模型数量
     */
    @Query("""
    SELECT COUNT(u) FROM QfModelPo u
    WHERE u.groupId = :groupId
    """)
    int countByGroupId(@Param("groupId") Long groupId);
}
