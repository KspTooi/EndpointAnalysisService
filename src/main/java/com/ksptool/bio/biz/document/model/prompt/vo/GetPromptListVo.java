package com.ksptool.bio.biz.document.model.prompt.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GetPromptListVo {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "标签(CTJ)")
    private String tags;

    @Schema(description = "参数数量")
    private String paramCount;

    @Schema(description = "版本号")
    private String version;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
