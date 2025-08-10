package com.ksptooi.biz.core.repository;

import com.ksptooi.biz.core.model.epdoc.EndpointDocPo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EndpointDocRepository extends CrudRepository<EndpointDocPo,Long> {


    @Query("""
        SELECT t FROM EndpointDocPo t
        ORDER BY t.createTime DESC
        """)
    Page<EndpointDocPo> getRelayDocPullConfigList(Pageable pageable);
}
