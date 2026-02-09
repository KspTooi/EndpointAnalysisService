package com.ksptooi.biz.qt.model.qttaskgroup.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GetQtTaskGroupDetailsVo {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "分组名")
    private String name;

    @Schema(description = "分组备注")
    private String remark;



}

