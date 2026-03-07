package com.ksptool.bio.biz.genoutblueprint.model.dto;

import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
public class EditGenOutBlueprintDto {

    @NotNull(message = "主键ID不能为空")
    @Schema(description = "主键ID")
    private Long id;

    @NotBlank(message = "蓝图名称不能为空")
    @Size(max = 32, message = "蓝图名称长度不能超过32个字符")
    @Schema(description = "蓝图名称")
    private String name;

    @Size(max = 80, message = "项目名称长度不能超过80个字符")
    @Schema(description = "项目名称")
    private String projectName;

    @NotBlank(message = "蓝图编码不能为空")
    @Size(max = 32, message = "蓝图编码长度不能超过32个字符")
    @Schema(description = "蓝图编码")
    private String code;

    @NotBlank(message = "SCM仓库地址不能为空")
    @Size(max = 1000, message = "SCM仓库地址长度不能超过1000个字符")
    @Schema(description = "SCM仓库地址")
    private String scmUrl;

    @NotNull(message = "SCM认证方式不能为空")
    @Range(min = 0, max = 2, message = "SCM认证方式无效，有效值：0公开 1账号密码 2SSH KEY")
    @Schema(description = "SCM认证方式 0:公开 1:账号密码 2:SSH KEY")
    private Integer scmAuthKind;

    @Schema(description = "SCM用户名")
    private String scmUsername;

    @Schema(description = "SCM密码")
    private String scmPassword;

    @Schema(description = "SSH KEY")
    private String scmPk;

    @NotBlank(message = "SCM分支不能为空")
    @Size(max = 80, message = "SCM分支长度不能超过80个字符")
    @Schema(description = "SCM分支")
    private String scmBranch;

    @NotBlank(message = "基准路径不能为空")
    @Size(max = 1280, message = "基准路径长度不能超过1280个字符")
    @Schema(description = "基准路径")
    private String scmBasePath;

    @NotBlank(message = "蓝图备注不能为空")
    @Schema(description = "蓝图备注")
    private String remark;

}
