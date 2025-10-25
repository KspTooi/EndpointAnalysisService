package com.ksptooi.biz.core.model.endpoint.dto;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddEndpointDto {

    @Schema(description = "父级ID")
    private Long parentId;

    @Schema(description = "端点名称")
    @Length(min =2, max = 128, message = "端点名称长度必须在2-128个字符之间")
    @NotBlank(message = "端点名称不能为空")
    private String name;

    @Length(max = 200, message = "端点描述长度不能超过200个字符")
    @Schema(description = "端点描述")
    private String description;

    @Length(max = 256, message = "端点路径长度不能超过256个字符")
    @NotBlank(message = "端点路径不能为空")
    @Schema(description = "端点路径")
    private String path;

    @Length(max = 320, message = "所需权限长度不能超过320个字符")
    @Schema(description = "所需权限")
    private String permission;

    @NotNull(message = "排序不能为空")
    @Range(min = 0, max = 655350, message = "排序只能在0-655350之间")
    @Schema(description = "排序")
    private Integer seq;


}

