package com.ksptooi.biz.drive.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class EntrySignVo {

    @Schema(description="团队ID")
    private Long cid;

    @Schema(description="条目ID")
    private Long eid;

    @Schema(description="条目IDS(多文件参数)")
    private String eids;

    @Schema(description="文件附件ID")
    private Long aid;

    @Schema(description="条目类型 0:文件 1:文件夹")
    private Integer ek;

    @Schema(description="文件附件路径")
    private String aPath;

    @Schema(description="条目名称")
    private String eName;

    @Schema(description="签名时间")
    private Long t;

    @Schema(description="是否批量签名 0:否 1:是")
    private Integer isBatch;

    @Schema(description="最终参数串")
    private String params;

}
