package com.ksptooi.biz.user.model.session;

import com.ksptooi.commons.utils.page.PageQuery;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
public class GetSessionListDto extends PageQuery {

    @Schema(description = "用户名")
    private String userName;

}
