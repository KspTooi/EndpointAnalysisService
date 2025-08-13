package com.ksptooi.biz.userrequest.model.userrequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Getter@Setter
public class RemoveUserRequestTreeDto {

    @NotNull(message = "对象ID不能为空")
    @Schema(description="对象ID")
    private Long id;

    @NotNull(message = "对象类型不能为空")
    @Schema(description="对象类型 0:请求组 1:用户请求")
    @Range(min = 0, max = 1, message = "对象类型只能为0或1")
    private Integer type;

}

