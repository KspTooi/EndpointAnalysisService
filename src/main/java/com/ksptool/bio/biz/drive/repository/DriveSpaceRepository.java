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
                 ), 3),
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
             ORDER BY s.updateTime DESC
            """)
    Page<GetDriveSpaceListVo> getDriveSpaceList(@Param("po") DriveSpacePo po,
                                                @Param("userId") Long userId,
                                                @Param("deptId") Long deptId,
                                                @Param("isSuper") boolean isSuper,
                                                Pageable pageable);
}
