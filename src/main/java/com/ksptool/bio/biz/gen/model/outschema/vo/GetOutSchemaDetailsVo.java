package com.ksptool.bio.biz.gen.model.outschema.vo;

import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
public class GetOutSchemaDetailsVo {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "数据源ID")
    private Long dataSourceId;

    @Schema(description = "类型映射方案ID")
    private Long typeSchemaId;

    @Schema(description = "输入SCM ID")
    private Long inputScmId;

    @Schema(description = "输出SCM ID")
    private Long outputScmId;

    @Schema(description = "输出方案名称")
    private String name;

    @Schema(description = "模型名称")
    private String modelName;

    @Schema(description = "数据源表名")
    private String tableName;

    @Schema(description = "移除表前缀")
    private String removeTablePrefix;

    @Schema(description = "权限码前缀")
    private String permCodePrefix;

    @Schema(description = "写出策略 0:不覆盖 1:覆盖")
    private Integer policyOverride;

    @Schema(description = "输入基准路径")
    private String baseInput;

    @Schema(description = "输出基准路径")
    private String baseOutput;

    @Schema(description = "备注")
    private String remark;

}

