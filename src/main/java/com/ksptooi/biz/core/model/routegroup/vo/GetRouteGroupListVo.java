package com.ksptooi.biz.core.model.routegroup.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class GetRouteGroupListVo {

    @Schema(description = "路由组ID")
    private Long id;

    @Schema(description = "组名称")
    private String name;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "负载均衡策略 0:轮询 1:随机 2:权重")
    private Integer loadBalance;

    @Schema(description = "自动降级 0:开启 1:关闭")
    private Integer autoDegradation;

    @Schema(description = "更新时间")
    private Date updateTime;

}

