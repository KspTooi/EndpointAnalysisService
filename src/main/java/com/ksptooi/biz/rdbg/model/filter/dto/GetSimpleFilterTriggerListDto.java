package com.ksptooi.biz.rdbg.model.filter.dto;

import com.ksptool.assembly.entity.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetSimpleFilterTriggerListDto extends PageQuery {

    @Schema(description = "触发器ID")
    private Long id;

    @Schema(description = "条件 0:包含 1:不包含 2:等于 3:不等于")
    private Integer kind;

    @Schema(description = "触发器名称")
    private String name;

    @Schema(description = "排序")
    private Integer seq;

    @Schema(description = "目标 0:标头 1:JSON载荷 2:URL 3:HTTP方法")
    private Integer target;

    @Schema(description = "目标键")
    private String tk;

    @Schema(description = "比较值")
    private String tv;

    @Schema(description = "过滤器ID")
    private Long filterId;

}

