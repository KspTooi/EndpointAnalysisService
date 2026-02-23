package com.ksptool.bio.biz.core.model.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Range;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddPostDto {

    @Schema(description = "岗位名称")
    @NotBlank(message = "岗位名称不能为空")
    @Size(max = 32, message = "岗位名称长度不能超过32个字符")
    private String name;

    @Schema(description = "岗位编码")
    @NotBlank(message = "岗位编码不能为空")
    @Size(max = 32, message = "岗位编码长度不能超过32个字符")
    private String code;

    @Schema(description = "岗位排序")
    @NotNull(message = "岗位排序不能为空")
    private Integer seq;

    @Schema(description = "0:启用 1:停用")
    @NotNull(message = "状态不能为空")
    @Range(min = 0, max = 1, message = "状态值只能为0或1")
    private Integer status;

    @Schema(description = "备注")
    @Size(max = 1000, message = "备注长度不能超过1000个字符")
    private String remark;

}

