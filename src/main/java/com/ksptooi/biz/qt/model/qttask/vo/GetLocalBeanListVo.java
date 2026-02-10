package com.ksptooi.biz.qt.model.qttask.vo;

import lombok.Getter;
import lombok.Setter;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
public class GetLocalBeanListVo {

    @Schema(description = "Bean名称")
    private String name;

    @Schema(description = "全类名")
    private String fullClassName;
    
}
