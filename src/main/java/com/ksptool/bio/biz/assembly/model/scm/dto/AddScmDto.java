package com.ksptool.bio.biz.assembly.model.scm.dto;

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
public class AddScmDto implements DtoCustomValidator {

    @NotBlank(message = "SCM名称不能为空")
    @Size(max = 32, message = "SCM名称长度不能超过32个字符")
    @Schema(description = "SCM名称")
    private String name;

    @Size(max = 80, message = "项目名称长度不能超过80个字符")
    @Schema(description = "项目名称")
    private String projectName;

    @NotBlank(message = "SCM仓库地址不能为空")
    @Size(max = 1000, message = "SCM仓库地址长度不能超过1000个字符")
    @Schema(description = "SCM仓库地址")
    private String scmUrl;

    @NotNull(message = "SCM认证方式不能为空")
    @Range(min = 0, max = 3, message = "SCM认证方式无效，有效值：0:公开 1:账号密码 2:SSH KEY 3:PAT")
    @Schema(description = "SCM认证方式 0:公开 1:账号密码 2:SSH KEY 3:PAT")
    private Integer scmAuthKind;

    @Size(max = 10000, message = "SCM用户名长度不能超过10000个字符")
    @Schema(description = "SCM用户名")
    private String scmUsername;

    @Size(max = 10000, message = "SCM密码长度不能超过10000个字符")
    @Schema(description = "SCM密码")
    private String scmPassword;

    @Size(max = 10000, message = "SSH KEY长度不能超过10000个字符")
    @Schema(description = "SSH KEY")
    private String scmPk;

    @NotBlank(message = "SCM分支不能为空")
    @Size(max = 80, message = "SCM分支长度不能超过80个字符")
    @Schema(description = "SCM分支")
    private String scmBranch;

    @Size(max = 500, message = "SCM备注长度不能超过500个字符")
    @Schema(description = "SCM备注")
    private String remark;

    /**
     * 验证DTO是否合法
     *
     * @return 错误信息 无错误返回null
     */
    @Override
    public String validate() {

        //校验 SCM 仓库地址格式
        if (StringUtils.isNotBlank(scmUrl)) {
            String url = scmUrl.trim();

            boolean isHttp = url.startsWith("http://") || url.startsWith("https://");
            boolean isStandardSsh = url.startsWith("ssh://") || url.startsWith("git://");
            boolean isScpStyle = url.startsWith("git@") && url.contains(":");
            boolean isCustomScp = url.startsWith("[") && url.contains("]:");

            if (!isHttp && !isStandardSsh && !isScpStyle && !isCustomScp) {
                return "SCM仓库地址格式不正确，请输入有效的 HTTP(S) / SSH 或标准 Git 地址";
            }
        }

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

            //剔除前后可能误输入的空格或换行符
            String pk = scmPk.trim();

            //必须以 OpenSSH 格式的头部开头
            if (!pk.startsWith("-----BEGIN OPENSSH PRIVATE KEY-----")) {
                return "SSH私钥格式不正确，系统目前仅支持 'BEGIN OPENSSH PRIVATE KEY' 格式的新版私钥";
            }

            //确保有对应的尾部（防止用户复制粘贴时漏掉后半截）
            if (!pk.endsWith("-----END OPENSSH PRIVATE KEY-----")) {
                return "SSH私钥内容不完整，请确保包含 'END OPENSSH PRIVATE KEY' 结尾";
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
