package com.ksptool.bio.biz.drive.model.driveentry.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetEntrySignVo {

    @Schema(description = "签名参数串")
    private String params;

    @Schema(description = "是否批量签名 0:否 1:是")
    private Integer isBatch;

}
