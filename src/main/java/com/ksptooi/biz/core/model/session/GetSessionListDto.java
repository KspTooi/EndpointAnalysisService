package com.ksptooi.biz.core.model.session;

import com.ksptooi.commons.utils.page.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetSessionListDto extends PageQuery {

    @Schema(description = "用户名")
    private String userName;

}
