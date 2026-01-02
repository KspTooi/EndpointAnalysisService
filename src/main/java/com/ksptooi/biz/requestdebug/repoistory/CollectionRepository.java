package com.ksptooi.biz.requestdebug.repoistory;

import com.ksptooi.biz.requestdebug.model.collection.CollectionPo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionRepository extends JpaRepository<CollectionPo, Long> {

    /**
     * 获取公司所拥有的全部请求集合树节点
     * @param companyId 公司ID
     * @return 请求集合树节点
     */
    @Query("""
            SELECT t FROM CollectionPo t
            WHERE t.companyId = :companyId
            ORDER BY t.seq ASC
            """)
    List<CollectionPo> getCollectionTreeListByCompanyId(@Param("companyId") Long companyId);
}
