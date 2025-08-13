package com.ksptooi.biz.userrequest.model.userrequest;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class GetUserRequestDetailsVo{

    @Schema(description="用户请求ID")
    private Long id;

    @Schema(description="请求组ID")
    private Long groupId;

    @Schema(description="原始请求ID")
    private Long requestId;

    @Schema(description="用户ID")
    private Long userId;

    @Schema(description="用户自定义请求名称")
    private String name;

    @Schema(description="排序")
    private Integer seq;

}

