package com.ksptooi.biz.drive.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class MultiEntrySignVo {

    @Schema(description="团队ID")
    private Long cid;

    @Schema(description="条目ID")
    private String eids;

    @Schema(description="签名时间")
    private Long t;

    @Schema(description="最终参数串")
    private String params;

}
