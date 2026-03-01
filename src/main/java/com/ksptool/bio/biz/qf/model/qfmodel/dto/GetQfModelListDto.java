package com.ksptool.bio.biz.qf.model.qfmodel.dto;

import com.ksptool.assembly.entity.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetQfModelListDto extends PageQuery {

    @Schema(description = "模型名称")
    private String name;

    @Schema(description = "模型编码")
    private String code;

    @Schema(description = "模型状态 0:草稿 1:已部署")
    private Integer status;

}

