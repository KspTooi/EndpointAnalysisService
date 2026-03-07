package com.ksptool.bio.biz.gen.model.outschema.dto;

import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
public class AddOutSchemaDto {

    @Schema(description = "数据源ID")
    private Long dataSourceId;

    @Schema(description = "类型映射方案ID")
    private Long typeSchemaId;

    @Schema(description = "输入SCM ID")
    private Long inputScmId;

    @Schema(description = "输出SCM ID")
    private Long outputScmId;

    @NotBlank(message = "输出方案名称不能为空")
    @Size(max = 32, message = "输出方案名称不能超过32个字符")
    @Schema(description = "输出方案名称")
    private String name;

    @NotBlank(message = "模型名称不能为空")
    @Size(max = 255, message = "模型名称不能超过255个字符")
    @Schema(description = "模型名称")
    private String modelName;

    @Size(max = 80, message = "数据源表名不能超过80个字符")
    @Schema(description = "数据源表名")
    private String tableName;

    @NotBlank(message = "移除表前缀不能为空")
    @Size(max = 80, message = "移除表前缀不能超过80个字符")
    @Schema(description = "移除表前缀")
    private String removeTablePrefix;

    @NotBlank(message = "权限码前缀不能为空")
    @Size(max = 32, message = "权限码前缀不能超过32个字符")
    @Schema(description = "权限码前缀")
    private String permCodePrefix;

    @NotNull(message = "写出策略不能为空")
    @Range(min = 0, max = 1, message = "写出策略值无效，0:不覆盖 1:覆盖")
    @Schema(description = "写出策略 0:不覆盖 1:覆盖")
    private Integer policyOverride;

    @NotBlank(message = "输入基准路径不能为空")
    @Size(max = 320, message = "输入基准路径不能超过320个字符")
    @Schema(description = "输入基准路径")
    private String baseInput;

    @NotBlank(message = "输出基准路径不能为空")
    @Size(max = 320, message = "输出基准路径不能超过320个字符")
    @Schema(description = "输出基准路径")
    private String baseOutput;

    @Schema(description = "备注")
    private String remark;

}
