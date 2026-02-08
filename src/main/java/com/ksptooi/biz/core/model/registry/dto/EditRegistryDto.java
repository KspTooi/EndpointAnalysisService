package com.ksptooi.biz.core.model.registry.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class EditRegistryDto {

    @Schema(description = "ID")
    @NotNull(message = "ID不能为空")
    private Long id;

    @Schema(description = "条目Value")
    @Length(max = 1024, message = "节点Value长度不能超过1024个字符")
    private String nvalue;

    @Schema(description = "条目标签")
    @Length(max = 32, message = "节点标签长度不能超过32个字符")
    private String label;

    @Schema(description = "排序")
    @NotNull(message = "排序不能为空")
    private Integer seq;

    @Schema(description = "说明")
    private String remark;
    
    public String validate() {
        return null;
    }

}
