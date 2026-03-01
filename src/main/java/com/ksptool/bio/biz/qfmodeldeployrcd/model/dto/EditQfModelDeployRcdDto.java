package com.ksptool.bio.biz.qfmodeldeployrcd.model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Getter
@Setter
public class EditQfModelDeployRcdDto {


    @Schema(description="主键ID")
    private Long id;

    @Schema(description="所属企业/租户ID")
    private Long rootId;

    @Schema(description="所属部门ID")
    private Long deptId;

    @Schema(description="模型ID")
    private Long modelId;

    @Schema(description="模型名称")
    private String name;

    @Schema(description="模型编码")
    private String code;

    @Schema(description="模型BPMN XML")
    private String bpmnXml;

    @Schema(description="模型版本号")
    private Integer dataVersion;

    @Schema(description="引擎"部署ID"(部署失败为NULL)")
    private String engDeploymentId;

    @Schema(description="引擎"流程ID"(部署失败为NULL)")
    private String engProcessDefId;

    @Schema(description="引擎返回的部署结果")
    private String engDeployResult;

    @Schema(description="部署状态 0:正常 1:部署失败")
    private Integer status;

    @Schema(description="创建时间")
    private LocalDateTime createTime;

    @Schema(description="创建人ID")
    private Long creatorId;

    @Schema(description="更新时间")
    private LocalDateTime updateTime;

    @Schema(description="更新人ID")
    private Long updaterId;

    @Schema(description="删除时间 NULL未删")
    private LocalDateTime deleteTime;

}

