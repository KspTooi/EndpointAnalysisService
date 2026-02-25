package com.ksptool.bio.biz.drive.common.aop;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;


@MappedSuperclass
@FilterDef(name = "driveSpaceFilter", parameters = {
        @ParamDef(name = "driveSpaceId", type = Long.class),
})
@Filter(
        name = "driveSpaceFilter",
        condition = """
                drive_space_id = :driveSpaceId
                """
)
public abstract class DriveSpacePo {

    @Transient
    @Column(name = "drive_space_id", nullable = false, updatable = false, comment = "云盘空间ID")
    private Long driveSpaceId;

}