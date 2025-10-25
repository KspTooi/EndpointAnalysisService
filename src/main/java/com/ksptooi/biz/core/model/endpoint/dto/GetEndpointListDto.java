package com.ksptooi.biz.core.model.endpoint.dto;


import com.ksptooi.commons.utils.page.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetEndpointListDto extends PageQuery {

    @Schema(description = "端点名称")
    private String name;

    @Schema(description = "端点路径")
    private String path;

    @Schema(description = "所需权限")
    private String permission;

}

