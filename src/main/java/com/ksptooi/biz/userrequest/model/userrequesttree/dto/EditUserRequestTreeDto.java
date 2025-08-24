package com.ksptooi.biz.userrequest.model.userrequesttree.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditUserRequestTreeDto {

    @NotNull(message = "对象ID不能为空")
    @Schema(description = "对象ID")
    private Long id;

    @NotNull(message = "名称不能为空")
    @Schema(description = "名称")
    private String name;

}

