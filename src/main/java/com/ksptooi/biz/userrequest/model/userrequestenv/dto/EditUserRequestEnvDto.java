package com.ksptooi.biz.userrequest.model.userrequestenv.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EditUserRequestEnvDto {

    @Schema(description = "环境ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "环境名")
    private String name;

    @Schema(description = "描述")
    private String remark;

    @Schema(description = "激活 0:启用 1:禁用")
    private Integer active;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}

