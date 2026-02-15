package com.ksptooi.biz.authgroupdept.model.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Getter
@Setter
public class GetAuthGroupDeptListVo{

    @Schema(description="组ID")
    private Long groupId;

    @Schema(description="部ID")
    private Long deptId;

    @Schema(description="创建时间")
    private LocalDateTime createTime;

}

