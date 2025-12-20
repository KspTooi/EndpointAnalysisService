package com.ksptooi.biz.core.model.attach.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplyChunkVo {

    @Schema(description = "附件ID")
    private Long attachId;

    @Schema(description = "分块总数")
    private Integer chunkTotal;

    @Schema(description = "分块已应用数量")
    private Integer chunkApplied;

}
