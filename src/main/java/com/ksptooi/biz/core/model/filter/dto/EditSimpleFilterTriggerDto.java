package com.ksptooi.biz.core.model.filter.dto;

import com.ksptooi.commons.dataprocess.Str;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
public class EditSimpleFilterTriggerDto {

    @Schema(description = "触发器ID 新增时为null")
    private Long id;

    @Schema(description = "触发器名称")
    private String name;

    @NotNull(message = "目标不能为空")
    @Range(min = 0, max = 4, message = "目标只能为0、1、2、3、4")
    @Schema(description = "目标 0:标头 1:JSON载荷 2:URL 3:HTTP方法 4:总是触发")
    private Integer target;

    @NotNull(message = "条件不能为空")
    @Range(min = 0, max = 4, message = "条件只能为0、1、2、3")
    @Schema(description = "条件 0:包含 1:不包含 2:等于 3:不等于 4:总是")
    private Integer kind;

    @Schema(description = "目标键")
    private String tk;

    @Schema(description = "比较值")
    private String tv;

    @NotNull(message = "排序不能为空")
    @Range(min = 0, message = "排序不能小于0")
    @Schema(description = "排序")
    private Integer seq;

    /**
     * 验证参数逻辑
     *
     * @return 错误信息 当没有错误时返回null
     */
    public String validate() {

        if (target == 4 && kind == 4) {
            return null;
        }

        //如果目标为4 则kind必须为4
        if (target == 4) {
            return "当目标为 总是触发 时，条件只能为 总是触发";
            //不允许提供目标键
        }


        //目标为 0:标头 时需提供目标键 可选提供目标值
        if (target == 0) {
            if (Str.isBlank(tk)) {
                return "目标键不能为空";
            }
        }

        //目标为 1:JSON载荷 时需提供目标键与目标值
        if (target == 1) {
            if (Str.isBlank(tk)) {
                return "目标键不能为空";
            }
        }

        //目标为 2:URL 3:HTTP方法 时只能提供比较值
        if (target == 2 || target == 3) {
            if (Str.isBlank(tv)) {
                return "比较值不能为空";
            }

            //不允许提供目标键
            if (Str.isNotBlank(tk)) {
                return "当目标为URL或HTTP方法时，不能提供目标键";
            }
        }

        return null;
    }


}

