package com.ksptooi.biz.requestdebug.model.filter.dto;

import com.ksptooi.commons.dataprocess.Str;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import java.util.List;

@Getter
@Setter
public class AddSimpleFilterDto {

    @NotBlank(message = "过滤器名称不能为空")
    @Schema(description = "过滤器名称")
    private String name;

    @NotNull(message = "过滤器方向不能为空")
    @Schema(description = "过滤器方向 0:请求过滤器 1:响应过滤器")
    @Range(min = 0, max = 1, message = "过滤器方向只能为0或1")
    private Integer direction;

    @NotNull(message = "状态不能为空")
    @Schema(description = "状态 0:启用 1:禁用")
    @Range(min = 0, max = 1, message = "状态只能为0或1")
    private Integer status;

    @NotNull(message = "触发器列表不能为空")
    @Valid
    @Schema(description = "触发器列表")
    private List<AddSimpleFilterTriggerDto> triggers;

    @NotNull(message = "操作列表不能为空")
    @Valid
    @Schema(description = "操作列表")
    private List<AddSimpleFilterOperationDto> operations;

    /**
     * 验证参数逻辑
     *
     * @return 错误信息 当没有错误时返回null
     */
    public String validate() {

        //一个过滤器至少包含一个触发器和一个操作
        if (triggers == null || triggers.isEmpty()) {
            return "触发器列表不能为空";
        }
        if (operations == null || operations.isEmpty()) {
            return "操作列表不能为空";
        }

        //验证每个触发器
        for (AddSimpleFilterTriggerDto trigger : triggers) {
            String validate = trigger.validate();
            if (Str.isNotBlank(validate)) {
                return validate;
            }
        }

        //验证每个操作
        for (AddSimpleFilterOperationDto operation : operations) {
            String validate = operation.validate();
            if (Str.isNotBlank(validate)) {
                return validate;
            }
        }

        return null;
    }

}

