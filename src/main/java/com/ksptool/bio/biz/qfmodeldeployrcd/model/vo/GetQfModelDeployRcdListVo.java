package com.ksptool.bio.biz.qfmodeldeployrcd.model.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Getter
@Setter
public class GetQfModelDeployRcdListVo{

    @Schema(description="主键ID")
    private Long id;

    @Schema(description="模型名称")
    private String name;

    @Schema(description="模型编码")
    private String code;

    @Schema(description="模型版本号")
    private Integer dataVersion;

    @Schema(description="部署状态 0:正常 1:部署失败")
    private Integer status;

}

