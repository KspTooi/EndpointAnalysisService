package com.ksptool.bio.biz.gentymschemafield.model.dto;

import com.ksptool.assembly.entity.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GetGenTymSchemaFieldListDto extends PageQuery {


    @Schema(description="主键ID")
    private Long id;

    @Schema(description="类型映射方案ID")
    private Long typeSchemaId;

    @Schema(description="匹配源类型")
    private String source;

    @Schema(description="匹配目标类型")
    private String target;

    @Schema(description="排序")
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

