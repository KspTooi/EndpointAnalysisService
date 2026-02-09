package com.ksptooi.biz.core.model.registry.dto;

import com.ksptooi.commons.dataprocess.Str;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

/**
 * 添加注册表节点DTO
 * <p>
 * 当记录为"节点"时可接受的字段如下
 * 1.parentId 父级项ID NULL顶级
 * 2.*kind 类型 0:节点
 * 3.*nkey 节点Key
 * 4.label 节点标签
 * 5.remark 说明
 * 6.*seq 排序
 * <p>
 * 当记录为"条目"时可接受的字段如下
 * 1.parentId 父级项ID NULL顶级
 * 2.kind 类型 1:条目
 * 3.nkey 条目Key
 * 4.nvalueKind 数据类型 0:字串 1:整数 2:浮点 3:日期(LDT)
 * 5.nvalue 条目Value
 * 6.label 条目标签
 * 7.remark 说明
 * 8.metadata 元数据JSON
 * 9.status 状态 0:正常 1:停用
 * 10.seq 排序
 */
@Getter
@Setter
public class AddRegistryDto {

    @Schema(description = "父级项ID NULL顶级")
    private Long parentId;

    @Schema(description = "类型 0:节点 1:条目")
    @NotNull(message = "类型不能为空")
    @Range(min = 0, max = 1, message = "类型只能在0-1之间")
    private Integer kind;

    @Schema(description = "节点Key")
    @NotBlank(message = "节点Key不能为空")
    @Length(max = 128, message = "节点Key长度不能超过128个字符")
    @Pattern(regexp = "^[a-zA-Z0-9_\\-]+$", message = "节点Key只能包含字母、数字、下划线或中划线")
    private String nkey;

    @Schema(description = "数据类型 0:字串 1:整数 2:浮点 3:日期(LDT)")
    @Range(min = 0, max = 3, message = "数据类型只能在0-3之间")
    private Integer nvalueKind;

    @Schema(description = "节点Value")
    @Length(max = 1024, message = "节点Value长度不能超过1024个字符")
    private String nvalue;

    @Schema(description = "节点标签")
    @Length(max = 32, message = "节点标签长度不能超过32个字符")
    private String label;

    @Schema(description = "说明")
    @Length(max = 1000, message = "说明长度不能超过1000个字符")
    private String remark;

    @Schema(description = "元数据JSON")
    private String metadata;

    @Schema(description = "状态 0:正常 1:停用")
    @Range(min = 0, max = 1, message = "状态只能在0-1之间")
    private Integer status;

    @Schema(description = "排序")
    @NotNull(message = "排序不能为空")
    private Integer seq;

    /**
     * 验证接口参数
     *
     * @return 错误信息 无错误返回null
     */
    public String validate() {

        //处理添加节点
        if (kind == 0) {

            //节点不允许填写数据类型
            if (nvalueKind != null) {
                return "节点不能填写数据类型";
            }

            //处理不允许填写
            if (Str.isNotBlank(nvalue)) {
                return "节点不能填写Value";
            }

            //节点不允许填写元数据
            if (Str.isNotBlank(metadata)) {
                return "节点不能填写元数据";
            }

            //节点不允许填写状态
            if (status != null) {
                return "节点不能填写状态";
            }

            return null;
        }


        //处理添加条目

        //条目必须有父级
        if (parentId == null) {
            return "条目必须有父级";
        }

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

        // 处理元数据
        if (Str.isBlank(metadata)) {
            metadata = "{}";
        }

        //条目必填状态
        if (status == null) {
            return "条目必须填写状态";
        }

        return null;
    }


}
