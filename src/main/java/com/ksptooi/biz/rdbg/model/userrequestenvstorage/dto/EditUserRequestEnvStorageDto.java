package com.ksptooi.biz.rdbg.model.userrequestenvstorage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditUserRequestEnvStorageDto {

    @Schema(description = "共享存储ID")
    private Long id;

    @Schema(description = "变量名")
    private String name;

    @Schema(description = "初始值")
    private String initValue;

    @Schema(description = "当前值")
    private String value;

    @Schema(description = "状态 0:启用 1:禁用")
    private Integer status;

}

