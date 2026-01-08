package com.ksptooi.biz.rdbg.model.filter.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GetSimpleFilterListVo {

    @Schema(description = "过滤器ID")
    private Long id;
    @Schema(description = "过滤器名称")
    private String name;
    @Schema(description = "过滤器方向 0:请求过滤器 1:响应过滤器")
    private Integer direction;
    @Schema(description = "状态 0:启用 1:禁用")
    private Integer status;
    @Schema(description = "触发器数量")
    private Integer triggerCount;
    @Schema(description = "操作数量")
    private Integer operationCount;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    public GetSimpleFilterListVo(Long id, String name, Integer direction, Integer status, Integer triggerCount, Integer operationCount, LocalDateTime createTime) {
        this.id = id;
        this.name = name;
        this.direction = direction;
        this.status = status;
        this.triggerCount = triggerCount;
        this.operationCount = operationCount;
        this.createTime = createTime;
    }

}

