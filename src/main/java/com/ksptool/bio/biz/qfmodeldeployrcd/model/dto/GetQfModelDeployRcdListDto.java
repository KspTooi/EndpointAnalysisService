package com.ksptool.bio.biz.qfmodeldeployrcd.model.dto;

import com.ksptool.assembly.entity.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetQfModelDeployRcdListDto extends PageQuery {

    @Schema(description="模型名称")
    private String name;

    @Schema(description="模型编码")
    private String code;

    @Schema(description="部署状态 0:正常 1:部署失败")
    private Integer status;

}


