package com.ksptool.bio.biz.qf.model.qfmodelgroup.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetQfModelGroupDetailsVo {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "组名称")
    private String name;

    @Schema(description = "组编码")
    private String code;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "排序")
    private Integer seq;

}

