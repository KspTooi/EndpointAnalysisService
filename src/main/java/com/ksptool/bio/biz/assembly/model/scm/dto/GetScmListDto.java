package com.ksptool.bio.biz.assembly.model.scm.dto;

import com.ksptool.assembly.entity.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetScmListDto extends PageQuery {

    @Schema(description = "SCM名称")
    private String name;

    @Schema(description = "项目名称")
    private String projectName;

}

