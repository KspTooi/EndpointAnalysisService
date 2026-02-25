package com.ksptool.bio.biz.drivespace.model.dto;

import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
public class AddDriveSpaceDto {


    @Schema(description="空间名称")
    private String name;

    @Schema(description="空间描述")
    private String remark;

    @Schema(description="配额限制(bytes)")
    private Long quotaLimit;

    @Schema(description="状态 0:正常 1:归档")
    private Integer status;

}

