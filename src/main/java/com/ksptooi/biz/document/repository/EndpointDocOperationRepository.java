package com.ksptooi.biz.document.repository;

import com.ksptooi.biz.document.model.epdocoperation.EndpointDocOperationPo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EndpointDocOperationRepository extends CrudRepository<EndpointDocOperationPo, Long> {


    /**
     * 根据版本查询端点文档接口
     *
     * @param versionId 版本ID
     * @return 端点文档接口列表
     */
    @Query("""
                    SELECT t FROM EndpointDocOperationPo t
                    WHERE t.version.id = :versionId
            """)
    List<EndpointDocOperationPo> getOperationListByVersionId(@Param("versionId") Long versionId);


}
