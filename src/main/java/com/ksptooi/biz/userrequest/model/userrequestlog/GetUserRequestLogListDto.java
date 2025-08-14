package com.ksptooi.biz.userrequest.model.userrequestlog;

import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import com.ksptooi.commons.utils.page.PageQuery;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class GetUserRequestLogListDto extends PageQuery{

    @Schema(description="用户请求ID")
    private Long userRequestId;

}

