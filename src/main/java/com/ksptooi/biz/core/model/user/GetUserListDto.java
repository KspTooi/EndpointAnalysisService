package com.ksptooi.biz.core.model.user;


import com.ksptool.assembly.entity.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetUserListDto extends PageQuery {

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "0:正常 1:封禁")
    private Integer status;
}
