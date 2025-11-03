package com.ksptooi.biz.relay.model.routeserver.dto;

import com.ksptool.assembly.entity.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetRouteServerListDto extends PageQuery {

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

}

