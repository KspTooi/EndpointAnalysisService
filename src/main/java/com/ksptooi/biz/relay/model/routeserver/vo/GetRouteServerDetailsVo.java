package com.ksptooi.biz.relay.model.routeserver.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GetRouteServerDetailsVo {

    @Schema(description = "路由服务器ID")
    private Long id;

    @Schema(description = "服务器名称")
    private String name;

    @Schema(description = "服务器主机")
    private String host;

    @Schema(description = "服务器端口")
    private Integer port;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "服务器状态 0:禁用 1:启用")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}

