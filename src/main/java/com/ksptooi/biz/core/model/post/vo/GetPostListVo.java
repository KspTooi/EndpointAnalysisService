package com.ksptooi.biz.core.model.post.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GetPostListVo {

    @Schema(description = "岗位ID")
    private Long id;

    @Schema(description = "岗位名称")
    private String name;

    @Schema(description = "岗位编码")
    private String code;

    @Schema(description = "岗位排序")
    private Integer seq;

    @Schema(description = "0:启用 1:停用")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}

