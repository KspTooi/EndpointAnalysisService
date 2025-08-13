package com.ksptooi.biz.userrequest.model.userrequest;

import org.hibernate.validator.constraints.Range;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class EditUserRequestTreeDto {

    @NotNull(message = "对象ID不能为空")
    @Schema(description="对象ID")
    private Long id;

    @Schema(description="父级ID")
    private Long parentId;

    @NotNull(message = "对象类型不能为空")
    @Schema(description="对象类型 0:请求组 1:用户请求")
    @Range(min = 0, max = 1, message = "对象类型只能为0或1")
    private Integer type;

    @NotNull(message = "名称不能为空")
    @Schema(description="名称")
    private String name;

    @Schema(description="排序")
    private Integer seq;

}

