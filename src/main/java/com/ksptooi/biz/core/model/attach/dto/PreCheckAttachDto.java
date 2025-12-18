package com.ksptooi.biz.core.model.attach.dto;

import org.hibernate.validator.constraints.Range;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class PreCheckAttachDto {

    @Schema(description = "文件名")
    @NotBlank(message = "文件名不能为空")
    @Size(max = 128,message = "文件名长度不能超过128")
    private String name;
    
    @Schema(description = "业务代码")
    @NotBlank(message = "业务代码不能为空")
    @Size(max = 64,message = "业务代码长度不能超过32")
    private String kind;

    @Schema(description = "文件大小")
    @NotNull(message = "文件大小不能为空")
    @Range(min = 0,message = "文件大小必须在0-32GB之间")
    private Long totalSize; 

    @Schema(description = "Sha256Hex")
    @NotBlank(message = "Sha256Hex不能为空")
    @Size(max = 64,message = "Sha256Hex长度不能超过64")
    @Pattern(regexp = "^[0-9a-fA-F]+$",message = "Sha256Hex格式不正确")
    private String sha256;

}
