package com.ksptooi.biz.post.model.dto;

import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Getter
@Setter
public class AddPostDto {

    @Schema(description="岗位名称")
    @NotBlank(message = "岗位名称不能为空")
    @Size(max = 32, message = "岗位名称长度不能超过32个字符")
    private String name;

    @Schema(description="岗位编码")
    @NotBlank(message = "岗位编码不能为空")
    @Size(max = 32, message = "岗位编码长度不能超过32个字符")
    private String code;

    @Schema(description="岗位排序")
    @NotNull(message = "岗位排序不能为空")
    private Integer seq;

}

