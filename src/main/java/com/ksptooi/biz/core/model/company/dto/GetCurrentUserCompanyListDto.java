package com.ksptooi.biz.core.model.company.dto;

import org.hibernate.validator.constraints.Length;

import com.ksptool.assembly.entity.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetCurrentUserCompanyListDto extends PageQuery {

    @Schema(description = "公司名")
    @Length(max = 50, message = "公司名称长度不能超过50个字符")
    private String name;

}

