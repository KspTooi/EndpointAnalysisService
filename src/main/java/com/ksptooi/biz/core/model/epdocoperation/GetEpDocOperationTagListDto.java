package com.ksptooi.biz.core.model.epdocoperation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetEpDocOperationTagListDto {

    @NotNull(message = "端点文档ID不能为空")
    @Schema(description = "端点文档ID")
    private Long epDocId;

    @Schema(description = "端点文档版本ID null为最新版本")
    private Long epDocVersionId;

}
