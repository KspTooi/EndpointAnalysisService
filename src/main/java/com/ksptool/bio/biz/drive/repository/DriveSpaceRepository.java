package com.ksptool.bio.biz.drive.repository;

import com.ksptool.bio.biz.drive.model.drivespace.DriveSpacePo;
import com.ksptool.bio.biz.drive.model.drivespace.vo.GetDriveSpaceListVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DriveSpaceRepository extends JpaRepository<DriveSpacePo, Long> {

    /**
     * 查询云盘空间列表(业务上保证只会有一个主管理员,且主管理员一定是用户)
     *
     * @param po       查询条件
     * @param userId   用户ID
     * @param deptId   部门ID
     * @param isSuper  是否超级权限
     * @param pageable 分页条件
     * @return 云盘空间列表
     */
    @Query("""
             SELECT new com.ksptool.bio.biz.drive.model.drivespace.vo.GetDriveSpaceListVo(
                 s.id,
                 s.name,
                 s.remark,
                 s.quotaLimit,
                 s.quotaUsed,
                 CAST((SELECT COUNT(m) FROM DriveSpaceMemberPo m WHERE m.driveSpaceId = s.id) AS integer),
                 (SELECT COALESCE(u.nickname, u.username)
                  FROM DriveSpaceMemberPo m
                  LEFT JOIN UserPo u ON u.id = m.memberId
                  WHERE m.driveSpaceId = s.id AND m.memberKind = 0 AND m.role = 0),
                 COALESCE((
                     SELECT MIN(m.role)
                     FROM DriveSpaceMemberPo m
                     WHERE m.driveSpaceId = s.id
                       AND (
                            (m.memberKind = 0 AND m.memberId = :userId)
                         OR (m.memberKind = 1 AND m.memberId = :deptId)
                       )
                 ), -1),
                 s.status
             )
             FROM DriveSpacePo s
             WHERE (:#{#po.name} IS NULL OR s.name LIKE CONCAT('%', :#{#po.name}, '%'))
               AND (:#{#po.remark} IS NULL OR s.remark LIKE CONCAT('%', :#{#po.remark}, '%'))
               AND (:#{#po.status} IS NULL OR s.status = :#{#po.status})
               AND (
                     :isSuper = true
                     OR EXISTS (
                         SELECT 1
                         FROM DriveSpaceMemberPo m
                         WHERE m.driveSpaceId = s.id
                           AND (
                                (m.memberKind = 0 AND m.memberId = :userId)
                             OR (m.memberKind = 1 AND m.memberId = :deptId)
                           )
                     )
                   )
             ORDER BY s.createTime DESC
            """)
    Page<GetDriveSpaceListVo> getDriveSpaceList(@Param("po") DriveSpacePo po,
                                                @Param("userId") Long userId,
                                                @Param("deptId") Long deptId,
                                                @Param("isSuper") boolean isSuper,
                                                Pageable pageable);

    /**
     * 查询用户在云盘空间中的最佳角色(如果用户和用户所在的部门同时都在云盘空间中,返回那个权限最高的角色)
     *
     * @param driveSpaceId 云盘空间ID
     * @param userId       用户ID
     * @param deptId       部门ID
     * @return 最佳角色(0:主管理员 1:行政管理员 2:编辑者 3:查看者) 不存在会返回null
     */
    @Query("""
            SELECT MIN(m.role)
            FROM DriveSpaceMemberPo m
            WHERE m.driveSpaceId = :driveSpaceId
              AND (
                (m.memberKind = 0 AND m.memberId = :userId)
                OR (:deptId IS NOT NULL AND m.memberKind = 1 AND m.memberId = :deptId)
              )
            """)
    Integer getBestRole(Long driveSpaceId, Long userId, Long deptId);
}
