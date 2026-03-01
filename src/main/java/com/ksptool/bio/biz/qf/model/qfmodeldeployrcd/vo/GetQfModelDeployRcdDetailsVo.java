package com.ksptool.bio.biz.qf.model.qfmodeldeployrcd.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetQfModelDeployRcdDetailsVo {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "模型名称")
    private String name;

    @Schema(description = "模型编码")
    private String code;

    @Schema(description = "模型BPMN XML")
    private String bpmnXml;

    @Schema(description = "模型版本号")
    private Integer dataVersion;

    @Schema(description = "引擎返回的部署结果")
    private String engDeployResult;

    @Schema(description = "部署状态 0:正常 1:部署失败")
    private Integer status;


}

