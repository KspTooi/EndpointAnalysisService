package com.ksptooi.biz.core.repository;

import com.ksptooi.biz.core.model.noticetemplate.NoticeTemplatePo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeTemplateRepository extends JpaRepository<NoticeTemplatePo, Long> {

    @Query("""
            SELECT u FROM NoticeTemplatePo u
            WHERE
            (:#{#po.id} IS NULL OR u.id  = :#{#po.id} )
            AND (:#{#po.name} IS NULL OR u.name  LIKE CONCAT('%', :#{#po.name}, '%') )
            AND (:#{#po.code} IS NULL OR u.code  LIKE CONCAT('%', :#{#po.code}, '%') )
            AND (:#{#po.content} IS NULL OR u.content  LIKE CONCAT('%', :#{#po.content}, '%') )
            AND (:#{#po.status} IS NULL OR u.status  = :#{#po.status} )
            AND (:#{#po.remark} IS NULL OR u.remark  LIKE CONCAT('%', :#{#po.remark}, '%') )
            AND (:#{#po.createTime} IS NULL OR u.createTime  = :#{#po.createTime} )
            AND (:#{#po.creatorId} IS NULL OR u.creatorId  = :#{#po.creatorId} )
            AND (:#{#po.updateTime} IS NULL OR u.updateTime  = :#{#po.updateTime} )
            AND (:#{#po.updaterId} IS NULL OR u.updaterId  = :#{#po.updaterId} )
            AND (:#{#po.deleteTime} IS NULL OR u.deleteTime  = :#{#po.deleteTime} )
            ORDER BY u.updateTime DESC
            """)
    Page<NoticeTemplatePo> getNoticeTemplateList(@Param("po") NoticeTemplatePo po, Pageable pageable);


    /**
     * 查询通知模板数量
     * 
     * @param code 通知模板编码
     * @return 通知模板数量
     */
    @Query("""
            SELECT COUNT(u) FROM NoticeTemplatePo u
            WHERE u.code = :code
            """)
    int countNoticeTemplateByCode(@Param("code") String code);

    /**
     * 查询通知模板数量 排除指定ID
     * 
     * @param code 通知模板编码
     * @param id 通知模板ID
     * @return 通知模板数量
     */
    @Query("""
            SELECT COUNT(u) FROM NoticeTemplatePo u
            WHERE u.code = :code AND u.id != :id
            """)
    int countNoticeTemplateByCodeExcludeId(@Param("code") String code, @Param("id") Long id);
}
