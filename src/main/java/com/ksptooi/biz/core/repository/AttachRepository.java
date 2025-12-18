package com.ksptooi.biz.core.repository;


import com.ksptooi.biz.core.model.attach.AttachPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AttachRepository extends JpaRepository<AttachPo, Long> {

    /**
     * 根据SHA256获取附件
     *
     * @param sha256 SHA256
     * @param kind 业务代码
     * @return 附件
     */
    @Query("""
    SELECT t FROM AttachPo t
    WHERE t.sha256 = :sha256
    """)
    AttachPo getBySha256(@Param("sha256") String sha256);

    /**
     * 根据SHA256和业务代码获取已经上传完成的附件
     * 
     * @param sha256 SHA256
     * @param kind 业务代码
     * @return 附件
     */
    @Query("""
    SELECT t FROM AttachPo t
    WHERE
    t.sha256 = :sha256 AND 
    t.kind = :kind
    """)
    AttachPo getBySha256AndKind(@Param("sha256") String sha256,@Param("kind") String kind);

    @Query("""
    SELECT t FROM AttachPo t
    WHERE t.path = :path AND t.kind = :kind
    ORDER BY t.createTime
    LIMIT 1
    """)
    AttachPo getByPath(@Param("path") String path,@Param("kind") String kind);


    @Query("""
    SELECT COUNT(t) FROM AttachPo t
    WHERE t.kind = :kind
    """)
    Long countByKind(@Param("kind") String kind);

}
