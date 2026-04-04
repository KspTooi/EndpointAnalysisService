package com.ksptool.bio.biz.assembly.model.tymschema.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GetTymSchemaListVo {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "方案名称")
    private String name;

    @Schema(description = "方案编码")
    private String code;

    @Schema(description = "类型数量")
    private Integer typeCount;

    @Schema(description = "默认类型")
    private String defaultType;

    @Schema(description = "排序")
    private Integer seq;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
