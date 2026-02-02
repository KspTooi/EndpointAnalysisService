package com.ksptooi.biz.document.model.epstdword.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GetEpStdWordDetailsVo {

    @Schema(description = "标准词ID")
    private Long id;

    @Schema(description = "简称")
    private String sourceName;

    @Schema(description = "全称")
    private String sourceNameFull;

    @Schema(description = "英文简称")
    private String targetName;

    @Schema(description = "英文全称")
    private String targetNameFull;

    @Schema(description = "备注")
    private String remark;


}

