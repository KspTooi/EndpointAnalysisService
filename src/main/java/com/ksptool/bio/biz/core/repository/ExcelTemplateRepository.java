package com.ksptool.bio.biz.core.repository;

import com.ksptool.bio.biz.core.model.exceltemplate.ExcelTemplatePo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExcelTemplateRepository extends JpaRepository<ExcelTemplatePo, Long> {

    @Query("""
            SELECT u FROM ExcelTemplatePo u
            WHERE
            (:#{#po.id} IS NULL OR u.id  = :#{#po.id} )
            AND (:#{#po.attachId} IS NULL OR u.attachId  = :#{#po.attachId} )
            AND (:#{#po.name} IS NULL OR u.name LIKE CONCAT('%', :#{#po.name}, '%') )
            AND (:#{#po.code} IS NULL OR u.code LIKE CONCAT('%', :#{#po.code}, '%') )
            AND (:#{#po.remark} IS NULL OR u.remark LIKE CONCAT('%', :#{#po.remark}, '%') )
            AND (:#{#po.status} IS NULL OR u.status  = :#{#po.status} )
            AND (:#{#po.createTime} IS NULL OR u.createTime  = :#{#po.createTime} )
            AND (:#{#po.creatorId} IS NULL OR u.creatorId  = :#{#po.creatorId} )
            AND (:#{#po.updateTime} IS NULL OR u.updateTime  = :#{#po.updateTime} )
            AND (:#{#po.updaterId} IS NULL OR u.updaterId  = :#{#po.updaterId} )
            AND (:#{#po.deleteTime} IS NULL OR u.deleteTime  = :#{#po.deleteTime} )
            ORDER BY u.updateTime DESC
            """)
    Page<ExcelTemplatePo> getExcelTemplateList(@Param("po") ExcelTemplatePo po, Pageable pageable);

    @Query("""
            SELECT u FROM ExcelTemplatePo u
            WHERE u.code = :code
            """)
    ExcelTemplatePo getExcelTemplateByCode(@Param("code") String code);
}
