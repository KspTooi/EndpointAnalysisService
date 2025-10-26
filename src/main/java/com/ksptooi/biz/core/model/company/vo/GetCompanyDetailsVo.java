package com.ksptooi.biz.core.model.company.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class GetCompanyDetailsVo {

    @Schema(description = "公司ID")
    private Long id;

    @Schema(description = "公司名")
    private String name;

    @Schema(description = "公司描述")
    private String description;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}

