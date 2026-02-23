package com.ksptool.bio.biz.core.repository;

import com.ksptool.bio.biz.core.model.menu.dto.GetMenuTreeDto;
import com.ksptool.bio.biz.core.model.resource.ResourcePo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<ResourcePo, Long> {

    /**
     * 获取资源列表
     *
     * @param po 获取资源列表参数
     * @return 资源列表
     */
    @Query("""
            SELECT u FROM ResourcePo u
            WHERE
            (:#{#po.id} IS NULL OR u.id  = :#{#po.id} )
            AND (:#{#po.name} IS NULL OR u.name  LIKE CONCAT('%', :#{#po.name}, '%') )
            AND (:#{#po.description} IS NULL OR u.description  LIKE CONCAT('%', :#{#po.description}, '%') )
            AND (:#{#po.kind} IS NULL OR u.kind  = :#{#po.kind} )
            AND (:#{#po.menuKind} IS NULL OR u.menuKind  = :#{#po.menuKind} )
            AND (:#{#po.menuPath} IS NULL OR u.menuPath  = :#{#po.menuPath} )
            AND (:#{#po.menuQueryParam} IS NULL OR u.menuQueryParam  = :#{#po.menuQueryParam} )
            AND (:#{#po.menuIcon} IS NULL OR u.menuIcon  = :#{#po.menuIcon} )
            AND (:#{#po.path} IS NULL OR u.path  = :#{#po.path} )
            AND (:#{#po.permission} IS NULL OR u.permission  = :#{#po.permission} )
            AND (:#{#po.seq} IS NULL OR u.seq  = :#{#po.seq} )
            AND (:#{#po.createTime} IS NULL OR u.createTime  = :#{#po.createTime} )
            AND (:#{#po.creatorId} IS NULL OR u.creatorId  = :#{#po.creatorId} )
            AND (:#{#po.updateTime} IS NULL OR u.updateTime  = :#{#po.updateTime} )
            AND (:#{#po.updaterId} IS NULL OR u.updaterId  = :#{#po.updaterId} )
            ORDER BY u.updateTime DESC
            """)
    Page<ResourcePo> getResourceList(@Param("po") ResourcePo po, Pageable pageable);


    /**
     * 获取菜单与按钮树
     *
     * @param po 获取菜单与按钮树参数
     * @return 菜单与按钮树
     */
    @Query("""
            SELECT t FROM ResourcePo t
            LEFT JOIN FETCH t.parent
            WHERE t.kind = 0
            ORDER BY t.seq ASC,t.updateTime DESC
            """)
    List<ResourcePo> getUserMenuTree();

    /**
     * 获取菜单与按钮树
     *
     * @param po 获取菜单与按钮树参数
     * @return 菜单与按钮树
     */
    @Query("""
            SELECT t FROM ResourcePo t
            LEFT JOIN FETCH t.parent
            WHERE (:#{#po.menuKind} IS NULL OR t.menuKind = :#{#po.menuKind})
            AND t.kind = 0
            AND (
                (
                    :#{#po.name} IS NULL OR (t.name LIKE CONCAT('%',:#{#po.name},'%') OR
                    t.description LIKE CONCAT('%',:#{#po.name},'%') )
                )
            )
            AND (:#{#po.permission} IS NULL OR t.permission LIKE CONCAT('%',:#{#po.permission},'%') )
            ORDER BY t.seq ASC,t.updateTime DESC
            """)
    List<ResourcePo> getMenuTree(@Param("po") GetMenuTreeDto po);


    /**
     * 根据关键字获取菜单与按钮树
     *
     * @param keyword 模糊匹配 菜单名称、菜单描述
     * @return 菜单与按钮树
     */
    @Query("""
            SELECT t FROM ResourcePo t
            LEFT JOIN FETCH t.parent
            WHERE t.kind = 0
            AND (
                (
                    :keyword IS NULL OR ((t.name LIKE CONCAT('%',:keyword,'%') OR
                    t.description LIKE CONCAT('%',:keyword,'%') ) OR
                    t.permission LIKE CONCAT('%',:keyword,'%'))
                )
            )
            ORDER BY t.seq ASC,t.updateTime DESC
            """)
    List<ResourcePo> getMenuTreeByKeyword(@Param("keyword") String keyword);


    /**
     * 获取菜单子级数量
     *
     * @param id 菜单ID
     * @return 菜单子级数量
     */
    @Query("""
            SELECT COUNT(t) FROM ResourcePo t
            WHERE t.parent.id = :id AND t.kind = 0
            """)
    int getMenuChildrenCount(@Param("id") Long id);

    /**
     * 根据资源类型获取资源列表
     *
     * @param kind 资源类型
     * @return 资源列表
     */
    @Query("""
            SELECT t FROM ResourcePo t
            WHERE t.kind = :kind
            ORDER BY t.seq ASC, t.updateTime DESC
            """)
    List<ResourcePo> findByKind(@Param("kind") Integer kind);

    /**
     * 清空菜单
     */
    @Modifying
    @Query("DELETE FROM ResourcePo WHERE kind = 0")
    void clearMenu();

    /**
     * 清空端点
     */
    @Modifying
    @Query("DELETE FROM ResourcePo WHERE kind = 1")
    void clearEndpoint();

}
