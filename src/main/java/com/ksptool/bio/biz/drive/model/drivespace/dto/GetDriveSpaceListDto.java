package com.ksptool.bio.biz.drive.model.drivespace.dto;

import com.ksptool.assembly.entity.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetDriveSpaceListDto extends PageQuery {

    @Schema(description = "空间名称")
    private String name;

    @Schema(description = "空间描述")
    private String remark;

    @Schema(description = "状态 0:正常 1:归档")
    private Integer status;

}

