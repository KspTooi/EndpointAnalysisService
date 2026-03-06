package com.ksptool.bio.biz.gen.model.tymschema.dto;

import com.ksptool.assembly.entity.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GetTymSchemaListDto extends PageQuery {


    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "方案名称")
    private String name;

    @Schema(description = "方案编码")
    private String code;

    @Schema(description = "映射源")
    private String mapSource;

    @Schema(description = "映射目标")
    private String mapTarget;

    @Schema(description = "类型数量")
    private Integer typeCount;

    @Schema(description = "默认类型")
    private String defaultType;

    @Schema(description = "排序")
    private Integer seq;

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
}

