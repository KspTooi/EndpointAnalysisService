package com.ksptool.bio.biz.document.model.prompt.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import com.ksptool.bio.biz.core.common.model.CustomizeTagJson;

@Getter
@Setter
public class GetPromptListVo {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "标签(CTJ)")
    private List<CustomizeTagJson> tags;

    @Schema(description = "参数数量")
    private Integer paramCount;

    @Schema(description = "版本号")
    private Integer version;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
