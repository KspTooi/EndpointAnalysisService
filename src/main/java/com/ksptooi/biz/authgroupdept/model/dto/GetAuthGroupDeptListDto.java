package com.ksptooi.biz.authgroupdept.model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import com.ksptool.assembly.entity.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class GetAuthGroupDeptListDto extends PageQuery {


    @Schema(description="组ID")
    private Long groupId;

    @Schema(description="部ID")
    private Long deptId;

    @Schema(description="创建时间")
    private LocalDateTime createTime;
}

