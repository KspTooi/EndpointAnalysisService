package com.ksptool.bio.biz.auth.model.session.dto;

import com.ksptool.assembly.entity.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetSessionListDto extends PageQuery {

    @Schema(description = "用户名")
    private String userName;

}
