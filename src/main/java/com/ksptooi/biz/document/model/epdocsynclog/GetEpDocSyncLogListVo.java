package com.ksptooi.biz.document.model.epdocsynclog;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GetEpDocSyncLogListVo {

    @Schema(description = "端点文档拉取记录ID")
    private Long id;

    @Schema(description = "端点文档ID")
    private Long epDocId;

    @Schema(description = "文档MD5值")
    private String hash;

    @Schema(description = "拉取地址")
    private String pullUrl;

    @Schema(description = "是否创建了新版本 0:否 1:是")
    private Integer newVersionCreated;

    @Schema(description = "新版本号，如果创建了新版本，则记录新版本号")
    private Long newVersionNum;

    @Schema(description = "状态 0:成功 1:失败")
    private Integer status;

    @Schema(description = "拉取时间")
    private LocalDateTime createTime;

}
