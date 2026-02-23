package com.ksptool.bio.biz.core.repository;

import com.ksptool.bio.biz.core.model.post.PostPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<PostPo, Long> {

    /**
     * 查询岗位列表
     *
     * @param po       查询条件
     * @param pageable 分页条件
     * @return 岗位列表
     */
    @Query("""
            SELECT u FROM PostPo u
            WHERE
            (:#{#po.id} IS NULL OR u.id  = :#{#po.id} )
            AND (:#{#po.name} IS NULL OR u.name  LIKE CONCAT('%', :#{#po.name}, '%') )
            AND (:#{#po.code} IS NULL OR u.code  LIKE CONCAT('%', :#{#po.code}, '%') )
            ORDER BY u.seq ASC,u.updateTime DESC
            """)
    Page<PostPo> getPostList(@Param("po") PostPo po, Pageable pageable);


    /**
     * 根据编码统计岗位数量
     *
     * @param code 编码
     * @return 岗位数量
     */
    @Query("""
            SELECT COUNT(u) FROM PostPo u WHERE u.code = :code
            """)
    Integer countByCode(@Param("code") String code);

    /**
     * 根据编码统计岗位数量 排除指定ID
     *
     * @param code 编码
     * @param id   岗位ID
     * @return 岗位数量
     */
    @Query("""
            SELECT COUNT(u) FROM PostPo u WHERE u.code = :code AND u.id != :id
            """)
    Integer countByCodeExcludeId(@Param("code") String code, @Param("id") Long id);

}
