package com.ksptooi.biz.auth.model.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class UserLoginVo {

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "用户性别")
    private Integer gender;

    @Schema(description = "用户手机号")
    private String phone;

    @Schema(description = "用户邮箱")
    private String email;

    @Schema(description = "用户状态")
    private Integer status;

    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginTime;

    @Schema(description = "所属企业ID")
    private Long rootId;

    @Schema(description = "所属部门ID")
    private Long deptId;

    @Schema(description = "所属企业名称")
    private String rootName;

    @Schema(description = "所属部门名称")
    private String deptName;

    @Schema(description = "用户头像附件ID")
    private Long avatarAttachId;

    @Schema(description = "是否为系统内置用户 0:否 1:是")
    private Integer isSystem;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "用户会话ID")
    private String sessionId;

    @Schema(description = "权限码")
    private Set<String> authorities;

}
