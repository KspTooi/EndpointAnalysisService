package com.ksptool.bio.biz.qfmodelgroup.model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import com.ksptool.assembly.entity.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class GetQfModelGroupListDto extends PageQuery {

    @Schema(description="组名称")
    private String name;

    @Schema(description="组编码")
    private String code;
}

