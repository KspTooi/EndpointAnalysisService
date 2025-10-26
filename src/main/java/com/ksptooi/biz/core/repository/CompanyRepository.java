package com.ksptooi.biz.core.repository;

import com.ksptooi.biz.core.model.company.CompanyPo;
import com.ksptooi.biz.core.model.company.vo.GetCurrentUserCompanyListVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyPo, Long> {


    /**
     * 获取当前用户加入的公司列表
     *
     * @param name     公司名称
     * @param userId   用户ID
     * @param pageable 分页信息
     * @return 公司列表
     */
    @Query("""
            SELECT new com.ksptooi.biz.core.model.company.vo.GetCurrentUserCompanyListVo(
                t.id,
                t.founder.username,
                (SELECT u2.username FROM CompanyMemberPo m2 JOIN m2.user u2 WHERE m2.company.id = t.id AND m2.role = 0),
                t.name,
                t.description,
                COUNT(m.id),
                t.createTime,
                t.updateTime
            )
            FROM CompanyPo t
            LEFT JOIN t.members m
            LEFT JOIN m.user u
            WHERE u.id = :userId AND ( :name IS NULL OR t.name LIKE CONCAT('%', :name, '%') )
            GROUP BY t.id, t.founder.username, t.name, t.description, t.createTime, t.updateTime
            ORDER BY t.updateTime DESC
            """)
    Page<GetCurrentUserCompanyListVo> getCurrentUserCompanyList(@Param("name") String name, @Param("userId") Long userId, Pageable pageable);


    /**
     * 根据公司名称统计公司数量
     *
     * @param name 公司名称
     * @return 公司数量
     */
    @Query("""
            SELECT COUNT(t) FROM CompanyPo t
            WHERE t.name = :name
            """)
    Long countByCompanyName(@Param("name") String name);


    /**
     * 根据公司名称统计公司数量 排除指定ID
     *
     * @param name 公司名称
     * @param id   需排除的ID
     * @return 公司数量
     */
    @Query("""
            SELECT COUNT(t) FROM CompanyPo t
            WHERE t.name = :name AND t.id != :id
            """)
    Long countByCompanyNameExcludeId(@Param("name") String name, @Param("id") Long id);


}
