package com.ksptool.bio.biz.document.model.epdocoperation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetEpDocOperationTagListOperationDefineVo {

    @Schema(description = "OperationId")
    private Long id;

    @Schema(description = "接口名")
    private String name;

    @Schema(description = "请求方式")
    private String method;

}
