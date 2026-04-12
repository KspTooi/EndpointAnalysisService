package com.ksptool.bio.biz.qfmodelgroup.model.vo;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
public class GetQfModelGroupDetailsVo {

    @Schema(description="主键ID")
    private Long id;

    @Schema(description="组名称")
    private String name;

    @Schema(description="组编码")
    private String code;

    @Schema(description="备注")
    private String remark;

    @Schema(description="排序")
    private Integer seq;

}
