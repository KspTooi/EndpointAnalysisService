package com.ksptooi.biz.document.model.epstdword.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddEpStdWordDto {

    @NotBlank(message = "简称不能为空")
    @Size(max = 128, message = "简称长度不能超过128个字符")
    @Schema(description = "简称")
    private String sourceName;

    @Size(max = 255, message = "全称长度不能超过255个字符")
    @Schema(description = "全称")
    private String sourceNameFull;

    @NotBlank(message = "英文简称不能为空")
    @Size(max = 128, message = "英文简称长度不能超过128个字符")
    @Schema(description = "英文简称")
    private String targetName;

    @Size(max = 128, message = "英文全称长度不能超过128个字符")
    @Schema(description = "英文全称")
    private String targetNameFull;

    @Size(max = 1000, message = "备注长度不能超过1000个字符")
    @Schema(description = "备注")
    private String remark;

}

