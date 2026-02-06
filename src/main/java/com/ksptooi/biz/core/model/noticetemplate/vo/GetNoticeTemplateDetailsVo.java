package com.ksptooi.biz.core.model.noticetemplate.vo;

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

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "创建人ID")
    private Long creatorId;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "更新人ID")
    private Long updaterId;

    @Schema(description = "删除时间 NULL未删")
    private LocalDateTime deleteTime;

}

