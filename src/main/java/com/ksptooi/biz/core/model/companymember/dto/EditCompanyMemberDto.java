package com.ksptooi.biz.core.model.companymember.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class EditCompanyMemberDto {

    @Schema(description = "记录ID")
    private Long id;

    @Schema(description = "公司ID")
    private Long companyId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "职务 0:CEO 1:成员")
    private Integer role;

    @Schema(description = "加入时间")
    private Date joinedTime;

    @Schema(description = "删除时间 为NULL时代表未删除")
    private Date deletedTime;

}

