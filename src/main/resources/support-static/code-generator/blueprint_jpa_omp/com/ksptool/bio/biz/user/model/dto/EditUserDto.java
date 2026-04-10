package com.ksptool.bio.biz.user.model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class EditUserDto {


    @NotBlank(message = "用户ID不能为空")
    @Schema(description="用户ID")
    private String id;

    @NotBlank(message = "用户名不能为空")
    @Schema(description="用户名")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Schema(description="密码")
    private String password;

    @Schema(description="昵称")
    private String nickname;

    @Schema(description="性别 0:男 1:女 2:不愿透露")
    private String gender;

    @Schema(description="手机号码")
    private String phone;

    @Schema(description="邮箱")
    private String email;

    @NotBlank(message = "登录次数不能为空")
    @Schema(description="登录次数")
    private String loginCount;

    @NotBlank(message = "用户状态 0:正常 1:封禁不能为空")
    @Schema(description="用户状态 0:正常 1:封禁")
    private String status;

    @Schema(description="最后登录时间")
    private String lastLoginTime;

    @Schema(description="所属企业ID")
    private String rootId;

    @Schema(description="所属企业名称")
    private String rootName;

    @Schema(description="部门ID")
    private String deptId;

    @Schema(description="部门名称")
    private String deptName;

    @Schema(description="已激活的公司ID(兼容字段)")
    private String activeCompanyId;

    @Schema(description="已激活的环境ID(兼容字段)")
    private String activeEnvId;

    @Schema(description="用户头像附件ID")
    private String avatarAttachId;

    @NotBlank(message = "内置用户 0:否 1:是不能为空")
    @Schema(description="内置用户 0:否 1:是")
    private String isSystem;

    @NotBlank(message = "数据版本号不能为空")
    @Schema(description="数据版本号")
    private String dataVersion;

    @NotBlank(message = "创建时间不能为空")
    @Schema(description="创建时间")
    private String createTime;

    @NotBlank(message = "创建人ID不能为空")
    @Schema(description="创建人ID")
    private String creatorId;

    @NotBlank(message = "修改时间不能为空")
    @Schema(description="修改时间")
    private String updateTime;

    @NotBlank(message = "更新人ID不能为空")
    @Schema(description="更新人ID")
    private String updaterId;

    @Schema(description="删除时间 为NULL未删")
    private String deleteTime;

}

