package com.ksptooi.biz.document.model.epstdword.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EditEpStdWordDto {

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

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "创建人ID")
    private Long creatorId;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "更新人ID")
    private Long updaterId;

}

