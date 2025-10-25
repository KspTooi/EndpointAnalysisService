package com.ksptooi.biz.core.model.menu.dto;

import com.ksptooi.commons.dataprocess.Str;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
public class EditMenuDto {

    @NotNull(message = "菜单ID不能为空")
    @Schema(description = "菜单ID")
    private Long id;

    @Schema(description = "父级ID -1:根节点")
    private Long parentId;

    @NotBlank(message = "菜单名称不能为空")
    @Length(min = 2, max = 128, message = "菜单名称长度必须在2-32个字符之间")
    @Schema(description = "菜单名称")
    private String name;

    @Length(max = 200, message = "菜单描述长度不能超过200个字符")
    @Schema(description = "菜单描述")
    private String description;

    @NotNull(message = "菜单类型不能为空")
    @Range(min = 0, max = 2, message = "菜单类型只能在0-2之间")
    @Schema(description = "菜单类型 0:目录 1:菜单 2:按钮")
    private Integer menuKind;

    @Length(max = 256, message = "菜单路径长度不能超过256个字符")
    @Schema(description = "菜单路径(目录不能填写)")
    private String menuPath;

    @Length(max = 256, message = "菜单查询参数长度不能超过512个字符")
    @Schema(description = "菜单查询参数(目录不能填写)")
    private String menuQueryParam;

    @Length(max = 64, message = "菜单图标长度不能超过64个字符")
    @Schema(description = "菜单图标")
    private String menuIcon;

    @NotNull(message = "是否隐藏不能为空")
    @Range(min = 0, max = 1, message = "是否隐藏只能在0-1之间")
    @Schema(description = "是否隐藏 0:否 1:是(menuKind = 1/2时生效)")
    private Integer menuHidden;

    @Length(max = 64, message = "按钮ID长度不能超过64个字符")
    @Schema(description = "按钮ID(menuKind = 2时必填)")
    private String menuBtnId;

    @Length(max = 320, message = "所需权限长度不能超过320个字符")
    @Schema(description = "所需权限(目录不能填写)")
    private String permission;

    @Range(min = 0, max = 655350, message = "排序只能在0-655350之间")
    @Schema(description = "排序")
    private Integer seq;

    /**
     * 纠正输入参数
     */
    public void adjust() {

        //permission为空时纠正为*
        if (Str.isBlank(permission)) {
            permission = "*";
        }

        //parent = -1时纠正为null
        if (parentId != null && parentId == -1) {
            parentId = null;
        }

    }


    /**
     * 验证菜单与按钮是否合法
     *
     * @return 错误信息 无错误返回null
     */
    public String validate() {

        //0:目录
        if (menuKind == 0) {

            //为目录时不能填写的字段
            if (Str.isNotBlank(menuPath)) {
                return "目录不支持填写路径";
            }
            if (Str.isNotBlank(menuQueryParam)) {
                return "目录不支持填写查询参数";
            }
            if (Str.isNotBlank(menuBtnId)) {
                return "目录不支持填写按钮ID";
            }
            if (parentId != null) {
                return "目录仅能放置于根节点";
            }

            //为目录时必须填写的字段
            if (Str.isBlank(name)) {
                return "目录名称不能为空";
            }

        }

        //1:菜单
        if (menuKind == 1) {

            //为菜单时不能填写的字段
            if (Str.isNotBlank(menuBtnId)) {
                return "菜单不支持填写按钮ID";
            }

            //为菜单时必须填写的字段
            if (Str.isBlank(menuPath)) {
                return "菜单路径不能为空";
            }

        }

        //2:按钮
        if (menuKind == 2) {

            //按钮必须有父级
            if (parentId == null) {
                return "按钮必须有父级";
            }

            //为按钮时不能填写的字段
            if (Str.isNotBlank(menuPath)) {
                return "按钮不支持填写路径";
            }
            if (Str.isNotBlank(menuQueryParam)) {
                return "按钮不支持填写查询参数";
            }
            if (Str.isNotBlank(menuIcon)) {
                return "按钮不支持填写图标";
            }

            //为按钮时必须填写的字段
            if (Str.isBlank(menuBtnId)) {
                return "按钮ID不能为空";
            }

        }

        return null;
    }

}

