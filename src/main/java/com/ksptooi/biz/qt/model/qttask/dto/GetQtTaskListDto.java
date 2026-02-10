package com.ksptooi.biz.qt.model.qttask.dto;

import com.ksptool.assembly.entity.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetQtTaskListDto extends PageQuery {

    @Schema(description = "任务分组ID")
    private Long groupId;

    @Schema(description = "任务名")
    private String name;

    @Schema(description = "0:正常 1:暂停")
    private Integer status;

}

