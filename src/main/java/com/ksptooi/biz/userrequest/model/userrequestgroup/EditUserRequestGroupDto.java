package com.ksptooi.biz.userrequest.model.userrequestgroup;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EditUserRequestGroupDto {

    @NotNull(message = "请求组ID不能为空")
    @Schema(description = "请求组ID")
    private Long id;

    @NotBlank(message = "请求组名称不能为空")
    @Schema(description = "请求组名称")
    private String name;

    @Schema(description = "请求组描述")
    private String description;

    @NotNull(message = "应用的简单过滤器不能为空")
    @Schema(description = "应用的简单过滤器")
    private List<Long> simpleFilterIds;
    
}

