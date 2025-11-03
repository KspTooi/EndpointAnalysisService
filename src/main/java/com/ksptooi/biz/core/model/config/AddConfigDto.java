package com.ksptooi.biz.core.model.config;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class AddConfigDto {

    @Schema(description = "用户ID，可选，为null时表示全局配置")
    private Long userId;

    @NotBlank(message = "配置键不能为空")
    @Length(min = 2, max = 50, message = "配置键长度必须在2-50个字符之间")
    @Schema(description = "配置键")
    private String configKey;

    @NotBlank(message = "配置值不能为空")
    @Length(max = 500, message = "配置值长度不能超过500个字符")
    @Schema(description = "配置值")
    private String configValue;

    @Schema(description = "配置描述")
    @Length(max = 200, message = "配置描述长度不能超过200个字符")
    private String description;
}
