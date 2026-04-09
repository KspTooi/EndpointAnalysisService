package com.ksptool.bio.biz.core.model.org.dto;

import com.ksptool.bio.biz.core.common.aop.DtoCustomValidator;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
public class AddOrgDto implements DtoCustomValidator {

    @Schema(description = "上级组织ID NULL顶级组织")
    private Long parentId;

    @NotNull(message = "组织机构类型不能为空")
    @Range(min = 0, max = 3, message = "组织机构类型必须在0-3之间")
    @Schema(description = "0:企业(租户) 1:子企业 2:部门 3:班组")
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
    @Override
    public String validate() {

        //企业(租户)不允许填写主管ID
        if (kind == 0) {
            if (principalId != null) {
                return "企业(租户)不允许填主管ID";
            }
        }

        //1:子企业 2:部门 3:班组 必须有父级
        if (kind == 1 || kind == 2 || kind == 3) {
            if (parentId == null) {
                return "新增子企业、部门、班组时必须填写上级组织ID";
            }
        }

        return null;
    }
}

