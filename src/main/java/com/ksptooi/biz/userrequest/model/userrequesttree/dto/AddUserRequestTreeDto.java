package com.ksptooi.biz.userrequest.model.userrequesttree.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Getter@Setter
public class AddUserRequestTreeDto {

    @Schema(name = "父级ID")
    private Long parentId;

    @Schema(name = "名称")
    @NotBlank(message = "名称不能为空")
    private String name;

    @Schema(name = "类型 0:请求组 1:请求")
    @NotNull(message = "类型不能为空")
    @Range(min = 0, max = 1, message = "类型只能为0或1")
    private Integer kind;

}
