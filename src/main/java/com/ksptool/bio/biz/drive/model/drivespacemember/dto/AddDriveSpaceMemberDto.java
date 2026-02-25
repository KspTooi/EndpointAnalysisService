package com.ksptool.bio.biz.drive.model.drivespacemember.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
public class AddDriveSpaceMemberDto {

    @NotNull(message = "成员类型不能为空")
    @Range(min = 0, max = 1, message = "成员类型只能在0-1之间")
    @Schema(description = "成员类型 0:用户 1:部门")
    private Integer memberKind;

    @NotNull(message = "成员ID不能为空")
    @Schema(description = "成员ID")
    private Long memberId;

    @NotNull(message = "成员角色不能为空")
    @Range(min = 0, max = 3, message = "成员角色只能在0-3之间")
    @Schema(description = "成员角色 0:主管理员 1:行政管理员 2:编辑者 3:查看者")
    private Integer role;

}

