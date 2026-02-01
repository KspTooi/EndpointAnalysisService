package com.ksptooi.biz.core.model.exceltemplate.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GetExcelTemplateListVo {

    @Schema(description = "模板ID")
    private Long id;

    @Schema(description = "模板附件ID")
    private Long attachId;

    @Schema(description = "模板名称")
    private String name;

    @Schema(description = "模板标识 唯一")
    private String key;

    @Schema(description = "模板备注")
    private String remark;

    @Schema(description = "状态 0:启用 1:禁用")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "创建人ID")
    private Long creatorId;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "更新人ID")
    private Long updaterId;

    @Schema(description = "删除时间 NULL未删除")
    private LocalDateTime deleteTime;

}

