package com.ksptooi.biz.core.model.group;


import com.ksptool.assembly.entity.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetGroupListDto extends PageQuery {

    @Schema(description = "模糊匹配 组标识、组名称、组描述")
    private String keyword;

    @Schema(description = "组状态：0:禁用 1:启用")
    private Integer status;

}
