package com.ksptool.bio.biz.auth.model.profile.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class GetCurrentUserProfileVo {

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

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginTime;

    @Schema(description = "是否为系统内置用户 0:否 1:是")
    private Integer isSystem;

    @Schema(description = "用户头像附件ID")
    private Long avatarAttachId;

    @Schema(description = "拥有的用户组")
    private List<String> groups;

    @Schema(description = "用户权限列表")
    private List<GetCurrentUserProfilePermissionVo> permissions;

}
