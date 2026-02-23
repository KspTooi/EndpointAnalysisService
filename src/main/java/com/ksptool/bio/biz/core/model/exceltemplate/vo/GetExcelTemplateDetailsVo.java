package com.ksptool.bio.biz.core.model.exceltemplate.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetExcelTemplateDetailsVo {

    @Schema(description = "模板ID")
    private Long id;

    @Schema(description = "模板附件ID")
    private Long attachId;

    @Schema(description = "模板名称")
    private String name;

    @Schema(description = "模板标识 唯一")
    private String code;

    @Schema(description = "模板备注")
    private String remark;

    @Schema(description = "状态 0:启用 1:禁用")
    private Integer status;

}

