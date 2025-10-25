package com.ksptooi.biz.requestdebug.repoistory;

import com.ksptooi.biz.requestdebug.model.userrequesttree.UserRequestTreePo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRequestTreeRepository extends JpaRepository<UserRequestTreePo, Long> {

    /**
     * 获取父级下的最小排序 -1
     *
     * @param parentId 父级ID
     * @return 最小排序 如果没有任何内容，则返回0
     */
    @Query("""
            SELECT COALESCE(MIN(t.seq) - 1, 1) FROM UserRequestTreePo t
            WHERE (:parentId IS NULL OR t.parent.id = :parentId) AND
                  (:parentId IS NOT NULL OR t.parent IS NULL)
            """)
    Integer getMinSeqInParent(@Param("parentId") Long parentId);

    /**
     * 获取父级下的最大排序+1
     *
     * @param parentId 父级ID
     * @return 最大排序 如果没有任何内容，则返回1
     */
    @Query("""
            SELECT COALESCE(MAX(t.seq) + 1,1) FROM UserRequestTreePo t
            WHERE (:parentId IS NULL OR t.parent.id = :parentId) AND
                  (:parentId IS NOT NULL OR t.parent IS NULL)
            """)
    Integer getMaxSeqInParent(@Param("parentId") Long parentId);


    /**
     * 获取用户所拥有的全部树节点
     *
     * @param userId 用户ID
     * @return 树节点
     */
    @Query("""
            SELECT t FROM UserRequestTreePo t
            LEFT JOIN FETCH t.parent
            LEFT JOIN FETCH t.request
            LEFT JOIN FETCH t.group
            WHERE t.user.id = :userId
            ORDER BY t.seq ASC
            """)
    List<UserRequestTreePo> getRequestTreeListByUserId(@Param("userId") Long userId);


    /**
     * 根据ID和用户ID获取节点
     *
     * @param id     节点ID
     * @param userId 用户ID
     * @return 节点
     */
    @Query("""
            SELECT t FROM UserRequestTreePo t
            WHERE t.id = :id AND t.user.id = :userId
            """)
    UserRequestTreePo getNodeByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);


    /**
     * 获取用户顶级节点
     *
     * @param userId 用户ID
     * @return 顶级节点
     */
    @Query("""
            SELECT t FROM UserRequestTreePo t
            WHERE t.parent IS NULL AND t.user.id = :userId
            ORDER BY t.seq ASC
            """)
    List<UserRequestTreePo> getRootNodeListByUserId(@Param("userId") Long userId);

}
