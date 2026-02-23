package com.ksptool.bio.biz.core.model.endpoint.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetEndpointTreeDto {

    @Schema(description = "端点名称")
    private String name;

    @Schema(description = "端点路径")
    private String path;

}
