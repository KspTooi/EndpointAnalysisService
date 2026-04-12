package com.ksptool.bio.biz.qfmodeldeployrcd.model.vo;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
public class GetQfModelDeployRcdDetailsVo {

    @Schema(description="主键ID")
    private Long id;

    @Schema(description="模型BPMN XML")
    private String bpmnXml;

}
