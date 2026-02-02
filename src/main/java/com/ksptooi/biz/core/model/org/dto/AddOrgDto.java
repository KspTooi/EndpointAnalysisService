package com.ksptooi.biz.core.model.org.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
public class AddOrgDto {

    @Schema(description = "上级组织ID NULL顶级组织")
    private Long parentId;

    @NotNull(message = "组织机构类型不能为空")
    @Range(min = 0, max = 1, message = "组织机构类型必须在0和1之间")
    @Schema(description = "0:部门 1:企业")
    private Integer kind;

    @NotNull(message = "组织机构名称不能为空")
    @Size(max = 128, message = "组织机构名称长度不能超过128个字符")
    @Schema(description = "组织机构名称")
    private String name;

    @Schema(description = "主管ID 企业不允许填")
    private Long principalId;

    @NotNull(message = "排序不能为空")
    @Schema(description = "排序")
    private Integer seq;

    /**
     * 验证
     *
     * @return
     */
    public String validate() {

        //企业不允许填写主管ID
        if (kind == 1) {
            if (principalId != null) {
                return "企业不允许填主管ID";
            }
        }

        //顶级组织只能是企业
        if (parentId == null) {
            if (kind != 1) {
                return "顶级组织只能是企业";
            }
        }

        //部门必填parentId
        if (kind == 0) {
            if (parentId == null) {
                return "新增部门时必须填写上级组织ID";
            }
        }


        return null;
    }
}

