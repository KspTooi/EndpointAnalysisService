package com.ksptool.bio.biz.gen.model.tymschemafield.dto;

import com.ksptool.assembly.entity.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetTymSchemaFieldListDto extends PageQuery {

    @NotNull(message = "类型映射方案ID不能为空")
    @Schema(description = "类型映射方案ID")
    private Long typeSchemaId;
    
}

