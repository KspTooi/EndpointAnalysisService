package com.ksptooi.biz.rdbg.model.userrequestgroup;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddUserRequestGroupDto {

    @Schema(description = "父级ID 为空表示根节点")
    private Long parentId;

    @NotBlank(message = "请求组名称不能为空")
    @Schema(description = "请求组名称")
    private String name;

}

