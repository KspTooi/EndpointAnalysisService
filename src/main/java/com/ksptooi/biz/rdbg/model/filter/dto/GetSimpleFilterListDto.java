package com.ksptooi.biz.rdbg.model.filter.dto;

import com.ksptool.assembly.entity.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetSimpleFilterListDto extends PageQuery {

    @Schema(description = "过滤器名称")
    private String name;

    @Schema(description = "过滤器方向 0:请求过滤器 1:响应过滤器")
    private Integer direction;

    @Schema(description = "状态 0:启用 1:禁用")
    private Integer status;

}

