package com.ksptool.bio.biz.drive.model.drivespacemember.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetDriveSpaceMemberDetailsVo {

    @Schema(description = "空间ID")
    private Long id;

    @Schema(description = "成员名称")
    private String memberName;

    @Schema(description = "成员类型 0:用户 1:部门")
    private Integer memberKind;

    @Schema(description = "成员ID")
    private Long memberId;

    @Schema(description = "成员角色 0:主管理员 1:行政管理员 2:编辑者 3:查看者")
    private Integer role;

}

