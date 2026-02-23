package com.ksptool.bio.biz.core.model.post.dto;

import com.ksptool.assembly.entity.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class GetPostListDto extends PageQuery {


    @Schema(description = "岗位名称")
    private String name;

    @Schema(description = "岗位编码")
    private String code;

}

