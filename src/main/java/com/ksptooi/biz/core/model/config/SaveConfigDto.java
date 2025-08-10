package com.ksptooi.biz.core.model.config;

import jakarta.validation.constraints.NotBlank;
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
    @Schema(description = "配置键")
    private String configKey;
    
    // 配置值
    @NotBlank(message = "配置值不能为空")
    @Schema(description = "配置值")
    private String configValue;
    
    // 配置描述
    @Schema(description = "配置描述")
    private String description;

} 