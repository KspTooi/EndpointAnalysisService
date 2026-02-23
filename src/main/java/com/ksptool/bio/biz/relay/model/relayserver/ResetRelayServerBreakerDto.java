package com.ksptool.bio.biz.relay.model.relayserver;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetRelayServerBreakerDto {

    @NotNull(message = "中继服务器ID不能为空")
    private Long id;

    @Schema(description = "主机 不填写则重置所有主机的熔断状态")
    private String host;

    @Schema(description = "端口")
    private Integer port;

    @Schema(description = "0:复位熔断 1:置为熔断")
    private Integer kind;

}
