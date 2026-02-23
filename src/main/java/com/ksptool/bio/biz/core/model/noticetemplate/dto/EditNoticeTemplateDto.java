package com.ksptool.bio.biz.core.model.noticetemplate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDateTime;

@Getter
@Setter
public class EditNoticeTemplateDto {


    @Schema(description = "主键ID")
    @NotNull(message = "主键ID不能为空")
    private Long id;

    @Schema(description = "模板名称")
    @NotBlank(message = "模板名称不能为空")
    @Length(max = 32, message = "模板名称长度不能超过32个字符")
    private String name;

    @Schema(description = "模板唯一编码 (业务调用用)")
    @NotBlank(message = "模板唯一编码不能为空")
    @Length(max = 32, message = "模板唯一编码长度不能超过32个字符")
    private String code;

    @Schema(description = "模板内容 (含占位符)")
    @NotBlank(message = "模板内容不能为空")
    private String content;

    @Schema(description = "状态: 0启用, 1禁用")
    @NotNull(message = "状态不能为空")
    @Range(min = 0, max = 1, message = "状态只能在0-1之间")
    private Integer status;

    @Schema(description = "备注")
    @Length(max = 1000, message = "备注长度不能超过1000个字符")
    private String remark;

}

