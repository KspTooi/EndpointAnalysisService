package com.ksptooi.biz.core.model.registry.dto;

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

}
