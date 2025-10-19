package com.ksptooi.biz.user.model.group;


import com.ksptooi.commons.utils.page.PageQuery;
import lombok.Getter;
import lombok.Setter;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter@Setter
public class GetGroupListDto extends PageQuery {

    @Schema(description = "模糊匹配 组标识、组名称、组描述")
    private String keyword;

    @Schema(description = "组状态：0:禁用 1:启用")
    private Integer status;

}
