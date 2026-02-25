package com.ksptool.bio.biz.drive.model.drivespace.dto;

import org.hibernate.validator.constraints.Range;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditDriveSpaceMembersDto {

    @NotNull(message = "云盘空间ID不能为空")
    @Schema(description = "云盘空间ID")
    private Long driveSpaceId;

    @NotNull(message = "成员ID不能为空")
    @Schema(description = "成员ID")
    private Long memberId;

    @NotNull(message = "成员类型不能为空")
    @Range(min = 0, max = 1, message = "成员类型必须在0-1之间")
    @Schema(description = "成员类型 0:用户 1:部门")
    private Integer memberKind;

    @NotNull(message = "成员角色不能为空")
    @Range(min = 1, max = 3, message = "成员角色必须在1-3之间")
    @Schema(description = "成员角色 1:行政管理员 2:编辑者 3:查看者")
    private Integer role;

    @NotNull(message = "操作类型不能为空")
    @Schema(description = "操作类型 0:添加/修改成员 1:删除成员")
    private Integer action;

}
