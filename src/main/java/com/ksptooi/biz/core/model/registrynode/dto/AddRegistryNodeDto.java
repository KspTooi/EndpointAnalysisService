package com.ksptooi.biz.core.model.registrynode.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class AddRegistryNodeDto {

    @Schema(description = "父级项ID NULL顶级")
    private Long parentId;

    @Schema(description = "节点Key")
    @NotBlank(message = "节点Key不能为空")
    @Length(max = 128, message = "节点Key长度不能超过128个字符")
    @Pattern(regexp = "^[a-zA-Z0-9_\\-]+$", message = "节点Key只能包含字母、数字、下划线或中划线")
    private String nkey;

    @Schema(description = "节点标签")
    @Length(max = 32, message = "节点标签长度不能超过32个字符")
    private String label;

    @Schema(description = "排序")
    @NotNull(message = "排序不能为空")
    private Integer seq;

    @Schema(description = "说明")
    private String remark;

}
