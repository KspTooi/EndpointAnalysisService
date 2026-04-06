package com.ksptool.bio.biz.assembly.model.opschema.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetOpBluePrintListVo {

    @Schema(description = "蓝图文件名")
    private String fileName;

    @Schema(description = "蓝图文件路径(相对于基准路径)")
    private String filePath;

}
