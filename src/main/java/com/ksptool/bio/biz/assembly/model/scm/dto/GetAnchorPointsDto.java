package com.ksptool.bio.biz.assembly.model.scm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
public class GetAnchorPointsDto {

    @NotNull(message = "SCM ID不能为空")
    @Schema(description = "SCM ID")
    private Long scmId;

    @NotNull(message = "类型不能为空")
    @Range(min = 0, max = 1, message = "类型只能为0或1")
    @Schema(description = "类型 0:输入 1:输出")
    private Integer kind;

}
