package com.ksptooi.biz.core.model.registry.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import com.ksptooi.commons.dataprocess.Str;

@Getter
@Setter
public class AddRegistryDto {

    @Schema(description = "父级ID NULL顶级")
    private Long parentId;

    @Schema(description = "键")
    @NotBlank(message = "键不能为空")
    @Length(max = 128, message = "键长度不能超过128个字符")
    @Pattern(regexp = "^[a-zA-Z0-9_\\-]+$", message = "键名只能包含字母、数字、下划线或中划线")
    private String nkey;

    @Schema(description = "数据类型 0:字串 1:整数 2:浮点 3:日期(LDT)")
    @NotNull(message = "数据类型不能为空")
    @Range(min = 0, max = 3, message = "数据类型只能在0-3之间")
    private Integer nvalueKind;

    @Schema(description = "值")
    private String nvalue;

    @Schema(description = "标签")
    @Length(max = 512, message = "标签长度不能超过512个字符")
    private String label;

    @Schema(description = "状态 0:正常 1:停用")
    @NotNull(message = "状态不能为空")
    @Range(min = 0, max = 1, message = "状态值只能是0或1")
    private Integer status;

    @Schema(description = "排序")
    @NotNull(message = "排序不能为空")
    private Integer seq;

    @Schema(description = "说明")
    private String remark;

    @Schema(description = "元数据JSON")
    private String metadata;

    /**
     * 验证接口参数
     * @return 错误信息 无错误返回null
     */
    public String validate() {

        //处理数据类型(如果用户填写了value)
        if(Str.isNotBlank(nvalue)){

            if(nvalueKind == 1 && Str.isNotInteger(nvalue)){
                return "值必须为整数 当前值:" + nvalue;
            }
    
            if(nvalueKind == 2 && Str.isNotDouble(nvalue)){
                return "值必须为浮点数 当前值:" + nvalue;
            }
    
            if(nvalueKind == 3 && Str.isNotDateTime(nvalue)){
                return "值必须为日期时间(yyyy-MM-dd HH:mm:ss) 当前值:" + nvalue;
            }  

        }

        //处理元数据
        if(Str.isBlank(metadata)){
            metadata = "{}";
        }

        return null;
    }
}
