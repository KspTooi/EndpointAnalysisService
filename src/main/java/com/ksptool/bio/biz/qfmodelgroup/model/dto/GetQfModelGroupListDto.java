package com.ksptool.bio.biz.qfmodelgroup.model.dto;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import com.ksptool.assembly.entity.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetQfModelGroupListDto extends PageQuery {

    @Schema(description="组名称")
    private String name;

    @Schema(description="组编码")
    private String code;

}
