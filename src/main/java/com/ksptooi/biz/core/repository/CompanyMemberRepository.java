package com.ksptooi.biz.core.repository;

import com.ksptooi.biz.core.model.companymember.CompanyMemberPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyMemberRepository extends JpaRepository<CompanyMemberPo, Long> {

    /**
     * 查询指定公司和用户的成员关系
     * 
     * @param companyId 公司ID
     * @param userId 用户ID
     * @return 成员关系
     */
    @Query("""
            SELECT m FROM CompanyMemberPo m
            WHERE m.company.id = :companyId AND m.user.id = :userId
            """)
    CompanyMemberPo findByCompanyIdAndUserId(@Param("companyId") Long companyId, @Param("userId") Long userId);

    @Query("""
            SELECT u FROM CompanyMemberPo u
            WHERE
            (:#{#po.id} IS NULL OR u.id  = :#{#po.id} )
            AND (:#{#po.role} IS NULL OR u.role  = :#{#po.role} )
            AND (:#{#po.joinedTime} IS NULL OR u.joinedTime  = :#{#po.joinedTime} )
            AND (:#{#po.deletedTime} IS NULL OR u.deletedTime  = :#{#po.deletedTime} )
            """)
    Page<CompanyMemberPo> getCompanyMemberList(@Param("po") CompanyMemberPo po, Pageable pageable);
}
