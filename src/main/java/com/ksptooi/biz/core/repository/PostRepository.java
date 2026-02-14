package com.ksptooi.biz.core.repository;

import com.ksptooi.biz.core.model.post.PostPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<PostPo, Long> {

    @Query("""
            SELECT u FROM PostPo u
            WHERE
            (:#{#po.id} IS NULL OR u.id  = :#{#po.id} )
            AND (:#{#po.name} IS NULL OR u.name  LIKE CONCAT('%', :#{#po.name}, '%') )
            AND (:#{#po.code} IS NULL OR u.code  LIKE CONCAT('%', :#{#po.code}, '%') )
            AND (:#{#po.seq} IS NULL OR u.seq  = :#{#po.seq} )
            ORDER BY u.updateTime DESC
            """)
    Page<PostPo> getPostList(@Param("po") PostPo po, Pageable pageable);
}
