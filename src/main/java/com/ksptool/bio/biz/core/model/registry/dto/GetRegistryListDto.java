package com.ksptool.bio.biz.core.model.registry.dto;

import com.ksptool.assembly.entity.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetRegistryListDto extends PageQuery {

    @NotBlank(message = "节点Key的全路径不能为空")
    @Schema(description = "节点Key的全路径")
    private String keyPath;

    @Schema(description = "节点Key")
    private String nkey;

    @Schema(description = "节点标签")
    private String label;

    @Schema(description = "数据类型 0:字串 1:整数 2:浮点 3:日期(LDT)")
    private Integer nvalueKind;

}
