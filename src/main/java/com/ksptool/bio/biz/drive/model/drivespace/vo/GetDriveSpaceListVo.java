package com.ksptool.bio.biz.drive.model.drivespace.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetDriveSpaceListVo {

    @Schema(description = "空间ID")
    private Long id;

    @Schema(description = "空间名称")
    private String name;

    @Schema(description = "空间描述")
    private String remark;

    @Schema(description = "配额限制(bytes)")
    private Long quotaLimit;

    @Schema(description = "已用配额(bytes)")
    private Long quotaUsed;

    @Schema(description = "成员数量")
    private Integer memberCount;

    @Schema(description = "主管理员名称")
    private String maName;

    @Schema(defaultValue = "我在该空间的角色,0:主管理员 1:行政管理员 2:编辑者 3:查看者")
    private Integer myRole;

    @Schema(description = "状态 0:正常 1:归档")
    private Integer status;

}

