package com.ksptool.bio.biz.assembly.model.tymschemafield.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetTymSchemaFieldDetailsVo {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "类型映射方案ID")
    private Long typeSchemaId;

    @Schema(description = "匹配源类型")
    private String source;

    @Schema(description = "匹配目标类型")
    private String target;

    @Schema(description = "排序")
    private Integer seq;

}

