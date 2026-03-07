package com.ksptool.bio.biz.gen.model.datsource.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetDataSourceTableListVo {

    @Schema(description = "表名")
    private String tableName;

    @Schema(description = "表注释")
    private String tableComment;

}
