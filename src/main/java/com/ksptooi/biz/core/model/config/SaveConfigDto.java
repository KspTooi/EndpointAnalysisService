package com.ksptooi.biz.core.model.config;

import jakarta.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class SaveConfigDto {

    // 主键ID，新增时为null
    @Schema(description = "主键ID，新增时为null")
    private Long id;
    
    // 用户ID，可选，为null时表示全局配置
    @Schema(description = "用户ID，可选，为null时表示全局配置")
    private Long userId;
    
    // 配置键
    @NotBlank(message = "配置键不能为空")
    @Length(min = 2, max = 50, message = "配置键长度必须在2-50个字符之间")
    @Schema(description = "配置键")
    private String configKey;
    
    // 配置值
    @NotBlank(message = "配置值不能为空")
    @Length(max = 500, message = "配置值长度不能超过500个字符")
    @Schema(description = "配置值")
    private String configValue;
    
    // 配置描述
    @Schema(description = "配置描述")
    @Length(max = 200, message = "配置描述长度不能超过200个字符")
    private String description;

} 