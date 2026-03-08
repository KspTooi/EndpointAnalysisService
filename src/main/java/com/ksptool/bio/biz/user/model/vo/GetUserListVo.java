package com.ksptool.bio.biz.user.model.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
public class GetUserListVo{


    @Schema(description="用户名")
    private String username;

    @Schema(description="昵称")
    private String nickname;

    @Schema(description="性别 0:男 1:女 2:不愿透露")
    private Integer gender;

    @Schema(description="手机号码")
    private String phone;

    @Schema(description="邮箱")
    private String email;

    @Schema(description="用户状态 0:正常 1:封禁")
    private Integer status;

    @Schema(description="最后登录时间")
    private LocalDateTime lastLoginTime;

    @Schema(description="内置用户 0:否 1:是")
    private Integer isSystem;
}

