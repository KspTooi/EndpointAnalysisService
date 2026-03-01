package com.ksptool.bio.biz.qf.model.qfmodel.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GetQfModelDetailsVo {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "所属企业/租户ID")
    private Long rootId;

    @Schema(description = "所属部门ID")
    private Long deptId;

    @Schema(description = "该模型生效的部署ID")
    private Long activeDeployId;

    @Schema(description = "模型组ID")
    private Long groupId;

    @Schema(description = "模型名称")
    private String name;

    @Schema(description = "模型编码")
    private String code;

    @Schema(description = "模型BPMN XML")
    private String bpmnXml;

    @Schema(description = "模型版本号")
    private Integer dataVersion;

    @Schema(description = "模型状态 0:草稿 1:已部署")
    private Integer status;

    @Schema(description = "排序")
    private Integer seq;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "创建人ID")
    private Long creatorId;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "更新人ID")
    private Long updaterId;

    @Schema(description = "删除时间 NULL未删")
    private LocalDateTime deleteTime;

}

