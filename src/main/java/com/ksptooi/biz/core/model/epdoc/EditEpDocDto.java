package com.ksptooi.biz.core.model.epdoc;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter@Setter
public class EditEpDocDto {

    @NotNull(message = "文档拉取配置ID不能为空")
    @Schema(description = "文档拉取配置ID")
    private Long id;

    @NotNull(message = "中继通道ID不能为空")
    @Schema(description = "中继通道ID")
    private Long relayServerId;

    @NotBlank(message = "文档拉取URL不能为空")
    @Schema(description = "文档拉取URL")
    private String docPullUrl;

}
