package com.ksptooi.biz.rdbg.model.userrequestenv.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GetUserRequestEnvListVo {

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
    @Schema(description = "当前用户是否已激活 0:否 1:是")
    private Integer active;

    public GetUserRequestEnvListVo(Long id, Long userId, String name, String remark, LocalDateTime createTime, LocalDateTime updateTime, Integer active) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.remark = remark;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.active = active;
    }

}

