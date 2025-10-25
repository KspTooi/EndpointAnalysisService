package com.ksptooi.biz.core.model.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import java.util.List;

@Getter
@Setter
public class SaveUserDto {

    @Schema(description = "用户ID，创建时为空")
    private Long id;

    @Schema(description = "用户名")
    @Pattern(regexp = "^[a-zA-Z0-9_]{4,20}$", message = "用户名只能包含4-20位字母、数字和下划线")
    private String username;

    @Schema(description = "用户密码，创建时必填，编辑时可选")
    @Length(max = 128, message = "用户密码长度不能超过128个字符")
    private String password;

    @Length(max = 50, message = "用户昵称长度不能超过50个字符")
    @Schema(description = "用户昵称")
    private String nickname;

    @Length(max = 64, message = "用户邮箱长度不能超过64个字符")
    @Schema(description = "用户邮箱")
    @Email(message = "邮箱格式不正确")
    private String email;

    @Range(min = 0, max = 1, message = "用户状态只能在0或1之间")
    @Schema(description = "用户状态：0-禁用，1-启用")
    private Integer status;

    @Schema(description = "用户组ID列表")
    private List<Long> groupIds;
}
