package com.ksptooi.biz.requestdebug.model.userrequesttree.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MoveUserRequestTreeDto {

    @Schema(description = "关键字查询")
    private String keyword;

    @Schema(description = "对象ID")
    private Long nodeId;

    @Schema(description = "目标ID")
    private Long targetId;

    @Schema(description = "移动方式 0:顶部 1:底部 2:内部")
    private Integer kind;

}
