package com.ksptool.bio.biz.auth.model.group.dto;

import com.ksptool.assembly.entity.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetGroupPermissionNodeDto extends PageQuery {

    @Schema(description = "组ID")
    @NotNull(message = "组ID不能为空")
    private Long groupId;

    @Schema(description = "模糊匹配 权限节点名称")
    private String keyword;

    @Schema(description = "是否已授权 0:否 1:是")
    private Integer hasPermission;

}
