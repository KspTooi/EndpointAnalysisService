package com.ksptool.bio.biz.outmodelpoly.model.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Getter
@Setter
public class GetOutModelPolyDetailsVo{

    @Schema(description="主键ID")
    private Long id;

    @Schema(description="输出方案ID")
    private Long outputSchemaId;

    @Schema(description="原始字段ID")
    private Long outputModelOriginId;

    @Schema(description="聚合字段名")
    private String name;

    @Schema(description="聚合数据类型")
    private String kind;

    @Schema(description="聚合长度")
    private String length;

    @Schema(description="聚合必填 0:否 1:是")
    private Integer require;

    @Schema(description="聚合可见性策略 ADD、EDIT、LIST_QUERY、LIST_VIEW")
    private String policyCrudJson;

    @Schema(description="聚合查询策略 0:等于 1:模糊")
    private Integer policyQuery;

    @Schema(description="聚合显示策略 0:文本框 1:文本域 2:下拉 3:单 4:多 5:LD 6:LDT")
    private Integer policyView;

    @Schema(description="placeholder")
    private String placeholder;

    @Schema(description="聚合排序")
    private Integer seq;

}

