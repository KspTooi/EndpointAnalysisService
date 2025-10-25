package com.ksptooi.biz.core.model.endpoint.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetEndpointDetailsVo {


    @Schema(description = "主键id")
    private Long id;

    @Schema(description = "父级ID")
    private Long parentId;

    @Schema(description = "端点名称")
    private String name;

    @Schema(description = "端点描述")
    private String description;

    @Schema(description = "端点路径")
    private String path;

    @Schema(description = "所需权限")
    private String permission;

    @Schema(description = "排序")
    private Integer seq;

}

