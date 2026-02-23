package com.ksptool.bio.biz.rdbg.model.userrequestenv.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class GetUserRequestEnvDetailsVo {

    @Schema(description = "环境ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "环境名")
    private String name;

    @Schema(description = "描述")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}

