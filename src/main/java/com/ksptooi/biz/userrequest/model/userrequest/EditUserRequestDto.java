package com.ksptooi.biz.userrequest.model.userrequest;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class EditUserRequestDto {

    @Schema(description="用户请求ID")
    private Long id;

    @Schema(description="请求组ID")
    private Long groupId;

    @Schema(description="用户自定义请求名称")
    private String name;

    @Schema(description="排序")
    private Integer seq;

}

