package com.ksptool.bio.biz.gen.model.scm.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GetScmListVo {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "SCM名称")
    private String name;

    @Schema(description = "项目名称")
    private String projectName;

    @Schema(description = "SCM编码")
    private String code;

    @Schema(description = "SCM仓库地址")
    private String scmUrl;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}

