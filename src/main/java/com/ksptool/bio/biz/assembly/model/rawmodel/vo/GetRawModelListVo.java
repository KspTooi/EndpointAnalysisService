package com.ksptool.bio.biz.assembly.model.rawmodel.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetRawModelListVo {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "输出方案ID")
    private Long outputSchemaId;

    @Schema(description = "字段名")
    private String name;

    @Schema(description = "数据类型")
    private String dataType;

    @Schema(description = "长度")
    private Integer length;

    @Schema(description = "必填 0:否 1:是")
    private Integer require;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "是否主键 0:否 1:是")
    private Integer pk;

    @Schema(description = "排序")
    private Integer seq;

}

