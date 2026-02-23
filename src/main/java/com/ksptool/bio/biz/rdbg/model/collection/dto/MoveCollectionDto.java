package com.ksptool.bio.biz.rdbg.model.collection.dto;

import org.hibernate.validator.constraints.Range;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MoveCollectionDto {

    @NotNull(message = "对象ID不能为空")
    @Schema(description = "对象ID")
    private Long nodeId;

    @Schema(description = "目标ID 为NULL顶级")
    private Long targetId;

    @NotNull(message = "移动方式不能为空")
    @Range(min = 0, max = 2, message = "移动方式只能在0-2之间")
    @Schema(description = "移动方式 0:顶部 1:底部 2:内部")
    private Integer kind;

}
