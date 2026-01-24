package com.ksptooi.biz.core.model.dept.dto;

import com.ksptool.assembly.entity.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetDeptListDto extends PageQuery {

    @Schema(description = "部门名")
    private String name;

    @Schema(description = "部门状态 0:正常 1:禁用")
    private Integer status;

}

