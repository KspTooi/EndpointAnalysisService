package com.ksptooi.biz.document.model.epdocoperation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GetEpDocOperationTagListVo {

    public GetEpDocOperationTagListVo(String tag, Long apiCount) {
        this.tag = tag;
        this.apiCount = apiCount;
    }

    @Schema(description = "标签")
    private String tag;

    @Schema(description = "接口数量")
    private Long apiCount;

    @Schema(description = "接口定义")
    private List<GetEpDocOperationTagListOperationDefineVo> operationDefineList;

}
