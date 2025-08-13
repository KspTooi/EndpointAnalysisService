package com.ksptooi.biz.userrequest.model.userrequestgroup;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class EditUserRequestGroupDto {

    @NotNull(message = "请求组ID不能为空")
    @Schema(description="请求组ID")
    private Long id;

    @Schema(description="父级ID 为空表示根节点")
    private Long parentId;

    @NotBlank(message = "请求组名称不能为空")
    @Schema(description="请求组名称")
    private String name;

    @Schema(description="请求组描述")
    private String description;

    @NotNull(message = "排序不能为空")
    @Schema(description="排序")
    private Integer seq;

}

