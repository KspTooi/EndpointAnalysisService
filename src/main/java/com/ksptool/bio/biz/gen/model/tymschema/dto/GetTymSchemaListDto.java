package com.ksptool.bio.biz.gen.model.tymschema.dto;

import com.ksptool.assembly.entity.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetTymSchemaListDto extends PageQuery {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "方案名称")
    private String name;

    @Schema(description = "方案编码")
    private String code;

    @Schema(description = "映射目标")
    private String mapTarget;

}

