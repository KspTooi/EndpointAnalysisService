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
public class EditEndpointDto {

    @Schema(description = "主键id")
    @NotNull(message = "主键id不能为空")
    private Long id;

    @Schema(description = "父级ID")
    private Long parentId;

    @Schema(description = "端点名称")
    @Length(min =2, max = 128, message = "端点名称长度必须在2-128个字符之间")
    @NotBlank(message = "端点名称不能为空")
    private String name;

    @Schema(description = "端点描述")
    @Length(max = 200, message = "端点描述长度不能超过200个字符")
    private String description;

    @Schema(description = "端点路径")
    @Length(max = 256, message = "端点路径长度不能超过256个字符")
    @NotBlank(message = "端点路径不能为空")
    private String path;

    @Schema(description = "所需权限")
    @Length(max = 320, message = "所需权限长度不能超过320个字符")
    @NotBlank(message = "所需权限不能为空")
    private String permission;

    @Schema(description = "排序")
    @NotNull(message = "排序不能为空")
    @Range(min = 0, max = 655350, message = "排序只能在0-655350之间")
    private Integer seq;


}

