package com.ksptool.bio.biz.document.model.epstdword.dto;

import com.ksptool.assembly.entity.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetEpStdWordListDto extends PageQuery {
    
    @Schema(description = "简称")
    private String sourceName;

    @Schema(description = "全称")
    private String sourceNameFull;

    @Schema(description = "英文简称")
    private String targetName;

    @Schema(description = "英文全称")
    private String targetNameFull;

    @Schema(description = "备注")
    private String remark;

}

