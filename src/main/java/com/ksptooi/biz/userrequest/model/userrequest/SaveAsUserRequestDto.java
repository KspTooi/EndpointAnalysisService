package com.ksptooi.biz.userrequest.model.userrequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class SaveAsUserRequestDto {

    @NotNull(message = "原始请求ID不能为空")
    @Schema(description="原始请求ID")
    private Long requestId;

    @Schema(description="重命名")
    private String name;

}

