package com.ksptool.bio.biz.genoutblueprint.model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Getter
@Setter
public class AddGenOutBlueprintDto {


    @Schema(description="蓝图名称")
    private String name;

    @Schema(description="项目名称")
    private String projectName;

    @Schema(description="蓝图编码")
    private String code;

    @Schema(description="SCM仓库地址")
    private String scmUrl;

    @Schema(description="SCM认证方式 0:公开 1:账号密码 2:SSH KEY")
    private Integer scmAuthKind;

    @Schema(description="SCM用户名")
    private String scmUsername;

    @Schema(description="SCM密码")
    private String scmPassword;

    @Schema(description="SSH KEY")
    private String scmPk;

    @Schema(description="SCM分支")
    private String scmBranch;

    @Schema(description="基准路径")
    private String scmBasePath;

    @Schema(description="蓝图备注")
    private String remark;

    @Schema(description="创建时间")
    private LocalDateTime createTime;

    @Schema(description="创建人ID")
    private Long creatorId;

    @Schema(description="更新时间")
    private LocalDateTime updateTime;

    @Schema(description="更新人ID")
    private Long updaterId;

}

