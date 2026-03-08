package com.ksptool.bio.biz.gen.model.outmodelorigin.dto;

import com.ksptool.assembly.entity.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class GetOutModelOriginListDto{

    @NotNull(message = "输出方案ID不能为空")
    @Schema(description = "输出方案ID")
    private Long outputSchemaId;

}

