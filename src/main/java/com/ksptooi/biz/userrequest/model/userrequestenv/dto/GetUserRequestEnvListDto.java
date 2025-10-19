package com.ksptooi.biz.userrequest.model.userrequestenv.dto;

import com.ksptooi.commons.utils.page.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetUserRequestEnvListDto extends PageQuery {

    @Schema(description = "环境名")
    private String name;

    @Schema(description = "描述")
    private String remark;

    @Schema(description = "激活 0:启用 1:禁用")
    private Integer active;

}

