package com.ksptooi.biz.rdbg.model.userrequesttree.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetUserRequestTreeDto {

    @Schema(description = "关键字查询")
    private String keyword;

}

