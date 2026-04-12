package com.ksptool.bio.biz.core.repository;

import com.ksptool.bio.biz.core.model.menu.MenuPo;
import com.ksptool.bio.biz.core.model.menu.dto.GetMenuTreeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<MenuPo, Long> {

    @Query("""
            SELECT u FROM MenuPo u
            WHERE
            (:#{#po.rootId} IS NULL OR u.rootId = :#{#po.rootId} )
            AND (:#{#po.deptId} IS NULL OR u.deptId = :#{#po.deptId} )
            AND (:#{#po.parentId} IS NULL OR u.parentId = :#{#po.parentId} )
            AND (:#{#po.name} IS NULL OR u.name LIKE CONCAT('%', :#{#po.name}, '%'))
            AND (:#{#po.kind} IS NULL OR u.kind = :#{#po.kind} )
            AND (:#{#po.path} IS NULL OR u.path LIKE CONCAT('%', :#{#po.path}, '%'))
            AND (:#{#po.icon} IS NULL OR u.icon LIKE CONCAT('%', :#{#po.icon}, '%'))
            AND (:#{#po.hide} IS NULL OR u.hide = :#{#po.hide} )
            AND (:#{#po.permissionCode} IS NULL OR u.permissionCode LIKE CONCAT('%', :#{#po.permissionCode}, '%'))
            AND (:#{#po.seq} IS NULL OR u.seq = :#{#po.seq} )
            AND (:#{#po.remark} IS NULL OR u.remark LIKE CONCAT('%', :#{#po.remark}, '%'))
            AND (:#{#po.createTime} IS NULL OR u.createTime = :#{#po.createTime} )
            AND (:#{#po.creatorId} IS NULL OR u.creatorId = :#{#po.creatorId} )
            AND (:#{#po.updateTime} IS NULL OR u.updateTime = :#{#po.updateTime} )
            AND (:#{#po.updaterId} IS NULL OR u.updaterId = :#{#po.updaterId} )
            AND (:#{#po.deleteTime} IS NULL OR u.deleteTime = :#{#po.deleteTime} )
            ORDER BY u.createTime DESC
            """ )
    Page<MenuPo> getMenuList(@Param("po") MenuPo po, Pageable pageable);

    /**
     * 获取菜单与按钮树
     *
     * @return 菜单与按钮树
     */
    @Query("""
            SELECT t FROM MenuPo t
            WHERE t.hide = 0
            ORDER BY t.seq ASC,t.createTime DESC
            """ )
    List<MenuPo> getUserMenuTree();

    /**
     * 获取菜单与按钮树
     *
     * @param po 获取菜单与按钮树参数
     * @return 菜单与按钮树
     */
    @Query("""
            SELECT t FROM MenuPo t
            WHERE (:#{#po.kind} IS NULL OR t.kind = :#{#po.kind})
            AND (
                :#{#po.name} IS NULL OR (t.name LIKE CONCAT('%',:#{#po.name},'%') OR
                t.remark LIKE CONCAT('%',:#{#po.name},'%') )
            )
            AND (:#{#po.permissionCode} IS NULL OR t.permissionCode LIKE CONCAT('%',:#{#po.permissionCode},'%') )
            ORDER BY t.seq ASC,t.createTime DESC
            """ )
    List<MenuPo> getMenuTree(@Param("po") GetMenuTreeDto po);

    /**
     * 按关键字查询菜单列表（用于权限分配视图）
     *
     * @param keyword 关键字，匹配名称或路径，为null时查全部
     * @return 菜单列表
     */
    @Query("""
            SELECT t FROM MenuPo t
            WHERE (:keyword IS NULL OR t.name LIKE CONCAT('%',:keyword,'%')
                   OR t.path LIKE CONCAT('%',:keyword,'%'))
            ORDER BY t.seq ASC, t.createTime DESC
            """ )
    List<MenuPo> getMenusByKeyword(@Param("keyword") String keyword);

    /**
     * 获取菜单子级数量
     *
     * @param id 菜单ID
     * @return 菜单子级数量
     */
    @Query("""
            SELECT COUNT(t) FROM MenuPo t
            WHERE t.parentId = :id
            """ )
    int getMenuChildrenCount(@Param("id") Long id);
}
