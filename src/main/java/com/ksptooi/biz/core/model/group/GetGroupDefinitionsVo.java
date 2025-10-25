package com.ksptooi.biz.core.model.group;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetGroupDefinitionsVo {

    @Schema(description = "组ID")
    private Long id;

    @Schema(description = "组名称")
    private String name;

}
