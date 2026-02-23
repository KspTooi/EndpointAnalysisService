package com.ksptool.bio.biz.core.model.attach.vo;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PreCheckAttachVo {

    @Schema(description = "文件预检ID")
    private Long preCheckId;

    @Schema(description = "服务端文件名")
    private String name;

    @Schema(description = "业务代码")
    private String kind;

    @Schema(description = "服务端文件路径")
    private String path;

    @Schema(description = "状态 0:预检文件 1:区块不完整 2:校验中 3:正常")
    private Integer status;

    @Schema(description = "缺失的分块ID")
    private List<Long> missingChunkIds;

}
