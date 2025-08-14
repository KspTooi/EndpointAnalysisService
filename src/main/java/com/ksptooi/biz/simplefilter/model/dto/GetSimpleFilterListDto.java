package com.ksptooi.biz.simplefilter.model.dto;

import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import com.ksptooi.commons.utils.page.PageQuery;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class GetSimpleFilterListDto extends PageQuery{

    @Schema(description="过滤器名称")
    private String name;

    @Schema(description="过滤器方向 0:请求过滤器 1:响应过滤器")
    private Integer direction;

    @Schema(description="触发条件 0:标头包含 1:标头不包含 2:JSON载荷包含 3:JSON载荷不包含 10:总是触发")
    private Integer triggerCondition;

    @Schema(description="过滤器操作 0:获取头 1:获取JSON 2:注入头 3:注入JSON 4:更改请求业务状态")
    private Integer operation;

    @Schema(description="状态 0:启用 1:禁用")
    private Integer status;

}

