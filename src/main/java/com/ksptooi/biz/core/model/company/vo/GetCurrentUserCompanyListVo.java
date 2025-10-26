package com.ksptooi.biz.core.model.company.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GetCurrentUserCompanyListVo {

    public GetCurrentUserCompanyListVo(Long id, String founderName, String ceoName, String name, String description, Long memberCount, LocalDateTime createTime, LocalDateTime updateTime) {
        this.id = id;
        this.founderName = founderName;
        this.ceoName = ceoName;
        this.name = name;
        this.description = description;
        this.memberCount = memberCount.intValue();
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    @Schema(description = "公司ID")
    private Long id;

    @Schema(description = "创始人名称")
    private String founderName;

    @Schema(description = "现任CEO名称")
    private String ceoName;

    @Schema(description = "公司名")
    private String name;

    @Schema(description = "公司描述")
    private String description;

    @Schema(description = "成员数量")
    private Integer memberCount;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}

