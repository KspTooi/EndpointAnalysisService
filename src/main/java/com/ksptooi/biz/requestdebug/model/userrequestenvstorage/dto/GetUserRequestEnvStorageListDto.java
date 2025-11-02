package com.ksptooi.biz.requestdebug.model.userrequestenvstorage.dto;

import com.ksptool.assembly.entity.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetUserRequestEnvStorageListDto extends PageQuery {

    @Schema(description = "用户环境ID")
    private Long requestEnvId;

    @Schema(description = "变量名")
    private String name;

    @Schema(description = "状态 0:启用 1:禁用")
    private Integer status;

}

