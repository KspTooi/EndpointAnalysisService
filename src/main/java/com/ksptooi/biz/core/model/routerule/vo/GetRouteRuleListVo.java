package com.ksptooi.biz.core.model.routerule.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class GetRouteRuleListVo {

    @Schema(description = "路由规则ID")
    private Long id;

    private String name;

    private Integer matchType;

    private String matchKey;

    private Integer matchOperator;

    @Schema(description = "匹配来源IP 为null时匹配所有IP")
    private String matchValue;

    @Schema(description = "所属服务器ID")
    private Long routeServerId;

    private Integer seq;

    private String remark;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "更新时间")
    private Date updateTime;

}

