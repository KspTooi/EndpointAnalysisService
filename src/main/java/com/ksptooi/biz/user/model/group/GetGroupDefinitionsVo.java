package com.ksptooi.biz.user.model.group;

import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
public class GetGroupDefinitionsVo {

    @Schema(description = "组ID")
    private Long id;

    @Schema(description = "组名称")
    private String name;

}
