package com.ksptool.bio.biz.drive.repository;

import com.ksptool.bio.biz.drive.model.drivespacemember.DriveSpaceMemberPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriveSpaceMemberRepository extends JpaRepository<DriveSpaceMemberPo, Long> {

    @Query("""
            SELECT u FROM DriveSpaceMemberPo u
            WHERE
            (:#{#po.id} IS NULL OR u.id  = :#{#po.id} )
            AND (:#{#po.memberKind} IS NULL OR u.memberKind  = :#{#po.memberKind} )
            AND (:#{#po.memberId} IS NULL OR u.memberId  = :#{#po.memberId} )
            AND (:#{#po.role} IS NULL OR u.role  = :#{#po.role} )
            AND (:#{#po.createTime} IS NULL OR u.createTime  = :#{#po.createTime} )
            AND (:#{#po.creatorId} IS NULL OR u.creatorId  = :#{#po.creatorId} )
            AND (:#{#po.updateTime} IS NULL OR u.updateTime  = :#{#po.updateTime} )
            AND (:#{#po.updaterId} IS NULL OR u.updaterId  = :#{#po.updaterId} )
            ORDER BY u.updateTime DESC
            """)
    Page<DriveSpaceMemberPo> getDriveSpaceMemberList(@Param("po") DriveSpaceMemberPo po, Pageable pageable);


    /**
     * 查询云盘空间下的成员
     *
     * @param driveSpaceId 云盘空间ID
     * @return 云盘空间成员列表
     */
    @Query("""
            SELECT u FROM DriveSpaceMemberPo u
            WHERE
            u.driveSpaceId = :#{#driveSpaceId}
            """)
    List<DriveSpaceMemberPo> getByDriveSpaceId(@Param("driveSpaceId") Long driveSpaceId);

    /**
     * 查询云盘空间下的某个成员
     *
     * @param driveSpaceId 云盘空间ID
     * @param memberId     成员ID
     * @return 云盘空间成员
     */
    @Query("""
            SELECT u FROM DriveSpaceMemberPo u
            WHERE
            u.driveSpaceId = :#{#driveSpaceId}
            AND u.memberId = :#{#memberId}
            """)
    DriveSpaceMemberPo getByDriveSpaceIdAndMemberId(@Param("driveSpaceId") Long driveSpaceId, @Param("memberId") Long memberId);
}
