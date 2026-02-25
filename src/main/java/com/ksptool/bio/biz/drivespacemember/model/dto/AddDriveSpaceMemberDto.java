package com.ksptool.bio.biz.drivespacemember.model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Getter
@Setter
public class AddDriveSpaceMemberDto {


    @Schema(description="成员类型 0:用户 1:部门")
    private Integer memberKind;

    @Schema(description="成员ID")
    private Long memberId;

    @Schema(description="成员角色 0:主管理员 1:行政管理员 2:编辑者 3:查看者")
    private Integer role;

    @Schema(description="加入时间")
    private LocalDateTime createTime;

    @Schema(description="邀请人/操作人ID")
    private Long creatorId;

    @Schema(description="更新时间")
    private LocalDateTime updateTime;

    @Schema(description="更新人")
    private Long updaterId;

}

