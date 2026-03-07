package com.ksptool.bio.biz.gen.model.outblueprint.dto;

import com.ksptool.assembly.entity.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetGenOutBlueprintListDto extends PageQuery {

    @Schema(description = "蓝图名称")
    private String name;

    @Schema(description = "项目名称")
    private String projectName;

    @Schema(description = "蓝图编码")
    private String code;

}

