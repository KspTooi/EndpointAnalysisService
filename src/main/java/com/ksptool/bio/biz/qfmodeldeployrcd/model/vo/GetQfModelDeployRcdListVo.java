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
public class GetQfModelDeployRcdListVo {

    @Schema(description="主键ID")
    private Long id;

    @Schema(description="模型ID")
    private Long modelId;

    @Schema(description="模型名称")
    private String name;

    @Schema(description="模型编码")
    private String code;

    @Schema(description="模型版本号")
    private Integer version;

    @Schema(description="部署状态 0:正常 1:部署失败")
    private Integer status;

    @Schema(description="部署时间")
    private LocalDateTime createTime;

}
