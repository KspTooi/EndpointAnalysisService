package com.ksptool.bio.biz.assembly.model.scm.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetScmDetailsVo {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "SCM名称")
    private String name;

    @Schema(description = "项目名称")
    private String projectName;

    @Schema(description = "SCM仓库地址")
    private String scmUrl;

    @Schema(description = "SCM认证方式 0:公开 1:账号密码 2:SSH KEY 3:PAT")
    private Integer scmAuthKind;

    @Schema(description = "SCM用户名")
    private String scmUsername;

    @Schema(description = "SCM密码")
    private String scmPassword;

    @Schema(description = "SSH KEY")
    private String scmPk;

    @Schema(description = "SCM分支")
    private String scmBranch;

    @Schema(description = "SCM备注")
    private String remark;

}

