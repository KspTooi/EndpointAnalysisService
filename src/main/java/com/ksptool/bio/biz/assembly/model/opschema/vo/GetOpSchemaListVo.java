package com.ksptool.bio.biz.assembly.model.opschema.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetOpSchemaListVo {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "输出方案名称")
    private String name;

    @Schema(description = "模型名称")
    private String modelName;

    @Schema(description = "数据源表名")
    private String tableName;

    @Schema(description = "字段数(原始)")
    private Integer fieldCountOrigin;

    @Schema(description = "字段数(聚合)")
    private Integer fieldCountPoly;

}

