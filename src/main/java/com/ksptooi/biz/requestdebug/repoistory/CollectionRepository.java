package com.ksptooi.biz.requestdebug.repoistory;

import com.ksptooi.biz.requestdebug.model.collection.CollectionPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollectionRepository extends JpaRepository<CollectionPo, Long> {

    /**
     * 获取公司所拥有的全部请求集合树节点
     *
     * @param companyId 公司ID
     * @return 请求集合树节点
     */
    @Query("""
            SELECT t FROM CollectionPo t
            WHERE t.companyId = :companyId
            ORDER BY t.seq ASC
            """)
    List<CollectionPo> getCollectionTreeListByCompanyId(@Param("companyId") Long companyId);


    /**
     * 获取父级下的最大排序+1
     *
     * @param parentId 父级ID
     * @return 最大排序 如果没有任何内容，则返回1
     */
    @Query("""
            SELECT COALESCE(MAX(t.seq) + 1, 1) FROM CollectionPo t
            WHERE (:parentId IS NULL OR t.parent.id = :parentId) AND
                  (:parentId IS NOT NULL OR t.parent.id IS NULL)
            """)
    Integer getMaxSeqInParent(@Param("parentId") Long parentId);

    /**
     * 根据ID和公司ID获取节点
     *
     * @param id        ID
     * @param companyId 公司ID
     * @return 数量
     */
    @Query("""
            SELECT t FROM CollectionPo t
            WHERE t.id = :id AND t.companyId = :companyId
            """)
    CollectionPo getByIdAndCompanyId(@Param("id") Long id, @Param("companyId") Long companyId);


    /**
     * 获取公司顶级节点
     *
     * @param companyId 公司ID
     * @return 顶级节点
     */
    @Query("""
            SELECT t FROM CollectionPo t
            WHERE t.parent IS NULL AND t.companyId = :companyId
            ORDER BY t.seq ASC
            """)
    List<CollectionPo> getRootNodeListByCompanyId(@Param("companyId") Long companyId);

}
