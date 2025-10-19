package com.ksptooi.biz.core.model.routerule.dto;

import com.ksptooi.commons.utils.page.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class GetRouteRuleListDto extends PageQuery {


    @Schema(description = "路由规则名")
    private String name;

    @Schema(description = "匹配类型 0:全部 1:IP地址")
    private Integer matchType;

    @Schema(description = "匹配值")
    private String matchValue;

}

