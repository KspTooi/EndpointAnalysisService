package com.ksptool.bio.biz.drivespace.model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Getter
@Setter
public class AddDriveSpaceDto {


    @Schema(description="租户ID")
    private Long rootId;

    @Schema(description="部门ID")
    private Long deptId;

    @Schema(description="空间名称")
    private String name;

    @Schema(description="空间描述")
    private String remark;

    @Schema(description="配额限制(bytes)")
    private Long quotaLimit;

    @Schema(description="已用配额(bytes)")
    private Long quotaUsed;

    @Schema(description="状态 0:正常 1:归档")
    private Integer status;

    @Schema(description="创建时间")
    private LocalDateTime createTime;

    @Schema(description="创建人")
    private Long creatorId;

    @Schema(description="更新时间")
    private LocalDateTime updateTime;

    @Schema(description="更新人")
    private Long updaterId;

    @Schema(description="删除时间 为NULL未删除")
    private LocalDateTime deleteTime;

}

