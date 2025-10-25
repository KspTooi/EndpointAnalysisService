package com.ksptooi.biz.requestdebug.model.userrequestgroup;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetUserRequestGroupListVo {

    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "父级ID 为空表示根节点")
    private Long parentId;

    @Schema(description = "请求组名称")
    private String name;

    @Schema(description = "请求组描述")
    private String description;

    @Schema(description = "排序")
    private Integer seq;

}

