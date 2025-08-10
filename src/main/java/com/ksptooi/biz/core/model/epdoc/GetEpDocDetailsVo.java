package com.ksptooi.biz.core.model.epdoc;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter@Setter
public class GetEpDocDetailsVo {

    @Schema(description = "文档拉取配置ID")
    private Long id;

    @Schema(description = "中继通道ID")
    private Long relayServerId;

    @Schema(description = "中继通道名称")
    private String relayServerName;

    @Schema(description = "文档拉取URL")
    private String docPullUrl;

    @Schema(description = "拉取时间")
    private LocalDateTime pullTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
