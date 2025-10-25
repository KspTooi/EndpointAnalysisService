package com.ksptooi.biz.requestdebug.model.userrequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RemoveUserRequestTreeDto {

    @NotNull(message = "对象ID不能为空")
    @Schema(description = "对象ID")
    private Long id;

}

