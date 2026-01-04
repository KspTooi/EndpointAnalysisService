package com.ksptooi.biz.requestdebug.model.collection;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestParamJson {

    @Schema(description = "是否启用")
    private boolean e;

    @Schema(description = "k")
    private String k;

    @Schema(description = "v")
    private String v;

    @Schema(description = "排序")
    private Integer s;

}
