package com.ksptooi.biz.rdbg.model.userrequestgroup;

import com.ksptooi.biz.rdbg.model.filter.vo.GetSimpleFilterListVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GetUserRequestGroupDetailsVo {

    private Long id;

    @Schema(description = "请求组名称")
    private String name;

    @Schema(description = "请求组描述")
    private String description;

    @Schema(description = "基本过滤器列表")
    private List<GetSimpleFilterListVo> simpleFilters;

}

