package com.ksptool.bio.biz.assembly.model.scm.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetAnchorPointsVo {

    @Schema(description = "锚点名称")
    private String name;

    @Schema(description = "锚点相对路径")
    private String relativePath;

}
