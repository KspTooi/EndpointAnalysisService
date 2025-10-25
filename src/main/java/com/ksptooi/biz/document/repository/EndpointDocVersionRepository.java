package com.ksptooi.biz.document.repository;

import com.ksptooi.biz.document.model.epdocversion.EndpointDocVersionPo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EndpointDocVersionRepository extends CrudRepository<EndpointDocVersionPo, Long> {

    /**
     * 根据HASH查询最新版本，如果存在多个版本，则返回最新版本
     *
     * @param hash HASH
     * @return 最新版本，如果不存在，则返回null
     */
    @Query("""
                    SELECT t FROM EndpointDocVersionPo t
                    WHERE t.hash = :hash 
                    AND t.isLatest = 1
                    ORDER BY t.version DESC
                    LIMIT 1
            """)
    EndpointDocVersionPo getLatestByHash(@Param("hash") String hash);

    /**
     * 获取最新版本
     *
     * @param epDocId 端点文档ID
     * @return 最新版本，如果不存在，则返回null
     */
    @Query("""
                    SELECT t FROM EndpointDocVersionPo t
                    WHERE t.endpointDoc.id = :epDocId
                    ORDER BY t.version DESC
                    LIMIT 1
            """)
    EndpointDocVersionPo getLatestVersion(@Param("epDocId") Long epDocId);

}
