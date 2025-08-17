package com.ksptooi.biz.userrequest.model.userrequestgroup;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetUserRequestGroupDetailsVo {

    private Long id;

    @Schema(description = "请求组名称")
    private String name;

    @Schema(description = "请求组描述")
    private String description;

    @Schema(description = "绑定的过滤器")
    

}

