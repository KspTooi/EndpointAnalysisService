package com.ksptooi.biz.user.model.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterDto {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

}
