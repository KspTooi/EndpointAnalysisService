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

    @Schema(description = "蓝图解析名(代入参数解析后的文件名)")
    private String parsedName;

    @Schema(description = "蓝图解析路径(代入参数解析后的路径)")
    private String parsedPath;

    @Schema(description = "蓝图SHA256")
    private String sha256Hex;

}
