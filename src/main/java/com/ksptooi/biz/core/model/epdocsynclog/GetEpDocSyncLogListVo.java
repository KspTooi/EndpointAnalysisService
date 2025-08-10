package com.ksptooi.biz.core.model.epdocsynclog;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class GetEpDocSyncLogListVo {

    @Schema(description = "端点文档拉取记录ID")
    private Long id;    

    @Schema(description = "端点文档ID")
    private Long epDocId;

    @Schema(description = "文档MD5值")
    private String hash;

    @Schema(description = "拉取地址")
    private String pullUrl;

    @Schema(description = "状态 0:成功 1:失败")
    private Integer status;

    @Schema(description = "拉取时间")
    private LocalDateTime createTime;

}
