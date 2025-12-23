package com.ksptooi.biz.drive.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class DriveSignVo {

    @Schema(description="团队ID")
    private Long cid;

    @Schema(description="条目ID")
    private Long eid;

    @Schema(description="文件附件ID")
    private Long aid;

    @Schema(description="文件附件路径")
    private String aPath;

    @Schema(description="签名时间")
    private Long t;

    @Schema(description="签名")
    private String sign;

}
