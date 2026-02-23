package com.ksptool.bio.biz.core.model.noticetemplate.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GetNoticeTemplateDetailsVo {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "模板名称")
    private String name;

    @Schema(description = "模板唯一编码 (业务调用用)")
    private String code;

    @Schema(description = "模板内容 (含占位符)")
    private String content;

    @Schema(description = "状态: 0启用, 1禁用")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

}

