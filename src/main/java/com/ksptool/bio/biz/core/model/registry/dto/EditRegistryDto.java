package com.ksptool.bio.biz.core.model.registry.dto;

import com.ksptool.bio.commons.dataprocess.Str;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
public class EditRegistryDto {

    @Schema(description = "ID")
    @NotNull(message = "ID不能为空")
    private Long id;

    @Schema(description = "类型 0:节点 1:条目")
    @NotNull(message = "类型不能为空")
    @Range(min = 0, max = 1, message = "类型只能在0-1之间")
    private Integer kind;

    @Schema(description = "数据类型 0:字串 1:整数 2:浮点 3:日期(LDT)")
    @Range(min = 0, max = 3, message = "数据类型只能在0-3之间")
    private Integer nvalueKind;

    @Schema(description = "条目Value")
    @Length(max = 1024, message = "节点Value长度不能超过1024个字符")
    private String nvalue;

    @Schema(description = "条目标签")
    @Length(max = 32, message = "节点标签长度不能超过32个字符")
    private String label;

    @Schema(description = "排序")
    @NotNull(message = "排序不能为空")
    private Integer seq;

    @Schema(description = "说明")
    @Length(max = 1000, message = "说明长度不能超过1000个字符")
    private String remark;

    @Schema(description = "元数据JSON")
    private String metadata;

    @Schema(description = "状态 0:正常 1:停用")
    @Range(min = 0, max = 1, message = "状态只能在0-1之间")
    private Integer status;

    /**
     * 验证接口参数
     *
     * @return 错误信息 无错误返回null
     */
    public String validate() {

        //处理编辑节点
        if (kind == 0) {

            //节点不允许修改nvalueKind
            if (nvalueKind != null) {
                return "节点不能修改数据类型";
            }

            //节点不允许修改nvalue
            if (nvalue != null) {
                return "节点不能修改Value";
            }

            //节点不允许修改metadata
            if (metadata != null) {
                return "节点不能修改元数据";
            }

            //节点不允许修改status
            if (status != null) {
                return "节点不能修改状态";
            }

        }

        //处理编辑条目
        if (kind == 1) {

            //条目必填Value和数据类型
            if (nvalueKind == null) {
                return "条目必须填写数据类型";
            }

            if (Str.isBlank(nvalue)) {
                return "条目必须填写Value";
            }

            if (nvalueKind == 1 && Str.isNotInteger(nvalue)) {
                return "值必须为整数 当前值:" + nvalue;
            }

            if (nvalueKind == 2 && Str.isNotDouble(nvalue)) {
                return "值必须为浮点数 当前值:" + nvalue;
            }

            if (nvalueKind == 3 && Str.isNotDateTime(nvalue)) {
                return "值必须为日期时间(yyyy-MM-dd HH:mm:ss) 当前值:" + nvalue;
            }

            //条目必填状态
            if (status == null) {
                return "条目必须填写状态";
            }

            //处理元数据
            if (Str.isBlank(metadata)) {
                metadata = "{}";
            }

        }

        return null;
    }

}
