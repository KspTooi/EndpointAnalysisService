package com.ksptool.bio.biz.document.model.prompt.dto;

import java.util.List;

import com.ksptool.bio.biz.core.common.model.CustomizeTagJson;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddPromptDto {

    @NotBlank(message = "名称不能为空")
    @Size(max = 80, message = "名称长度不能超过80个字符")
    @Schema(description = "名称")
    private String name;

    @NotNull(message = "标签不能为空")
    @Size(max = 6, message = "标签数量不能超过6个")
    @Schema(description = "标签")
    private List<CustomizeTagJson> tags;

    @Size(max = 10000, message = "内容长度不能超过10000个字符")
    @Schema(description = "内容")
    private String content;

}
