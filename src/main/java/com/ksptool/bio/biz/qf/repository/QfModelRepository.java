package com.ksptool.bio.biz.qf.repository;

import com.ksptool.bio.biz.qf.model.qfmodel.QfModelPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface QfModelRepository extends JpaRepository<QfModelPo, Long>{

    @Query("""
    SELECT u FROM QfModelPo u
    WHERE
    (:#{#po.name} IS NULL OR u.name LIKE CONCAT('%', :#{#po.name}, '%'))
    AND (:#{#po.code} IS NULL OR u.code LIKE CONCAT('%', :#{#po.code}, '%'))
    ORDER BY u.createTime DESC
    """)
    Page<QfModelPo> getQfModelList(@Param("po") QfModelPo po, Pageable pageable);

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
}
