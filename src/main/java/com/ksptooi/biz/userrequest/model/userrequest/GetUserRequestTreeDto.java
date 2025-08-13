package com.ksptooi.biz.userrequest.model.userrequest;

import io.swagger.v3.oas.annotations.media.Schema;
import com.ksptooi.commons.utils.page.PageQuery;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class GetUserRequestTreeDto{

    @Schema(description="关键字查询")
    private String keyword;

}

