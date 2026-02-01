package com.ksptooi.biz.core.repository;

import com.ksptooi.biz.core.model.org.OrgPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrgRepository extends JpaRepository<OrgPo, Long> {

    /**
     * 根据id查询企业
     *
     * @param id 企业id
     * @return 企业
     */
    @Query("SELECT d FROM OrgPo d WHERE d.id = :id AND d.kind = 1")
    OrgPo getRootById(@Param("id") Long id);

    /**
     * 根据部门id查询部门
     *
     * @param deptId 部门id
     * @return 部门
     */
    @Query("SELECT d FROM OrgPo d WHERE d.id = :deptId AND d.kind = 0")
    OrgPo getDeptById(@Param("deptId") Long deptId);

    /**
     * 根据部门名称查询部门
     *
     * @param name  部门名称
     * @param orgId 组织id
     * @return 部门
     */
    @Query("SELECT d FROM OrgPo d WHERE d.name = :name AND d.kind = 0 AND d.rootId = :orgId")
    OrgPo getDeptByName(@Param("name") String name, @Param("orgId") Long orgId);


    @Query("""
            SELECT u FROM OrgPo u
            WHERE
            (:#{#po.id} IS NULL OR u.id  = :#{#po.id} )
            AND (:#{#po.rootId} IS NULL OR u.rootId  = :#{#po.rootId} )
            AND (:#{#po.parentId} IS NULL OR u.parentId  = :#{#po.parentId} )
            AND (:#{#po.kind} IS NULL OR u.kind  = :#{#po.kind} )
            AND (:#{#po.name} IS NULL OR u.name  LIKE CONCAT('%', :#{#po.name}, '%') )
            AND (:#{#po.principalId} IS NULL OR u.principalId  = :#{#po.principalId} )
            AND (:#{#po.principalName} IS NULL OR u.principalName  LIKE CONCAT('%', :#{#po.principalName}, '%') )
            ORDER BY u.updateTime DESC
            """)
    Page<OrgPo> getOrgList(@Param("po") OrgPo po, Pageable pageable);


    /**
     * 根据企业ID查询部门列表
     *
     * @param orgId 企业ID
     * @return 部门列表
     */
    @Query("SELECT d FROM OrgPo d WHERE d.rootId = :orgId AND d.kind = 0")
    List<OrgPo> getDeptByOrgId(@Param("orgId") Long orgId);

    /**
     * 根据一级组织ID查询组织机构数量
     *
     * @param rootId 一级组织ID
     * @return 组织机构数量
     */
    @Query("""
            SELECT COUNT(u) FROM OrgPo u
            WHERE u.parentId = :parentId
            """)
    Integer countByParentId(@Param("parentId") Long parentId);

    
    /**
     * 根据id列表和公司id查询部门列表
     *
     * @param ids       部门id列表
     * @param companyId 公司id
     * @return 部门列表
     */
    @Query("""
                    SELECT d FROM OrgPo d
                    WHERE d.id IN :ids AND d.rootId = :orgId AND d.kind = 0
            """)
    List<OrgPo> getDeptByIdsAndCompanyId(@Param("ids") List<Long> ids, @Param("orgId") Long orgId);


    /**
     * 根据部门id列表查询用户数量
     *
     * @param ids 部门id列表
     * @return 用户数量
     */
    @Query("""
            SELECT COUNT(u) FROM UserPo u
            WHERE u.deptId IN :ids
            """)
    Integer countUserByDeptIds(@Param("ids") List<Long> ids);
}
