package com.ksptool.bio.biz.core.model.config;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class EditConfigDto {

    @Schema(description = "主键ID")
    @NotNull(message = "配置ID不能为空")
    private Long id;

    @NotBlank(message = "配置值不能为空")
    @Length(max = 500, message = "配置值长度不能超过500个字符")
    @Schema(description = "配置值")
    private String configValue;

    @Schema(description = "配置描述")
    @Length(max = 200, message = "配置描述长度不能超过200个字符")
    private String description;
}
