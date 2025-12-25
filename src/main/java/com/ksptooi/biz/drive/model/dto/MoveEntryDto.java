package com.ksptooi.biz.drive.model.dto;

import java.util.List;
import org.hibernate.validator.constraints.Range;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class MoveEntryDto {

    @Schema(description="目标ID 为NULL顶级")
    private Long targetId;

    @Schema(description="条目ID列表 不能为空")
    @NotNull(message = "条目ID列表不能为空")
    private List<Long> entryIds;

    @Schema(description="移动模式 0:覆盖 1:跳过")
    @NotNull(message = "移动模式不能为空")
    @Range(min = 0, max = 1, message = "移动模式只能在0-1之间")
    private Integer mode;

}
