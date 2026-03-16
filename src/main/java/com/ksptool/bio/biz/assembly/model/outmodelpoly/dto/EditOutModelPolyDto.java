package com.ksptool.bio.biz.assembly.model.outmodelpoly.dto;

import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Range;

import java.util.Set;

@Getter
@Setter
public class EditOutModelPolyDto {

    @NotNull(message = "主键ID不能为空")
    @Schema(description = "主键ID")
    private Long id;

    @NotNull(message = "输出方案ID不能为空")
    @Schema(description = "输出方案ID")
    private Long outputSchemaId;

    @NotNull(message = "原始字段ID不能为空")
    @Schema(description = "原始字段ID")
    private Long outputModelOriginId;

    @NotBlank(message = "聚合字段名不能为空")
    @Size(max = 255, message = "聚合字段名长度不能超过255")
    @Schema(description = "聚合字段名")
    private String name;

    @NotBlank(message = "聚合数据类型不能为空")
    @Size(max = 255, message = "聚合数据类型长度不能超过255")
    @Schema(description = "聚合数据类型")
    private String kind;

    @Size(max = 255, message = "聚合长度不能超过255")
    @Schema(description = "聚合长度")
    private String length;

    @NotNull(message = "聚合必填不能为空")
    @Range(min = 0, max = 1, message = "聚合必填值只能为0或1")
    @Schema(description = "聚合必填 0:否 1:是")
    private Integer require;

    @NotNull(message = "聚合可见性策略不能为空")
    @Schema(description = "聚合可见性策略 ADD、EDIT、DETAILS、LIST_DTO、LIST_VO")
    private Set<String> policyCrudJson;

    @NotNull(message = "聚合查询策略不能为空")
    @Range(min = 0, max = 1, message = "聚合查询策略值只能为0或1")
    @Schema(description = "聚合查询策略 0:等于 1:模糊")
    private Integer policyQuery;

    @NotNull(message = "聚合显示策略不能为空")
    @Range(min = 0, max = 6, message = "聚合显示策略值范围为0-6")
    @Schema(description = "聚合显示策略 0:文本框 1:文本域 2:下拉 3:单 4:多 5:LD 6:LDT")
    private Integer policyView;

    @NotNull(message = "是否主键不能为空")
    @Range(min = 0, max = 1, message = "是否主键值只能为0或1")
    @Schema(description = "是否主键 0:否 1:是")
    private Integer pk;

    @NotBlank(message = "聚合字段备注不能为空")
    @Size(max = 80, message = "聚合字段备注长度不能超过80")
    @Schema(description = "聚合字段备注")
    private String remark;

    @NotNull(message = "聚合排序不能为空")
    @Schema(description = "聚合排序")
    private Integer seq;

}
