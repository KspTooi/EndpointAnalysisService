package com.ksptooi.biz.userrequest.model.userrequest;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class GetUserRequestTreeVo {

    @Schema(description="对象ID")
    private Long id;

    @Schema(description="对象类型 0:请求组 1:用户请求")
    private String type;

    @Schema(description="名称")
    private String name;

    @Schema(description="请求方法")
    private String method;

    @Schema(description="子节点")
    private List<GetUserRequestTreeVo> children;
}

