package com.ksptool.bio.biz.qf.model.qfmodeldeployrcd.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetQfModelDeployRcdDetailsVo {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "模型BPMN XML")
    private String bpmnXml;

}
