package com.ksptooi.biz.requestdebug.model.collection;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestHeaderJson {

    @Schema(description = "是否启用")
    private boolean e = true;

    @Schema(description = "请求头键")
    private String k;

    @Schema(description = "请求头值")
    private String v;

}
