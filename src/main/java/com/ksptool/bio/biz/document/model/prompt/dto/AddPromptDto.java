package com.ksptool.bio.biz.document.model.prompt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddPromptDto {

    @Schema(description = "名称")
    private String name;

    @Schema(description = "标签(CTJ)")
    private String tags;

    @Schema(description = "内容")
    private String content;

}
