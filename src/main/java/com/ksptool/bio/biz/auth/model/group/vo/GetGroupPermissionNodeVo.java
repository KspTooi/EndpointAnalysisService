package com.ksptool.bio.biz.auth.model.group.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetGroupPermissionNodeVo {

    @Schema(description = "权限ID")
    private Long id;

    @Schema(description = "权限名称")
    private String name;

    @Schema(description = "权限标识")
    private String code;

    @Schema(description = "权限描述")
    private String remark;

    @Schema(description = "排序号")
    private Integer seq;

    @Schema(description = "是否已授权 0:否 1:是")
    private Integer hasPermission;

}
