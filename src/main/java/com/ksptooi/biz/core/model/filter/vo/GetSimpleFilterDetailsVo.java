package com.ksptooi.biz.core.model.filter.vo;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetSimpleFilterDetailsVo {

    @Schema(description = "过滤器ID")
    private Long id;

    @Schema(description = "过滤器名称")
    private String name;

    @Schema(description = "过滤器方向 0:请求过滤器 1:响应过滤器")
    private Integer direction;

    @Schema(description = "状态 0:启用 1:禁用")
    private Integer status;

    @Schema(description = "触发器列表")
    private List<GetSimpleFilterTriggerDetailsVo> triggers;

    @Schema(description = "操作列表")
    private List<GetSimpleFilterOperationDetailsVo> operations;

}

