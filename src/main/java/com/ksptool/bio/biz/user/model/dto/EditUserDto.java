package com.ksptool.bio.biz.user.model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
public class EditUserDto {


    @Schema(description="用户ID")
    private Long id;

    @Schema(description="用户名")
    private String username;

    @Schema(description="密码")
    private String password;

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

}

