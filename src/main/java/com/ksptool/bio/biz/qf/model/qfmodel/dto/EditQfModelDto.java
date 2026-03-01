package com.ksptool.bio.biz.qf.model.qfmodel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditQfModelDto {

    @Schema(description = "主键ID")
    @NotNull(message = "主键ID不能为空")
    private Long id;

    @Schema(description = "所属企业/租户ID")
    @NotNull(message = "所属企业/租户ID不能为空")
    private Long rootId;

    @Schema(description = "所属部门ID")
    @NotNull(message = "所属部门ID不能为空")
    private Long deptId;

    @Schema(description = "该模型生效的部署ID")
    @NotNull(message = "部署ID不能为空")
    private Long activeDeployId;

    @Schema(description = "模型组ID")
    @NotNull(message = "模型组ID不能为空")
    private Long groupId;

    @Schema(description = "模型名称")
    @NotBlank(message = "模型名称不能为空")
    @Size(max = 80, message = "模型名称长度不能超过80个字符")
    private String name;

    @Schema(description = "模型编码")
    @NotBlank(message = "模型编码不能为空")
    @Size(max = 32, message = "模型编码长度不能超过32个字符")
    private String code;

    @Schema(description = "模型BPMN XML")
    private String bpmnXml;

    @Schema(description = "模型版本号")
    @NotNull(message = "模型版本号不能为空")
    private Integer dataVersion;

    @Schema(description = "模型状态 0:草稿 1:已部署 2:历史")
    @NotNull(message = "模型状态不能为空")
    private Integer status;

    @Schema(description = "排序")
    @NotNull(message = "排序不能为空")
    private Integer seq;

}
