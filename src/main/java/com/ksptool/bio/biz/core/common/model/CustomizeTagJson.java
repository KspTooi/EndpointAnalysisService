package com.ksptool.bio.biz.core.common.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomizeTagJson {
    @NotBlank(message = "name不可为空")
    private String n;
}
