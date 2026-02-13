package com.ksptooi.biz.core.repository;

import com.ksptooi.biz.core.model.noticercd.NoticeRcdPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRcdRepository extends JpaRepository<NoticeRcdPo, Long> {


    /**
     * 获取当前用户未读通知数量
     *
     * @param userId
     * @return
     */
    @Query("""
            SELECT COUNT(u) FROM NoticeRcdPo u
            WHERE
            u.userId = :userId
            AND u.readTime IS NULL
            """)
    Integer getUserNoticeCount(@Param("userId") Long userId);

    /**
     * 根据ID列表和用户ID查询通知记录
     *
     * @param ids    消息接收记录ID列表
     * @param userId 用户ID
     * @return 消息接收记录列表
     */
    @Query("""
            SELECT u FROM NoticeRcdPo u
            WHERE
            u.id IN :ids
            AND u.userId = :userId
            """)
    List<NoticeRcdPo> getNotifyRcdByIdsAndUserId(@Param("ids") List<Long> ids, @Param("userId") Long userId);

    /**
     * 分页查询消息接收记录列表
     * 优先返回未读消息(readTime为null)，然后返回已读消息
     *
     * @param pageable 分页信息
     * @return 消息接收记录列表
     */
    @Query("""
            SELECT u FROM NoticeRcdPo u
            WHERE
            u.userId = :userId
            ORDER BY u.createTime DESC,u.id
            """)
    Page<NoticeRcdPo> getNoticeRcdsByUserId(@Param("userId") Long userId, Pageable pageable);

}
