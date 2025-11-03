package com.ksptooi.biz.core.model.companymember.dto;

import com.ksptool.assembly.entity.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetCurrentUserActiveCompanyMemberListDto extends PageQuery {

    @Schema(description = "用户名称 模糊查询")
    private String username;

    @Schema(description = "职务 0:CEO 1:成员")
    private Integer role;

}

