package com.ksptooi.biz.requestdebug.model.userrequestenvstorage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddUserRequestEnvStorageDto {

    @Schema(description = "用户环境ID")
    private Long requestEnvId;

    @Schema(description = "变量名")
    private String name;

    @Schema(description = "初始值")
    private String initValue;

    @Schema(description = "当前值")
    private String value;

    @Schema(description = "状态 0:启用 1:禁用")
    private Integer status;

}

