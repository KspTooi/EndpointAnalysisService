package com.ksptool.bio.biz.assembly.model.outmodelorigin.vo;

import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
public class GetOutModelOriginDetailsVo {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "输出方案ID")
    private Long outputSchemaId;

    @Schema(description = "原始字段名")
    private String name;

    @Schema(description = "原始数据类型")
    private String kind;

    @Schema(description = "原始长度")
    private String length;

    @Schema(description = "原始必填 0:否 1:是")
    private Integer require;

    @Schema(description = "原始备注")
    private String remark;

    @Schema(description = "是否主键 0:否 1:是")
    private Integer pk;

    @Schema(description = "原始排序")
    private Integer seq;

}

