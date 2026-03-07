package com.ksptool.bio.biz.outschema.model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import com.ksptool.assembly.entity.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class GetOutSchemaListDto extends PageQuery {

    @Schema(description="输出方案名称")
    private String name;

    @Schema(description="模型名称")
    private String modelName;

    @Schema(description="数据源表名")
    private String tableName;

}

