package com.ksptooi.biz.core.model.companymember.vo;

import java.util.List;

import com.ksptool.assembly.entity.web.PageResult;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetCurrentUserActiveCompanyMemberListVo{

    @Schema(description = "公司名称")
    private String companyName;

    @Schema(description = "公司ID")
    private Long companyId;

    @Schema(description = "成员列表")
    private PageResult<GetCompanyMemberListVo> members;

}

