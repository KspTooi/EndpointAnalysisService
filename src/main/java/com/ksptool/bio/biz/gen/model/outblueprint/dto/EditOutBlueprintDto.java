package com.ksptool.bio.biz.gen.model.outblueprint.dto;

import com.ksptool.bio.biz.core.common.aop.DtoCustomValidator;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
public class EditOutBlueprintDto implements DtoCustomValidator{

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

    @Size(max = 500, message = "蓝图备注长度不能超过500个字符")
    @Schema(description = "蓝图备注")
    private String remark;


    @Override
    public String validate() {

        //认证方式为 1:账号密码 时，SCM用户名和SCM密码不能为空
        if (scmAuthKind == 1) {
            if (StringUtils.isBlank(scmUsername)) {
                return "当SCM认证方式为账号密码时，SCM用户名不能为空";
            }
            if (StringUtils.isBlank(scmPassword)) {
                return "当SCM认证方式为账号密码时，SCM密码不能为空";
            }
        }

        //认证方式为 2:SSH KEY 时，SSH KEY不能为空
        if (scmAuthKind == 2) {
            if (StringUtils.isBlank(scmPk)) {
                return "当SCM认证方式为SSH KEY时，SSH KEY不能为空";
            }
        }

        //认证方式为 3:PAT 时，账号与密码不能为空(此时密码填写PAT令牌)
        if (scmAuthKind == 3) {
            if (StringUtils.isBlank(scmUsername)) {
                return "当SCM认证方式为PAT时，SCM用户名不能为空";
            }
            if (StringUtils.isBlank(scmPassword)) {
                return "当SCM认证方式为PAT时，SCM密码不能为空";
            }
        }

        return null;

    }

}
