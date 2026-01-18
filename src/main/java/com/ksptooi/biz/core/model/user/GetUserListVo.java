package com.ksptooi.biz.core.model.user;

import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter@Setter
public class GetUserListVo {

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

    @Schema(description = "创建时间")
    private String createTime;

    @Schema(description = "最后登录时间")
    private String lastLoginTime;

    @Schema(description = "用户状态 0:正常 1:封禁")
    private Integer status;
}
