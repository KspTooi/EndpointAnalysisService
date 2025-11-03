package com.ksptooi.biz.core.repository;

import com.ksptooi.biz.core.model.companymember.CompanyMemberPo;
import com.ksptooi.biz.core.model.companymember.dto.GetCompanyMemberListDto;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyMemberRepository extends JpaRepository<CompanyMemberPo, Long> {

    /**
     * 查询指定公司和用户的成员关系
     *
     * @param companyId 公司ID
     * @param userId    用户ID
     * @return 成员关系
     */
    @Query("""
            SELECT m FROM CompanyMemberPo m
            WHERE m.company.id = :companyId AND m.user.id = :userId
            """)
    CompanyMemberPo findByCompanyIdAndUserId(@Param("companyId") Long companyId, @Param("userId") Long userId);

    /**
     * 使用悲观锁查询指定公司和用户的成员关系（防止并发插入）
     *
     * @param companyId 公司ID
     * @param userId    用户ID
     * @return 成员关系
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT m FROM CompanyMemberPo m
            WHERE m.company.id = :companyId AND m.user.id = :userId
            """)
    CompanyMemberPo getByCompanyIdAndUserIdWithLock(@Param("companyId") Long companyId, @Param("userId") Long userId);

    /**
     * 查询公司成员列表
     *
     * @param dto 查询条件
     * @param pageable 分页参数
     * @return 公司成员列表
     */
    @Query("""
            SELECT t FROM CompanyMemberPo t
            LEFT JOIN t.company c
            LEFT JOIN t.user u
            WHERE (:#{#dto.companyId} IS NULL OR c.id = :#{#dto.companyId})
            AND (:#{#dto.username} IS NULL OR u.username LIKE CONCAT('%', :#{#dto.username}, '%'))
            AND (:#{#dto.role} IS NULL OR t.role = :#{#dto.role})
            ORDER BY t.joinedTime DESC
            """)
    Page<CompanyMemberPo> getCompanyMemberList(@Param("dto") GetCompanyMemberListDto po, Pageable pageable);

    /**
     * 统计指定公司的成员数量
     *
     * @param companyId 公司ID
     * @return 成员数量
     */
    @Query("""
            SELECT COUNT(m) FROM CompanyMemberPo m
            WHERE m.company.id = :companyId
            """)
    Long countByCompanyId(@Param("companyId") Long companyId);
}
