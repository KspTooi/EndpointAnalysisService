package com.ksptool.bio.biz.document.model.prompt.dto;

import com.ksptool.assembly.entity.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetPromptListDto extends PageQuery {

    @Schema(description = "名称")
    private String name;

    @Schema(description = "标签(CTJ)")
    private String tags;

}
