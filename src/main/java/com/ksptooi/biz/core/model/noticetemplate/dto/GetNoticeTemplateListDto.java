package com.ksptooi.biz.core.model.noticetemplate.dto;

import com.ksptool.assembly.entity.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GetNoticeTemplateListDto extends PageQuery {


    @Schema(description = "模板名称")
    private String name;

    @Schema(description = "模板唯一编码 (业务调用用)")
    private String code;

    @Schema(description = "模板内容 (含占位符)")
    private String content;

    @Schema(description = "状态: 0启用, 1禁用")
    private Integer status;

}

