package com.ksptool.bio.biz.qfmodeldeployrcd.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditQfModelDeployRcdDto {

    @Schema(description = "主键ID")
    @NotNull(message = "主键ID不能为空")
    private Long id;

    @Schema(description = "所属企业/租户ID")
    @NotNull(message = "所属企业/租户ID不能为空")
    private Long rootId;

    @Schema(description = "所属部门ID")
    @NotNull(message = "所属部门ID不能为空")
    private Long deptId;

    @Schema(description = "模型ID")
    @NotNull(message = "模型ID不能为空")
    private Long modelId;

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

    @Schema(description = "引擎\"部署ID\"(部署失败为NULL)")
    @Size(max = 128, message = "引擎部署ID长度不能超过128个字符")
    private String engDeploymentId;

    @Schema(description = "引擎\"流程ID\"(部署失败为NULL)")
    @Size(max = 128, message = "引擎流程ID长度不能超过128个字符")
    private String engProcessDefId;

    @Schema(description = "引擎返回的部署结果")
    @NotBlank(message = "引擎部署结果不能为空")
    private String engDeployResult;

    @Schema(description = "部署状态 0:正常 1:部署失败")
    @NotNull(message = "部署状态不能为空")
    private Integer status;

}
