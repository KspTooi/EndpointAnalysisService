package com.ksptool.bio.biz.assembly.model.datsource.dto;

import com.ksptool.assembly.entity.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class GetDataSourceListDto extends PageQuery {

    @Schema(description = "数据源名称")
    private String name;

    @Schema(description = "数据源编码")
    private String code;

}

