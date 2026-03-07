package com.ksptool.bio.biz.outmodelorigin.model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Getter
@Setter
public class EditOutModelOriginDto {


    @Schema(description="主键ID")
    private Long id;

    @Schema(description="输出方案ID")
    private Long outputSchemaId;

    @Schema(description="原始字段名")
    private String name;

    @Schema(description="原始数据类型")
    private String kind;

    @Schema(description="原始长度")
    private String length;

    @Schema(description="原始必填 0:否 1:是")
    private Integer require;

    @Schema(description="原始备注")
    private String remark;

    @Schema(description="原始排序")
    private Integer seq;

    @Schema(description="创建时间")
    private LocalDateTime createTime;

    @Schema(description="创建人ID")
    private Long creatorId;

    @Schema(description="更新时间")
    private LocalDateTime updateTime;

    @Schema(description="更新人ID")
    private Long updaterId;

}

